public class Stat {
    private final double avg, max, stdDev;

    public Stat(double avg, double max, double stdDev) {
        this.avg = avg;
        this.max = max;
        this.stdDev = stdDev;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }

    public double getStdDev() {
        return stdDev;
    }
}
