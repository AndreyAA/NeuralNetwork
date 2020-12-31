import java.util.List;

public class NeuralNetwork {

    private volatile boolean stop = false;
    private final Matrix weights_ih, weights_ho, bias_h, bias_o;

    /**
     * create Neural Network with sigmoid for hidden layer
     * @param input number of imputs
     * @param hidden number of hidden neurons in 1 hidden layer
     * @param output number of outputs
     */
    public NeuralNetwork(int input, int hidden, int output) {
        weights_ih = new Matrix(hidden, input);
        weights_ho = new Matrix(output, hidden);

        bias_h = new Matrix(hidden, 1);
        bias_o = new Matrix(output, 1);
    }

    public void print() {
        System.out.println("inputs: " + weights_ih.cols);
        System.out.println("hidden: " + weights_ih.rows);
        System.out.println("output: " + weights_ho.rows);
        System.out.println("weights ih:");
        weights_ih.print();

        System.out.println("weights ho:");
        weights_ho.print();

        System.out.println("bias_h:");
        bias_h.print();

        System.out.println("bias_o:");
        bias_o.print();
    }

    public void fit(double[][] X, double[][] Y, int trains, Driver driver, double lRate, double updateEachTrains) {
        for (int i = 0; i < trains; i++) {
            int sampleN = (int) (Math.random() * X.length);
            driver.train(sampleN);
            train(X[sampleN], Y[sampleN], lRate);
            if (i % updateEachTrains == 0) {
                driver.invokeCallback();
                if (stop) {
                    return;
                }
            }
        }
    }

    public List<Double> predict(double[] X) {
        Matrix input = Matrix.fromArray(X);
        Matrix output = predictInternal(input).getFirst();
        return output.toArray();
    }

    private Pair<Matrix> predictInternal(Matrix input) {

        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);
//        output.sigmoid();
        return new Pair<>(output, hidden);
    }


    public void train(double[] X, double[] Y, double lRate) {
        Matrix input = Matrix.fromArray(X);
        Pair<Matrix> res = predictInternal(input);
        Matrix output = res.getFirst();
        Matrix hidden = res.getSecond();
/*        Matrix input = Matrix.fromArray(X);
        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);
        output.sigmoid();*/

        Matrix target = Matrix.fromArray(Y);

        Matrix error = Matrix.subtract(target, output);
//        Matrix gradient = output.dsigmoid();
//        gradient.multiply(error);
        error.multiply(lRate);

        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix who_delta = Matrix.multiply(error, hidden_T);

        weights_ho.add(who_delta);
        bias_o.add(error);

        Matrix who_T = Matrix.transpose(weights_ho);
        Matrix hidden_errors = Matrix.multiply(who_T, error);

        Matrix h_gradient = hidden.dsigmoid();
        h_gradient.multiply(hidden_errors);
        h_gradient.multiply(lRate);

        Matrix i_T = Matrix.transpose(input);
        Matrix wih_delta = Matrix.multiply(h_gradient, i_T);

        weights_ih.add(wih_delta);
        bias_h.add(h_gradient);

    }

    public void stop() {
        stop = true;
    }

    public static class Pair<T> {
        private final T first, second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }
    }


}