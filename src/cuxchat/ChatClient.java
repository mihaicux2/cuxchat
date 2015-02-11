package cuxchat;

import cuxchat.ui.client.ClientUI;
import cuxchat.ui.client.ClientLoginUI;
import cuxchat.commands.ClientCommands;
import cuxchat.ui.client.ClientChatUI;
import cuxchat.ui.client.ClientListUI;
import static cuxchat.ui.client.ClientListUI.mHoveredJListIndex;
import static cuxchat.ui.client.ClientUI.client;
import cuxchat.utils.CuxClientListRenderer;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxMessenger;
import cuxchat.utils.CuxStringEncrypter;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

// clasa care asigura conexiunea la un server si creaza interfata grafica pentru utilizator
public class ChatClient extends Thread {

    public String nume;
    Socket socket;
//  Processor p;

    public writer out;
    public reader in;
    public ClientUI clientBox;
    
    // constructor
    public ChatClient(Socket clientSocket, String name) {
        socket = clientSocket;
        nume = name;

    }

    public void run() {

        try {
            out = new writer();
            in = new reader();

            /*
            if (OSValidator.isWindows()){
                p = new Processor("cmd", this);
            }
            else{
                p = new Processor("bash", this);
            }
            p.runProcess();
            */
            
            in.start();
            out.start();

            clientBox = new ClientUI(this);
            clientBox.setTitle("CuxChat Client - v.0.1.1 | "+this.nume);
            clientBox.setVisible(true);
            
        } catch (IOException ioe) {
            CuxLogger.getInstance().logException2(ioe);
        }
            
    }

    // subclasa care citeste datele primite de la server (neblocanta)
    public class reader extends Thread {

        public DataInputStream in;
        private String raspuns;

        public reader() throws IOException {
            in = new DataInputStream(socket.getInputStream());
        }

        public void run() {

            try {
                while (true){
                    try {
                        raspuns = in.readUTF();
//                        p.writeToStream(raspuns.substring(8).replaceAll("(\\r|\\n)", ""));
                        
                        CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
                        String responce = desEncrypter.decrypt(raspuns);
                        System.out.println(responce);
                        
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);                        
                        JsonNode actualObj = mapper.readTree(responce);
                        String subject = actualObj.get("subject").asText();
        //                String body = actualObj.get("body").asText();
                        JsonNode body = actualObj.get("body");
                        
                        switch(subject){
                            case "QUIT":
                                ClientCommands.updateLog("Serverul se inchide");
                                CuxMessenger.getInstance().popUp("Serverul se inchide", CuxMessenger.LEVEL_INFO);
                                try {
                                    in.close();
                                    socket.close();
                                    System.exit(0);
                                } catch (IOException ioe2) {
//                                ioe2.printStackTrace();
                                    CuxLogger.getInstance().logException2(ioe2);
                                    System.exit(1);
                                }
                                break;
                            case "ERROR":
                                String errMsg = body.get("message").asText();
                                CuxMessenger.getInstance().popUp(errMsg, CuxMessenger.LEVEL_ERROR);
                                break;
                            case "HELP":
                                String helpMsg = body.get("message").asText();
                                CuxMessenger.getInstance().popUp(helpMsg, CuxMessenger.LEVEL_FINE);
                                break;
                            case "LIST":
//                                String listMsg = body.get("message").asText();
                                //CuxMessenger.getInstance().popUp(listMsg, CuxMessenger.LEVEL_FINE);
                                JsonNode clients = body.get("clients");
                                ArrayList<String> nameList = new ArrayList<String>();
                                clientBox.map.clear();
                                
//                                clientBox.jList1.removeAll();
                                clientBox.listModel.removeAllElements();
                                for (int i = 0; i < clients.size(); i++){
                                    JsonNode client = clients.get(i);
//                                    System.out.println(client.get("value")+", "+client.get("description"));
                                    
                                    clientBox.map.put(client.get("value").asText(), new ImageIcon(new URL(client.get("description").asText())));
                                    clientBox.listModel.addElement(client.get("value").asText());
                                }
                                
                                clientBox.setupList();
                                
                                break;
                            case "MESSAGE":
                                String msg = body.get("message").asText();
                                ClientCommands.updateLog(msg);
                                JsonNode fromN = body.get("from");
                                if (fromN != null){
                                    String from = fromN.asText();
                                    if (!clientBox.boxes.containsKey(from)){
                                        ClientChatUI box = new ClientChatUI(client);
                                        box.setTitle("CuxChat Client - v.0.1.1 | " + from);
                                        box.setVisible(true);
                                        box.endpoint = from;
                                        clientBox.boxes.put(from, box);
                                    }
                                    clientBox.repaint();
                                    clientBox.setVisible(true);
                                    msg = msg.replaceAll("(\r\n|\n)", "<br/>")+"<br />";
                                    
                                    HTMLEditorKit kit = clientBox.boxes.get(from).kit;
                                    HTMLDocument doc = clientBox.boxes.get(from).doc;
                                    
                                    if (from == ChatClient.this.nume){
                                        msg = "you : " + msg;
                                    }
                                    else{
                                        msg = from + " : " + msg;
                                    }
                                    
                                    kit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
                                    clientBox.boxes.get(from).outputTxt.setCaretPosition(doc.getLength());
                                    clientBox.boxes.get(from).commandText.requestFocus();
                                }
                                
                                break;
                            case "SERVER":
                                String serverMsg = body.get("message").asText();
                                ClientCommands.updateLog("[SERVER] " + serverMsg);
                                break;
                            default:
                                CuxMessenger.getInstance().popUp("Unknown message type", CuxMessenger.LEVEL_WARNING);
                                CuxLogger.getInstance().log(CuxLogger.LEVEL_WARNING, responce);
                        }
                    } catch (IOException ioe) {
//                        ioe.printStackTrace();
                        CuxLogger.getInstance().logException2(ioe);
                        try {
                            in.close();
                            socket.close();
                            //System.exit(0);
                        } catch (IOException ioe2) {
//                            ioe2.printStackTrace();
                            CuxLogger.getInstance().logException2(ioe);
                        }
                        break;
                    } catch (BadLocationException ex) {
                        Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } finally {
                try {
                    in.close();
                    socket.close();
                    //System.exit(0);
                } catch (IOException ioe) {
//                    ioe.printStackTrace();
                    CuxLogger.getInstance().logException2(ioe);
                }
            }
        }

    }

