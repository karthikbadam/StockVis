package StockVisServer;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * User: Karthik
 * Date: 10/1/13
 * Time: 5:29 PM
 */
public class TrainingData {

    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    ArrayList<Stock> stocks;
    int numberOfStocks;

    Date trainStartDate;
    Date trainEndDate;

    Date testStartDate;
    Date testEndDate;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    //TODO fill this variable
    int numberOfRecords = 100;

    public TrainingData(int num_of_records, int num_of_Stocks) {
        stocks = new ArrayList<Stock>();
        this.numberOfStocks = num_of_Stocks;
        this.numberOfRecords = num_of_records;
        try {
            trainStartDate = df.parse(Config.BEGIN_TRAINING_DATE);
            trainEndDate = df.parse(Config.END_TRAINING_DATE);
            testStartDate = df.parse(Config.BEGIN_TESTING_DATE);
            testEndDate = df.parse(Config.END_TESTING_DATE);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void changeNumOfRecords(int num_of_records) {
        numberOfRecords = num_of_records;
    }

    public void changeNumOfStocks(int num_of_stocks) {
        numberOfStocks = num_of_stocks;
    }

    public void add(Stock stock) {
         stocks.add(stock);
    }

    public Stock get(int i) {
        return stocks.get(i);
    }

    public double SOM_INPUT[][];

    public void print_spatial_input(int index) {
        double[] data = SOM_INPUT[index];
        System.out.println("data "+(index+1));
        for (int i = 0; i < numberOfStocks; i++) {
            System.out.print(data[i] +"\t");
        }
        System.out.println();

    }

    public MLDataSet getSpatialTrainingData() {

        // create the training set
        SOM_INPUT = new double[numberOfRecords][];
        for (int i = 0; i < numberOfRecords; i++) {
            SOM_INPUT[i] = new double[numberOfStocks];
        }
        System.out.println("Records --"+numberOfRecords+" Stocks--"+numberOfStocks);
        int stockCounter = 0;

        for (Stock stock : stocks) {
            int startDateId = stock.dates.indexOf(trainStartDate);
            int endDateId = stock.dates.indexOf(trainEndDate);
            int dateCounter = 0;
            for (int i = startDateId; i >=endDateId; i--) {
                if (i >= numberOfRecords)
                    continue;
                //SOM_INPUT[dateCounter][stockCounter] = stock.getAdjCloseNormalized(i);
                int index = 0;
                if (i > 0)
                    index = i;
                else
                    index = i + 1;
                SOM_INPUT[dateCounter][stockCounter] = Double.parseDouble(decimalFormat.format((100/(stock.getAdjClose(index-1)+0.00002))*(stock.getAdjClose(index) - stock.getAdjClose(index-1))));
                //System.out.print(SOM_INPUT[dateCounter][stockCounter]+"\t");
                dateCounter++;
            }
            //System.out.println();
            stockCounter++;
        }

        MLDataSet training_data = new BasicMLDataSet(SOM_INPUT, null);
        return training_data;
    }

    public MLDataSet getTemporalTrainingData(Stock stock) {
        int startDateId = stock.dates.indexOf(trainStartDate);
        int endDateId = stock.dates.indexOf(trainEndDate);

        double[][] temporalData = new double[Math.abs(startDateId - endDateId) - Config.INPUT_WINDOW - Config.PREDICT_WINDOW + 1][];
        double[][] expectedOutput = new double[Math.abs(startDateId - endDateId) - Config.INPUT_WINDOW - Config.PREDICT_WINDOW + 1][];
        for (int i = 0; i <= Math.abs(startDateId - endDateId)- Config.INPUT_WINDOW - Config.PREDICT_WINDOW; i++) {
            temporalData[i] = new double[Config.INPUT_WINDOW];
            expectedOutput[i] = new double[Config.PREDICT_WINDOW];
        }

        //TODO inefficient! can be better!!
        int counter = 0;
        for (int i=startDateId; i >= endDateId + Config.INPUT_WINDOW + Config.PREDICT_WINDOW; i--) {
            for (int j = 0; j < Config.INPUT_WINDOW; j++) {
                temporalData[counter][j] = stock.getAdjCloseNormalized(i-j);
                //System.out.print(temporalData[counter][j] +"\t");
            }
            for (int j = 0; j < Config.PREDICT_WINDOW; j++) {
                expectedOutput[counter][0] = stock.getAdjCloseNormalized(i - Config.INPUT_WINDOW - j);
                //System.out.print("\t"+expectedOutput[counter][0]);
            }
            //System.out.println();
            counter++;
        }
        //System.out.println("------------------------------------------------------");
        MLDataSet training_data = new BasicMLDataSet(temporalData, expectedOutput);
        return training_data;
    }

    public MLDataSet getSpatialTestingData() {
        // create the training set
        SOM_INPUT = new double[numberOfRecords][];
        for (int i = 0; i < numberOfRecords; i++) {
            SOM_INPUT[i] = new double[numberOfStocks];
        }
        System.out.println("Records --"+numberOfRecords+" Stocks--"+numberOfStocks);
        int stockCounter = 0;

        for (Stock stock : stocks) {
            int num_of_records = stock.getSize();
            int startDateId = stock.dates.indexOf(testStartDate);
            int endDateId = stock.dates.indexOf(testEndDate);
            int dateCounter = 0;
            for (int i = startDateId; i >=endDateId; i--) {
                if (i >= numberOfRecords)
                    continue;
                SOM_INPUT[dateCounter][stockCounter] = stock.getAdjClose(i);
                System.out.print(SOM_INPUT[dateCounter][stockCounter]+"\t");
                dateCounter++;
            }
            System.out.println();
            stockCounter++;
        }

        MLDataSet testing_data = new BasicMLDataSet(SOM_INPUT, null);
        return testing_data;
    }



    public MLDataSet getTemporalTestingData(Stock stock) {
        int startDateId = stock.dates.indexOf(testStartDate);
        int endDateId = stock.dates.indexOf(testEndDate);
        
        System.out.println("start and end dates "+startDateId+", "+endDateId);
        double[][] temporalData = new double[Math.abs(startDateId - endDateId) - Config.INPUT_WINDOW - Config.PREDICT_WINDOW + 1][];
        double[][] expectedOutput = new double[Math.abs(startDateId - endDateId) - Config.INPUT_WINDOW - Config.PREDICT_WINDOW + 1][];
        for (int i = 0; i <= Math.abs(startDateId - endDateId)- Config.INPUT_WINDOW - Config.PREDICT_WINDOW; i++) {
            temporalData[i] = new double[Config.INPUT_WINDOW];
            expectedOutput[i] = new double[Config.PREDICT_WINDOW];
        }

        //TODO inefficient! can be better!!
        int counter = 0;
        for (int i=startDateId; i >= endDateId + Config.INPUT_WINDOW + Config.PREDICT_WINDOW; i--) {
            for (int j = 0; j < Config.INPUT_WINDOW; j++) {
                temporalData[counter][j] = stock.getAdjCloseNormalized(i-j);
                //System.out.print(temporalData[counter][j] +"\t");
            }
            for (int j = 0; j < Config.PREDICT_WINDOW; j++) {
                expectedOutput[counter][0] = stock.getAdjCloseNormalized(i - Config.INPUT_WINDOW - j);
                //System.out.print("\t"+expectedOutput[counter][0]);
            }
            counter++;
        }

        MLDataSet training_data = new BasicMLDataSet(temporalData, expectedOutput);
        return training_data;
    }
}
