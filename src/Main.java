import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame {

    private MyGraphic canvas;
    private SetupPanel panel;

    public Main(String title) throws HeadlessException {
        super(title);
        setLayout(new GridBagLayout());
        canvas = new MyGraphic(this.getGraphicsConfiguration());
        canvas.setSize(800, 600);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.9;

        add(canvas, c);

        panel = new SetupPanel(canvas);
        panel.setSize(800, 50);
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.1;
        add(panel, c);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main("Neural Network");
    }

    public interface Drawable {
        public static Drawable EMPTY = new Drawable() {
            @Override
            public void paint(double[] base, double[] predict) {
                //empty
            }
        };
        public void paint(double[] base, double[] predict);
    }

    private static class SetupPanel extends JPanel {
        private final JTextField trainNumber;
        private final JTextField numberOfNeurons;
        private final JTextField rateTextField;
        private final JComboBox<String> functionTextField;
        private final Drawable drawable;
        private ThreadPoolExecutor executor =
                new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                        new ArrayBlockingQueue(1));
        private Driver dr;

        public SetupPanel(Drawable drawable) {
            super();
            this.drawable = drawable;
            setLayout(new GridLayout(1, 8));
            // 1 row
            add(new JLabel("Trainings number"));
            trainNumber = new JTextField("1000000");
            add(trainNumber);

            // 2 row
            add(new JLabel("Hidden neurons"));
            numberOfNeurons = new JTextField("3");
            add(numberOfNeurons);

            // 3 row
            add(new JLabel("Learning rate"));
            rateTextField = new JTextField("0.1");
            add(rateTextField);

            // 4 row

            functionTextField = new JComboBox<>();
            for (Func value : Func.values()) {
                functionTextField.addItem(value.getLabel());
            }
            add(functionTextField);

            // 5 row
            JButton button = new JButton("Apply");
            add(button);
            button.addActionListener((l) -> {
                if (dr != null) {
                    dr.stop();
                }
                dr = new Driver(new DataProvider(func()), numberOfNeurons(), numberForTrainings(), drawable);
                executor.execute(() -> startTeach(dr));
            });
        }

        private void startTeach(Driver dr) {
            dr.teach(rate());
            drawable.paint(dr.getSourceRes(), dr.getPredictValues());
            dr.printNN();
        }

        public int numberOfNeurons() {
            return Integer.parseInt(numberOfNeurons.getText());
        }

        public Func func() {
            return Func.labelOf((String) functionTextField.getSelectedItem());
        }

        public int numberForTrainings() {
            return Integer.parseInt(trainNumber.getText());
        }

        public double rate() {
            return Double.parseDouble(rateTextField.getText());
        }
    }

    public static class MyGraphic extends Canvas implements Drawable {
        double min, max;
        private double[] base;
        private double[] predict;

        public MyGraphic(GraphicsConfiguration config) {
            super(config);
        }

        @Override
        public void paint(double[] base, double[] predict) {
            this.base = base;
            this.predict = predict;
            if (base == null || predict == null) {
                return;
            }
            min = Math.min(Arrays.stream(base).min().getAsDouble(), Arrays.stream(predict).min().getAsDouble());
            max = Math.max(Arrays.stream(base).max().getAsDouble(), Arrays.stream(predict).max().getAsDouble());
            repaint();
        }


        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            drawData(g, predict);
            g.setColor(Color.WHITE);
            drawData(g, base);
        }

        private void drawData(Graphics g, double[] base) {
            if (base != null && base.length != 0) {
                double yScale = getHeight() / (2 * max);
                int stepX = getWidth() / base.length;
                for (int i = 1; i < base.length; i++) {
                    g.drawLine((i - 1) * stepX,
                            getHeight() / 2 - (int) (yScale * base[i - 1]),
                            i * stepX, getHeight() / 2 - (int) (yScale * base[i]));
                }
            }
        }
    }
}
