package StockVisServer;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.market.MarketDataDescription;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.MarketMLDataSet;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.ml.data.market.loader.YahooFinanceLoader;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * User: Karthik
 * Date: 10/3/13
 * Time: 3:03 PM
 */
public class Prediction {

    int INPUT_WINDOW = 10;
    int PREDICT_WINDOW = 1;
    TickerSymbol ticker;
    YahooFinanceLoader loader;
    MarketMLDataSet data;
    MarketDataDescription desc;
    GregorianCalendar end;
    GregorianCalendar begin;

    int determineDirection(int direction) {
        if (direction < 0)
            return -1;
        else
            return 1;
    }

    public Prediction() {
        ticker = new TickerSymbol("GOOG");
        loader = new YahooFinanceLoader();
        data = new MarketMLDataSet(loader, INPUT_WINDOW, PREDICT_WINDOW);
        desc = new MarketDataDescription(ticker, MarketDataType.ADJUSTED_CLOSE, true, true);
        data.addDescription(desc);
        end = new GregorianCalendar();
        begin = new GregorianCalendar();
        begin.add(Calendar.DATE, -60); //2 months from now
        end.add(Calendar.DATE, 0);
        data.load(begin.getTime(), end.getTime());
        data.generate();
    }

    public void createNetwork() {

    }

}
