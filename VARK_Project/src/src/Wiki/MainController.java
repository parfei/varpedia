package Wiki;

import javafx.fxml.FXML;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {


    //private MainController controller = new MainController();

    /**
     * When this method is called, it will change the Scene to CreateView
     * @param event
     * @throws IOException
     */

    @FXML
    public void create(ActionEvent event) throws IOException {


        Parent createViewParent = FXMLLoader.load(getClass().getResource("Creation.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Create View");
        window.setScene(createViewScene);
        window.show();






    }
    @FXML
    public void view(ActionEvent event)throws IOException{
        Parent createViewParent = FXMLLoader.load(getClass().getResource("View.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("View Menu");
        window.setScene(createViewScene);
        window.show();
    }
    @FXML
    public void play(ActionEvent event)throws IOException{

        this.view(event);
    }
    @FXML
    public void delete(ActionEvent event)throws IOException{
        this.view(event);
    }



}

