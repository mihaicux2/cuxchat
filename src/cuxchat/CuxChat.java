package cuxchat;

import cuxchat.handlers.ServerHandler;
import cuxchat.handlers.ClientHandler;
import cuxchat.handlers.ServerHandlerCMD;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.ArrayList;

/* clasa care porneste server-ul 
 1 : partea care se ocupa de clienti           ->  ServerHandler
 2 : partea care tine de comenzile server-ului ->  ServerHandlerCMD
 */
public class CuxChat {

    public static Hashtable<String, ClientHandler> handler;
    public static int clientCrt;
    public static ServerHandler sh;
	//public static final int                      PORT = 9800 ;

    public static void main(String[] args) {

        int port;
        if (args.length < 1) {
            port = 9800;
        } else {
            port = Integer.parseInt(args[0]);
        }

        handler = new Hashtable<String, ClientHandler>();
        sh = new ServerHandler(port);
        sh.start();
        ServerHandlerCMD shcmd = new ServerHandlerCMD();
        shcmd.start();

    }

}
