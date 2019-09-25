package application.controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;

import java.io.IOException;

public class SaveToAudioController {
  @FXML
  private RadioButton kal;
  @FXML
  private RadioButton jdt;
  @FXML
  private RadioButton cw;
  @FXML
  private TextField textField;


  public void save(ActionEvent event){




  }

  public void cancel(ActionEvent event){
      try {

          Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/EditText.fxml"));
          Scene createViewScene = new Scene(createViewParent);
          // gets the Stage information
          Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
          window.setTitle("Select Line Menu");
          window.setScene(createViewScene);
          window.show();
      } catch (IOException e) {

      }
  }

}
