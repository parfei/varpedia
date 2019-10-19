package application;

import application.bashwork.BashCommand;

import java.util.ArrayList;
import java.util.Comparator;

public class CreationsList {
    private static ArrayList<Creation> creations;

    public CreationsList() throws Exception {
        initialize(findCreations("creations"));
        initialize(findCreations("favourites"));
    }

    private ArrayList<String> findCreations(String path) throws Exception {
        BashCommand list = new BashCommand();
        ArrayList<String> creations = list.bash("ls -R"+ " \""+ PathCD.getPathInstance().getPath() + "/mydir/creations/" + path + "\""+ " | grep .mp4 | cut -f1 -d'.' | sort");
        return creations;
    }

    public ArrayList<String> getCreations(){
        ArrayList<String> output = null;
        for (Creation creation:creations){
            output.add(creation.toString());
        }
        return output;
    }

    public ArrayList<String> getFavourites(){
        ArrayList<String> output = null;
        for (Creation creation:creations){
            if (creation.isFav() == true){
                output.add(creation.toString());
            }
        }
        return output;
    }

    public void addCreation(Creation creation){
        creations.add(creation);
        sort();
    }

    private void initialize(ArrayList<String> list) throws Exception {
        for (String name : list) {
            BashCommand path = new BashCommand();
            ArrayList<String> pathway  = path.bash("find \"" + PathCD.getPathInstance().getPath() + "/mydir/creations/\"*\"/\"*\"/" + name + ".mp4\"");

            String inputPath = pathway.get(0);
            String inputTerm = inputPath.substring(inputPath.substring(0,inputPath.lastIndexOf("/")).lastIndexOf("/") + 1, inputPath.lastIndexOf("/"));
            creations.add(new Creation(inputTerm, name));
            //TODO add information about plays and confidence.
        }
    }

    private void sort(){
        creations.sort(new Comparator<Creation>() {
            @Override
            public int compare(Creation o1, Creation o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
    }

}
