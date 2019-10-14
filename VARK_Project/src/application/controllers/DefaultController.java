package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public class DefaultController {
    @FXML private ProgressBar progressBar;
    @FXML private Button addFav;

    @FXML
    public void initialize(){
        //TODO bind visibility of progress bar and addfav to managed state
        progressBar.setVisible(false);
        addFav.setVisible(false);
    }

    public void startCreationProgress(){
        progressBar.setVisible(true);
        addFav.setVisible(true);
    }

    //TODO update the progress bar.

}
