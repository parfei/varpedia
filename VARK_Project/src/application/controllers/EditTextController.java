package application.controllers;

import application.ChangeScene;
import application.FlickrWork;
import application.Main;
import application.PathCD;
import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import application.bashwork.PreviewHelper;
import application.bashwork.SaveHelper;
import application.values.FlickrDone;
import application.values.PathIs;
import application.values.SceneFXML;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTextController {
    @FXML private TextArea textArea;
    @FXML private Label askForVoice;
    @FXML private ToggleGroup group;
    @FXML private RadioButton default_voice;
    @FXML private RadioButton male_voice;
    @FXML private RadioButton female_voice;
    @FXML private Label remindLabel;

    @FXML private ListView existingAudioView;
    @FXML private Button playButton;
    @FXML private Button deleteButton;
    @FXML private ImageView audioSaveResponse;

    private String _audioChoice;
    private String _term;
    private String _selectedText;
    private List<String> _audioExisted= new ArrayList<>();
    static final int OUT = 0;
    static final int IN = 1;
    private ExecutorService team = Executors.newSingleThreadExecutor();
    private ExecutorService flickrteam = Executors.newSingleThreadExecutor();
    private MediaPlayer _mediaPlayer;

    public void initData(String term){
        _term = term;
        FlickrWork images = new FlickrWork(_term, "12");
        flickrteam.submit(images);
        images.setOnSucceeded(workerStateEvent -> {
            FlickrDone.isDone(true);
        });
    }

    /**
     * this method will add the search result to the text area
     */
    @FXML
    public void initialize() throws Exception {
        remindLabel.setVisible(false);

        BashCommand audio = new BashCommand();
        String cmd = "cat \"" + PathIs.EXTRA + "/temp.txt\"";
        ArrayList<String> output = audio.bash(cmd);
        String finalText = output.toString().substring(1);
        finalText = finalText.substring(0,finalText.lastIndexOf("]"));
        textArea.setText(finalText); //Put all current audio pieces list view.
    }

    private void updateExistingAudio() throws Exception {
        String command = "ls \"" + PathIs.TEMP + "/audioPiece\"" + " | cut -f1 -d'.'\n";
        BashCommand update = new BashCommand();
        ArrayList<String> items = update.bash(command);

        existingAudioView.getItems().setAll(items);
    }

    public int countNumberOfAudioFileInAudioPiece() {
        String path= PathIs.TEMP + "/audioPiece";
        return new File(path).listFiles().length;
    }

    private Boolean checkValidSelection(String selectedText){
        RadioButton selectedRadioButton = (RadioButton) group .getSelectedToggle();
        int numberOfWords = countWords(selectedText);

        if (selectedText == null || selectedText.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("No selection");
            error.setHeaderText("please select a chunk");
            error.setContentText("please select a part of text ");
            error.showAndWait();
            return false;
        } else if (numberOfWords > 25) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too much words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();
            return false;
        }else if (selectedRadioButton==null){
            askForVoice.setText("SELECT A VOICE PLEASE");
            return false;
        }
        return true;
    }

    @FXML
    public void preview() throws IOException {
        _selectedText = textArea.getSelectedText();

        if (!checkValidSelection(textArea.getSelectedText())) {
            return;
        } else {
            //String textWithoutBrackets = _selectedText.replaceAll("[\\[\\](){}']",""); // remove the text in brackets to make it readable
            if (default_voice.isSelected()) {
                team.submit(new PreviewHelper("default_voice", _selectedText));
                System.out.println("default voice");
            } else {

                PreviewHelper preview = null;
                if (male_voice.isSelected()) {
                    preview = new PreviewHelper("male_voice", _selectedText);
                } else if (female_voice.isSelected()) {
                    preview = new PreviewHelper("female_voice", _selectedText);
                }

                team.submit(preview);

                PreviewHelper finalPreview = preview;
                preview.setOnSucceeded(workerStateEvent -> {
                    try {
                        if (finalPreview.get() == 255) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("voice changed to default");
                            alert.setHeaderText("switch voice to the default due to limitation of other voice options");
                            alert.setContentText("Sorry for the inconvenience");
                            alert.showAndWait();

                            team.submit(new PreviewHelper("default_voice", _selectedText));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void clearAudio(String file_path){
        String deleteCmd = "rm -f " + file_path;
        try {
            new BashCommand().bash(deleteCmd);
            updateExistingAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if text is readable.
     * @param number
     * @return String valid path of audio to be saved.
     */
    private String checkReadableText(String number){
        String file_path = PathIs.TEMP + "/audioPiece/" + _term + "-"+number+ ".wav";
        File file = new File(file_path);
        // handle the case when audio is not saved successfully
        if (file.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("audio not save");
            alert.setHeaderText("part not readable");
            alert.setContentText("check the part that you select is readable");
            alert.showAndWait();
        }
        return file_path;
    }

    /**
     * This method will check exception and go to saveToAudio interface when a save button is clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void save(ActionEvent event) throws Exception {
        //RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        audioSaveResponse.setVisible(true);
        String selectedText = textArea.getSelectedText();

        if (!checkValidSelection(textArea.getSelectedText())) {
            return;
        } else { // save the selected text to a file and switch scene to saveToAudio interface
            String saveble = selectedText.replaceAll("[\\[\\](){}']", "");
            int numberOfAudio=countNumberOfAudioFileInAudioPiece();
            String number=Integer.toString(numberOfAudio);

            ManageFolder.writeToFile(PathIs.EXTRA + "/savedText" + countNumberOfAudioFileInAudioPiece() + ".txt", saveble);

            if (default_voice.isSelected()) {
                SaveHelper sh = new SaveHelper("default_voice", number, _term);
                team.submit(sh);

                sh.setOnSucceeded(workerStateEvent -> {
                    String file_path = PathIs.TEMP + "/audioPiece/" + _term+ "-"+ number + ".wav";
                    File file = new File(file_path);
                    try {
                        if (file.length() == 0){
                            System.out.println("bad");
                            clearAudio(checkReadableText(number));
                        } else {
                           updateExistingAudio();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                SaveHelper sh;
                if (male_voice.isSelected()){
                    sh = new SaveHelper("male_voice", number, _term);
                } else {
                    sh = new SaveHelper("female_voice", number, _term);
                }

                team.submit(sh);
                sh.setOnSucceeded(workerStateEvent -> {
                    String file_path = PathIs.TEMP + "/audioPiece/" + _term+ "-"+ number + ".wav";
                    File file = new File(file_path);

                    /* ask user to save in default voice or give up saving if the male voice option can't save the audio*/
                    if (file.length() == 0) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Give up or save in default voice");
                        alert.setHeaderText("Can't save the audio in this voice");
                        alert.setContentText("Do you want to save in default voice?");
                        Optional<ButtonType> result = alert.showAndWait();

                        clearAudio(file_path);
                        System.out.println("Cleared");

                        if (result.get() == ButtonType.OK) {
                            System.out.println("OK");

                            SaveHelper retry = new SaveHelper("default_voice", number, _term);
                            team.submit(retry);
                            retry.setOnSucceeded(workerStateEvent1 -> {
                                try {
                                    if (file.length() == 0){
                                        clearAudio(checkReadableText(number));
                                    } else {
                                        System.out.println("update");
                                        updateExistingAudio();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } else {
                        //Finally
                        try { updateExistingAudio(); } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        audioSaveResponse.setVisible(false);
    }

    /**
     * THis method will remove the saved text and audios when the user want to restart a creation process
     * @param event
     * @throws IOException
     */
    @FXML
    public void backToMain(ActionEvent event) throws Exception { //TODO set up back to search term functionality, change backto main to a little x button at the top?
        String cmd1="rm -rf \""+ PathIs.TEMP + "/audioPiece\" ; rm -f \""+ PathIs.EXTRA + "/temp.txt\"; ";
        new BashCommand().bash(cmd1);
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }

    /**
     * This method will take the user to the creation of video interface when "create' button is clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void readyToCombine(ActionEvent event) throws IOException {
        EditPicturesController controller = (EditPicturesController) Main.getController().setTOPVIEW(SceneFXML.IMAGES.toString());
        controller.initData(_term);
    }

    /**
     * This method will count how many words in users' selected part
     * @param str
     * @return
     */
    public static int countWords(String str) {
        int state = OUT;
        int wc = 0;  // word count
        int i = 0;

        // Scan all characters one by one
        while (i < str.length())
        {
            // If next character is a separator, set the state as OUT
            if (str.charAt(i) == ' ' || str.charAt(i) == '\n'
                    || str.charAt(i) == '\t')
                state = OUT;
                // If next character is not a word separator and state is OUT, then set the state as IN and increment word count
            else if (state == OUT)
            {
                state = IN;
                ++wc;
            }
            ++i; // Move to next character
        }
        return wc;
    }

    /**
     * Get the selection of the audio list.
     */
    @FXML
    public void getAudioSelection(){
        try{
            ObservableList selectedCreation = existingAudioView.getSelectionModel().getSelectedItems();
            _audioChoice = selectedCreation.get(0).toString();
        }catch (Exception e){
        }
        if (_audioChoice!=null){
            playButton.setDisable(false);
            deleteButton.setDisable(false);
        }
    }

    @FXML
    public void playAudio(ActionEvent event) throws Exception {
        String wavFile = findAudio(_audioChoice);

        Media sound = new Media(new File(wavFile).toURI().toString());
        _mediaPlayer = new MediaPlayer(sound);
        _mediaPlayer.play();
    }

    @FXML
    public void deleteAudio(ActionEvent event) throws Exception {
        if (_mediaPlayer!=null) {
            _mediaPlayer.stop();
            _mediaPlayer.dispose();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Delete");
        alert.setHeaderText("Check again!");
        alert.setContentText("Are you sure to delete this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {

            team.submit(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String command = "find \"" + PathIs.TEMP + "/audioPiece/" + _audioChoice + ".wav\"";
                    String path = new BashCommand().bash(command).get(0); //finds the audio

                    Platform.runLater(() -> {
                        _audioChoice = null;
                        _mediaPlayer.dispose();
                        try {
                            String cmd = "rm -f \"" + path + "\"";
                            new BashCommand().bash(cmd);
                            updateExistingAudio(); //get the list of creations for currently ticked option.
                            playButton.setDisable(true); //disable buttons again if deleted, prompt user to reselect option.
                            deleteButton.setDisable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return null;
                }
            });
        }else{
            return;
        }
    }

    private String findAudio(String name) throws Exception {
        String command = "find \"" + PathIs.TEMP + "/audioPiece/" + name + ".wav\"";
        return new BashCommand().bash(command).get(0);
    }
}