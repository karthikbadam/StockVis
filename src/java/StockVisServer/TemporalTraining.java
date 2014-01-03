package StockVisServer;

import java.io.File;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.util.ArrayList;
import javax.servlet.ServletContext;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * User: Karthik
 * Date: 10/13/13
 * Time: 11:54 AM
 */

public class TemporalTraining {

    public static ArrayList<MLDataSet> temporalTraining = new ArrayList<MLDataSet>();
    public static ArrayList<MLDataSet> temporalTesting = new ArrayList<MLDataSet>();
    
    Parser parser; 
    TrainingData data;
    int numOfStocks; 
    ServletContext context;
    
    public TemporalTraining(ServletContext context) {
        this.context = context;
        parser = new Parser(context);
        parser.parseFiles();
        
        data = new TrainingData(parser.minNumOfRecords, parser.stockIds.size());

        int num_of_stocks = 0;
        int num_of_records = 0;

        ArrayList<String> stockIds = parser.stockIds;
        
        for (int i = 0 ; i < stockIds.size(); i++ ) {
            String stockId = stockIds.get(i);
            Stock stock = parser.getStockValues(stockId);
            if (stock == null || stock.numberOfRecords < parser.minNumOfRecords) {
                continue;
            }

            //check if stock has the needed data
            int startDateId = stock.dates.indexOf(data.trainStartDate);
            int endDateId = stock.dates.indexOf(data.trainEndDate);

            if (startDateId < 0  || endDateId < 0) {
                continue;
            }
            
            num_of_records = Math.abs(startDateId - endDateId) + 1;
            data.changeNumOfRecords(num_of_records);
            
            data.add(stock);
            MLDataSet temporal_training_dataset = data.getTemporalTrainingData(stock);
            MLDataSet temporal_testing_dataset = data.getTemporalTestingData(stock);
            temporalTraining.add(temporal_training_dataset);
            temporalTesting.add(temporal_testing_dataset);
            System.out.println(num_of_stocks);
            num_of_stocks++;
        }
        data.changeNumOfStocks(num_of_stocks);
        
        numOfStocks = num_of_stocks;
    }
    
    
    public String temporalTrain(String stockId) {
        System.out.println("Training");
        
        final BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(Config.INPUT_WINDOW));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, Config.HIDDEN1_COUNT));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, Config.HIDDEN2_COUNT));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, Config.PREDICT_WINDOW));
        network.getStructure().finalizeStructure();
        network.reset();
        
        // train the neural network
        Train train = null;
        
        for (int i = 0; i < numOfStocks; i++) {
            System.out.println(data.stocks.get(i).getSymbol());
            
            if (!data.stocks.get(i).getSymbol().contains(stockId)) {
                continue;
            }
                
            int epoch = 1;
            train = new ResilientPropagation(network, temporalTraining.get(i));
            
            for (int j = 0; j < data.stocks.get(i).adj_close.size(); j++) {
                System.out.println(data.stocks.get(i).getAdjCloseNormalized(j)+" --- "+data.stocks.get(i).adj_close.get(j));
            }
            
            train.addStrategy(new RequiredImprovementStrategy(10));

            double error;
            try{
                do
                {
                    train.iteration();
                    error = train.getError();
                    epoch++;
                    System.out.println(epoch+"--"+error);
                } while (epoch < 30000);
            } catch(Exception e) {
                e.printStackTrace();
            }
            finally
            {
                train.finishTraining();
            }

            //EncogUtility.trainConsole(network,  temporalTraining.get(3), Config.TRAINING_MINUTES);
            //EncogUtility.trainConsole(train, network, temporalTraining.get(0), Config.TRAINING_MINUTES);
            System.out.println("Final Error: " + network.calculateError(temporalTesting.get(i)));
            double[] array = NetworkCODEC.networkToArray(network);
            for (int j = 0; j < array.length; j++) {
                System.out.println("weights - "+ array[j]);    
            }
            System.out.println("weights - "+ array.length);   
            
            NetworkCODEC.arrayToNetwork(array, network);
            System.out.println("Final Error: " + network.calculateError(temporalTesting.get(i)));
            
            File network_structure = new File(context.getRealPath(Config.SAVE_DIRECTORY), stockId+".eg");   
            EncogDirectoryPersistence.saveObject(network_structure, network);
           
            
            
            return ""+network.calculateError(temporalTesting.get(i));
            
        }     
        return null;
    }

