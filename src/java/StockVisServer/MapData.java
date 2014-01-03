package StockVisServer;

import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.som.SOM;

/**
 * User: Karthik
 * Date: 10/1/13
 * Time: 7:36 PM
 */
public class MapData {

    int inputSize;
    Matrix weights;

    public MapData(SOM som, int inputSize)
    {
        this.weights = som.getWeights();
        this.inputSize = inputSize;
        initialRandom();

    }

    private void initialRandom() {
        for (int i = 0; i < Config.SOM_HIDDEN_LAYER_X; i++) {
            for (int j = 0; j < Config.SOM_HIDDEN_LAYER_Y; j++) {
                int index = (j*Config.SOM_HIDDEN_LAYER_X)+i;
                for (int k = 0; k < inputSize; k++) {
                    weights.set(index, i, Math.random()*10);
                }
            }
        }
    }

    public double[] getValue(int x, int y) {
        int index = (y*Config.SOM_HIDDEN_LAYER_X)+x;
        double[] weight = new double[inputSize];

        for (int i = 0; i < inputSize; i++) {
            weight[i] = weights.get(index, i);
        }

        return weight;
    }
}
