package cuxchat;

import com.sun.net.httpserver.HttpServer;
import cuxchat.ui.server.ServerSetupUI;
import cuxchat.handlers.ServerHandler;
import cuxchat.handlers.ClientHandler;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxBase64;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


/* clasa care porneste server-ul 
 1 : partea care se ocupa de clienti           ->  ServerHandler
 2 : partea care tine de comenzile server-ului ->  ServerHandlerCMD
 */
public class ChatServer {

    public static Map<String, ClientHandler> handler;
    public static int clientCrt;
    public static ServerHandler sh;
    public static HttpServer server;
    
    /**
     * Static variable. Used to communicate with the existing database
     */
    public static Connection con = null;
    
    /**
     * Public static method that decodes an encoded string using the
     * base64_decode algorithm
     *
     * @param s the string to be decoded
     * @return the decoded string
     */
    public static String decodeBase64(String s) {
        return CuxBase64.decode(s);
    }

    /**
     * Public static method that encodes a string using the base64 algorithm
     *
     * @param s the string to be encoded
     * @return the encoded string
     */
    public static String encodeBase64(String s) {
        return CuxBase64.encode(s);
    }

    /**
     * Public static method that hashes a string using the MD5 algorithm
     *
     * @param message the string to be hashed
     * @return the hashed string
     */
    public static String md5Java(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8")); //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            CuxLogger.getInstance().logException2(ex);
        }
        return digest;
    }

    /**
     * Public method used to login an existing player
     *
     * @param peer The connected peer
     * @param user The name of the player trying to register
     * @param pass The password of the player trying to register
     * @param config The endpoint config, containing information about the peer
     * session (if any)
     */
    public static ResultSet logIn(String user, String pass) {
//        System.out.println(user+" : "+md5Java(pass));
        try {
            String query = "SELECT * FROM `user` WHERE `username`=? AND `password`=? AND `status`=1;";
            PreparedStatement st = (PreparedStatement) ChatServer.con.prepareStatement(query);
            st.setString(1, user);
            st.setString(2, md5Java(pass));
//            System.out.println(query);
            ResultSet ret = st.executeQuery();
            if (ret.next()) {
//                System.out.println(ret.getInt(1));
//                ret.close();
//                st.close();
                //return ret.getInt(1);
                return ret;
            }
//            ret.close();
//            st.close();
            return null;
        } catch (SQLException ex) {
            CuxLogger.getInstance().logException2(ex);
            return null;
        }
    }
    
    public static int register(String firstName, String lastName, String email, String phone, Integer country, Integer city, String street, String address, String username, String password, byte[] avatar, String fileName, String fileExtension){
        try{
            
            String checkQuery = "SELECT id FROM `user` WHERE `username`=? OR `email`=?;";
            PreparedStatement checkSt = (PreparedStatement) ChatServer.con.prepareStatement(checkQuery);
            checkSt.setString(1, username);
            checkSt.setString(2, email);
//            System.out.println(query);
            ResultSet checkRet = checkSt.executeQuery();
            if (checkRet.next()) {
                return -2;
            }
            
            String query = "INSERT INTO `user` SET"
                    + " first_name=?,"
                    + " last_name=?,"
                    + " email=?,"
                    + " phone=?,"
                    + " country_id_fk=?,"
                    + " city_id_fk=?,"
                    + " street=?,"
                    + " address=?,"
                    + " username=?,"
                    + " password=?,"
                    + " status=1,"
                    + " created_at=NOW(),"
                    + " updated_at=NOW(),"
                    + " avatar=''";
            PreparedStatement st = (PreparedStatement) ChatServer.con.prepareStatement(query,  Statement.RETURN_GENERATED_KEYS);
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, email);
            st.setString(4, phone);
            st.setInt(5, country);
            st.setInt(6, city);
            st.setString(7, street);
            st.setString(8, address);
            st.setString(9, username);
            st.setString(10, md5Java(password));
            st.executeUpdate();
            ResultSet ret = st.getGeneratedKeys();
            if (ret.next()){
                
                int userID = ret.getInt(1);
                
                try {
//                    System.out.println(ret.getInt(1));
//                ret.close();
//                st.close();
                    
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(avatar));
                    String location = "images/avatars/";
                    File outputfile = new File(location+"avatar_"+userID+"."+fileExtension);
                    ImageIO.write(img, fileExtension, outputfile);
                    String upQ = "UPDATE user SET avatar=? WHERE id=?";
                    PreparedStatement upSt = (PreparedStatement) ChatServer.con.prepareStatement(upQ,  Statement.RETURN_GENERATED_KEYS);
                    upSt.setString(1, "avatar_"+userID+"."+fileExtension);
                    upSt.setInt(2, userID);
                    upSt.executeUpdate();
                } catch (IOException ex) {
                    CuxLogger.getInstance().logException2(ex);
                }
                
                return userID;
            }
//            ret.close();
//            st.close();
            return 0;
        } catch (SQLException ex) {
            CuxLogger.getInstance().logException2(ex);
            return -1;
        }
    }    
    
    public static void updateClients(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    ArrayList<CuxItem<String>> clientList = ClientHandler.clientList();
                    Iterator it = ChatServer.handler.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
                        ClientHandler ch = (ClientHandler) pairs.getValue();
                        ch.handlerList();
                    }
                } catch (InterruptedException ex) {
                    CuxLogger.getInstance().logException2(ex);
                } catch (IOException ex2) {
                    CuxLogger.getInstance().logException2(ex2);
                }
            }
        }).start();
    }
    
    public static void main(String[] args) {

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
            @Override
            public void run() {
                javax.swing.JFrame serverBox = new ServerSetupUI();
                serverBox.setTitle("CuxChat Server - v.0.1.1");
                serverBox.setVisible(true);
            }
        });

    }

}
