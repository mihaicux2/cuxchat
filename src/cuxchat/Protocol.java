package cuxchat;

// clasa care se asigura ca nu exista 2 clienti cu acelasi nume (neblocanta)

import cuxchat.handlers.ClientHandler;
import cuxchat.commands.ServerCommands;
import static cuxchat.handlers.ClientHandler.sendMessage;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxStringEncrypter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class Protocol extends Thread {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    public Protocol(Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    public int login(String username, String password) throws IOException, SQLException{
        //int res = ChatServer.logIn(username, password);
        ResultSet ret = ChatServer.logIn(username, password);
        if (ret != null) {
            if (ChatServer.handler.get(username) != null) {
                ChatServer.handler.get(username).handlerQuit();
            }
            ServerCommands.updateLog("Client nou (" + (++ChatServer.clientCrt) + ") : " + username);          
            
            ClientHandler ch = new ClientHandler(socket, dis, dos, ChatServer.clientCrt, username);
            ch.avatar = "http://localhost:8000/images?img="+ret.getString("avatar");
            ChatServer.handler.put(username, ch);
            ChatServer.handler.get(username).start();
            
            ChatServer.updateClients();            
            
            return ret.getInt("id");
        }
        return -1;
    }
    
    public int register(JsonNode body) throws IOException{
        String firstName = body.get("firstName").asText();
        String lastName = body.get("lastName").asText();
        String email = body.get("email").asText();
        String phone = body.get("phone").asText();
        Integer country = body.get("country").asInt();
        Integer city = body.get("city").asInt();
        String street = body.get("street").asText();
        String address = body.get("address").asText();
        String username = body.get("username").asText();
        String password = body.get("password").asText();
        String fileName = body.get("fileName").asText();
        String fileExtension = body.get("fileExtension").asText();
        byte[] avatar = null;
        if (body.get("image") != null){
            avatar = body.get("image").getBinaryValue();
        }
        
        int res = ChatServer.register(firstName, lastName, email, phone, country, city, street, address, username, password, avatar, fileName, fileExtension);
        if (res > 0) {
            ServerCommands.updateLog("S-a inregistrat un nou client : " + username);
        }
        return res;
    }
    
    private boolean handleLogin(JsonNode body) throws IOException, SQLException{
        String user = body.get("user").asText();
        String pass = body.get("pass").asText();
        int userID = login(user, pass);
        if (userID > 0){
            CuxMessage msg = new CuxMessage("LOGIN_OK");
            msg.put("userID", userID);
            msg.put("message", "Login accepted");
            ClientHandler.sendMessage(msg, dos);
            return true;
        }
        CuxMessage msg = new CuxMessage("LOGIN_NOK");
        msg.put("message", "Login failed");
        ClientHandler.sendMessage(msg, dos);
        return false;
    }
    
    private boolean handleRegister(JsonNode body) throws IOException{
        int userID = register(body);
        if (userID > 0){
            CuxMessage msg = new CuxMessage("REGISTER_OK");
            msg.put("userID", userID);
            msg.put("message", "Register accepted");
            ClientHandler.sendMessage(msg, dos);
            return true;
        }
        else if (userID == -2){
            CuxMessage msg = new CuxMessage("REGISTER_DUPLICATE");
            msg.put("message", "Duplicate registration");
            ClientHandler.sendMessage(msg, dos);
            return false;
        }
        else if (userID == -1){
            CuxMessage msg = new CuxMessage("ERROR");
            msg.put("message", "Server error");
            ClientHandler.sendMessage(msg, dos);
            return false;
        }
        CuxMessage msg = new CuxMessage("REGISTER_NOK");
        msg.put("message", "Register failed");
        ClientHandler.sendMessage(msg, dos);
        return false;
    }
    
    public void run() {

        try {
            
            // protocol de autentificare
            boolean ok = true;
            while (ok) {
//                byte[] line = new byte[128*1024];
//                dis.read(line);
//                String message = line.toString(); // comanda pe care o trimite clientul
                String message = dis.readUTF();
                CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
                JsonNode actualObj = mapper.readTree(desEncrypter.decrypt(message));
                String subject = actualObj.get("subject").asText();
//                String body = actualObj.get("body").asText();
                JsonNode body = actualObj.get("body");
                switch (subject){
                    case "LOGIN":
                        if (handleLogin(body)) ok = false;
                        break;
                    case "REGISTER":
                        if (handleRegister(body)) ok = false;
                        break;
                    default:
                        CuxMessage msg = new CuxMessage("ERROR");
                        msg.put("message", "Unknown subject");
                        ClientHandler.sendMessage(msg, dos);
                }
            }
        } catch (IOException ioe) {
//            ioe.printStackTrace();
            ServerCommands.updateLog(ioe.getMessage());
            CuxLogger.getInstance().logException2(ioe);
        } catch (SQLException ex) {
            ServerCommands.updateLog(ex.getMessage());
            CuxLogger.getInstance().logException2(ex);
        }
    }

}
