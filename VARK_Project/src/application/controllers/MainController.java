package application.controllers;

import application.ChangeScene;
import application.Main;
import application.values.PicPath;
import application.values.SceneFXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    private ChangeScene _changeSceneObject=new ChangeScene();
    @FXML private AnchorPane TOPVIEW;
    @FXML private ImageView creatingImg;
    @FXML private Button starBtn;

    private static final Image LOADING = new Image(PicPath.MENU + "/download.png");
    private static final Image PLACEHOLDER = new Image(PicPath.MENU + "/placeholder.png");
    private final Popup helpFromStar = new Popup();

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
    public void showInstructions(ActionEvent event) throws IOException {
        starBtn.setDisable(true);
        Popup instructions = popupHelper("I show tips from time to time!");
        instructions.show(((Node)event.getTarget()).getScene().getWindow());
        Executors.newSingleThreadExecutor().submit(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    instructions.hide();
                    starBtn.setDisable(false);
                });
                return null;
            }
        });
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

    public Popup popupHelper(String text) throws IOException {
        Popup popup = new Popup();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(SceneFXML.TIP.toString()));
        popup.getContent().add((Parent)loader.load());
        ((TipController) loader.getController()).setTipText(text);
        popup.setAnchorX(-100);
        popup.setAnchorY(480);

        return popup;
    }

}

