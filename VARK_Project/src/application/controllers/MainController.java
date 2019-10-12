package application.controllers;

import application.ChangeScene;
import application.Main;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;

public class MainController {

    private ChangeScene _changeSceneObject=new ChangeScene();
    @FXML private AnchorPane TOPVIEW;
    @FXML private AnchorPane BOTTOMVIEW;

    @FXML
    public void initialize() throws IOException {
        Parent node;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/MainMenu.fxml"));
        node = loader.load();
        TOPVIEW.getChildren().setAll(node);
    }


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

