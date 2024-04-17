package org.example;

import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.beans.SimpleBeanInfo;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;


public class PrimaryController {

    @FXML
    private ImageView imageView, imageViewScanned;

    Image image;

    int width, height;

    int[] pictureSize;

    PixelReader pixelReader;

    WritableImage wImage;

    PixelWriter pwImage;


    public void initialize() {
        openImage();
    }

    @FXML
    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            image = new Image(selectedFile.toURI().toString(), 640, 360, true, true);
            width = (int) image.getWidth();
            height = (int) image.getHeight();

            pixelReader = image.getPixelReader();

            imageView.setImage(image);
            System.out.println(image.getWidth() + " " + image.getHeight());
            System.out.println(image.getWidth() * image.getHeight());
            System.out.println((int) (image.getWidth() * image.getHeight()));
            pictureSize = new int[(int) (image.getWidth() * image.getHeight())];
            Arrays.fill(pictureSize, -1);
        }
    }

    @FXML
    public void scanImage(MouseEvent event) {
        wImage = new WritableImage(width, height);
        pwImage = wImage.getPixelWriter();
        for (int i = 0; i < pictureSize.length;) {
            pictureSize[i] = i++;
        }
        Color colourToCompare = pixelReader.getColor((int) event.getSceneX() - 65, (int) event.getSceneY() - 65);
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Math.abs(pixelReader.getColor(x, y).getHue() - colourToCompare.getHue()) <= 2) {
                    if (x + 1 < width && Math.abs(pixelReader.getColor(x + 1, y).getHue() - colourToCompare.getHue()) <= 2 && i + 1 < pictureSize.length && pictureSize[i + 1] == i + 1) {
                        pictureSize[i + 1] = i;
                    }
                    if (y + 1 < height && Math.abs(pixelReader.getColor(x, y + 1).getHue() - colourToCompare.getHue()) <= 2 && i + width < pictureSize.length && pictureSize[i + width] == i + 1) {
                        pictureSize[i + width] = i;
                    }
                }
                else
                    pictureSize[i] = -1;
                i++;
            }
        }
        System.out.println("set pictureSize");
        int[] counter = new int[pictureSize.length];
        int index;
        for(i = 0; i < pictureSize.length; i++){
            if(pictureSize[i] != -1) {
                index = i;
                while(pictureSize[index] != index)
                    index = pictureSize[index];
            counter[index]++;
            }
        }
        System.out.println("Counted");
        i = 0;
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(pictureSize[i] != -1) {
                    index = i;
                    while (pictureSize[index] != index)
                        index = pictureSize[index];
                    if (counter[index] > 5)
                        pwImage.setColor(x, y, WHITE);
                }
                else pwImage.setColor(x, y, BLACK);
                i++;
            }
        }
        System.out.println("changed colours");
    imageViewScanned.setImage(wImage);
    System.out.println("Set image");
    }

    public int find(int[] array, int id) {
        return array[id]==id ? id : find(array, array[id]);
    }
}