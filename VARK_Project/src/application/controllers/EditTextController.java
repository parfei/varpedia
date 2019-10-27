package application.controllers;

import application.CustomAlert;
import application.FlickrWork;
import application.Main;
import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import application.bashwork.PreviewHelper;
import application.bashwork.SaveHelper;
import application.values.CustomAlertType;
import application.values.PathIs;
import application.values.SceneFXML;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTextController {
    @FXML private TextArea textArea;
    @FXML private ToggleGroup group;
    @FXML private RadioButton default_voice;
    @FXML private RadioButton male_voice;
    @FXML private RadioButton female_voice;
    @FXML private Label remindLabel;

    @FXML private ListView existingAudioView;
    @FXML private Button playButton;
    @FXML private Button deleteButton;

    @FXML private Button createBtn;
    @FXML private HBox audioControlGroup;

    private String _audioChoice;
    private String _term;
    private String _selectedText;
    static final int OUT = 0;
    static final int IN = 1;
    private ExecutorService team = Executors.newSingleThreadExecutor();
    private ExecutorService flickrTeam = Executors.newSingleThreadExecutor();
    private ExecutorService alertTeam = Executors.newSingleThreadExecutor();
    private MediaPlayer _mediaPlayer;

    public void initData(String term){
        _term = term;
        FlickrWork images = new FlickrWork(_term, "12");
        flickrTeam.submit(images);
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

    /**
     * update the list view which displays the existing audio file
     * @throws Exception
     */

    private void updateExistingAudio() throws Exception {
        String command = "ls \"" + PathIs.TEMP + "/audioPiece\"" + " | cut -f1 -d'.'\n";
        BashCommand update = new BashCommand();
        ArrayList<String> items = update.bash(command);

        if (!items.isEmpty()){
            createBtn.setDisable(false);
        }

        existingAudioView.getItems().setAll(items);
    }

    public int countNumberOfAudioFile() {
        String path= PathIs.TEMP + "/audioPiece";
        return new File(path).listFiles().length;
    }

    /**
     * checkValidSelection method will check if the text user selected is valid and whether user choose a voice
     * @return
     */
    @FXML
    private Boolean checkValidSelection() throws IOException {
        audioControlGroup.setDisable(true);
        _selectedText = textArea.getSelectedText();

        RadioButton selectedRadioButton = (RadioButton) group .getSelectedToggle();
        int numberOfWords = countWords(_selectedText);
        String checkString = _selectedText.replaceAll("\\p{P}", ""); //Delete all punctuation to check string itself isn't empty itself.

        if (_selectedText == null || _selectedText.isEmpty() || checkString.isEmpty()) {
            Main.getController().popupHelper("Choose a chunk of text!", false);
            return false;
        } else if (numberOfWords > 25) {
            Main.getController().popupHelper("Choose a smaller chunk of text!", false);
            return false;
        }else if (selectedRadioButton==null){
            Main.getController().popupHelper("Now select a voice!", false);
            return false;
        }
        audioControlGroup.setDisable(false);
        Main.getController().popupHelper("Create more audio or go next!", false);
        return true;
    }


    /**
     * play the speech of text when preview button is clicked
     */
    @FXML
    public void preview() {
        remindLabel.setVisible(false);
        _selectedText = textArea.getSelectedText();

        if (default_voice.isSelected()) {
            team.submit(new PreviewHelper("default_voice", _selectedText));
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
                        remindLabel.setVisible(true);
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

    private void clearAudio(String file_path){
        String deleteCmd = "rm -f \"" + file_path + "\"";
        System.out.println(deleteCmd);
        try {
            new BashCommand().bash(deleteCmd);
            updateExistingAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void failedSave(CustomAlert alert, String file_path){
        Optional<ButtonType> result = alert.showAlert();
        System.out.println("clearing--------------");
        clearAudio(file_path);

        if (result.get() == ButtonType.OK) {
            SaveHelper retry = new SaveHelper("default_voice", Integer.toString(countNumberOfAudioFile()), _term);
            team.submit(retry);
            retry.setOnSucceeded(workerStateEvent1 -> {
                try {
                    updateExistingAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            try {
                updateExistingAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This method will save the text in an audio file according to the voice that user selected
     * @throws IOException
     */
    @FXML
    public void save() throws Exception {
        String selectedText = textArea.getSelectedText();
        remindLabel.setVisible(false);

        String saveble = selectedText.replaceAll("[\\[\\](){}']", ""); //remove the symbol that is not saveable
        String number=Integer.toString(countNumberOfAudioFile());

        ManageFolder.writeToFile(PathIs.TEMP +"/savedText" + countNumberOfAudioFile() + ".txt", saveble);

        if (default_voice.isSelected()) {
            SaveHelper sh = new SaveHelper("default_voice", number, _term);
            team.submit(sh);

            sh.setOnSucceeded(workerStateEvent -> {
                try {
                    updateExistingAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            String file_path = PathIs.TEMP + "/audioPiece/" + _term+ "-"+ number + ".wav";
            File file = new File(file_path);
            SaveHelper sh;
            if (male_voice.isSelected()){
                sh = new SaveHelper("male_voice", number, _term);
            } else {
                sh = new SaveHelper("female_voice", number, _term);
            }

            team.submit(sh);
            sh.setOnSucceeded(workerStateEvent -> {
                /* ask user to save in default voice or give up saving if the male voice option can't save the audio*/
                if (file.length() == 0) {
                    System.out.println("hi");
                    CustomAlert alert = new CustomAlert(CustomAlertType.SAVE);
                    alertTeam.submit(alert);
                    alert.setOnSucceeded(workerStateEvent1 -> {
                        try {
                            failedSave(alert.get(), file_path);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    /**
     * THis method will remove the saved text and audios when the user want to restart a creation process
     *
     * @throws IOException
     */
    @FXML
    public void backToMain() throws Exception { //TODO set up back to search term functionality, change backto main to a little x button at the top?
        String cmd1="rm -rf \""+ PathIs.TEMP + "/audioPiece\" ; rm -f \""+ PathIs.EXTRA + "/temp.txt\" ; rm -f \""+ PathIs.EXTRA + "/saveTextFolder\"";
        new BashCommand().bash(cmd1);
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }

    /**
     * This method will take the user to the creation of video interface when "create' button is clicked
     * @param
     * @throws IOException
     */
    @FXML
    public void readyToCombine() throws Exception {
        EditPicturesController controller = (EditPicturesController) Main.getController().setTOPVIEW(SceneFXML.IMAGES.toString());
        controller.initData(_term);

        // Main.getController().setTOPVIEW(SceneFXML.SHOWTEXT.toString());
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

    /**
     * play the audio existed in audio list when user select an audio and click play button
     * @throws Exception
     */
    @FXML
    public void playAudio() throws Exception {
        String wavFile = findAudio(_audioChoice);

        Media sound = new Media(new File(wavFile).toURI().toString());
        _mediaPlayer = new MediaPlayer(sound);
        _mediaPlayer.play();
    }

    /**
     * delete the audio existed in audio list when user select an audio and click delete button
     * @throws Exception
     */
    @FXML
    public void deleteAudio() throws Exception {
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
                            playButton.setDisable(true); //disable buttons again if audio played or deleted, prompt user to reselect option.
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



