package StockVisServer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.encog.app.quant.loader.yahoo.YahooDownload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletContext;

/**
 * User: Karthik
 * Date: 10/1/13
 * Time: 1:48 PM
 */
public class Parser {

    public HashMap<String, String> stocks = new HashMap<String, String>();
    public static ArrayList<String> stockIds = new ArrayList<String>();

    public List<String> columns = new ArrayList<String>();

    int minNumOfRecords = 100;

    //Date
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    DateFormat df2 = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

    ServletContext context;
    public Parser(ServletContext context) {
       columns = Arrays.asList(new String[]{"Date", "Open", "High", "Low", "Close", "Adj Close"});
       this.context = context;
    }

    public void parseFiles() {
        try {
            //read from context
            String filename = Config.TRAINING_DIRECTORY + Config.STOCK_LIST_FILE + ".csv";          
                
            //awfully important to find relative path!
            String pathname =context.getRealPath(filename); 
                
            CSVParser parser = new CSVParser(new FileReader(pathname), CSVFormat.DEFAULT);
            List<CSVRecord> values = parser.getRecords();
            for (CSVRecord currentValue : values) {
                String company = currentValue.get(0);
                int count = currentValue.size();
                String stockId = currentValue.get(count-1);

                if (stockId.contains("symbols")) {
                    continue;
                }
                //System.out.println("Stock --"+company+" Id --"+stockId);
                stocks.put(stockId, company);
                stockIds.add(stockId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Stock getStockValues(String stockId) {
        try {
            CSVParser parser;
            if (!Config.USE_ONLINE_DATA) {
                parser = new CSVParser(new FileReader(Config.TRAINING_DIRECTORY + Config.STOCK_LIST_FILE + "/" + stockId + ".csv"), CSVFormat.DEFAULT);
                List<CSVRecord> values = parser.getRecords();

                Stock stock = new Stock(values.size() - 1);
                stock.setSymbol(stockId);

                //System.out.println("size --"+ values.size());
                for (int i = 1; i < values.size(); i++) {

                    CSVRecord currentValue = values.get(i);
                    System.out.println(currentValue.toString());
                    if (currentValue.size() != 7)
                        continue;

                    String date = currentValue.get(0);
                    Double open = Double.parseDouble(currentValue.get(1));
                    Double high = Double.parseDouble(currentValue.get(2));
                    Double low = Double.parseDouble(currentValue.get(3));
                    Double close = Double.parseDouble(currentValue.get(4));
                    long stock_volume = Long.parseLong(currentValue.get(5));
                    Double adj_close = Double.parseDouble(currentValue.get(6));

                    Date current_date =  df.parse(date);

                    stock.add(open, high, low, close, adj_close);
                    stock.addVolume(stock_volume);
                    stock.addDate(current_date);
                }
                stock.normalize();
                return stock;

            } else {
                String filename = Config.CACHE_DIRECTORY + stockId + ".csv";          
                
                //awfully important to find relative path!
                String pathname =context.getRealPath(filename); 
                
                File downloadFile = new File(pathname);
                if(downloadFile.exists()) {
                    downloadFile.delete();
                }
                //if (!downloadFile.exists()) {
                    System.out.println("Downloading Stock ticker --"+stockId);
                    YahooDownload download = new YahooDownload();
                    download.loadAllData(stockId, downloadFile, org.encog.util.csv.CSVFormat.DECIMAL_POINT, df.parse(Config.BEGIN_TRAINING_DATE), df.parse(Config.END_TESTING_DATE));
                //}
                parser = new CSVParser(new FileReader(pathname), CSVFormat.DEFAULT);
                List<CSVRecord> values = parser.getRecords();

                Stock stock = new Stock(values.size() - 1);
                stock.setSymbol(stockId);

                //System.out.println("size --"+ values.size());
                for (int i = 1; i < values.size(); i++) {

                    CSVRecord currentValue = values.get(i);

                    String date = currentValue.get(0);
                    Double open = Double.parseDouble(currentValue.get(2));
                    Double high = Double.parseDouble(currentValue.get(3));
                    Double low = Double.parseDouble(currentValue.get(4));
                    Double close = Double.parseDouble(currentValue.get(5));
                    long stock_volume = Long.parseLong(currentValue.get(6));
                    Double adj_close = Double.parseDouble(currentValue.get(7));

                    Date current_date =  df2.parse(date);
                    //System.out.println(current_date.toString()+", "+open+", "+adj_close);

                    stock.add(open, high, low, close, adj_close);
                    stock.addVolume(stock_volume);
                    stock.addDate(current_date);
                }
                stock.normalize();
                return stock;
            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();  
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}