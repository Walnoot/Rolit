package team144.rolit;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.TextField;

public class LoginPanel extends Panel{
    public LoginPanel(){
        Panel panel = new Panel();
        add(panel);
        
        panel.add(new TextField()).setPreferredSize(new Dimension(200, 16));
        panel.add(new TextField());
        panel.add(new TextField());
    }
}
