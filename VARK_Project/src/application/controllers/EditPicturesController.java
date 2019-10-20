package application.controllers;

import application.FlickrWork;
import application.Main;
import application.bashwork.BashCommand;
import application.values.PathIs;
import application.values.SceneFXML;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditPicturesController {
    @FXML private HBox downloading;
    private String _term;
    private String _name;
    ExecutorService team = Executors.newSingleThreadExecutor();

    public void initData(String term){
        _term = term;
    }

    @FXML
    public void initialize(){
        FlickrWork images = new FlickrWork(_term,"10");
        team.submit(images);
        images.setOnSucceeded(workerStateEvent -> {
            //TODO insert loading images into tableview or listview for user to select.
            //TODO when user selects images, put the names into imgs.txt...(need to modify CreationWork then)
        });
    }

    @FXML
    public void backToMainMenu() throws Exception {
        String cmd1="rm -rf \""+ PathIs.TEMP + "/audioPiece\" ; rm -f \""+ PathIs.EXTRA + "/temp.txt\"; ";
        new BashCommand().bash(cmd1);
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }

    @FXML
    public void toFinalScene() throws IOException {
        CreateNewController controller = (CreateNewController) Main.getController().setTOPVIEW(SceneFXML.CREATE.toString());
        controller.initData(_term);
    }

}
