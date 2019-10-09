package application;

import application.controllers.SaveToAudioController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeScene {


    public void changeScene(ActionEvent event, String scene, String title){
        try {




            FXMLLoader loader = new FXMLLoader(Main.class.getResource(scene));
            Parent createViewParent = loader.load();
           // SaveToAudioController controller = loader.getController();

            //controller.initData(_term);

            Scene createViewScene = new Scene(createViewParent);
            // gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(title);
            window.setScene(createViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}