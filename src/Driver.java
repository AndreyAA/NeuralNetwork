import java.util.Arrays;
import java.util.List;

public class Driver {

    private final int number;
    private final int hidden;
    private final int trains;
    private final DataProvider dataProvider;
    private double[][] x;
    private double[][] y;
    private NeuralNetwork nn;
    private final int[] trainPoses;
    private double[] predictValues, sourceRes;
    private Main.Drawable callback;

    public Driver(DataProvider dataProvider, int hidden, int trains, Main.Drawable callback) {
        this(dataProvider, hidden, trains, callback, null);
    }
    public Driver(DataProvider dataProvider, int hidden, int trains, Main.Drawable callback, NeuralNetwork nn) {
        this.hidden = hidden;
        this.trains = trains;
        this.dataProvider = dataProvider;
        this.number = dataProvider.getNumber();
        this.trainPoses = new int[dataProvider.getNumber()];
        this.callback = callback;
        this.nn = nn;
    }

    public static void main(String[] args) {
        Driver dr = new Driver(new DataProvider(Func.sinx, -Math.PI, Math.PI, 100), 3, 100000, Main.Drawable.EMPTY);
        dr.teach(0.1);
        dr.print();
    }

    public void teach(double l_rate) {
        prepareSourceData();
        System.out.println("\n\n\n");
        nn = new NeuralNetwork(2, hidden, 1, l_rate, 100);
        System.out.println("---------- Init Network setup:");
        nn.print();
        System.out.println("Teaching ...");
        nn.fit(x, y, trains, this);
        invokeCallback();
        nn.print();
//        printStat();
        printDiffStat();
    }

    void prepareSourceData() {
        x = dataProvider.getInput(number);
        y = dataProvider.getOutput(number);
        sourceRes = new double[number];
        for (int pos = 0; pos < number; pos++) {
            sourceRes[pos] = y[pos][0];
        }
        predictValues = new double[number];
    }

    private void printDiffStat() {
        double[] delta = new double[sourceRes.length];
        for (int pos = 0; pos < number; pos++) {
            delta[pos] = predictValues[pos] - sourceRes[pos];
        }
        double averageDelta = Arrays.stream(delta).average().getAsDouble();
        double maxDelta = Arrays.stream(delta).map(Math::abs).max().getAsDouble();
        double sdevPre=Arrays.stream(delta).map(v->Math.pow(v-averageDelta,2)).sum();
        double sdev = Math.sqrt(sdevPre/ number);
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

    void invokeCallback() {
        updatePredictValues();
        callback.paint(getSourceRes(), getPredictValues());
    }

    void updatePredictValues() {
        for (int pos = 0; pos < number; pos++) {
            predictValues[pos] = nn.predict(x[pos]).get(0);
        }
    }

    public void printNN() {
        nn.print();
    }

    public void print() {
        System.out.printf("%10s %10s %10s %10s \n", "x", "func(x)", "pred", "error");
        for (int pos = 0; pos < number; pos++) {
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

    public Driver copyNN(DataProvider dataProvider, int numberOfNeurons, int numberForTrainings, Main.Drawable drawable) {
        return new Driver(dataProvider, numberOfNeurons, numberForTrainings, drawable, this.nn);
    }
}