package application.controllers;

import application.ChangeScene;
import application.Main;
import application.bashwork.BashCommand;
import application.values.CreationStep;
import application.values.PathIs;
import application.values.PicPath;
import application.values.SceneFXML;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
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


    @FXML private AnchorPane TOPVIEW;
    @FXML private ImageView creatingImg;
    @FXML private ImageView createImg;
    @FXML private Button starBtn;
    @FXML private Button createBtn;

    private static final Image LOADING = new Image(PicPath.MENU + "/loading_star.png");
    private static final Image PLACEHOLDER = new Image(PicPath.MENU + "/placeholder.png");
    private static final Image SEARCH = new Image(PicPath.DEFAULT + "/search_p.png");
    private static final Image AUDIO = new Image(PicPath.DEFAULT + "/audio_p.png");
    private static final Image PICTURE = new Image(PicPath.DEFAULT + "/pic_p.png");
    private static final Image FINAL = new Image(PicPath.DEFAULT + "/final_p.png");
    private static final Image DEFAULT_CREATE = new Image(PicPath.MENU + "/create.png");

    @FXML
    public void initialize() throws IOException {
        this.setTOPVIEW(SceneFXML.MENU.toString());
        creationInProgress(false);
    }

    /**
     * When this method is called, it will change the Scene to Create view and start create process
     * @param event
     * @throws IOException
     */

    @FXML
    public void create(ActionEvent event) throws IOException {
        popupHelper("Enter a word to search up!", false);
        this.setTOPVIEW(SceneFXML.SEARCH.toString());
    }

    /**
     * When this method is called, it will change the Scene to View creations
     * @param event
     * @throws IOException
     */

    @FXML
    public void view(ActionEvent event)throws IOException{
        currentCreationStep(CreationStep.CREATED);
        popupHelper("Click on a creation to get started!", false);
        this.setTOPVIEW(SceneFXML.VIEW.toString());
    }

    /**
     * show instructions for user when user click star button
     * @param event
     * @throws IOException
     */
    @FXML
    public void showInstructions(ActionEvent event) throws IOException {
        starBtn.setDisable(true);
        Popup instructions = popupHelper("I show tips from time to time!", true);
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

    /**
     *
     * Load the scene into the topview of the main window.
     * @param layout
     * @return
     * @throws IOException
     */
    public Object setTOPVIEW(String layout) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(layout));
        Parent node = loader.load();
        TOPVIEW.getChildren().setAll(node);

        return loader.getController();
    }

    @FXML
    public void getInfo(){

    }

    /**
     * When home button clicked, will go back to the home screen and also clear any execess files that may block future creations being made.
     * @throws Exception
     */
    @FXML
    public void goHome() throws Exception {
        currentCreationStep(CreationStep.FINISH);
        currentCreationStep(CreationStep.CREATED);
        String delete ="rm -rf \""+ PathIs.TEMP + "/audioPiece\" ; rm -f \""+ PathIs.EXTRA + "/temp.txt\"";
        new BashCommand().bash(delete);
        Main.clear();
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }

    /**
     * Show user a downloading image when creation is still being made,  and disable creation button.
     * Else enable create button and replace back with the help star image.
     * @param inProgress
     */
    public void creationInProgress(Boolean inProgress){
        if (inProgress){
            creatingImg.setImage(LOADING);
            createImg.setImage(DEFAULT_CREATE);
        } else {
            creatingImg.setImage(PLACEHOLDER);
            createBtn.setDisable(false);
        }
    }

    public void currentCreationStep(CreationStep step){
        createBtn.setDisable(true);

        if (step == CreationStep.SEARCH){
            createImg.setImage(SEARCH);
        } else if (step == CreationStep.AUDIO){
            createImg.setImage(AUDIO);
        } else if (step == CreationStep.PICTURE){
            createImg.setImage(PICTURE);
        } else if (step == CreationStep.FINAL){
            createImg.setImage(FINAL);
        } else if (step == CreationStep.FINISH){
            createImg.setImage(DEFAULT_CREATE);
        } else if (step == CreationStep.CREATED){
            Main.clear();
            createBtn.setDisable(false);
        }
    }

    /**
     * popupHelper helps to create a new pop up
     * @param text
     * @return
     * @throws IOException
     */

    public Popup popupHelper(String text, Boolean temp) throws IOException {
        Popup popup = new Popup();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(SceneFXML.TIP.toString()));
        popup.getContent().add((Parent)loader.load());
        ((TipController) loader.getController()).setTipText(text); //Set text of the star

        Stage stage = (Stage) TOPVIEW.getScene().getWindow();
        popup.show(stage);
        Point2D starPoint = starBtn.localToScene(0.0,  0.0);

        //Set help star initial coordinates
        popup.setAnchorX(stage.getX() + starPoint.getX() - 150);
        popup.setAnchorY(stage.getY() + starPoint.getY());

        //Whenever window changes, star speech bubble follows it.
        stage.xProperty().addListener((observableValue, number, t1) -> popup.setAnchorX(stage.getX() + starPoint.getX() - 250));
        stage.yProperty().addListener((observableValue, number, t1) -> { popup.setAnchorY(stage.getY() + starPoint.getY() - 50); });

        //When window is unfocused, hide popup.
        if (!temp){
            stage.focusedProperty().addListener((ov, oldValue, newValue) -> {
                if (!stage.focusedProperty().get()){
                    popup.hide();
                } else {
                    popup.show(stage);
                }
            });

        }

        return popup;
    }
}

