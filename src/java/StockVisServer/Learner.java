package StockVisServer;

import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.som.SOM;

import java.text.DecimalFormat;

/**
 * User: Karthik
 * Date: 10/1/13
 * Time: 1:48 PM
 */
public class Learner {
    SOM som;
    MapData mapdata;
    int numberOfInputs;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");


    public Learner(int num_of_inputs) {
        som = new SOM(num_of_inputs, Config.SOM_HIDDEN_LAYER_X * Config.SOM_HIDDEN_LAYER_Y);
        mapdata = new MapData(som, num_of_inputs);
        this.numberOfInputs = num_of_inputs;
    }

    public void printWeights() {
        Matrix weights = mapdata.weights;
        for (int i = 0; i < Config.SOM_HIDDEN_LAYER_X * Config.SOM_HIDDEN_LAYER_Y; i++) {
            for (int j = 0; j < numberOfInputs; j++) {
                System.out.print(weights.get(i, j)+"\t");
            }
            System.out.println();
        }
    }

    public void setWeightVector (int index, double[] weight) {
        for (int j = 0; j < numberOfInputs; j++) {
           mapdata.weights.set(index, j, weight[j]);
        }
    }


    public double[] getWeightVector (int index) {
        double[] weight_vector = new double[numberOfInputs];
        for (int j = 0; j < numberOfInputs; j++) {
            weight_vector[j] = Double.parseDouble(decimalFormat.format(mapdata.weights.get(index, j)));
        }
        return weight_vector;
    }

    public void printWeights(int index) {
        Matrix weights = mapdata.weights;
        for (int j = 0; j < numberOfInputs; j++) {
            System.out.print(weights.get(index, j)+"\t");
        }
        System.out.println();

    }
}
