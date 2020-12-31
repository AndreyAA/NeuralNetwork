import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.awt.GridBagConstraints.*;

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
        c.fill = GridBagConstraints.BOTH;

        add(canvas, c);

        panel = new SetupPanel(canvas);
        panel.setSize(800, 50);
        c.fill = HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.1;
        c.weightx = 1;
        add(panel, c);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main("Neural Network");
    }

    public interface Drawable {
        public static Drawable EMPTY = new Drawable() {
            @Override
            public void paint(double[] base, double[] predict, Stat stat) {
                //empty
            }
        };
        public void paint(double[] base, double[] predict, Stat stat);
    }

    private static class SetupPanel extends JPanel {
        private final JTextField trainNumber;
        private final JTextField numberOfNeurons;
        private final JTextField rateTextField;
        private final JTextField fromXTextField;
        private final JTextField toXTextField;
        private final JTextField numberTextField;
        private final JCheckBox freezNNCheckBox;
        private final JComboBox<String> functionTextField;
        private final Drawable drawable;
        private ThreadPoolExecutor executor =
                new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                        new ArrayBlockingQueue(1));
        private volatile Driver dr;

        public SetupPanel(Drawable drawable) {
            super();
            this.drawable = drawable;
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = 1;
            c.weightx = 1;
            c.fill = NONE;
            c.ipadx = 5;

            addComponent(0, 0, c, new JLabel("Trainings number: "), GridBagConstraints.NONE, LINE_END);
            trainNumber = new JTextField("200000");
            addComponent(0, 1, c, trainNumber, GridBagConstraints.HORIZONTAL, LINE_START);

            addComponent(0, 2, c, new JLabel("Hidden neurons: "), GridBagConstraints.NONE, LINE_END);
            numberOfNeurons = new JTextField("3");
            addComponent(0, 3, c, numberOfNeurons, GridBagConstraints.HORIZONTAL, LINE_START);

            addComponent(0, 4, c, new JLabel("Learning rate: "), GridBagConstraints.NONE, LINE_END);
            rateTextField = new JTextField("0.2");
            addComponent(0, 5, c, rateTextField, GridBagConstraints.HORIZONTAL, LINE_START);

            addComponent(0, 6, c, new JLabel("Function: "), GridBagConstraints.NONE, LINE_END);
            functionTextField = new JComboBox<>();
            for (Func value : Func.values()) {
                functionTextField.addItem(value.getLabel());
            }
            addComponent(0, 7, c, functionTextField, GridBagConstraints.NONE, LINE_START);

            addComponent(1, 0, c, new JLabel("From X: "), GridBagConstraints.NONE, LINE_END);
            fromXTextField = new JTextField("-3.1415");
            addComponent(1, 1, c, fromXTextField, GridBagConstraints.HORIZONTAL, c.anchor);

            addComponent(1, 2, c, new JLabel("To X: "), GridBagConstraints.NONE, LINE_END);
            toXTextField = new JTextField("3.1415");
            addComponent(1, 3, c, toXTextField, GridBagConstraints.HORIZONTAL, LINE_START);

            addComponent(1, 4, c, new JLabel("Number of data: "), GridBagConstraints.NONE, LINE_END);
            numberTextField = new JTextField("100");
            addComponent(1, 5, c, numberTextField, GridBagConstraints.HORIZONTAL, LINE_START);

            addComponent(1, 6, c, new JLabel("Freeze NN: "), GridBagConstraints.NONE, LINE_END);
            freezNNCheckBox = new JCheckBox("", false);
            addComponent(1, 7, c, freezNNCheckBox, GridBagConstraints.NONE, LINE_START);

            JButton contButton = new JButton("Continue");
            c.gridheight=1;
            addComponent(0, 8, c, contButton, HORIZONTAL, GridBagConstraints.CENTER);
            contButton.addActionListener((l) -> {
                executor.execute(() -> {
                    dr.continueTeach(rate());
                    this.drawable.paint(dr.getSourceRes(), dr.getPredictValues(), dr.calcDiff());
                });
            });
            contButton.setEnabled(false);

            JButton button = new JButton(" Apply ");
            c.gridheight=1;
            addComponent(1, 8, c, button, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            button.addActionListener((l) -> {
                if (dr != null) {
                    dr.stop();
                }
                if (needTeachNN()) {
                    dr = new Driver(new DataProvider(func(), fromX(), toX(), numberOfSourceData()),
                            numberOfNeurons(), numberForTrainings(), drawable);
                } else {
                    dr = dr.copyNN(new DataProvider(func(), fromX(), toX(), numberOfSourceData()),
                            numberOfNeurons(), numberForTrainings(), drawable);
                }
                executor.execute(() -> {
                    if (needTeachNN()) {
                        dr.newTeach(rate());
                    } else {
                        dr.prepareSourceData();
                        dr.updatePredictValues();
                    }
                    this.drawable.paint(dr.getSourceRes(), dr.getPredictValues(), dr.calcDiff());
                });

                contButton.setEnabled(true);
            });
        }

        private void addComponent(int row, int column, GridBagConstraints c, JComponent comp, int fill, int anchor) {
            c.gridy = row;
            c.gridx = column;
            c.anchor = anchor;
            c.fill = fill;
            add(comp, c);
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

        public boolean needTeachNN() {
            return !freezNNCheckBox.isSelected();
        }
        public double fromX() {
            return Double.parseDouble(fromXTextField.getText());
        }

        public double toX() {
            return Double.parseDouble(toXTextField.getText());
        }

        public int numberOfSourceData() {
            return Integer.parseInt(numberTextField.getText());
        }

        public double rate() {
            return Double.parseDouble(rateTextField.getText());
        }
    }

    public static class MyGraphic extends Canvas implements Drawable {
        double min, max;
        private double[] base;
        private double[] predict;
        private Stat stat;

        public MyGraphic(GraphicsConfiguration config) {
            super(config);
        }

        @Override
        public void paint(double[] base, double[] predict, Stat stat) {
            this.base = base;
            this.predict = predict;
            if (base == null || predict == null) {
                return;
            }
            min = Math.min(Arrays.stream(base).min().getAsDouble(), Arrays.stream(predict).min().getAsDouble());
            max = Math.max(Arrays.stream(base).max().getAsDouble(), Arrays.stream(predict).max().getAsDouble());
            this.stat = stat;

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
            printStat(g, stat);
        }

        private void printStat(Graphics g, Stat stat) {
            if (stat!=null) {
                g.setColor(Color.WHITE);
                drawText(g, String.format("Average delta: %f", stat.getAvg()), 10, 10);
                drawText(g, String.format("Max delta: %f", stat.getMax()), 10, 25);
                drawText(g, String.format("Std dev: %f", stat.getStdDev()), 10, 40);
            }
        }

        private void drawText(Graphics g, String averageDelta, int x, int y) {
            g.drawChars(averageDelta.toCharArray(), 0, averageDelta.toCharArray().length, x, y);
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
