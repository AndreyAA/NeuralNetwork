public class DataProvider {

    private Func func;

    public DataProvider(Func func) {
        this.func = func;
    }

    private static double getXforStep(double step, int i) {
        return -Math.PI + step * i;
    }

    private static double calcStep(int n) {
        return 2 * Math.PI / n;
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
