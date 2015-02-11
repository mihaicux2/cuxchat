/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuxchat.utils;

/**
 *
 * @author mihaicux
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    private BufferedImage image;
    private String header = "Custom Title";
    
    public ImagePanel() {
       try {                
          image = ImageIO.read(new File(getClass().getResource("/cuxchat/images/menubackground.png").getFile()));
       } catch (IOException ex) {
            // handle exception...
       }
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters    
        g.setFont(new Font("TimesRoman", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        g.drawString(header, 20, 40);
    }
    
}