    // subclasa care transmite serverului comenzi - citite de la tastatura (neblocanta)
    public class writer extends Thread {

        public DataOutputStream out;
        private String comanda;

        public writer() throws IOException {
            out = new DataOutputStream(socket.getOutputStream());
        }

        public void run() {

            Scanner sc = new Scanner(System.in);
            try {
                while (true) {
                    comanda = sc.nextLine();
                    CuxMessage msg = new CuxMessage(("MESSAGE"));
                    msg.put("message", comanda);
                    CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
                    out.writeUTF(desEncrypter.encrypt(msg.toString()));
                    //messages.appendText("\n(ME) : " + comanda);
                    ClientCommands.updateLog("(ME) : " + comanda);
                    Thread.sleep(10);
                }
            } catch (IOException ioe) {
//                ioe.printStackTrace();
                CuxLogger.getInstance().logException2(ioe);
                try {
                    out.close();
                    socket.close();
                    System.exit(0);
                } catch (IOException ioe2) {
//                    ioe2.printStackTrace();
                    CuxLogger.getInstance().logException2(ioe2);
                }
            } catch (InterruptedException ex) {
//                ex.printStackTrace();
                CuxLogger.getInstance().logException2(ex);
            } finally {
                try {
                    out.close();
                    socket.close();
                    System.exit(0);
                } catch (IOException ioe3) {
//                    ioe3.printStackTrace();
                    CuxLogger.getInstance().logException2(ioe3);
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                // From Ubuntu 14.04 LTS, Java(TM) SE Runtime Environment (build 1.7.0_76-b13) :
                // Metal, Nimbus, CDE/Motif, GTK+
                if ("GTK+".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ClientLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            CuxLogger.getInstance().logException2(ex);
        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ClientLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            CuxLogger.getInstance().logException2(ex);
        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ClientLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            CuxLogger.getInstance().logException2(ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ClientLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            CuxLogger.getInstance().logException2(ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                javax.swing.JFrame loginBox = new ClientLoginUI();
                loginBox.setTitle("CuxChat Client - v.0.1.1 | LOGIN");
                loginBox.setVisible(true);
            }
        });

    }
}
