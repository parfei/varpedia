package application;

import javafx.application.Platform;
import javafx.concurrent.Task;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Path;

/**
 * This class is called with its instance to be worked in the background, due to its intensive computational complexity.
 * It creates the sound and video part of the Creation, and then combines them for the user to enjoy,
 * whilst layering text on top.
 * Uses ffmpeg and concat.
 */
public class CreationWork extends Task<String> {
    private String _name;
    private String _term;
    private String _path;
    private String _musicChoice;
    private int _picNum;
    private Boolean _combine;

    public CreationWork(String term, String name, int picNum, Boolean combine, String musicChoice) {
        _name = name;
        _term = term;
        _picNum = picNum;
        _path = PathCD.getPathInstance().getPath() + "/mydir/extra/" + _term + "/" + _name + "/";
        System.out.println(_path);
        _combine = combine;
        _musicChoice = musicChoice;
    }

    /**
     * Main template method of the task class.
     *
     * @return
     * @throws Exception
     */
    @Override
    protected String call() throws Exception {
        if (_combine) { //If user specifies to combine audio, we generate combined audio.
            generateCombinedAudio();
            addBackgroundMusic();
        } else {
            generateAudio();
        }
        if (_picNum == 0) { //If failed to get Flickr images or there are no Flickr images available then generate blue video.
            generateBlueVideo();
        } else {
            generatePicVideo();
        }
        combineForms(); //combine the video and the sound part.

        System.out.println("creationwork done");

        return null;
    }

    /**
     * Generate combined audio of all the audio pieces we have.
     */
    private void generateCombinedAudio() {
        String combine = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece\" ; sox $(ls -tcr | grep wav) \"" +
                PathCD.getPathInstance().getPath() + "/mydir/extra/" + _term + "/" + _name + "/sound.wav\"";
        System.out.println(combine);
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", combine);
        try {
            Process process = pb.start(); //combine audio
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Generate audio of specified number of lines. NOT USED FOR THIS ASSIGNMENT.
     */
    private void generateAudio() {
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

    /**
     * Generate empty video, blue background, if no images.
     */
    private void generateBlueVideo() {
        //video
        String videoCommand = "duration=`soxi -D \"" + _path + "combinedSound.wav\"` ; ffmpeg -f lavfi -i color=c=blue:s=320x240:d=\"$duration\" "
                + "-vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + "'" + _term + "'" + "\" \"" + _path + "video.mp4\"";
        ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
        Process vid = null;
        try {
            vid = video.start(); //Generate video
            vid.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate slideshow video of all the images related to the term.
     */
    private void generatePicVideo() {

        //video
        try {
            generateFilesTxt();

            //String command2 = "ffmpeg -framerate " + _picNum + "/\"$duration\" -f image2 -s 800x600 -i \"" + _path + "img%01d.jpg\" -vcodec libx264 -crf 25 -pix_fmt yuv420p -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" -r 25 \"" + _path + "slideshow.mp4\"";

            //Create the slideshow.
            String command2 = "ffmpeg -y -f concat -safe 0 -i \"" + _path + "imgs.txt\" -pix_fmt yuv420p -r 25 -vf 'scale=trunc(iw/2)*2:trunc(ih/2)*2' \"" + _path + "slideshow.mp4\"";
            //Create the text layering on top.
            String command3 = "ffmpeg -y -i \"" + _path + "slideshow.mp4\" -vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _term + "'\" \"" + _path + "video.mp4\"";
            String videoCommand = command2 + " ; " + command3;
            System.out.println(videoCommand);

            ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
            Process vid = null;
            vid = video.start(); //Start the video
            vid.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the combined verison of sound.wav and video.mp4.
     */
    private void combineForms() {

        String combineCommand = "ffmpeg -y -i \"" + _path + "combinedSound.wav\" -i \"" + _path + "video.mp4\" -c:v copy -c:a aac -strict experimental \"" +
                PathCD.getPathInstance().getPath() + "/mydir/creations/" + _term + "/" + _name + ".mp4\" 2>/dev/null";
        System.out.println(combineCommand);
        ProcessBuilder getTogether = new ProcessBuilder("bash", "-c", combineCommand);

        try {
            Process combine = getTogether.start(); //Combine into the final product.
            combine.waitFor();

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Helper function to generate the required text file to specify duration and files needed for the slideshow.
     */
    private void generateFilesTxt() {
        double duration = 0;

        String command1 = "soxi -D \"" + _path + "combinedSound.wav\"";
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command1);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            duration = Double.parseDouble(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        double dura = duration / _picNum; //Calculate duration of each picture.

        try {
            PrintWriter writer = new PrintWriter(_path + "imgs.txt", "UTF-8");
            for (int i = 0; i < _picNum; i++) {
                writer.println("file '" + _path + "img" + i + ".jpg'"); //Write file name for each image to be included.
                writer.println("duration " + dura); //Write duration for each image to be included.
            }
            writer.println("file '" + _path + "img" + Integer.toString(_picNum - 1) + ".jpg'");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void addBackgroundMusic() throws InterruptedException, IOException {
        System.out.println("start adding background music");


        //measure the length of audio working
       // String pathOfSoundWithoutMusic=PathCD.getPathInstance().getPath() + "/mydir/extra/" + _term + "/" + _name;
        //String command = "soxi -D ./myaudio/sound.wav";
        String command= "soxi -D "+_path+"sound.wav";
        ProcessBuilder measureLength = new ProcessBuilder("/bin/bash", "-c", command);
        Process process = measureLength.start();
        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        int exitStatus = process.waitFor();
        int seconds = 0;
        if (exitStatus == 0) {
            double duration = Double.parseDouble(stdout.readLine());
            seconds = (int) Math.ceil(duration);


        }

        //change mp3 file to wav file
        String createMusicFile = null;
        String path = PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece";

        if (_musicChoice==null||_musicChoice.equals("No music")){
            createMusicFile = "";
        }
        else if (_musicChoice.equals("Clouds")) {
            createMusicFile = "ffmpeg -i ./songs/clouds.mp3 -acodec pcm_u8 -ar 16000 "+ path+"/song.wav";
        } else if (_musicChoice.equals("Fingers")) {
            createMusicFile = "ffmpeg -i ./songs/fingers.mp3 -acodec pcm_u8 -ar 16000 " + path + "/song.wav";
        } else if (_musicChoice.equals("Sun")) {
            createMusicFile = "ffmpeg -i ./songs/sun.mp3 -acodec pcm_u8 -ar 16000 " + path + "/song.wav";
        }
        else {
            System.out.println("Bug");
        }
        if (!(createMusicFile == "")) {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", createMusicFile);
            Process createMusic = builder.start();
            int exit = createMusic.waitFor();
            if (exit == 0) {
                System.out.println("convert the music to wav file");
            }



            //"sox -m ./myaudio/sound.wav ./myaudio/song.wav ./myaudio/out.wav trim 0 "+seconds;

            String combineAudiocommand="sox -m " + _path + "sound.wav "+ path+"/song.wav "+ _path + "combinedSound.wav trim 0 "+seconds;
            System.out.println(combineAudiocommand);
            ProcessBuilder builder1 = new ProcessBuilder("bash", "-c", combineAudiocommand);
            Process combineMusic = builder1.start();
            int exit1=combineMusic.waitFor();
            if (exit1==0){
                System.out.println("combine music completed");
            }

        }
        else {
            String renameCommand="mv "+_path+"sound.wav "+_path+"combinedSound.wav";
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", renameCommand);
            Process rename = builder.start();
            int exit2=rename.waitFor();
            if (exit2==0){
                System.out.println("rename completed");
            }

        }

    }
}
