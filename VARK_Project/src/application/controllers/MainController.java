package application.controllers;

import application.ChangeScene;
import application.Main;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private ChangeScene _changeSceneObject=new ChangeScene();



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




}

