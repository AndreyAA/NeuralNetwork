public class DataProvider {

    private final Func func;
    private final double fromX, toX;
    private final int number;

    public DataProvider(Func func, double fromX, double toX, int number) {
        this.func = func;
        this.fromX = fromX;
        this.toX = toX;
        this.number = number;
    }

    private double getXforStep(double step, int i) {
        return fromX + step * i;
    }

    private double calcStep(int n) {
        return (toX - fromX) / number;
    }

    public Func getFunc() {
        return func;
    }

    public double getFromX() {
        return fromX;
    }

    public double getToX() {
        return toX;
    }

    public int getNumber() {
        return number;
    }

    public double[][] getOutput(int n) {
        double step = calcStep(n);
        double[][] result = new double[n][1];
        for (int i = 0; i < n; i++) {
            result[i][0] = func.getFunct().apply(getXforStep(step, i));
        }
        return result;
    }

    public double[][] getInput(int n) {
        double step = calcStep(n);
        double[][] result = new double[n][2];
        for (int i = 0; i < n; i++) {
            result[i][0] = getXforStep(step, i);
            result[i][1] = 1;//shift
        }

        return result;
    }

}
