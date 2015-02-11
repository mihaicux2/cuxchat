/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuxchat;

import cuxchat.utils.CuxLogger;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;

/**
 *
 * @author mihaicux
 */
public class CuxMessage {
    
    public String subject;
    public HashMap<String, Object> body;
    public Integer status;
    public String messageTime;
    
    public CuxMessage(String subject){
        this.subject = subject;
        this.body = new HashMap<String, Object>();
        this.status = 200;
        this.messageTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }
    
    public void put(String key, Object value){
        this.body.put(key, value);
    }
    
    /**
     * Public method used to convert the character to JSON, to be sent to a client
     * @return The JSON representation of the character
     */
    @Override
    public String toString(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
//        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        try {
//            return ow.writeValueAsString(this);
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
//            CuxLogger.getInstance().logException2(ex);
            return ex.getMessage();
        }
    }
    
}
