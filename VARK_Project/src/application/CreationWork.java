package application;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;

public class CreationWork extends Task<String> { //TODO check if actually concurrent.
    private String _name;
    private String _term;
    private String _path;

    public CreationWork(String name){
        _name = name;
        _term = TransportClass.getInstance().getter();
        _path = PathCD.getPathInstance().getPath();
    }

    @Override
    protected String call() throws Exception {
        generateAudio();
        generateBlueVideo();
        combineForms();

        Platform.runLater(() -> {

            String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/* ; cd -"; //Clear files in extra folder.
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            try {
                Process end = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return null;
    }

    private void generateBlueVideo(){
        //video
        String videoCommand = "duration=`soxi -D \"" + _path + "/mydir/extra/sound.wav\"` ; ffmpeg -f lavfi -i color=c=blue:s=320x240:d=\"$duration\" "
                + "-vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + "'" + _name + "'" + "\" \"" + _path + "/mydir/extra/video.mp4\"";

        ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
        Process vid = null;
        try {
            vid = video.start();
            vid.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void generateAudio(){
        String soundCommand = "cat \"" + _path + "/mydir/extra/lines.txt\" | text2wave -o \"" + _path + "/mydir/extra/sound.wav\"";

        ProcessBuilder sound = new ProcessBuilder("bash", "-c", soundCommand);
        try {
            Process sod = sound.start();
            sod.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void combineForms(){
        String combineCommand = "ffmpeg -i \"" + _path + "/mydir/extra/sound.wav\" -i \"" + _path + "/mydir/extra/video.mp4\" -c:v copy -c:a aac -strict experimental \"" + _path + "/mydir/creations/" + _name + ".mp4\" 2>/dev/null";
        ProcessBuilder getTogether = new ProcessBuilder("bash", "-c", combineCommand);

        try {
            Process combine = getTogether.start();
            combine.waitFor();

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
