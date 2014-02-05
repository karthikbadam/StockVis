package StockVisServer;

import org.encog.ml.data.market.TickerSymbol;

import java.util.ArrayList;

/**
 * User: Karthik
 * Date: 10/11/13
 * Time: 11:53 AM
 */
public class Config {

    //public static final String PROJECT_DIR = System.getProperty("user.dir");
    //change this and run
    //public static final String PROJECT_DIR = "/Users/karthik/Dropbox/StockVis/code/StockVisServlet";
    public static final String PROJECT_DIR = "/data";
    
    
    public static final String BEGIN_TRAINING_DATE = "2010-05-06";
    public static final String END_TRAINING_DATE = "2013-05-02";

    public static final String BEGIN_TESTING_DATE = "2013-05-03";
    public static final String END_TESTING_DATE = "2014-01-31";

    //spatial configuration files
    
    public static final String SAVE_DIRECTORY =  PROJECT_DIR+"/train/";
    public static final String SOM_FILE = "SOM_WEIGHTS.json";
    public static final String TEMPORAL_PREDICTION_FILE = "TEMPORAL_PREDICT.json";

    //training time
    public static final int TRAINING_MINUTES = 5;

    //Spatial Parameters
    public static final int SOM_HIDDEN_LAYER_X = 25;
    public static final int SOM_HIDDEN_LAYER_Y = 25;

    //Back Propagation layer
    public static final int HIDDEN1_COUNT = 41;
    public static final int HIDDEN2_COUNT = 41;

    //temporal input and window
    public static final int INPUT_WINDOW = 15;
    public static final int PREDICT_WINDOW = 1;

    //training or read from file
    public static final Boolean DO_TRAIN = true;

    //change the training Dir
    public static final String TRAINING_DIRECTORY= "/data/";

    //change the training file if needed
    public static final String STOCK_LIST_FILE = "internet information providers";

    //Download or use saved stock data
    public static final Boolean USE_ONLINE_DATA = true;

    //Download Cache Directory
    //public static final String CACHE_DIRECTORY= "/Users/karthik/Downloads/StockVis/cache/";
    public static final String CACHE_DIRECTORY= PROJECT_DIR+"/";

}
