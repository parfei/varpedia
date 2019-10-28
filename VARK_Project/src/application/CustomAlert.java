package application;

import application.values.CustomAlertType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * This class is used to generate alert box
 */
public class CustomAlert extends Task<CustomAlert> {
    private CustomAlertType _type;
    private Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);

    public CustomAlert(CustomAlertType type) {
        _type = type;
    }

    public Optional<ButtonType> showAlert(){
        return _alert.showAndWait();
    }

    private void setUpCSS(){
        _alert.getDialogPane().getStylesheets().add(String.valueOf(Main.class.getResource("css/alert.css")));

        Button okBtn = (Button) _alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        okBtn.setStyle("-fx-background-color: #F3BCB6");

        Button cancelBtn = (Button) _alert.getDialogPane().lookupButton(ButtonType.OK);
        cancelBtn.setStyle("-fx-background-color: #B6F3C3");
    }

    @Override
    protected CustomAlert call() throws Exception {
        String windowTitle = "";
        String title = "";
        String context = "";
        if (_type == CustomAlertType.SAVE){
            windowTitle += "Switch to normal voice?";
            title += "Boy/girl voice cannot save your text!";
            context += "Do you want to save your text in normal voice?";
        } else if (_type == CustomAlertType.OVERWRITE){
            windowTitle += "Duplicated name!";
            title += "There is already an exising name!";
            context += "Press OK to overwrite or CANCEL to write another name.";
        }
        _alert.setTitle(windowTitle);
        _alert.setContentText(title);
        _alert.setContentText(context);
        setUpCSS();

        return this;
    }
}
