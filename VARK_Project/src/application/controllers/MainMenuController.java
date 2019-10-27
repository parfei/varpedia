package application.controllers;

import application.Reminder;
import application.values.SetFont;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * set initial appearance of main menu
 */
public class MainMenuController {
    @FXML private Label titleLabel;
    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize(){
        titleLabel.setFont(SetFont.getQarmic(80));
        Reminder reminder = new Reminder();
        team.submit(reminder);
    }

}
