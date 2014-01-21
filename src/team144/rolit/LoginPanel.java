package team144.rolit;

import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.TextField;

public class LoginPanel extends Panel{
    public LoginPanel(){
        setLayout(new FlowLayout());

        add(new TextField());
        add(new TextField());
        add(new TextField());
    }
}
