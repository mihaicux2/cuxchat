package cuxchat.ui.client;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;

public class Test2 {

    JFXPanel fxPanel;
    WebView webview; 
    WebEngine webengine;
    Scene  scene;
    Group root;
    
    private void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Swing and JavaFX");
        fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
       });
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        scene = createScene();
        fxPanel.setScene(scene);
    }

    private Scene createScene() {
        root  =  new  Group();
        Scene  scene  =  new  Scene(root, Color.ALICEBLUE);
        
        webview=new WebView();
        root.getChildren().removeAll();
        root.getChildren().add(webview);
        webview.setMaxSize(500,500);
        webview.setMinSize(500,500);
        webengine=webview.getEngine();

        StringBuffer view = new StringBuffer();
        view.append("<html>\n");
        view.append("<head>\n");
        
        view.append("</head>\n");
        view.append("<body>\n");
        view.append("<p id=\"test\">\n");
        view.append("<a href=\"https://www.google.ro\">google</a>\n");
        view.append("</p>\n");
        view.append("<video width=\"400\" controls>\n" +
//"  <source src=\"http://www.w3schools.com/html/mov_bbb.mp4\" type=\"video/mp4\">\n" +
"  <source src=\"http://www.w3schools.com/html/mov_bbb.ogg\" type=\"video/ogg\">\n" +
"  Your browser does not support HTML5 video.\n" +
"</video>");
        
//        view.append("<iframe width=\"400\" height=\"300\" src=\"https://www.youtube.com/embed/SHF_TEH6Z8k\" frameborder=\"0\" allowfullscreen></iframe>\n");
        view.append("</body>\n");
        view.append("</html>");

        JSObject window = (JSObject) webengine.executeScript("window");
        window.setMember("app", new JavaApplication());

        webengine.loadContent(view.toString());
        
        return (scene);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test2().initAndShowGUI();
            }
        });
    }
}

class JavaApplication {
        public void exit() {
        System.out.println("hello");
        Platform.exit();
        System.exit(0);
    }
}