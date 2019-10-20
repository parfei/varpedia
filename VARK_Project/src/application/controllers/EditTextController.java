package application.controllers;

import application.ChangeScene;
import application.Main;
import application.PathCD;
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
    @FXML private Label select;

    @FXML private Button create;

    private StringBuffer _stringBuffer = new StringBuffer();
    private List<String> _audioExisted= new ArrayList<>();

    @FXML private RadioButton default_voice;
    @FXML private RadioButton male_voice;
    @FXML private RadioButton female_voice;
    @FXML private Label remindLabel;

    @FXML private ListView existingAudioView;
    @FXML private TextField textField;
    private String _term;
    private String _selectedText;

    static final int OUT = 0;
    static final int IN = 1;

    private ExecutorService team = Executors.newSingleThreadExecutor();

    public void initData(String term){
        _term = term;
        System.out.println(_term);
    }

    /**
     * this method will add the search result to the text area
     */
    @FXML
    public void initialize() {

        remindLabel.setVisible(false);

        String cmd = "cat \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"";

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                _stringBuffer.append(line);
            }
            textArea.setText(_stringBuffer.toString());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public int countNumberOfAudioFileInAudioPiece() {
        String path= PathCD.getPathInstance().getPath()+"/mydir/extra/audioPiece";
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
        } else if (numberOfWords > 25) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too much words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();
        }else if (selectedRadioButton==null){
            askForVoice.setText("SELECT A VOICE PLEASE");
            return false;
        }
        return true;
    }

    private class PreviewHelper extends Task<Integer> {
        private String _voice;
        private String _textWithoutBrackets;

        public PreviewHelper(String voice){
            _selectedText = textArea.getSelectedText();
            _textWithoutBrackets = _selectedText.replaceAll("[\\[\\](){}']",""); // remove the text in brackets to make it readable
            _voice = voice;
        }

        @Override
        protected Integer call() throws Exception {
            FileWriter writer = null;
            String cmd = "";

            if (_voice.equals("default_voice")){
                writer=new FileWriter(_voice);
                writer.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+_selectedText +"\"" + ")");
                cmd = "festival -b default_voice";
            } else if (_voice.equals("male_voice")){
                writer=new FileWriter(_voice);
                writer.write("(voice_akl_nz_jdt_diphone)"+"\n"+"(SayText" + " "+"\""+_textWithoutBrackets+"\"" + ")");
                cmd = "festival -b male_voice";
            } else if (_voice.equals("female_voice")){
                writer=new FileWriter("female_voice.scm");
                writer.write("(voice_akl_nz_cw_cg_cg)"+"\n"+"(SayText" + " "+"\""+_textWithoutBrackets+"\"" + ")");
                cmd = "festival -b female_voice.scm";
            }
            writer.close();

            Process process = null;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                process = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return process.waitFor();
        }
    }

    @FXML
    public void preview() throws IOException {
        _selectedText = textArea.getSelectedText();
        String textWithoutBrackets = _selectedText.replaceAll("[\\[\\](){}']",""); // remove the text in brackets to make it readable

        RadioButton selectedRadioButton = (RadioButton) group .getSelectedToggle();

        int numberOfWords = countWords(_selectedText);
        Alert alert = new Alert(Alert.AlertType.ERROR);

        if (numberOfWords==0) { //TODO merge into one error?
            alert.setTitle("No chunk selected");
            alert.setHeaderText("Please select a chunk");
            alert.setContentText("select a chunk so we can carry on");
            alert.showAndWait();
        } else if (numberOfWords > 25) {
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too many words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();

        } else if (selectedRadioButton==null) {
            askForVoice.setText("SELECT A VOICE PLEASE");
            return;

        } else {

            if (default_voice.isSelected()){
                team.submit(new PreviewHelper("default_voice"));
            } else {

                PreviewHelper preview = null;
                if (male_voice.isSelected()) {
                    preview = new PreviewHelper("male_voice");
                } else if (female_voice.isSelected()){
                    preview = new PreviewHelper("female_voice");
                }

                team.submit(preview);

                PreviewHelper finalPreview = preview;
                preview.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        try {
                            if (finalPreview.get().intValue() == 255){
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("voice changed to default");
                                alert.setHeaderText("switch voice to the default due to limitation of other voice options");
                                alert.setContentText("Sorry for the inconvenience");
                                alert.showAndWait();

                                team.submit(new PreviewHelper("default_voice"));
                                /*FileWriter newWriter=new FileWriter("default_voice");
                                newWriter.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+_selectedText +"\"" + ")") ;
                                newWriter.close();
                                String useDefault="festival -b default_voice";
                                ProcessBuilder pronounce = new ProcessBuilder("bash", "-c", useDefault);
                                pronounce.start();*/
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });

                /*FileWriter writer=new FileWriter("male_voice");
                writer.write("(voice_akl_nz_jdt_diphone)"+"\n"+"(SayText" + " "+"\""+textWithoutBrackets+"\"" + ")");
                writer.close();
                String cmd="festival -b male_voice";
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();
                    int exitStatus=process.waitFor();
                    // if the male voice can't read the text, switch to default voice
                    if (exitStatus==255){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("voice changed to default");
                        alert.setHeaderText("switch voice to the default due to limitation of other voice options");
                        alert.setContentText("Sorry for the inconvenience");
                        alert.showAndWait();
                        FileWriter newWriter=new FileWriter("default_voice");
                        newWriter.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+_selectedText +"\"" + ")") ;
                        newWriter.close();
                        String useDefault="festival -b default_voice";
                        ProcessBuilder pronounce = new ProcessBuilder("bash", "-c", useDefault);
                        pronounce.start();

                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }*/

            }
                /*else if (female_voice.isSelected()){
                    FileWriter writer=new FileWriter("female_voice.scm");
                    writer.write("(voice_akl_nz_cw_cg_cg)"+"\n"+"(SayText" + " "+"\""+textWithoutBrackets+"\"" + ")");
                    writer.close();
                    String cmd="festival -b female_voice.scm";
                    ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                    try {
                        Process process = pb.start();
                        int exitStatus=process.waitFor();
                        // if the female voice can't read the text, switch to default voice
                        if (exitStatus==255){
                            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                            alert2.setTitle("voice changed to default");
                            alert2.setHeaderText("switch voice to the default due to limitation of other voice options");
                            alert2.setContentText("Sorry for the inconvenience");
                            alert2.showAndWait();

                            FileWriter newWriter=new FileWriter("default_voice");
                            newWriter.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+_selectedText +"\"" + ")") ;
                            newWriter.close();
                            String useDefault="festival -b default_voice";
                            ProcessBuilder pronounce = new ProcessBuilder("bash", "-c", useDefault);
                            pronounce.start();

                        }
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }*/

        }
    }

    private class SaveHelper extends Task<Void>{

        @Override
        protected Void call() throws Exception {
            return null;
        }
    }

    /**
     * This method will check exception and go to saveToAudio interface when a save button is clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void save(ActionEvent event) throws IOException {
        //RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        String selectedText = textArea.getSelectedText();

        if (!checkValidSelection(textArea.getSelectedText())) {
            return;
        } else { // save the selected text to a file and switch scene to saveToAudio interface
            String saveble = selectedText.replaceAll("[\\[\\](){}']", "");
            int numberOfAudio=countNumberOfAudioFileInAudioPiece();
            String number=Integer.toString(numberOfAudio);

            FileWriter writer = new FileWriter(PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt"); //TODO combine text so we can read as description of file.
            writer.write(saveble);
            writer.close();



            if (default_voice.isSelected()) {
                String createAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term + "-"+ number+ ".wav\" \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval kal.scm";
                System.out.println(createAudio);

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                String file_path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term + "-"+number+ ".wav";
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
                _audioExisted.clear();
                existingAudioView.getItems().clear();
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


            } else if (male_voice.isSelected()) {
                String createAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term+ "-"+ number + ".wav\" \"" +
                        PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval jdt.scm";

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                String file_path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term+ "-"+ number + ".wav";
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
                        int numberOfAudio2=countNumberOfAudioFileInAudioPiece();
                        String number2=Integer.toString(numberOfAudio2);
                        String createDefaultAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term+ "-"+ number2  + ".wav\" \"" +
                                PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval kal.scm";

                        ProcessBuilder pb3 = new ProcessBuilder("bash", "-c", createDefaultAudio);
                        try {
                            Process process = pb3.start();
                            process.waitFor();

                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }


                    } else {
                        //do nothing
                    }


                }
                _audioExisted.clear();
                existingAudioView.getItems().clear();
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


            }else if (female_voice.isSelected()) {
                String createAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term+ "-"+ number + ".wav\" \"" +
                        PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval cw.scm";
                System.out.println(createAudio);

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                String file_path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term+ "-"+ number  + ".wav";
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

                    if (result.get() == ButtonType.OK) {
                        int numberOfAudio2=countNumberOfAudioFileInAudioPiece();
                        String number2=Integer.toString(numberOfAudio2);

                        String createDefaultAudio = "text2wave -o \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + _term+ "-"+ number2  + ".wav\" \"" +
                                PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt\" -eval kal.scm";

                        ProcessBuilder pb3 = new ProcessBuilder("bash", "-c", createDefaultAudio);
                        try {
                            Process process = pb3.start();
                            process.waitFor();

                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //do nothing
                    }


                }
                _audioExisted.clear();
                existingAudioView.getItems().clear();
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

            } else {//do nothing
            }
        }
    }

    /**
     * THis method will remove the saved text and audios when the user want to restart a creation process
     * @param event
     * @throws IOException
     */
    @FXML
    public void backToMain(ActionEvent event) throws IOException { //TODO set up back to search term functionality, change backto main to a little x button at the top?

        String cmd1="rm -rf \""+PathCD.getPathInstance().getPath()+"/mydir/extra/audioPiece\" ; rm -f \""+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"; ";
        //String cmd2="rm -r"+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt"; //TODO make static method

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd1);
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //_changeSceneObject.changeScene(event, "resources/menu.fxml","Main Menu");
        Main.getController().setBOTTOMVIEW("resources/DefaultBottom.fxml");

    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    /**
     * This method will take the user to the creation of video interface when "create' button is clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void readyToCombine(ActionEvent event) throws IOException {
        CreateNewController controller = (CreateNewController) Main.getController().setBOTTOMVIEW("resources/CreateNew.fxml");
        controller.initData(_term);
    }



    /**
     * This method will count how many words in users' selected part
     * @param str
     * @return
     */
    static int countWords(String str)
    {
        int state = OUT;
        int wc = 0;  // word count
        int i = 0;

        // Scan all characters one by one
        while (i < str.length())
        {
            // If next character is a separator, set the
            // state as OUT
            if (str.charAt(i) == ' ' || str.charAt(i) == '\n'
                    || str.charAt(i) == '\t')
                state = OUT;


                // If next character is not a word separator
                // and state is OUT, then set the state as IN
                // and increment word count
            else if (state == OUT)
            {
                state = IN;
                ++wc;
            }

            // Move to next character
            ++i;
        }
        return wc;
    }






}




