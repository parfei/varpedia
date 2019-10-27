package application.controllers;

import application.Main;
import application.bashwork.BashCommand;
import application.values.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This class helps user to select images they want to include in their final creation
 */
public class EditPicturesController {
    @FXML private HBox downloading;
    @FXML private Button finalBtn;
    private String _term;
    private List<Image> _imageToDeleteList = new ArrayList<Image>();

    @FXML ImageView view1;
    @FXML ImageView view2;
    @FXML ImageView view3;
    @FXML ImageView view4;
    @FXML ImageView view5;
    @FXML ImageView view6;
    @FXML ImageView view7;
    @FXML ImageView view8;
    @FXML ImageView view9;
    @FXML ImageView view10;
    @FXML ImageView view11;
    @FXML ImageView view12;

    public void initData(String term) {
        finalBtn.setDisable(true);
        _term = term;
        if (!FlickrDone.checkDone()){ //Check if downloading of images is done yet.
            FlickrDone.addListener(this); //Listen to the thread until all images have been downloaded.
        } else {
            setGrid(); //When done, set the grid of images.
        }
    }

    @FXML
    public void initialize() throws IOException {
        Main.getController().currentCreationStep(CreationStep.PICTURE);
        Main.getController().popupHelper("Choose some pictures to go into your creation!", false);
    }

    public void setGrid(){
        FlickrDone.reset(); //Reset value of flickr being done...
        loadImages(_imageToDeleteList); //Load list of images, iterate through files in images folder (.temp)
        addImages(); //Add images to the gridview.
        downloading.setVisible(false);
    }

    /**
     * User will go to next scene to when click "next" or "I want no pictures" button
     * If user click "I want no pictures", no picture will be included in the creation
     * @throws IOException
     */
    @FXML
    public void toFinalScene(ActionEvent event) throws IOException {
        int picNum;

        Button btn = (Button) event.getSource();
        String text=btn.getText();

        if (text.equals(ButtonLiterals.NO_IMAGES.toString())){
            picNum = 0;
        } else {
            picNum = 12 - _imageToDeleteList.size();
        }

        // Delete remaining images (undesired ones)
        for(Image image : _imageToDeleteList) {
            Path toFile = Paths.get(image.getUrl().substring(5));
            File toDelete = toFile.toFile();
            toDelete.delete();
        }

        CreateNewController controller = (CreateNewController) Main.getController().setTOPVIEW(SceneFXML.CREATE.toString());
        controller.initData(_term, picNum);
    }

    /**
     * loadImages method load images to image list
     */
    private void loadImages(List<Image> list) {
        String photoFolder = PathIs.TEMP + "/photos";
        Path dirPath = Paths.get(photoFolder);
        File dirFiles = dirPath.toFile();
        File[] imageListing = dirFiles.listFiles();
        if (imageListing != null) {
            for (File file : imageListing) {
                Image image = new Image(file.toURI().toString());
                list.add(image);
            }
        }
    }

    /**
     * addImages method add images to image view
     */
    private void addImages() {

        view1.setImage(_imageToDeleteList.get(0));
        view2.setImage(_imageToDeleteList.get(1));
        view3.setImage(_imageToDeleteList.get(2));
        view4.setImage(_imageToDeleteList.get(3));
        view5.setImage(_imageToDeleteList.get(4));
        view6.setImage(_imageToDeleteList.get(5));
        view7.setImage(_imageToDeleteList.get(6));
        view8.setImage(_imageToDeleteList.get(7));
        view9.setImage(_imageToDeleteList.get(8));
        view10.setImage(_imageToDeleteList.get(9));
        view11.setImage(_imageToDeleteList.get(10));
        view12.setImage(_imageToDeleteList.get(11));
    }

    /**
     * let user choose images and make the _imageToDeleteList contain the images that user don't want to include
     */
    @FXML
    public void chooseImage(MouseEvent event) {
        ImageView clickedImageView = (ImageView) (event.getPickResult().getIntersectedNode());
        Image selectedImage = clickedImageView.getImage();

        if (_imageToDeleteList.contains(selectedImage)) {
            // Remove image from _imageToDeleteList and make it opaque
            _imageToDeleteList.remove(selectedImage);

            clickedImageView.setStyle("-fx-opacity: 1.0");
        } else {
            // Add image to _imageToDeleteList, and make it transparent
            _imageToDeleteList.add(selectedImage);
            clickedImageView.setStyle("-fx-opacity: 0.4");
        }

        if (12 - _imageToDeleteList.size() > 12 || 12 - _imageToDeleteList.size() < 1){
            finalBtn.setDisable(true);
        } else {
            finalBtn.setDisable(false);
        }
    }


}
