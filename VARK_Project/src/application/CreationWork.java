package application;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class CreationWork extends Task<String> { //TODO check if actually concurrent.
    private String _name;
    private String _term;
    private String _path;
    private int _picNum;
    private Boolean _overwrite;

    public CreationWork(String name, int picNum, Boolean overwrite){//TODO call methods of these instead to remove the need for constructor
        _name = name;
        _term = TransportClass.getInstance().getter();
        _path = PathCD.getPathInstance().getPath();
        _overwrite = overwrite;
        _picNum = picNum;
    }

    @Override
    protected String call() throws Exception { //TODO check when you mke th video first time
        generateAudio();
        if (_picNum == 0){
            generateBlueVideo();
        } else {
            generatePicVideo();
        }
        combineForms();

        System.out.println("creationwork done");
        Platform.runLater(() -> {

            String command = "cd \"" + _path + "/mydir\" ; rm -rf extra/* ; cd -"; //Clear files in extra folder.
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            try {
                Process end = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return null;
    }

    private void generateAudio(){
        String soundCommand = "cat \"" + _path + "/mydir/extra/lines.txt\" | text2wave -o \"" + _path + "/mydir/extra/sound.wav\"";

        ProcessBuilder sound = new ProcessBuilder("bash", "-c", soundCommand);
        try {
            Process sod = sound.start();
            sod.waitFor();
        } catch (IOException | InterruptedException e) { //TODO need to fix this
            // e.printStackTrace();
        }
    }

    private void generateBlueVideo(){
        //video
        String videoCommand = "duration=`soxi -D \"" + _path + "/mydir/extra/sound.wav\"` ; ffmpeg -f lavfi -i color=c=blue:s=320x240:d=\"$duration\" "
                + "-vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + "'" + _term + "'" + "\" \"" + _path + "/mydir/extra/video.mp4\"";
        ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
        Process vid = null;
        try {
            vid = video.start();
            vid.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void generatePicVideo(){

        //video
        String audioLength = "echo `soxi -D \"" + _path + "/mydir/extra/sound.wav\"`";
        ProcessBuilder length = new ProcessBuilder("bash", "-c", audioLength);
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(length.start().getInputStream()));
            String dura = reader.readLine();
            double duration = Double.parseDouble(dura)/_picNum;
            String videoCommand = "ffmpeg -r 1/" + duration + " -f image2 -s 800x600 -i \"" + _path + "/mydir/extra/img%01d.jpg\" " +
                    "-vcodec libx264 -crf 25 -pix_fmt yuv420p -vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _term + "'\" \"" + _path + "/mydir/extra/video.mp4\"";

            System.out.println(videoCommand);
            ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
            Process vid = null;
            vid = video.start();
            vid.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void combineForms(){

        String extra = "";

        if (_overwrite){
            extra = "-y";
        }

        String combineCommand = "ffmpeg " + extra + " -i \"" + _path + "/mydir/extra/sound.wav\" -i \"" + _path + "/mydir/extra/video.mp4\" -c:v copy -c:a aac -strict experimental \"" + _path + "/mydir/creations/" + _name + ".mp4\" 2>/dev/null";
        System.out.println(combineCommand);
        ProcessBuilder getTogether = new ProcessBuilder("bash", "-c", combineCommand);

        try {
            Process combine = getTogether.start();
            combine.waitFor();

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
