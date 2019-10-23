package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class MainWindowController {
    @FXML private static ImageView creatingImg;
    @FXML private static ImageView placeholderImg;

    public static void creationInProgress(Boolean inProgress){
            creatingImg.setVisible(inProgress);
            placeholderImg.setVisible(!inProgress);
    }


}
