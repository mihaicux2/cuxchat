package cuxchat.handlers;

import cuxchat.ChatServer;
import cuxchat.CuxItem;
import cuxchat.CuxMessage;
import cuxchat.commands.ServerCommands;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxStringEncrypter;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

// clasa care se ocupa de fiecare client in parte (neblocanta)
public class ClientHandler extends Thread {

    public Socket socket;
    public DataInputStream in;
    public DataOutputStream out;
    public int nrCrt;
    public String nume;
    public String avatar;
    public Map<String, byte[]> files;

    // constructor
    public ClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos, int index, String name) throws IOException {

        this.socket = socket;
        this.nrCrt = index;
        this.nume = name;
//        this.in = new DataInputStream(socket.getInputStream());
//        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = dis;
        this.out = dos;
        
        this.files = new HashMap<String, byte[]>();

    }

    public static void sendMessage(CuxMessage msg, DataOutputStream out) throws IOException {
        CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
        out.writeUTF(desEncrypter.encrypt(msg.toString()));
        out.flush();
    }

    // handler pentru quit
    public void handlerQuit() throws IOException {
        CuxMessage msg = new CuxMessage("QUIT");
        sendMessage(msg, out);
        out.close();
        in.close();
        socket.close();
        ChatServer.handler.remove(this.nume);
        ChatServer.handler.remove(this.nume);
        ChatServer.clientCrt--;
        ChatServer.updateClients(); 
    }

    // handler pentru trimitere de mesaj catre un client
    public void handlerMessage(String linie, String[] parsare) throws IOException {
        String client = parsare[1];
        ServerCommands.updateLog(this.nume + " catre " + client + " : " + linie.substring(linie.indexOf(" ", 5) + 1, linie.length()));
        if (!ChatServer.handler.containsKey(client)) {
            CuxMessage msg = new CuxMessage("ERROR");
            msg.put("message", "Client inexistent");
            sendMessage(msg, out);
            ServerCommands.updateLog("(ERR : client inexistent)");
        } else {
            CuxMessage msg = new CuxMessage("MESSAGE");
            msg.put("message", linie.substring(linie.indexOf(" ", 5) + 1, linie.length()));
            msg.put("from", this.nume);
            sendMessage(msg, ChatServer.handler.get(client).out);
            ServerCommands.updateLog("(OK)");
        }
    }

    // handler pentru broadcast de mesaj
    public void handlerBroadcast(String linie, String[] parsare) throws IOException {
        CuxMessage msg = new CuxMessage("MESSAGE");
        msg.put("message", this.nume + "(mass) : " + linie.substring(linie.indexOf(" ") + 1, linie.length()));
        msg.put("from", this.nume);
        Iterator it = ChatServer.handler.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            ClientHandler ch = (ClientHandler) pairs.getValue();
            if (this.nume.equals(ch.nume)) {
                continue;
            }
            sendMessage(msg, ch.out);
//            it.remove();
        }

        ServerCommands.updateLog(this.nume + "(mass) : " + linie.substring(linie.indexOf(" ") + 1, linie.length()));
    }

    public static ArrayList<CuxItem<String>> clientList(){
        Iterator it = ChatServer.handler.entrySet().iterator();
        ArrayList<CuxItem<String>> clients = new ArrayList<CuxItem<String>>();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            ClientHandler ch = (ClientHandler) pairs.getValue();
            clients.add(new CuxItem<String>(ch.nume, ch.avatar));
        }
        return clients;
    }
    
    // handler pentru listarea clientilor conectati
    public void handlerList() throws IOException {

        CuxMessage msg = new CuxMessage("LIST");
        msg.put("clients", clientList());
        sendMessage(msg, out);
        ServerCommands.updateLog(this.nume + " : /list");
    }

    // handler pentru schimbarea numelui curent
    public void handlerNick(String linie, String[] parsare) throws IOException {
        if ((parsare.length > 1) && (ChatServer.handler.get(parsare[1]) != null)) {
            CuxMessage msg = new CuxMessage("ERROR");
            msg.put("message", "Numele (" + parsare[1] + ") este deja utilizat");
//            sendMessage(msg, ChatServer.handler.get(nume).out);
            sendMessage(msg, out);
            ServerCommands.updateLog("(ERR : " + nume + " a incercat sa-si schimbe numele in " + parsare[1] + ", insa "
                    + "acesta exista deja...)");
        } else if (parsare.length <= 1) {
            CuxMessage msg = new CuxMessage("ERROR");
            msg.put("message", "Numele nu poate fi vid/nul");
//            sendMessage(msg, ChatServer.handler.get(nume).out);
            sendMessage(msg, out);
            ServerCommands.updateLog("(ERR : " + this.nume + " incearca sa-si schimbe numele in '')");
        } else {
            ClientHandler tmp = ChatServer.handler.remove(this.nume);
            String oldName = this.nume;
            tmp.nume = parsare[1];
            ChatServer.handler.put(tmp.nume, tmp);
            this.nume = parsare[1];
            CuxMessage msg = new CuxMessage("SERVER");
            msg.put("message", "Nick nou : " + nume);
            sendMessage(msg, out);

            /*
            CuxMessage msg2 = new CuxMessage("SERVER");
            msg2.put("message", oldName + " si-a schimbat numele in " + this.nume);

            Iterator it = ChatServer.handler.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                ClientHandler ch = (ClientHandler) pairs.getValue();
                if (this.nume.equals(ch.nume)) {
                    continue;
                }
                sendMessage(msg2, ch.out);
//                it.remove();
            }
            */
            ChatServer.updateClients(); 
            ServerCommands.updateLog(oldName + " si-a schimbat numele in " + this.nume);
        }
    }

    // handler pentru help
    public void handlerHelp() throws IOException {

        CuxMessage msg = new CuxMessage("HELP");
        String helpMsg = "UTILIZARE : \n\r"
                + "\n\r/list -> afisarea tuturor utilizatorilor conectati"
                + "\n\r/bcast [mesaj] -> trimite mesaj tuturoc utilizatorilor conectati"
                + "\n\r/msg [utilizator] [mesaj] -> trimite mesaj utilizatorului dorit"
                + "\n\r/nick [numeNou] -> schimba numele utilizatorului curent"
                + "\n\r/quit -> terminarea sesiunii curente";
        msg.put("message", helpMsg);
        sendMessage(msg, out);

        ServerCommands.updateLog(this.nume + " : /help");
    }

    // handler pentru comanda invalida
    public void handlerError(String linie) throws IOException {
        //out.writeUTF("ERR : comanda invalida\nTastati /help pentru a vizualiza comenzile valabile\r");
        //ServerUI.updateLog("(ERR : " + this.nume + " : " + linie + " -> comanda invalida)");
        ServerCommands.updateLog(this.nume + " : " + linie);
    }

    // handler pentru anuntarea tuturor ca un client s-a deconectat
    public void handlerQuitBCast() throws IOException {
        try {
            in.close();
            out.close();
            socket.close();
            ChatServer.handler.remove(this.nume);

            /*
            CuxMessage msg = new CuxMessage("MESSAGE");
            msg.put("message", this.nume + " a iesit");

            Iterator it = ChatServer.handler.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                ClientHandler ch = (ClientHandler) pairs.getValue();
                if (this.nume.equals(ch.nume)) {
                    continue;
                }
                sendMessage(msg, ch.out);
//                it.remove();
            }
            */
            ChatServer.updateClients(); 
        } catch (IOException ioe2) {
//            ioe2.printStackTrace();
            CuxLogger.getInstance().logException2(ioe2);
            ServerCommands.updateLog(ioe2.getMessage());
        }
    }

    public void handlerFileTransfer(JsonNode body) throws IOException{
        String fileTo     = body.get("file_to").asText();
        String fileName   = body.get("file_name").asText();
        String fileExt    = body.get("file_type").asText();
        int    fileChunks = body.get("file_chunks").asInt();
        int    fileChunk  = body.get("file_chunk_no").asInt();
        int    fileBytes  = body.get("file_bytes").asInt();
        byte[] filePart   = body.get("file_part").getBinaryValue();
        
        if (!files.containsKey(fileName)){
            files.put(fileName, new byte[fileBytes]);
        }
        byte[] crt = files.get(fileName);
        int kbyte32 = 1024*32;
        int start = kbyte32 * fileChunk;
        int end   = kbyte32 * (fileChunk+1);
        for (int i = 0; i < filePart.length; i++){
            if (i+start >= fileBytes){
                System.out.println(fileBytes+" < "+(i+start));
                break;
            }
            crt[i+start] = filePart[i];
        }
        files.put(fileName, crt);
        if (fileChunk == fileChunks){
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(files.get(fileName));
            fos.close();
        }
    }
    
    public void handlerMessage(String message) throws IOException {
        String[] parsare = message.split(" ");
        if (parsare[0].equalsIgnoreCase("/quit")) {
            handlerQuit();
        } else if (parsare[0].equalsIgnoreCase("/msg")) {
            handlerMessage(message, parsare);
        } else if (parsare[0].equalsIgnoreCase("/bcast")) {
            handlerBroadcast(message, parsare);
        } else if (parsare[0].equalsIgnoreCase("/list")) {
            handlerList();
        } else if (parsare[0].equalsIgnoreCase("/nick")) {
            handlerNick(message, parsare);
        } else if (parsare[0].equalsIgnoreCase("/help")) {
            handlerHelp();
        } else {
            handlerError(message);
        }
    }

    public void run() {
        String linie;
        try {
            while ((linie = in.readUTF()) != null) {
                if (linie == null) {
                    continue;
                }

                CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
                String message = desEncrypter.decrypt(linie);
                System.out.println(message);
                JsonNode actualObj = mapper.readTree(message);
                String subject = actualObj.get("subject").asText();
                JsonNode body = actualObj.get("body");

                switch (subject) {
                    case "QUIT":
                        handlerQuit();
                        break;
                    case "HELP":
                        handlerHelp();
                        break;
                    case "LIST":
                        handlerList();
                        break;
                    case "MESSAGE":
                        handlerMessage(body.get("message").asText());
                        break;
                    case "FILE":
                        handlerFileTransfer(body);
                        break;
                    default:
                        handlerError("UNKNOWN MESSAGE TYPE");
                }
            }
        } catch (IOException ioe) {
            //ioe.printStackTrace();
            CuxLogger.getInstance().logException2(ioe);
            try {
                handlerQuitBCast();
            } catch (IOException ioe2) {
                //ioe2.printStackTrace();
                CuxLogger.getInstance().logException2(ioe2);
            }
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
                ChatServer.handler.remove(this.nume);
                ServerCommands.updateLog(this.nume + " a iesit");
                ChatServer.clientCrt--;
            } catch (IOException ioe3) {
                //ioe3.printStackTrace();
                CuxLogger.getInstance().logException2(ioe3);
            }
        }
    }
}
