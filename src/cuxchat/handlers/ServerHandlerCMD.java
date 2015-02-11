package cuxchat.handlers;

// clasa care se ocupa de comenzile server-ului (neblocanta)
import cuxchat.commands.ServerCommands;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxMessenger;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class ServerHandlerCMD extends Thread {

    Scanner sc;
    String cmd;

    // constructor
    public ServerHandlerCMD() {
        sc = new Scanner(System.in);
    }
    
    public void run() {
        while (true) {
            cmd = sc.nextLine();
            String[] comanda = cmd.split(" ");
            if (comanda[0].equalsIgnoreCase("/list")) {
                ServerCommands.handlerList();
            } else if (comanda[0].equalsIgnoreCase("/all")) {
                ServerCommands.handlerBroadcast(cmd.substring(5, cmd.length()));
            } else if (comanda[0].equalsIgnoreCase("/msg")) {
                ServerCommands.handlerMessage(comanda, cmd);
            } else if (comanda[0].equalsIgnoreCase("/quit")) {
                ServerCommands.handlerQuit();
            } else if (comanda[0].equalsIgnoreCase("/help")) {
                ServerCommands.handlerHelp();
            } else if (comanda[0].equalsIgnoreCase("/whois")) {
                ServerCommands.handlerWhois(comanda, cmd);
            } else if (comanda[0].equalsIgnoreCase("/kick")) {
                ServerCommands.handlerKick(comanda, cmd);
            } else {
                ServerCommands.updateLog("ERR : comanda invalida\nTastati /help pentru a vizualiza comenzile valabile");
            }
        }
    }

}
