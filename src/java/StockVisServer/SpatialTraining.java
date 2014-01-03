package StockVisServer;

import com.google.gson.Gson;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.BasicTrainSOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodRBF;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * User: Karthik
 * Date: 10/12/13
 * Time: 10:06 PM
 */
public class SpatialTraining {

    public static void main(String[] args) {
        Parser parser = new Parser(null);
        parser.parseFiles();
        //Training Data
        TrainingData data = new TrainingData(parser.minNumOfRecords, parser.stockIds.size());

        int num_of_stocks = 0;
        int num_of_records = 0;

        for (String stockId : parser.stockIds) {
            Stock stock = parser.getStockValues(stockId);
            if (stock == null || stock.numberOfRecords < parser.minNumOfRecords) {
                continue;
            }

            //check if stock has the needed data
            int startDateId = stock.dates.indexOf(data.trainStartDate);
            int endDateId = stock.dates.indexOf(data.trainEndDate);

            if (startDateId < 0  || endDateId < 0) {
                System.out.println("Not using "+ stockId);
                continue;
            }
            num_of_records = Math.abs(startDateId - endDateId) + 1;

            data.add(stock);
            num_of_stocks++;
        }

        data.changeNumOfRecords(num_of_records);
        data.changeNumOfStocks(num_of_stocks);

        MLDataSet dataset = data.getSpatialTrainingData();

        Learner learner = new Learner(num_of_stocks);
        SOM network = learner.som;

        Gson gson = new Gson();

        if (Config.DO_TRAIN) {
            NeighborhoodRBF gaussian = new NeighborhoodRBF(RBFEnum.Gaussian, Config.SOM_HIDDEN_LAYER_X, Config.SOM_HIDDEN_LAYER_Y);
            BasicTrainSOM train = new BasicTrainSOM(
                    network,
                    0.3,
                    dataset,
                    gaussian);

            int epoch = 0;
            double error;

            try{
                do
                {
                    train.iteration();
                    error = train.getError();
                    epoch++;
                    System.out.println("Iteration "+ epoch+" -- Error "+error);
                } while (epoch < 100 && error > 50);
            } catch(Exception e) {
                e.printStackTrace();
            }
            finally
            {
                train.finishTraining();
            }

        } else {
            //read from file
            JSONParser fileParser = new JSONParser();
            try {
                JSONArray a = (JSONArray) fileParser.parse(new FileReader(Config.SAVE_DIRECTORY+Config.SOM_FILE));
                JSONObject weight_obj = (JSONObject) a.get(0);
                String weights = "[" +(weight_obj.get("weights")).toString()+ "]";
                double[][] SOM_WEIGHTS = gson.fromJson(weights, (Type) double[][].class);

                System.out.println("----------------------------------------------");
                for (int i = 0; i < Config.SOM_HIDDEN_LAYER_X*Config.SOM_HIDDEN_LAYER_Y ; i++) {
                     learner.setWeightVector(i, SOM_WEIGHTS[i]);
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 400; i++) {
            MLData data1 = new BasicMLData(data.SOM_INPUT[i]);
            data.print_spatial_input(i);
            learner.printWeights(network.classify(data1));
            System.out.println("Pattern "+i+" winner: " + network.classify(data1));
        }

        //save weights to a file!
        String weights = "{ \"stocks\": [";
        for (int i = 0; i < num_of_stocks; i++) {
            if (i == num_of_stocks -1) {
                weights += gson.toJson(data.stocks.get(i).getSymbol());
                continue;
            }
            weights += gson.toJson(data.stocks.get(i).getSymbol()) +",";
        }
        weights +="],";

        weights += "\"weights\": [";
        for (int i = 0; i < Config.SOM_HIDDEN_LAYER_X*Config.SOM_HIDDEN_LAYER_Y; i++) {
            double[] temp_weights = learner.getWeightVector(i);
            String weight_vector = gson.toJson(temp_weights);
            if (i==Config.SOM_HIDDEN_LAYER_X*Config.SOM_HIDDEN_LAYER_Y - 1) {
                weights = weights + weight_vector;
                continue;
            }
            weights = weights + weight_vector + ",";
        }

        weights += "]}";

//
//        String temp = "[";
//        for (int j = 0; j < num_of_stocks; j++) {
//            if (j == num_of_stocks - 1) {
//                temp += "\""+data.stocks.get(j).getSymbol()+"\": "+ gson.toJson(temp_weights[j]);
//                continue;
//            }
//            temp += "\""+data.stocks.get(j).getSymbol()+"\": "+ gson.toJson(temp_weights[j])+",";
//        }
//        temp += "]";

        //write data to a file which can be sent
        try {
            PrintWriter out = new PrintWriter(Config.SAVE_DIRECTORY+Config.SOM_FILE);
            out.println(weights);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
