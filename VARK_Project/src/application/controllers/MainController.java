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


    //private MainController controller = new MainController();

    /**
     * When this method is called, it will change the Scene to CreateView
     * @param event
     * @throws IOException
     */

    @FXML
    public void create(ActionEvent event) throws IOException {
        _changeSceneObject.changeScene(event,"resources/Creation.fxml","Creation");

       /* Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/Creation.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Creation");
        window.setScene(createViewScene);
        window.show();*/

    }
    @FXML
    public void view(ActionEvent event)throws IOException{
        _changeSceneObject.changeScene(event,"resources/View.fxml","View Menu");

    }
    /*@FXML
    public void play(ActionEvent event)throws IOException{

        this.view(event);
    }
    @FXML
    public void delete(ActionEvent event)throws IOException{
        this.view(event);
    }*/



}

