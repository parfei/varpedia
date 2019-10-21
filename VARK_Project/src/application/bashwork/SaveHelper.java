package application.bashwork;

import application.values.PathIs;
import javafx.concurrent.Task;

public class SaveHelper extends Task<Void> {
    private String _voice;
    private String _number;
    private String _term;

    public SaveHelper(String voice, String number, String term){
        _term = term;
        _number = number;
        _voice = voice;
    }

    @Override
    protected Void call() throws Exception {
        String createAudio = "";
        String path = "\"" + PathIs.EXTRA + "/savedText" + _number + ".txt\"";
        if (_voice.equals("default_voice")){
            createAudio = "text2wave -o \"" + PathIs.TEMP + "/audioPiece/" + _term + "-"+ _number+ ".wav\" " +
                    path + " -eval \"" + PathIs.TEMP +"/kal.scm\"";
        } else if (_voice.equals("male_voice")){
            createAudio = "text2wave -o \"" + PathIs.TEMP +"/audioPiece/" + _term+ "-"+ _number + ".wav\" " +
                    path + " -eval \"" + PathIs.TEMP + "/jdt.scm\"";
        } else if (_voice.equals("female_voice")){
            createAudio = "text2wave -o \"" + PathIs.TEMP + "/audioPiece/" + _term+ "-"+ _number + ".wav\" " +
                    path + " -eval \"" + PathIs.TEMP + "/cw.scm\"";
        }
        new BashCommand().bash(createAudio);
        return null;
    }
}
