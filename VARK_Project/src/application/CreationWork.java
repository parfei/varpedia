package application;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.nio.file.Path;

public class CreationWork extends Task<String> { //TODO check if actually concurrent.
    private String _name;
    private String _term;
    private String _path;
    private int _picNum;
    private Boolean _overwrite;
    private Boolean _combine;

    public CreationWork(String name, int picNum, Boolean overwrite, Boolean combine){
        _name = name;
        _term = TransportClass.getInstance().getter();
        _overwrite = overwrite;
        _picNum = picNum;
        _path = PathCD.getPathInstance().getPath() + "/mydir/extra/" + _term + "/" + _name + "/";
        _combine = combine;
    }

    @Override
    protected String call() throws Exception { //TODO check when you mke th video first time
        if (_combine){
            generateCombinedAudio();
        } else {
            generateAudio();
        }
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

    private void generateCombinedAudio(){
        String combine= "$(cd " + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece ; sox $(ls -tcr | grep wav) sound.wav)"; //TODO path
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
        System.out.println(soundCommand);
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
            generateFilesTxt();

            //String command2 = "ffmpeg -framerate " + _picNum + "/\"$duration\" -f image2 -s 800x600 -i \"" + _path + "img%01d.jpg\" -vcodec libx264 -crf 25 -pix_fmt yuv420p -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" -r 25 \"" + _path + "slideshow.mp4\"";

            String command2 = "ffmpeg -y -f concat -safe 0 -i \"" + _path + "imgs.txt\" -pix_fmt yuv420p -r 25 -vf 'scale=trunc(iw/2)*2:trunc(ih/2)*2' \"" + _path + "slideshow.mp4\"";
            String command3 = "ffmpeg -y -i \"" + _path + "slideshow.mp4\" -vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _term + "'\" \"" + _path + "video.mp4\"";
            String videoCommand = command2 + " ; " + command3;
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

    private void generateFilesTxt(){
        double duration = 0;

        String command1 = "soxi -D \"" + _path + "sound.wav\"";
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command1);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            duration = Double.parseDouble(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        double dura = duration/_picNum;

        try {
            PrintWriter writer = new PrintWriter(_path + "imgs.txt", "UTF-8");
            for (int i=0;i<_picNum;i++){
                writer.println("file '" + _path + "img" + i + ".jpg'");
                writer.println("duration " + dura);
            }
            writer.println("file '" + _path + "img" + Integer.toString(_picNum-1) + ".jpg'");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
