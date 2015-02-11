/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuxchat.ui.server;
import com.sun.net.httpserver.HttpServer;
import cuxchat.ChatServer;
import cuxchat.CuxImageServer;
import cuxchat.commands.ServerCommands;
import cuxchat.handlers.ServerHandler;
import cuxchat.handlers.ClientHandler;
import cuxchat.handlers.ServerHandlerCMD;
import cuxchat.utils.CuxLogger;
import cuxchat.utils.CuxMessenger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author mihaicux
 */
public class ServerSetupUI extends javax.swing.JFrame {
    
    public static ServerUI serverBox;
    ServerHandlerCMD shcmd;
    
    /**
     * Creates new form ServerUI
     */
    public ServerSetupUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        portText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        httpPortText = new javax.swing.JTextField();
        startBtn = new javax.swing.JButton();
        quitBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        connectionStringText = new javax.swing.JTextField();
        databaseNameText = new javax.swing.JTextField();
        databaseUserText = new javax.swing.JTextField();
        databasePasswordText = new javax.swing.JPasswordField();
        stopBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Config"));

        jLabel1.setText("Port");

        portText.setText("9800");
        portText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formSubmit(evt);
            }
        });

        jLabel6.setText("HTTP Port");

        httpPortText.setText("8000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(portText, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                    .addComponent(httpPortText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(portText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(httpPortText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        startBtn.setText("Start");
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

        quitBtn.setText("QUIT");
        quitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitBtnActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Connection"));

        jLabel2.setText("Connection String");

        jLabel3.setText("Database Name");

        jLabel4.setText("Database User");

        jLabel5.setText("Database Password");

        connectionStringText.setText("jdbc:mysql://localhost:3306/");
        connectionStringText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formSubmit(evt);
            }
        });

        databaseNameText.setText("cux_chat");
        databaseNameText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formSubmit(evt);
            }
        });

        databaseUserText.setText("cux_chat_user");
        databaseUserText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formSubmit(evt);
            }
        });

        databasePasswordText.setText("cux_chat_password");
        databasePasswordText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formSubmit(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(connectionStringText, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                    .addComponent(databaseNameText)
                    .addComponent(databaseUserText)
                    .addComponent(databasePasswordText))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(connectionStringText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(databaseNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(databaseUserText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(databasePasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        stopBtn.setText("Stop");
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(quitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startBtn)
                    .addComponent(stopBtn)
                    .addComponent(quitBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitBtnActionPerformed
        int ret = CuxMessenger.getInstance().popUpConfirm("Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.OK_OPTION){
            this.dispose();
            this.setVisible(false);
            if (serverBox != null){
                try {
                    ServerCommands.stopServer();
                } catch (IOException ex) {}
            }
            System.exit(0);
        }
    }//GEN-LAST:event_quitBtnActionPerformed

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        int port = Integer.parseInt(portText.getText().trim());
        int httpPort = Integer.parseInt(httpPortText.getText().trim());
        
        String connectionString = connectionStringText.getText().trim();
        String databaseName = databaseNameText.getText().trim();
        String databaseUser = databaseUserText.getText().trim();
        String databasePassword = databasePasswordText.getText().trim();
        
        if (port == 0){
            CuxMessenger.getInstance().popUp("Enter server port", CuxMessenger.LEVEL_WARNING);
            portText.requestFocus();
            return;
        }
        
         if (httpPort == 0){
            CuxMessenger.getInstance().popUp("Enter HTTP server port", CuxMessenger.LEVEL_WARNING);
            httpPortText.requestFocus();
            return;
        }
        
        if (connectionString.equals("")){
            CuxMessenger.getInstance().popUp("Enter database connection string", CuxMessenger.LEVEL_WARNING);
            connectionStringText.requestFocus();
            return;
        }
        
        if (databaseName.equals("")){
            CuxMessenger.getInstance().popUp("Enter database name", CuxMessenger.LEVEL_WARNING);
            databaseNameText.requestFocus();
            return;
        }
        
        if (databaseUser.equals("")){
            CuxMessenger.getInstance().popUp("Enter database user", CuxMessenger.LEVEL_WARNING);
            databaseUserText.requestFocus();
            return;
        }
        
        if (databasePassword.equals("")){
            CuxMessenger.getInstance().popUp("Enter database password", CuxMessenger.LEVEL_WARNING);
            databasePasswordText.requestFocus();
            return;
        }
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            ChatServer.con = DriverManager.getConnection(connectionString + databaseName,
                    databaseUser, databasePassword);
            if (!ChatServer.con.isClosed()) {
                CuxLogger.getInstance().log(CuxLogger.LEVEL_FINE, "Connected to MySQL Database...");
            }
            
            ChatServer.server = HttpServer.create(new InetSocketAddress(8000), 0);
            ChatServer.server.createContext("/images", new CuxImageServer());
            ChatServer.server.setExecutor(null); // creates a default executor
            ChatServer.server.start();
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            CuxLogger.getInstance().logException2(e);
            CuxMessenger.getInstance().popUp("Cannot connect to database", CuxMessenger.LEVEL_EXCEPTION);
            return;
        } catch (IOException ex) {
            CuxLogger.getInstance().logException2(ex);
            CuxMessenger.getInstance().popUp("Cannot start HTTP Server", CuxMessenger.LEVEL_EXCEPTION);
        }
        
        
//        this.dispose();
//        this.setVisible(false);
        
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        
        ChatServer.handler = Collections.synchronizedMap(new HashMap<String, ClientHandler>());
//        ChatServer.handler = new HashMap<String, ClientHandler>();
        ChatServer.sh = new ServerHandler(port);
        ChatServer.sh.start();        
        
        // direct input from the terminal
        shcmd = new ServerHandlerCMD();
        shcmd.start();
        
        serverBox = new ServerUI();
        serverBox.setTitle("CuxChat Server - v.0.1.1");
        serverBox.setVisible(true);        
        
    }//GEN-LAST:event_startBtnActionPerformed

    private void formSubmit(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formSubmit
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10){ // enter
            this.startBtnActionPerformed(null);
        }
    }//GEN-LAST:event_formSubmit

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        try {
            ServerCommands.stopServer();
            serverBox.dispose();
            serverBox.setVisible(false);
            
            stopBtn.setEnabled(false);
            startBtn.setEnabled(true);
            
        } catch (IOException ex) {
            Logger.getLogger(ServerSetupUI.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
    }//GEN-LAST:event_stopBtnActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField connectionStringText;
    private javax.swing.JTextField databaseNameText;
    private javax.swing.JPasswordField databasePasswordText;
    private javax.swing.JTextField databaseUserText;
    private javax.swing.JTextField httpPortText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField portText;
    private javax.swing.JButton quitBtn;
    public static javax.swing.JButton startBtn;
    public static javax.swing.JButton stopBtn;
    // End of variables declaration//GEN-END:variables
}