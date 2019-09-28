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

    public CreationWork(String name, int picNum, Boolean overwrite){
        _name = name;
        _term = TransportClass.getInstance().getter();
        _overwrite = overwrite;
        _picNum = picNum;
        _path = PathCD.getPathInstance().getPath() + "/mydir/extra/" + _term + "/" + _name + "/";
    }

    @Override
    protected String call() throws Exception { //TODO check when you mke th video first time
        generateAudio(); //TODO Will make a separate audio instead of needing this...
        if (_picNum == 0){
            generateBlueVideo();
        } else {
            generatePicVideo();
        }
        combineForms();

        System.out.println("creationwork done");
        /*Platform.runLater(() -> {

            String command = "cd \"" + _path + "/mydir\" ; rm -rf extra/* ; cd -"; //Clear files in extra folder.
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            try {
                Process end = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/

        return null;
    }

    private void generateCombinedAudio(){ //TODO retrieve combined audio string, for each string concantenate together to produce audiox`
        String combine= "$(cd " + PathCD.getPathInstance().getPath() + "/mydir/extra/audio ; sox $(ls -tcr | grep wav) sound.wav)";
        System.out.println(combine);
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", combine);
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void generateAudio(){
        String soundCommand = "cat \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/lines.txt\" | text2wave -o \"" + _path + "sound.wav\"";

        ProcessBuilder sound = new ProcessBuilder("bash", "-c", soundCommand);
        try {
            Process sod = sound.start();
            sod.waitFor();
        } catch (IOException | InterruptedException e) {
            // e.printStackTrace();
        }
    }

    private void generateBlueVideo(){
        //video
        String videoCommand = "duration=`soxi -D \"" + _path + "sound.wav\"` ; ffmpeg -f lavfi -i color=c=blue:s=320x240:d=\"$duration\" "
                + "-vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + "'" + _term + "'" + "\" \"" + _path + "video.mp4\"";
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
        try{
            String videoCommand = "duration=`soxi -D \"" + _path + "sound.wav\"` ; " +
                    "ffmpeg -framerate " + _picNum + "/\"$duration\" -f image2 -s 800x600 -i \"" + _path + "img%01d.jpg\" -vcodec libx264 -crf 25 -pix_fmt yuv420p -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" -r 25 \"" + _path + "slideshow.mp4\" ; " +
                    "ffmpeg -y -i \"" + _path + "slideshow.mp4\" -vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _term + "'\" \"" + _path + "video.mp4\"";

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

        String combineCommand = "ffmpeg " + extra + " -i \"" + _path + "sound.wav\" -i \"" + _path + "video.mp4\" -c:v copy -c:a aac -strict experimental \"" +
                PathCD.getPathInstance().getPath() + "/mydir/creations/" + _term + "/" + _name + ".mp4\" 2>/dev/null";
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
