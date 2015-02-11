/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuxchat.commands;

import cuxchat.ui.server.ServerUI;
import cuxchat.handlers.ClientHandler;
import cuxchat.*;
import cuxchat.ui.server.ServerSetupUI;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxMessenger;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author mihaicux
 */
public class ServerCommands {
    
    public static void stopServer() throws IOException{
        ServerCommands.updateLog("SERVERUL SE INCHIDE...");
                
        Iterator it = ChatServer.handler.entrySet().iterator();
        CuxMessage msg = new CuxMessage("QUIT");
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ClientHandler ch = (ClientHandler)pairs.getValue();
            ClientHandler.sendMessage(msg, ch.out);
//                    ch.handlerQuit();
//                    it.remove();
        }

        if (ChatServer.sh.socket != null) {
            ChatServer.sh.socket.close();
        }
        if (ChatServer.sh.serverSocket != null) {
            ChatServer.sh.serverSocket.close();
        }
        if (ChatServer.server != null){
            ChatServer.server.stop(0);
        }
    }
    
    public static int stopServerPrompt(){
        int ret = CuxMessenger.getInstance().popUpConfirm("Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.OK_OPTION) {
            try {
                stopServer();                
                return 1;
            } catch (IOException ioe) {
                //ioe.printStackTrace();
                CuxMessenger.getInstance().popUp(ioe.getMessage(), CuxMessenger.LEVEL_EXCEPTION);
                CuxLogger.getInstance().logException2(ioe);
                
                return 2;
            }
        }
        return 0;
    }
    
    public static void handlerQuit(){
        
        int stop = ServerCommands.stopServerPrompt();
        if (stop == 1){
            System.exit(0);
        }
        else if (stop == 2){
            System.exit(1);
        }
    }
    
    public static void handlerList(){
        StringBuilder str = new StringBuilder();
        if (ChatServer.handler.size() > 0) {
            
            Iterator it = ChatServer.handler.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                ClientHandler ch = (ClientHandler)pairs.getValue();
                ServerCommands.updateLog(ch.nume);
//                it.remove();
            }
        } else {
            ServerCommands.updateLog("Niciun client conectat");
        }
    }
    
    public static void handlerClear(){
        try {
            ServerCommands.updateLog("Eliberez servereul");
            Iterator it = ChatServer.handler.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                ClientHandler ch = (ClientHandler)pairs.getValue();
                ch.handlerQuit();
//                it.remove();
            }
        } catch (IOException ioe) {
//            ioe.printStackTrace();
            ServerCommands.updateLog(ioe.getMessage());
            CuxMessenger.getInstance().popUp(ioe.getMessage(), CuxMessenger.LEVEL_EXCEPTION);
            CuxLogger.getInstance().logException2(ioe);
        }
    }
    
    public static void handlerHelp(){
        StringBuilder str = new StringBuilder();
        str.append("UTILIZARE SERVER : \r\n");
        str.append("\n/help -> afisarea comenzilor disponibile"
                + "\n/list -> afisarea tuturor utilizatorilor conectati"
                + "\n/all [mesaj] -> trimite mesaj tuturor utilizatorilor conectati"
                + "\n/msg [utilizator] [mesaj] -> trimite mesaj utilizatorului dorit"
                + "\n/whois [utilizator] -> afisarea IP-ului clientului"
                + "\n/kick [utilizator] -> elimina un utilizator"
                + "\n/quit -> inchiderea server-ului\n");
        ServerCommands.updateLog(str.toString());
    }
    
    public static void handlerBroadcast(String msg){
        ServerCommands.updateLog("SERVER(mass) : " + msg);
        if (ChatServer.handler.size() > 0) {
            try {
                CuxMessage cMsg = new CuxMessage("SERVER");
                cMsg.put("message", "(mass) : " + msg);
                Iterator it = ChatServer.handler.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    ClientHandler ch = (ClientHandler)pairs.getValue();
                    ClientHandler.sendMessage(cMsg, ch.out);
//                    it.remove();
                }
                System.out.println("(OK)");
            } catch (IOException ioe) {
//                ioe.printStackTrace();
                ServerCommands.updateLog(ioe.getMessage());
                CuxLogger.getInstance().logException2(ioe);
            }
        } else {
            ServerCommands.updateLog("Niciun client conectat");
        }
    }
    
    public static void handlerMessage(String[] comanda, String cmd){
        if (comanda.length > 1) {
            ServerCommands.updateLog("SERVER : " + cmd.substring(cmd.indexOf(" ", 5) + 1, cmd.length()) + "\r");
            String client = comanda[1];
            if (!ChatServer.handler.containsKey(client)) {
                ServerCommands.updateLog("(ERR : client inexistent)");
            } else {
                try {
                    CuxMessage msg = new CuxMessage("SERVER");
                    msg.put("message", cmd.substring(cmd.indexOf(" ", 5) + 1, cmd.length()));
                    ClientHandler.sendMessage(msg, ChatServer.handler.get(client).out);
                    ServerCommands.updateLog("(OK)");
                } catch (IOException ioe) {
//                    ioe.printStackTrace();
                    ServerCommands.updateLog(ioe.getMessage());
                    CuxLogger.getInstance().logException2(ioe);
                }
            }
        } else {
            ServerCommands.updateLog("ERR : scrieti numele utilizatorului care va primi mesajul");
        }
    }
    
    public static void handlerKick(String[] comanda, String cmd){
        if (comanda.length > 1) {
            String client = comanda[1];
            if (!ChatServer.handler.containsKey(client)) {
                ServerCommands.updateLog("(ERR : client inexistent)");
            } else {
                try {
                    Iterator it = ChatServer.handler.entrySet().iterator();
                    CuxMessage msg = new CuxMessage("SERVER");
                    CuxMessage msg2 = new CuxMessage("SERVER");
                    msg.put("message", client + " a fost deconectat de catre server");
                    msg2.put("message", "Ai fost deconectat de catre server");
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
                        ClientHandler ch = (ClientHandler) pairs.getValue();
                        if (ch.nume.equals(client)) continue;
                        ClientHandler.sendMessage(msg, ch.out);
//                                it.remove();
                    }
                    ClientHandler.sendMessage(msg2, ChatServer.handler.get(client).out);
                    ChatServer.handler.get(client).handlerQuit();
                    ServerCommands.updateLog("(OK)");
                } catch (IOException ioe) {
//                            ioe.printStackTrace();		
                    ServerCommands.updateLog(ioe.getMessage());
                    CuxLogger.getInstance().logException2(ioe);
                }
            }
        } else {
            ServerCommands.updateLog("ERR : scrieti numele utilizatorului pe care vreti sa-l eliminati\n");
        }
    }
    
    public static void handlerWhois(String[] comanda, String cmd){
        if (comanda.length > 1) {
            String client = comanda[1];
            if (!ChatServer.handler.containsKey(client)) {
                ServerCommands.updateLog("(ERR : client inexistent)");
            } else {
                try {
                    ServerCommands.updateLog("IP:" + ChatServer.handler.get(client).socket.getInetAddress());
                } catch (Exception e) {
                    ServerCommands.updateLog("Clientul nu are IP");
                    CuxLogger.getInstance().logException2(e);
                }
            }
        } else {
            ServerCommands.updateLog("ERR : scrieti numele utilizatorului pe care vreti sa-l eliminati\n");
        }
    }
    
    public static void updateLog(String msg){
        System.out.println(msg);
        CuxLogger.getInstance().log(CuxLogger.LEVEL_FINE, msg);
        ServerUI.outputTxt.append(msg+"\n");
    }    
    
}
