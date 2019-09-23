package application;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;

public class CreationWork extends Task<String> {
    private String _name;

    public CreationWork(String name){
        _name = name;
    }

    @Override
    protected String call() throws Exception {

        String term = TransportClass.getInstance().getter();
        String path = PathCD.getPathInstance().getPath();
        String soundCommand = "cat \"" + path + "/mydir/extra/lines.txt\" | text2wave -o \"" + path + "/mydir/extra/sound.wav\"";

        ProcessBuilder sound = new ProcessBuilder("bash", "-c", soundCommand);
        try {
            Process sod = sound.start();
            sod.waitFor();

            //video
            String videoCommand = "duration=`soxi -D \"" + path + "/mydir/extra/sound.wav\"` ; ffmpeg -f lavfi -i color=c=blue:s=320x240:d=\"$duration\" "
                    + "-vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + "'" + term + "'" + "\" \"" + path + "/mydir/extra/video.mp4\"";

            ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
            Process vid = video.start();
            vid.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String combineCommand = "ffmpeg -i \"" + path + "/mydir/extra/sound.wav\" -i \"" + path + "/mydir/extra/video.mp4\" -c:v copy -c:a aac -strict experimental \"" + path + "/mydir/creations/" + _name + ".mp4\" 2>/dev/null";
        ProcessBuilder getTogether = new ProcessBuilder("bash", "-c", combineCommand);

        try {
            Process combine = getTogether.start();
            combine.waitFor();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Platform.runLater(() -> {

            //TODO CHECK WORKING DELETION
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
}
