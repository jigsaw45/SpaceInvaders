import javax.swing.*;
import java.awt.*;

public class Sprite {
    private Image image;

    public Sprite(String imagePath){
        ImageIcon icon = new ImageIcon(imagePath);
        this.image = icon.getImage();
    }

    public Image getImage(){
        return this.image;
    }

    public void setImage(String imagePath){
        ImageIcon icon = new ImageIcon(imagePath);
        this.image = icon.getImage();
    }

}
