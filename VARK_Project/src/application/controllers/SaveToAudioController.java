package application.controllers;

import application.Main;
import application.PathCD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SaveToAudioController {
  @FXML
  private RadioButton kal;
  @FXML
  private RadioButton jdt;
  @FXML
  private RadioButton cw;
  @FXML
  private TextField textField;
  @FXML
  private ToggleGroup group;
  @FXML
  private ListView existingAudioView;


  @FXML
  private Label errorName;

  @FXML
  private Label noneSelection;

    private List<String> _audioExisted = new ArrayList<String>();


  /*public void playKal(){

  }

  public void playJdt(){

  }

  public void playCw(){

  }*/

  public void initialize(){
      errorName.setVisible(false);
      noneSelection.setVisible(false);
      String command = "ls -tcr " + PathCD.getPathInstance().getPath() + "/mydir/audioPiece" + " | cut -f1 -d'.'\n";
      System.out.println(PathCD.getPathInstance().getPath());
      ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
      try {
          String line;
          Process process = builder.start();
          BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


          while ((line = reader.readLine()) != null) {
              _audioExisted.add(line);
          }
          existingAudioView.getItems().addAll(_audioExisted);

      } catch (IOException e) {
          e.printStackTrace();
      }

  }

  public void save(ActionEvent event) throws IOException{
      String userInput=textField.getText();
      RadioButton selectedRadioButton = (RadioButton) group .getSelectedToggle();
      if (selectedRadioButton==null){
          noneSelection.setVisible(true);
          noneSelection.setText("please select a button");
          return;
      }
      String toggleGroupValue = selectedRadioButton.getText();
      System.out.println(toggleGroupValue);

      if (userInput.trim().isEmpty() || userInput == null) {
          errorName.setVisible(true);
          errorName.setText("Complete the fields!");
          return;
      }

      else if (_audioExisted.contains(textField.getText())){
          errorName.setText("Duplicated name.");
          Alert error = new Alert(Alert.AlertType.ERROR);
          error.setTitle("Duplicated Name");
          error.setHeaderText("please enter another name");
          error.setContentText("please enter another name");
          error.showAndWait();

      } else if (!textField.getText().matches("[a-zA-Z0-9_-]*")) {
          errorName.setVisible(true);
          errorName.setText("Invalid name. Please enter again.");
          return;
      }
      else{
          if (kal.isSelected()){
              String cmd="text2wave -o \""+PathCD.getPathInstance().getPath()+"/mydir/audioPiece/" + userInput+".wav\" " + "savedText.txt -eval kal.scm";
              ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
              try {
                  Process process = pb.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }
             /* String cmd2="mv "+ userInput+".wav " +PathCD.getPathInstance().getPath() + "/mydir/audioPiece";
              ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", cmd2);
              try {
                  Process process = pb2.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }*/
          }
          else if (jdt.isSelected()){
              String cmd="text2wave -o \""+PathCD.getPathInstance().getPath()+"/mydir/audioPiece/" + userInput+".wav\" " + "savedText.txt -eval jdt.scm";
              System.out.println(cmd);
              ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
              try {
                  Process process = pb.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              /*String cmd2="mv "+userInput+".wav " +PathCD.getPathInstance().getPath() + "/mydir/audioPiece";
              ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", cmd2);
              try {
                  Process process = pb2.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }*/

          }
          else if (cw.isSelected()){
              String cmd="text2wave -o \""+PathCD.getPathInstance().getPath()+"/mydir/audioPiece/" + userInput+".wav\" " + "savedText.txt -eval cw.scm";
              ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
              try {
                  Process process = pb.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              /*String cmd2="mv "+userInput+".wav " +PathCD.getPathInstance().getPath() + "/mydir/audioPiece";
              System.out.println(cmd2);
              ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", cmd2);
              try {
                  Process process = pb2.start();
              } catch (IOException e) {
                  e.printStackTrace();
              }*/

          }

          try {
              _audioExisted.clear();
              existingAudioView.getItems().clear(); //TODO when kill the program, the audioPiece should be cleared, move to extra folder

              Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/EditText.fxml"));
              Scene createViewScene = new Scene(createViewParent);
              // gets the Stage information
              Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
              window.setTitle("Edit text menu");
              window.setScene(createViewScene);
              window.show();
          } catch (IOException e) {

          }


      }

  }

  public void cancel(ActionEvent event){
      try {

          Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/EditText.fxml"));
          Scene createViewScene = new Scene(createViewParent);
          // gets the Stage information
          Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
          window.setTitle("Edit text Menu");
          window.setScene(createViewScene);
          window.show();
      } catch (IOException e) {

      }
  }

  public void readyToCombine(ActionEvent event) throws IOException {

      Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/CombineAudio.fxml"));
      Scene createViewScene = new Scene(createViewParent);
      // gets the Stage information
      Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
      window.setTitle("Edit text Menu");
      window.setScene(createViewScene);
      window.show();


  }


}
