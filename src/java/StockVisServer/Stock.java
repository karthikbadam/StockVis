package StockVisServer;

import java.util.ArrayList;
import java.util.Date;

/**
 * User: Karthik
 * Date: 10/1/13
 * Time: 4:41 PM
 */

public class Stock {

    ArrayList<Double> open = new ArrayList<Double>();
    ArrayList<Double> close = new ArrayList<Double>();
    ArrayList<Double> high = new ArrayList<Double>();
    ArrayList<Double> low = new ArrayList<Double>();
    ArrayList<Double> adj_close = new ArrayList<Double>();

    double minimum = 100000;
    double normalization = -1000;

    int numberOfRecords;
    int valueTypes = 5;
    int currentRecord = 0;
    public String stockSymbol;

    public ArrayList<Long> volume =  new ArrayList<Long>();
    public ArrayList<Date> dates = new ArrayList<Date>();


    public Stock(int numberOfRecords) {
        currentRecord = 0;
        this.numberOfRecords = numberOfRecords;

    }

    public String getSymbol() {
        return stockSymbol;
    }

    public void setSymbol(String symbol) {
        stockSymbol = symbol;
    }

    public void addDate(Date date) {
        dates.add(date);
    }

    public Date getDate(int i) {
        return dates.get(i);
    }

    public int getSize() {
        return dates.size();
    }

    public void addOpen(Double value) {
        if (currentRecord < numberOfRecords)
            open.add(value);
            //stockValues[currentRecord][0] = value;
    }

    public void addHigh(Double value) {
        if (currentRecord < numberOfRecords)
            high.add(value);
            //stockValues[currentRecord][1] = value;
    }

    public void addLow(Double value) {
        if (currentRecord < numberOfRecords)
            low.add(value);
            //stockValues[currentRecord][2] = value;
    }

    public void addClose(Double value) {
        if (currentRecord < numberOfRecords)
            close.add(value);
            //stockValues[currentRecord][3] = value;
    }

    public void addAdjClose(Double value) {
        if (currentRecord <= numberOfRecords)
            adj_close.add(value);
            //stockValues[currentRecord][4] = value;
    }

    public void add(double open1, double high1, double low1, double close1, double adj_close1) {
        open.add(open1);
        close.add(close1);
        high.add(high1);
        low.add(low1);
        adj_close.add(adj_close1);

        //finding min for normalization
        if (adj_close1 < minimum) {
            minimum = adj_close1;
        }
        currentRecord++;
    }

    public void normalize() {
        minimum -= 0.0002;
        for (double adjC: adj_close) {
            if (normalization < adjC - minimum) {
                normalization = adjC - minimum;
            }

        }

        normalization += 0.0003;
        //System.out.println("Minimum --"+minimum+" ,Normalization --"+normalization);
    }

    public void addVolume(long stock_volume) {
        volume.add(stock_volume);
    }

    public long getVolume(int i) {
        return volume.get(i);
    }

    public void increment() {
        currentRecord++;
    }

    public double getOpen(int i) {
        //return stockValues[i][0];
        return open.get(i);
    }

    public double getHigh(int i) {
        return high.get(i);
        //return stockValues[i][1];
    }

    public double getLow(int i) {
        return low.get(i);
        //return stockValues[i][2];
    }

    public double getClose(int i) {
        return close.get(i);
        //return stockValues[i][3];
    }

    public double getAdjCloseNormalized(int i) {
         return (adj_close.get(i) - minimum)/normalization;
        //return stockValues[i][4];
    }

    public double getAdjClose(int i) {
        return adj_close.get(i);
        //return stockValues[i][4];
    }
}
