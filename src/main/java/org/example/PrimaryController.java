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
        Color colourToCompare = pixelReader.getColor((int) event.getSceneX() - 65, (int) event.getSceneY() - 65);
        int i = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.abs(pixelReader.getColor(x, y).getHue() - colourToCompare.getHue()) <= 2) {
                        pwImage.setColor(x, y, WHITE);
                        pictureSize[i] = i;
                    } else {
//                        if(!(pictureSize[i] > 0))
//                            try {
//                                int temp = pictureSize[i];
//                                while (pictureSize[temp] != temp) {
//                                    temp = pictureSize[temp];
//                                    System.out.println("2: " + temp + " " + pictureSize[temp]);
//                                }
//                                pictureSize[i] = temp;
//                        } catch (ArrayIndexOutOfBoundsException e) {System.out.println("AIOUBE");}
//                    }
                            if (x+1 < image.getWidth() && Math.abs(pixelReader.getColor(x + 1, y).getHue() - colourToCompare.getHue()) <= 2) {
                                if(i + 1 <= pictureSize.length && pictureSize[i + 1] >= 0) {
                                    if (pictureSize[i + 1] == 0) {
                                        pwImage.setColor(x + 1, y, WHITE);
                                        pictureSize[i + 1] = pictureSize[i];
                                    } else {
                                        int temp = i;
                                        while(pictureSize[temp] != temp)
                                            temp++;
                                        pictureSize[i] = temp;
                                    }
                                }
                                else {pictureSize[i + 1] = -1;}
                            }
                            if (y + 1< image.getHeight() && Math.abs(pixelReader.getColor(x, y + 1).getHue() - colourToCompare.getHue()) <= 2) {
                                if(i+width < pictureSize.length && pictureSize[i + width] >= 0) {
                                    if (pictureSize[i + width] == 0) {
                                        pwImage.setColor(x + 1, y, WHITE);
                                        pictureSize[i + width] = pictureSize[i];
                                    } else {
                                        int temp = i;
                                        while(pictureSize[temp] != temp)
                                            temp++;
                                        pictureSize[i] = temp;
                                    }
                                }
                                else {pictureSize[i + width] = -1;}
                            }
                    }
                } else {
                    try {
                        System.out.println("No");
                        pwImage.setColor(x, y, BLACK);
                        pictureSize[i+1] = -1;
                    }
                    catch (ArrayIndexOutOfBoundsException e) {}
                }
                i++;
            }
        }
        imageViewScanned.setImage(wImage);
        StringBuilder str = new StringBuilder();
        for (i = 0; i < pictureSize.length; i++) {
            if (pictureSize[i] > 0)
                str.append(pictureSize[i]).append(", ");
        }
        System.out.println("Picture size: " + str);
    }
}
