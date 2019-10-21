package application.controllers;

import application.Main;
import application.bashwork.BashCommand;
import application.values.ButtonLiterals;
import application.values.FlickrDone;
import application.values.PathIs;
import application.values.SceneFXML;
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

public class EditPicturesController {
    @FXML
    private HBox downloading;
    private String _term;
    private String _name;
    ExecutorService team = Executors.newSingleThreadExecutor();

    //private ChangeScene changeSceneObject=new ChangeScene();
    private List<Image> _imageToDeleteList = new ArrayList<Image>();
    private List<Image> _allImage=new ArrayList<Image>();
    private List<Image>  _remainImage=new ArrayList<Image>();

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
        _term = term;
        if (!FlickrDone.checkDone()){
            FlickrDone.addListener(this);
        } else {
            setGrid();
        }
        //FlickrWork images = new FlickrWork(_term, "12");
        /*team.submit(images);
        images.setOnSucceeded(workerStateEvent -> {
            loadImages(_imageList);
            addImages();
            //TODO when user selects images, put the names into imgs.txt...(need to modify CreationWork then)
        });*/
    }

    public void setGrid(){
        FlickrDone.reset();
        loadImages(_imageToDeleteList);
        addImages();
        downloading.setVisible(false);
    }

    @FXML
    public void backToMainMenu() throws Exception {
        //String cmd1 = "rm -rf \"" + PathIs.TEMP + "/audioPiece\" ; rm -f \"" + PathIs.EXTRA + "/temp.txt\"; ";
        String cmd1 = "rm -rf \"" + PathIs.TEMP + "\" ; rm -f \"" + PathIs.EXTRA + "/temp.txt\"; ";
        new BashCommand().bash(cmd1);
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }

    /**
     * If user wants no pictures in their video, then signal to produce no images.
     * @throws IOException
     */
    @FXML
    public void toFinalScene(ActionEvent event) throws IOException {
        int picNum;

        Button btn = (Button) event.getSource();
        if (btn.getText().equals(ButtonLiterals.NO_IMAGES)){
            picNum = 0;
        } else {
            picNum = 12 - _imageToDeleteList.size();
        }

        if(_imageToDeleteList.size() ==12 || _imageToDeleteList.size() < 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Choosing Images");
            alert.setHeaderText("You haven't selected right amount of images!");
            alert.setContentText("Please select at least one image, but no more than 10.");
            alert.showAndWait();
            return;
        }
        // Delete remaining images (undesired ones)
        for(Image image : _imageToDeleteList) {
            Path toFile = Paths.get(image.getUrl().substring(5));
            File toDelete = toFile.toFile();
            toDelete.delete();
        }
        // load remaing Images
        //loadImages(_remainImage);


        CreateNewController controller = (CreateNewController) Main.getController().setTOPVIEW(SceneFXML.CREATE.toString());
        controller.initData(_term, picNum);
    }

    /**
    load images to image list
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
    add images to image view
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
    let user choose images and make the _imageList contain the images that user don't want to include
     */
    @FXML
    public void chooseImage(MouseEvent event) {
        ImageView clickedImageView = (ImageView) (event.getPickResult().getIntersectedNode());
        Image selectedImage = clickedImageView.getImage();

        if (_imageToDeleteList.contains(selectedImage)) {
            // Remove image from _imageList and make it opaque
            _imageToDeleteList.remove(selectedImage);

            clickedImageView.setStyle("-fx-opacity: 1.0");
        } else {
            // Add image to _imageList, and make it transparent
            _imageToDeleteList.add(selectedImage);
            clickedImageView.setStyle("-fx-opacity: 0.4");
        }
    }


}
