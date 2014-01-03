package Communication;

import StockVisServer.Config;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lupin
 */
public class SpatialPrediction extends HttpServlet {
    
    static final long serialVersionUID = 1L;
    private static final int BUFFSIZE = 4096;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context  = getServletConfig().getServletContext();
        
        String filePath = context.getRealPath(Config.SAVE_DIRECTORY+Config.SOM_FILE);
        
        File file = new File(filePath);
        int length = 0;
        ServletOutputStream outStream = response.getOutputStream();
        String mimetype = context.getMimeType(filePath);
        
        // sets response content type
        if (mimetype == null) {
            mimetype = "application/octet-stream";
        }
        response.setContentType(mimetype);
        response.setContentLength((int)file.length());
        
        byte[] byteBuffer = new byte[BUFFSIZE];
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        
        while ((in != null) && ((length = in.read(byteBuffer)) != -1))
        {
            outStream.write(byteBuffer,0,length);
        }
        
        in.close();
        outStream.close();
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override   
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
