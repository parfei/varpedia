package application.values;

import application.Main;
import javafx.scene.text.Font;

public class SetFont {

    public static Font getQarmic(int size){
        return Font.loadFont(Main.class.getResourceAsStream("css/fonts/qarmic-sans-abridged.ttf"), size);
    }
}
