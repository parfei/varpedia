package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * TipController class set tips for user
 */
public class TipController {
    @FXML private Text helpTxt;

    public void setTipText(String text){
        helpTxt.setText(text);
    }
}
