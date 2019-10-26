package application.controllers;

import application.bashwork.BashCommand;
import application.values.PathIs;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.ArrayList;


/**
 * can be deleted
 */
public class TextPopUpController {

    @FXML
    TextArea textArea;

    //textArea.setText()
    public void initialize() throws Exception {
        String textFolder = PathIs.EXTRA + "/saveTextFolder";

        ArrayList<String> output = new BashCommand().bash("cd \"" + textFolder+"\"; cat *.txt");
        String finalText = output.toString().substring(1);
        finalText = finalText.substring(0,finalText.lastIndexOf("]"));



        textArea.setText(finalText);
    }

}
