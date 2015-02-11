/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuxchat.utils;

import cuxchat.ui.client.ClientListUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author mihaicux
 */
//public class CuxClientListRenderer extends  DefaultListCellRenderer{
public class CuxClientListRenderer extends JPanel implements ListCellRenderer{
        
    Font font = new Font("helvitica", Font.BOLD, 14);

    private Map<String, ImageIcon> imageMap;
    
    JLabel[] lbl = new JLabel[3];
    
    public CuxClientListRenderer()
    {      
        initComponents();
        String[] nameList = {"Mario", "Luigi", "Bowser", "Koopa", "Princess"};
        imageMap = createImageMap(nameList);
    }
    
    public CuxClientListRenderer(Map<String, ImageIcon> imageMap){
        initComponents();
        this.imageMap = imageMap;
    }
    
    public void initComponents(){
        lbl[0] = new javax.swing.JLabel();
        lbl[1] = new javax.swing.JLabel();
        lbl[2] = new javax.swing.JLabel();
        
        this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbl[0].setIcon(new javax.swing.ImageIcon(getClass().getResource("/cuxchat/images/avatar.png"))); // NOI18N
        lbl[0].setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl[1].setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lbl[1].setText("Username");

        lbl[2].setFont(new java.awt.Font("Ubuntu", 2, 14)); // NOI18N
        lbl[2].setText("Available");
        
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lbl[0], javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl[1])
                    .addComponent(lbl[2])))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl[0])
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl[1])
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl[2]))))
        );
    }
    
    private Map<String, ImageIcon> createImageMap(String[] list) {
        Map<String, ImageIcon> map = new HashMap<>();
        try {
            map.put("Mario", new ImageIcon(new URL("http://i.stack.imgur.com/NCsHu.png")));
            map.put("Luigi", new ImageIcon(new URL("http://i.stack.imgur.com/UvHN4.png")));
            map.put("Bowser", new ImageIcon(new URL("http://i.stack.imgur.com/s89ON.png")));
            map.put("Koopa", new ImageIcon(new URL("http://i.stack.imgur.com/QEK2o.png")));
            map.put("Princess", new ImageIcon(new URL("http://i.stack.imgur.com/f4T4l.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    

    @Override
    public Component getListCellRendererComponent(
            JList list, final Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        
        Color backgroundColor = ClientListUI.mHoveredJListIndex == index ? Color.gray : Color.white;
        
        lbl[0].setIcon(imageMap.get((String) value));
        lbl[1].setText((String) value);
        this.setBorder(javax.swing.BorderFactory.createLineBorder(backgroundColor));
        
        return this;
    }
    
}
