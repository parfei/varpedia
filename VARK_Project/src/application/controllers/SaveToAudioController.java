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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaveToAudioController {

    @FXML
    private TextField textField;
    @FXML
    private ToggleGroup group;
    @FXML
    private ListView existingAudioView;

    @FXML
    private Toggle kal;
    @FXML
    private Toggle jdt;
    @FXML
    private Toggle cw;


    @FXML
    private Label errorName;
    @FXML
    private Label noneSelection;

    private List<String> _audioExisted = new ArrayList<String>();
    private String _term;

    public void initData(String term){
        _term = term;
    }

    /**
     * This method will list all the existing audios in list view
     */
    public void initialize() {
        errorName.setVisible(false);
        noneSelection.setVisible(false);
        String command = "ls \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece\"" + " | cut -f1 -d'.'\n";
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

    /**
     * This method will enable user to save their selected text to an audio file
     * @param event
     * @throws IOException
     */
    public void save(ActionEvent event) throws IOException {
        String userInput = textField.getText();
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        if (selectedRadioButton == null) {
            noneSelection.setVisible(true);
            noneSelection.setText("please select a button");
            return;
        }

        if (userInput.trim().isEmpty() || userInput == null) {
            errorName.setVisible(true);
            errorName.setText("Complete the fields!");
            return;
        } else if (_audioExisted.contains(textField.getText())) {
            errorName.setText("Duplicated name.");
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Duplicated Name");
            error.setHeaderText("please enter another name");
            error.setContentText("come up with another name");
            error.showAndWait();

        } else if (!textField.getText().matches("[a-zA-Z0-9_-]*")) {
            errorName.setVisible(true);
            errorName.setText("Invalid name. Please enter again.");
            return;
        } else {
            if (kal.isSelected()) {
                String createAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav\" \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval kal.scm";
                System.out.println(createAudio);

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                String file_path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav";
                File file = new File(file_path);
                // handle the case when audio is not saved successfully
                if (file.length() == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("audio not save");
                    alert.setHeaderText("part not readable");
                    alert.setContentText("check the part that you select is readable");
                    alert.showAndWait();
                    String deleteCmd = "rm -f " + file_path;
                    System.out.println(deleteCmd);
                    ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", deleteCmd);
                    try {
                        Process delete = pb2.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // guide the user back to EditText interface to continue saving
                try {
                    _audioExisted.clear();
                    existingAudioView.getItems().clear();

                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/EditText.fxml"));
                    Parent createViewParent = loader.load();
                    EditTextController controller = loader.getController();

                    controller.initData(_term);
                    Scene createViewScene = new Scene(createViewParent);
                    // gets the Stage information
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    window.setTitle("Edit text Menu");
                    window.setScene(createViewScene);
                    window.show();
                } catch (IOException e) {
                    e.printStackTrace();

                }

            } else if (jdt.isSelected()) {
                String createAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".\" \"" +
                        PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval jdt.scm";

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                String file_path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav";
                File file = new File(file_path);
                /*
                ask user to save in default voice or give up saving if the male voice option can't save the audio
                 */
                if (file.length() == 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Give up or save in default voice");
                    alert.setHeaderText("Can't save the audio in this voice");
                    alert.setContentText("Do you want to save in default voice?");
                    Optional<ButtonType> result = alert.showAndWait();
                    String deleteCmd = "rm -f " + file_path;
                    System.out.println(deleteCmd);
                    ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", deleteCmd);
                    try {
                        Process delete = pb2.start();
                        int exitStatus = delete.waitFor();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (result.get() == ButtonType.OK) {
                        String createDefaultAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav\" \"" +
                                PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval kal.scm";

                        ProcessBuilder pb3 = new ProcessBuilder("bash", "-c", createDefaultAudio);
                        try {
                            Process process = pb3.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //do nothing
                    }


                }
                try {
                    _audioExisted.clear();
                    existingAudioView.getItems().clear();

                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/EditText.fxml"));
                    Parent createViewParent = loader.load();
                    EditTextController controller = loader.getController();

                    controller.initData(_term);
                    Scene createViewScene = new Scene(createViewParent);
                    // gets the Stage information
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    window.setTitle("Edit text Menu");
                    window.setScene(createViewScene);
                    window.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                }

            else if (cw.isSelected()) {
                String createAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav\" \"" +
                        PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval cw.scm";

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                String file_path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav";
                File file = new File(file_path);
                /*
                ask user to save in default voice or give up saving if the female voice option can't save the audio
                 */
                if (file.length() == 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Give up or save in default voice");
                    alert.setHeaderText("Can't save the audio in this voice");
                    alert.setContentText("Do you want to save in default voice?");
                    Optional<ButtonType> result = alert.showAndWait();
                    String deleteCmd = "rm -f " + file_path;
                    System.out.println(deleteCmd);
                    ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", deleteCmd);
                    try {
                        Process delete = pb2.start();
                        int exitStatus = delete.waitFor();
                        System.out.println(exitStatus);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (result.get()==ButtonType.OK){
                        String createDefaultAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + userInput + ".wav\" \"" +
                                PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval kal.scm";

                        ProcessBuilder pb3= new ProcessBuilder("bash", "-c", createDefaultAudio);
                        try {
                            Process process = pb3.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //do nothing
                    }


                }
                try {
                    _audioExisted.clear();
                    existingAudioView.getItems().clear();

                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/EditText.fxml"));
                    Parent createViewParent = loader.load();
                    EditTextController controller = loader.getController();

                    controller.initData(_term);
                    Scene createViewScene = new Scene(createViewParent);
                    // gets the Stage information
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    window.setTitle("Edit text Menu");
                    window.setScene(createViewScene);
                    window.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else {//do nothing
            }
        }

    }

    /**
     * let user go back when they don't want to save this audio
     * @param event
     */
    public void cancel(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/EditText.fxml"));
            Parent createViewParent = loader.load();
            EditTextController controller = loader.getController();

            controller.initData(_term);
            Scene createViewScene = new Scene(createViewParent);
            // gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle("Edit text Menu");
            window.setScene(createViewScene);
            window.show();
        } catch (IOException e) {

        }
    }


}