//    public static void main(String[] args) {
//        Parser parser = new Parser();
//        parser.parseFiles();
//
//        //Training Data
//        TrainingData data = new TrainingData(parser.minNumOfRecords, parser.stockIds.size());
//
//        int num_of_stocks = 0;
//        int num_of_records = 0;
//
//        for (String stockId : parser.stockIds) {
//            Stock stock = parser.getStockValues(stockId);
//            if (stock == null || stock.numberOfRecords < parser.minNumOfRecords) {
//                continue;
//            }
//
//            //check if stock has the needed data
//            int startDateId = stock.dates.indexOf(data.trainStartDate);
//            int endDateId = stock.dates.indexOf(data.trainEndDate);
//
//            if (startDateId <= 0  || endDateId <= 0) {
//                continue;
//            }
//            num_of_records = Math.abs(startDateId - endDateId) + 1;
//            data.changeNumOfRecords(num_of_records);
//
//            data.add(stock);
//            //System.out.println(stock.stockSymbol);
//            MLDataSet temporal_training_dataset = data.getTemporalTrainingData(stock);
//            MLDataSet temporal_testing_dataset = data.getTemporalTestingData(stock);
//            temporalTraining.add(temporal_training_dataset);
//            temporalTesting.add(temporal_testing_dataset);
//
//            num_of_stocks++;
//        }
//        data.changeNumOfStocks(num_of_stocks);
//
//        //create neural network!
//        // create a network
////        final BasicNetwork network = EncogUtility.simpleFeedForward(
////                Config.INPUT_WINDOW,
////                Config.HIDDEN1_COUNT,
////                Config.HIDDEN2_COUNT,
////                Config.PREDICT_WINDOW,
////                true);
//
//        final BasicNetwork network = new BasicNetwork();
//        network.addLayer(new BasicLayer(Config.INPUT_WINDOW));
//        network.addLayer(new BasicLayer(new ActivationTANH(), true, Config.HIDDEN1_COUNT));
//        network.addLayer(new BasicLayer(new ActivationTANH(), true, Config.HIDDEN2_COUNT));
//        network.addLayer(new BasicLayer(new ActivationTANH(), true, Config.PREDICT_WINDOW));
//        network.getStructure().finalizeStructure();
//        network.reset();
//
//        // train the neural network
//        //CalculateScore score = new TrainingSetScore(temporalTraining.get(3));
//        //final NeuralGeneticAlgorithm train = new NeuralGeneticAlgorithm(network, new RangeRandomizer(-1, 1), score, 500, 0.1, 0.25);
//
//        // train the neural network
//        Train train = null;
//
//        for (int i = 0; i < num_of_stocks; i++) {
//            if (!data.stocks.get(i).getSymbol().contains("GOOG"))
//                continue;
//            int epoch = 1;
//            train = new ResilientPropagation(network, temporalTraining.get(i));
//            
//            for (int j = 0; j < data.stocks.get(i).adj_close.size(); j++) {
//                System.out.println(data.stocks.get(i).getAdjCloseNormalized(j)+"---"+data.stocks.get(i).adj_close.get(j));
//            }
//            train.addStrategy(new RequiredImprovementStrategy(10));
//
//            double error;
//            try{
//                do
//                {
//                    train.iteration();
//                    error = train.getError();
//                    epoch++;
//                    //System.out.println(epoch+"--"+error);
//                } while (epoch < 30000);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//            finally
//            {
//                train.finishTraining();
//            }
//            
//            
//            //EncogUtility.trainConsole(network,  temporalTraining.get(3), Config.TRAINING_MINUTES);
//            //EncogUtility.trainConsole(train, network, temporalTraining.get(0), Config.TRAINING_MINUTES);
//            System.out.println("Final Error: " + network.calculateError(temporalTesting.get(i)));
//        }
//
//    }
}