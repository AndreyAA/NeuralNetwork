# NeuralNetwork
Very simple neural network to predict trigonometric functions with GUI and console parts.

Based on article: https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c

Neural Network:
- inputs: 1;
- hidden layers: 1;
- hidden neuros: configurable;
- output: 1;

Requirements:
only Java SE 8;

UI has control to choose:
- Trainings number;
- Size of hidden neurons;
- Learning rate;
- fromX, toX, number of data;
- Function to predict (sin(x), sin(2x), sin(x)*x, cos(2x)*x, x*x, sqrt(x)

About Neural Network:
- 3 hidden neurons are enough to emulate sinx on [-pi, pi], x*x
- 4 hidden neurons are enough to emulate sin2x on [-pi, pi]
- 5 hidden neurons are enough to emulate x*cos2x on [-pi, pi]

How to run:
- UI : Main.main()
- Console: Driver.main()

TODO:
- add min error to stop learning;
- save/load NN state;

![Alt text](res/nn.png?raw=true "NeuralNetwork")
