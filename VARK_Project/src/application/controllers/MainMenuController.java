package application.controllers;

import application.values.SetFont;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController {
    @FXML private Label titleLabel;

    @FXML
    public void initialize(){
        titleLabel.setFont(SetFont.getQarmic(80));
    }

}
