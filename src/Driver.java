import java.util.Arrays;
import java.util.List;

public class Driver {

    private int n = 100;// number of source data

    private final int hidden;
    private final int trains;
    private final DataProvider dataProvider;
    private double[][] x;
    private double[][] y;
    private NeuralNetwork nn;
    private final int[] trainPoses = new int[n];
    private double[] predictValues, sourceRes;
    private Main.Drawable callback;

    public Driver(DataProvider dataProvider, int hidden, int trains, Main.Drawable callback) {
        this.hidden = hidden;
        this.trains = trains;
        this.dataProvider = dataProvider;
        this.callback = callback;
    }

    public static void main(String[] args) {
        Driver dr = new Driver(new DataProvider(Func.sinx), 3, 100000, Main.Drawable.EMPTY);
        dr.teach(0.1);
        dr.print();
    }

    public void teach(double l_rate) {
        x = dataProvider.getInput(n);
        y = dataProvider.getOutput(n);
        sourceRes = new double[n];
        for (int pos = 0; pos < n; pos++) {
            sourceRes[pos] = y[pos][0];
        }
        System.out.println("\n\n\n");

        predictValues = new double[n];
        nn = new NeuralNetwork(2, hidden, 1, l_rate, 100);
        System.out.println("---------- Init Network setup:");
        nn.print();
        System.out.println("Teaching ...");
        nn.fit(x, y, trains, this);
        updatePredictValues();
//        printStat();
        printDiffStat();
    }

    private void printDiffStat() {
        double[] delta = new double[sourceRes.length];
        for (int pos = 0; pos < n; pos++) {
            delta[pos] = predictValues[pos] - sourceRes[pos];
        }
        double averageDelta = Arrays.stream(delta).average().getAsDouble();
        double maxDelta = Arrays.stream(delta).map(Math::abs).max().getAsDouble();
        double sdevPre=Arrays.stream(delta).map(v->Math.pow(v-averageDelta,2)).sum();
        double sdev = Math.sqrt(sdevPre/n);
        System.out.println("max delta: " + maxDelta);
        System.out.println("average delta: " + averageDelta);
        System.out.println("standard deviation: " + sdev);
    }

    private void printStat() {
        int sum = Arrays.stream(trainPoses).sum();
        for (int i=0; i< trainPoses.length; i++) {
            System.out.println("pos: " + i + ", " + trainPoses[i] + ", " + trainPoses[i]*100.0/sum + " %");
        }
    }

    void updatePredictValues() {
        for (int pos = 0; pos < n; pos++) {
            predictValues[pos] = nn.predict(x[pos]).get(0);
        }
        callback.paint(getSourceRes(), getPredictValues());
    }

    public void printNN() {
        nn.print();
    }

    public void print() {
        System.out.printf("%10s %10s %10s %10s \n", "x", "func(x)", "pred", "error");
        for (int pos = 0; pos < n; pos++) {
            List<Double> output = nn.predict(x[pos]);
            System.out.printf("%10f, %10f, %10f, %10f \n", x[pos][0], y[pos][0], output.get(0),
                    (y[pos][0] - output.get(0)));

        }
    }

    public double[] getPredictValues() {
        return predictValues;
    }

    public double[] getSourceRes() {
        return sourceRes;
    }


    public void stop() {
        nn.stop();
    }

    public void train(int trainPos) {
        trainPoses[trainPos]++;
    }
}