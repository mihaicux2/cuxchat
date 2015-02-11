/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuxchat.commands;

import cuxchat.ui.client.ClientUI;
import cuxchat.CuxMessage;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxMessenger;
import cuxchat.utils.CuxStringEncrypter;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author mihaicux
 */
public class ClientCommands {
    
    public static void updateLog(String msg){
        System.out.println(msg);
        CuxLogger.getInstance().log(CuxLogger.LEVEL_FINE, msg);
//        ClientUI.outputTxt.append(msg+"\n");
    }
    
    public static void handlerQuit(){
        int ret = CuxMessenger.getInstance().popUpConfirm("Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.OK_OPTION) {
            try {
                CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
                CuxMessage msg = new CuxMessage("QUIT");
                ClientUI.client.out.out.writeUTF(desEncrypter.encrypt(msg.toString()));
                System.exit(0);
            } catch (IOException ex) {
                ClientCommands.updateLog(ex.getMessage());
                CuxLogger.getInstance().logException2(ex);
                System.exit(1);
            }
        }
    }
    
    public static void handlerList(){
        try {
            
            CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
            CuxMessage msg = new CuxMessage("LIST");
            ClientUI.client.out.out.writeUTF(desEncrypter.encrypt(msg.toString()));
//            System.exit(0);
        } catch (IOException ex) {
            CuxLogger.getInstance().logException2(ex);
//            System.exit(1);
        }
    }
    
    public static void handlerHelp(){
        try {
            
            CuxStringEncrypter desEncrypter = new CuxStringEncrypter();
            CuxMessage msg = new CuxMessage("HELP");
            ClientUI.client.out.out.writeUTF(desEncrypter.encrypt(msg.toString()));
//            System.exit(0);
        } catch (IOException ex) {
            CuxLogger.getInstance().logException2(ex);
//            System.exit(1);
        }
    }
    
}
