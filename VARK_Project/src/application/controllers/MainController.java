package application.controllers;

import application.ChangeScene;
import application.Main;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private ChangeScene _changeSceneObject=new ChangeScene();
    @FXML private Button help;


    /**
     * When this method is called, it will change the Scene to CreateView
     * @param event
     * @throws IOException
     */

    @FXML
    public void create(ActionEvent event) throws IOException {

        _changeSceneObject.changeScene(event,"resources/Creation.fxml","Creation");
    }
    @FXML
    public void view(ActionEvent event)throws IOException{
        _changeSceneObject.changeScene(event,"resources/View.fxml","View Menu");
    }

    @FXML
    public void help(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("PROTOTYPE FOR THE APPLICATION.\nMain target audience: young children (7-12 users)\n\n" +
                "Please enjoy our prototype. :).\n\nNOTE: background music feature does not work currently. Disabled for now.");
        alert.setTitle("VARpedia - Help");
        alert.showAndWait();
    }


}

