package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public class DefaultController {
    @FXML private ProgressBar progressBar;
    @FXML private Button addFav;

    @FXML
    public void initialize(){
        progressBar.setVisible(false);
        addFav.setVisible(false);
    }
    
}
