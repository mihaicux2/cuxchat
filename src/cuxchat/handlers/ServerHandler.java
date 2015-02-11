package cuxchat.handlers;

// clasa care se ocupa de clienti - porneste server-ul si accepta conexiuni (neblocanta)

//import chat.Protocol;
import cuxchat.Protocol;
import cuxchat.commands.ServerCommands;
import cuxchat.utils.CuxLogger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerHandler extends Thread {

    public ServerSocket serverSocket = null;
    public Socket socket = null;
    public int port;
    static int nrCrt;
//    public static ArrayList<Protocol> connP;
    public static List<Protocol> connP;

    public ServerHandler(int port) {
        this.port = port;
//        connP = new ArrayList<Protocol>();
        connP = Collections.synchronizedList(new ArrayList<Protocol>());
    }

    public void run() {

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serverul a pornit\n Tastati /help pentru a vedea comenzile disponibile.");
            // accepta clienti
            while (serverSocket!= null && !serverSocket.isClosed()) {
                socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                nrCrt++;
                Protocol clientProtocol = new Protocol(socket, dis, dos);
                clientProtocol.start();
                ServerHandler.connP.add(clientProtocol);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            ServerCommands.updateLog(e.getMessage());
            CuxLogger.getInstance().logException2(e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioe) {
//                ioe.printStackTrace();
                ServerCommands.updateLog(ioe.getMessage());
                CuxLogger.getInstance().logException2(ioe);
            }
        }
    }

}
