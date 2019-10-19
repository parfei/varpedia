package application.bashwork;

import application.PathCD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ManageFolder {

    public static void initializeFolders() throws Exception {
        String path = PathCD.getPathInstance().getPath();

        BashCommand output = new BashCommand();
        ArrayList<String> list = output.bash("[ -e \"" + path + "/mydir\" ]");
        if (list.isEmpty()){
        } else if (list.get(0).equals("1")) {
            output.bash("mkdir -p \"" + path + "/mydir/extra/\" ; mkdir -p \"" + path + "/mydir/creations/creations\" ; mkdir -p \"" + path + "/mydir/creations/favourites\"");
        }
    }

    public static String findPath(String name, Boolean isVideo) throws Exception {
        String command;
        if (isVideo){
            command = "find \"" + PathCD.getPathInstance().getPath() + "/mydir/creations/creations/" + name + ".mp4\"";
        } else {
            command = "find \"" + PathCD.getPathInstance().getPath() + "/mydir/creations/extra/\"*\"/" + name + ".mp4\"";
        }
        BashCommand cmd = new BashCommand();
        return cmd.bash(command).get(0);

    }

}
