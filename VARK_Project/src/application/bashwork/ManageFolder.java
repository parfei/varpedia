package application.bashwork;

import application.PathCD;
import application.values.PathIs;

import java.io.*;
import java.util.ArrayList;

public class ManageFolder {
    /**
     * this method return an arraylist containing all creation names
     * @param path
     * @return
     * @throws Exception
     */
    public static ArrayList<String> getCreations(String path) throws Exception {
        BashCommand list = new BashCommand();
        ArrayList<String> creations = null;
        if (path.equals("favourites")){
            creations = list.bash("ls -R"+ " \""+ PathCD.getPathInstance().getPath() + "/mydir/creations/favourites\""+ " | grep .mp4 | cut -f1 -d'.' | sort");
        } else if (path.equals("creations")){
            creations = list.bash("ls -R"+ " \""+ PathCD.getPathInstance().getPath() + "/mydir/creations\""+ " | grep .mp4 | cut -f1 -d'.' | sort");
        }
        return creations;
    }

    /**
     * Create folders
     * @throws Exception
     */
    public static void initializeFolders() throws Exception {
        String path = PathCD.getPathInstance().getPath();

        BashCommand output = new BashCommand();
        ArrayList<String> list = output.bash("[ -e \"" + path + "/mydir\" ]");
        if (list.isEmpty()){
        } else if (list.get(0).equals("1")) {
            output.bash("mkdir -p \"" + path + "/mydir/.extra\" ; mkdir -p \"" + path + "/mydir/creations/creations\" ; mkdir -p \"" + path + "/mydir/creations/favourites\"");

        }
    }

    /**
     * find the path of folder that contains extra files and created video
     * @param name
     * @param isVideo
     * @return
     * @throws Exception
     */

    public static String findPath(String name, Boolean isVideo) throws Exception {
        String command;
        if (isVideo){
            command = "find \"" + PathCD.getPathInstance().getPath() + "/mydir/creations/\"*\"/" + name + ".mp4\""; //Video is in favourites or creations folder.
        } else {
            command = "find \"" + PathIs.EXTRA + "/\"*\"/" + name + "\""; //Find the path of the folder that contains the extra files of the creation.
        }
        BashCommand cmd = new BashCommand();
        return cmd.bash(command).get(0);
    }

    /**
     * write a string to the file
     * @param path
     * @param content
     */
    public static void writeToFile(String path, String content){
        try{
            File file = new File(path);

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * read the content of a file
     * @param path
     * @return
     * @throws FileNotFoundException
     */

    public static String readFile(String path) throws FileNotFoundException {
        String output = "";

            BufferedReader br = new BufferedReader(new FileReader(path));

            String line;
            try {
                while ((line = br.readLine()) != null){
                    output += line;
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return output;
    }

}
