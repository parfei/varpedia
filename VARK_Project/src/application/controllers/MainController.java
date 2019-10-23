package application.controllers;

import application.ChangeScene;
import application.Main;
import application.values.PicPath;
import application.values.SceneFXML;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;

public class MainController {

    private ChangeScene _changeSceneObject=new ChangeScene();
    @FXML private AnchorPane TOPVIEW;
    @FXML private ImageView creatingImg;

    private static final Image LOADING = new Image(PicPath.MENU + "/download.png");
    private static final Image PLACEHOLDER = new Image(PicPath.MENU + "/placeholder.png");

    @FXML
    public void initialize() throws IOException {
        this.setTOPVIEW(SceneFXML.MENU.toString());
        creationInProgress(false);
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

    public void creationInProgress(Boolean inProgress){
        if (inProgress){
            creatingImg.setImage(LOADING);
        } else {
            creatingImg.setImage(PLACEHOLDER);
        }
    }

}

