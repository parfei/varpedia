package application.controllers;

import application.values.SetFont;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * set initial appearance of main menu
 */
public class MainMenuController {
    @FXML private Label titleLabel;

    @FXML
    public void initialize(){
        titleLabel.setFont(SetFont.getQarmic(80));

    }

}
