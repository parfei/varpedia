package application;

import application.values.FlickrDone;
import application.values.PathIs;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Retrieving and downloading required images from Flickr is all done in this class.
 * It is done in the background, as it is a heavily computational task.
 */
public class FlickrWork extends Task<String> {
    private String _term;
    private int _num;
    private ArrayList<String> _html = new ArrayList<String>();

    public FlickrWork(String term, String num){

        _term = term;
        _num = Integer.parseInt(num);
    }

    /**
     * Main template method of the application.
     * @return
     * @throws Exception
     */
    @Override
    protected String call() throws Exception {

        try {
            if (_num == 0){
                return "0";
            }
            getPhotos();
        } catch (RuntimeException e){
            return "0";
        }

        return Integer.toString(_num);
    }

    /**
     * Retrieve the specified API keys from the config folder.
     * @param key
     * @return String API key
     * @throws IOException
     */
    private String getAPIKey(String key) throws IOException {
        String direct = null;
        try {
            direct = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        direct = direct.substring(0,direct.lastIndexOf("/"));
        String config = direct + "/flickr-api-key.txt";



        File file = new File(config);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            String line; //Read in the required string, according to the key that was specified.
            while ( (line = br.readLine()) != null ) {
                if (line.trim().startsWith(key)) { //Read in the necessary line as requested by the type of key in the input.
                    br.close();
                    return line.substring(line.indexOf("=")+1).trim();
                }
            }
            br.close();

        } catch (FileNotFoundException e) { //DEFAULT KEYS IF FILE NOT FOUND
            if (key.equals("apikey")){
                return "717f64bdd9d9d00bfcdb7a8871bd79b4";
            } else if (key.equals("secret")){
                return "db6f6b6070311dfe";
            }
        }
        return null;
    }

    /**
     * Retrieve and downloaded the photos from Flickr, through the Flickr API. Used code from Dr. Nasser Giacaman's ACP project
     * that demonstrates Flickr API.
     */
    private void getPhotos(){
        try {
            String apiKey = getAPIKey("apikey");
            String sharedSecret = getAPIKey("secret");

            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

            String query = _term;
            int resultsPerPage = _num;
            int page = 0;

            PhotosInterface photos = flickr.getPhotosInterface(); //Get Flickr interface
            SearchParameters params = new SearchParameters(); //Set paramaters
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(query);

            PhotoList<Photo> results = photos.search(params, resultsPerPage, page); //Look for photos


            int count = 0;
            for (Photo photo: results) {
                try {
                    BufferedImage image = photos.getImage(photo, Size.LARGE);
                    String filename = "img" + Integer.toString(count) + ".jpg";
                    //File outputfile = new File(PathIs.EXTRA + "/" + _term + "/" + _name,filename);

                    String photoFolder=PathIs.TEMP+"/photos";
                    //File outputfile = new File(String.valueOf(PathIs.TEMP),filename);
                    File outputfile = new File(photoFolder,filename);
                    ImageIO.write(image, "jpg", outputfile); //Download the image



                    count++;
                } catch (FlickrException fe) {
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); //throw exception if no images were found for such term.
            throw new RuntimeException("No Flickr results for " + _term + ". Creating a blue video instead...");
        }


        Platform.runLater(() -> FlickrDone.isDone(true));
    }

}
