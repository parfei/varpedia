package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * TipController class set tips for user
 */
public class TipController {
    @FXML private Label helpTxt;

    public void setTipText(String text){
        helpTxt.setText(text);
    }
}
