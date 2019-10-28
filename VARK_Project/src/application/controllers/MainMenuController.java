package application.controllers;

import application.Main;
import application.bashwork.ManageFolder;
import application.values.SetFont;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * set initial appearance of main menu
 */
public class MainMenuController {
    @FXML private Label titleLabel;
    @FXML private ListView itemsToReview;
    @FXML private AnchorPane review;
    private Boolean reviewIsShowing = false;
    ExecutorService team = Executors.newSingleThreadExecutor();

    /***
     * Set the font of the main menu screen and also load in the creations needed to be reviewed through ExecutorService.
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        titleLabel.setFont(SetFont.getQarmic(80));
        review.setVisible(false);
        fillList();
    }

    /**
     * Button to toggle showing the review list on and off.
     */
    @FXML
    public void showReview(){
        if (!reviewIsShowing) {
            review.setVisible(true);
            reviewIsShowing = true;
        } else {
            review.setVisible(false);
            reviewIsShowing = false;
        }
    }

    /**
     * Populate the review arraylist.
     */
    public void fillList(){
        team.submit(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ArrayList<String> toReview = new ArrayList<String>();
                ArrayList<String> creations = ManageFolder.getCreations("creations");
                for (String creation : creations) {
                    int rate = Integer.parseInt(ManageFolder.readFile(ManageFolder.findPath(creation, false) + "/confidence.txt"));
                    int plays = Integer.parseInt(ManageFolder.readFile(ManageFolder.findPath(creation, false) + "/plays.txt"));

                    if (rate < 3 || plays == 0) { //If confidence is below 3 or the video has not been played yet
                        toReview.add(creation); //Creations to be reviewed.
                    }
                }

                if (toReview.isEmpty()){ //If there are no creations
                    toReview.add("Nothing to review!");
                }

                ArrayList<String> finalToReview = toReview;
                Platform.runLater(() -> {
                    itemsToReview.getItems().addAll(finalToReview);
                    itemsToReview.setMouseTransparent( true ); //Disable user being able to click on the listview itself.
                    itemsToReview.setFocusTraversable( false );
                });
                return null;
            }
        });
    }
}
