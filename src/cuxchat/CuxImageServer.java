package cuxchat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mihaicux
 */
public class CuxImageServer implements HttpHandler {
    @Override
    public void handle(HttpExchange arg0) throws IOException {
        
        Map<String, String> params = queryToMap(arg0.getRequestURI().getQuery());         
        
        File file = new File("images/avatars/"+params.get("img"));
        arg0.sendResponseHeaders(200, file.length());
//        // TODO set the Content-Type header to image/gif
//
        OutputStream outputStream=arg0.getResponseBody();
        Files.copy(file.toPath(), outputStream);
        outputStream.close();
        
    }
    
    public Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
    
}
