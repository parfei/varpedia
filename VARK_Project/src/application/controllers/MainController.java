package application.controllers;

import application.ChangeScene;
import application.Main;
import application.values.SceneFXML;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;

public class MainController {

    private ChangeScene _changeSceneObject=new ChangeScene();
    @FXML private AnchorPane TOPVIEW;

    @FXML
    public void initialize() throws IOException {
        this.setTOPVIEW(SceneFXML.MENU.toString());
    }

    /**
     * When this method is called, it will change the Scene to CreateView
     * @param event
     * @throws IOException
     */

    @FXML
    public void create(ActionEvent event) throws IOException {
        //_changeSceneObject.changeScene(event,"resources/Creation.fxml","Creation");
        this.setTOPVIEW(SceneFXML.SEARCH.toString());
    }

    @FXML
    public void view(ActionEvent event)throws IOException{
        //_changeSceneObject.changeScene(event,"resources/View.fxml","View Menu");
        this.setTOPVIEW(SceneFXML.VIEW.toString());
    }

    @FXML
    public void help(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Help sheet will be included soon.");
        alert.showAndWait();
    }

    public Object setTOPVIEW(String layout) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(layout));
        Parent node = loader.load();
        TOPVIEW.getChildren().setAll(node);

        return loader.getController();
    }

}

