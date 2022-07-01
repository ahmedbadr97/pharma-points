package scenes.images;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageLoader {
    public static ImageView LoadImageView(String imageName)
    {
        return new ImageView(LoadImage(imageName));
    }
    public static ImageView LoadImageView(String imageName,int width,int height)
    {
        ImageView img=new ImageView(LoadImage(imageName));
        img.setFitWidth(width);img.setFitHeight(height);
        return img;

    }


    public static Image LoadImage(String imageName)
    {
       return new Image("Images/"+imageName);

    }
    public static Image LoadImage(String imageName,int width,int height)
    {
        return new Image("Images/"+imageName);

    }
    public static ImageView getIcon(String iconName,int size)
    {
        ImageView i=LoadImageView(iconName);
        i.setFitWidth(size);
        i.setFitHeight(size);
        return i;
    }
    public static void icoButton(Button b,String iconName,int size)
    {
        ImageView i=LoadImageView(iconName);
        i.setFitWidth(size);
        i.setFitHeight(size);
        b.setPrefHeight(size);
        b.setPrefWidth(size);
        b.setGraphic(i);
    }
}
