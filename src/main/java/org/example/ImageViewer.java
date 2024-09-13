package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCode.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.paint.Color.*;


public class ImageViewer {

    @FXML
    private ImageView imageView, imageViewScanned;

    @FXML
    private StackPane stackPane;

    private int width, height, numOfPills1, numOfPills2, totalPills, minimum, red, green, blue;

    private int[] colour1, colour2, counter1, counter2;

    private PixelReader pixelReader;

    private WritableImage wImage;

    private PixelWriter pwImage;

    @FXML
    private Text pills, pillsText1, pillsText2;

    @FXML
    private TextField minimumText, redText, greenText, blueText;

    private Color colourToCompare1, colourToCompare2;

    boolean control;

    public void initialize() {
        openImage();
    }

    @FXML
    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString(), 600, 360, false, false);
            width = (int) image.getWidth();
            height = (int) image.getHeight();
            pixelReader = image.getPixelReader();
            imageView.setImage(image);
            imageViewScanned.setImage(null);
            pills.setText(null);
            pillsText1.setText(null);
            pillsText2.setText(null);
            colour1 = new int[(int) (image.getWidth() * image.getHeight())];
            colour2 = new int[(int) (image.getWidth() * image.getHeight())];
            wImage = new WritableImage(width, height);
            pwImage = wImage.getPixelWriter();
            counter1 = new int[colour1.length];
            counter2 = new int[colour2.length];
            minimum = 50;
            red = 5;
            green = 5;
            blue = 5;
            minimumText.setText(null);
            redText.setText(null);
            blueText.setText(null);
            greenText.setText(null);
            stackPane.setOnKeyPressed(event -> {
                if(event.getCode() == CONTROL)
                    control = true;
            });
            stackPane.setOnKeyReleased(event -> {
                if(event.getCode() == CONTROL)
                    control = false;
            });
            minimumText.setOnKeyTyped(event -> {
                try {
                    minimum = Integer.parseInt(minimumText.getCharacters().toString());
                }
                catch (Exception e) {
                    minimum = 50;
                }
            });
            redText.setOnKeyTyped(event -> {
                try {
                    red = Integer.parseInt(redText.getCharacters().toString());
                }
                catch (Exception e) {
                    red = 50;
                }
            });
            greenText.setOnKeyTyped(event -> {
                try {
                    green = Integer.parseInt(greenText.getCharacters().toString());
                }
                catch (Exception e) {
                    green = 50;
                }
            });
            blueText.setOnKeyTyped(event -> {
                try {
                    blue = Integer.parseInt(blueText.getCharacters().toString());
                }
                catch (Exception e) {
                    blue = 50;
                }
            });
        }
    }

    @FXML
    public void mouseClicked(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY) && control)
            secondColour(event);
        else if(event.getButton().equals(MouseButton.PRIMARY))
            firstColour(event);
        else info();
    }

    @FXML
    public void firstColour(MouseEvent event) {
            Arrays.fill(colour1, -1);
            colourToCompare1 = pixelReader.getColor((int) event.getSceneX() - 30, (int) event.getSceneY() - 50);
            int i = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (compareColour(colourToCompare1, x, y)) {
                        if (colour1[i] == -1)
                            colour1[i] = i;
                        if (x + 1 < width && compareColour(colourToCompare1, x + 1, y) && i + 1 < colour1.length) {
                            if (colour1[i + 1] == -1)
                                colour1[i + 1] = i;
                            else Set.union(colour1, colour1[i + 1], colour1[i]);
                        }
                        if (y + 1 < height && compareColour(colourToCompare1, x, y + 1) && i + width < colour1.length)
                            colour1[i + width] = i;
                        if (y > 0 && compareColour(colourToCompare1, x, y - 1))
                            Set.union(colour1, colour1[i - width], colour1[i]);
                    }
                    i++;
                }
            }
        Arrays.fill(counter1, 0);
            for (int j : colour1) {
                if (j != -1)
                    counter1[Set.find(colour1, j)]++;
            }
            numOfPills1 = 0;
            totalPills = 0;
            String sizeOfPills1 = "";
            for (int j : counter1)
                if (j > minimum) {
                    numOfPills1++;
                    sizeOfPills1+= j + ", ";
                }
            setColour(WHITE);
            totalPills = numOfPills1 + numOfPills2;
            pills.setText("Number of Pills: " + totalPills);
            if(sizeOfPills1.length() > 1)
                pillsText1.setText("Size of Pill Group 1: " + sizeOfPills1.substring(0, sizeOfPills1.length() - 2));
            imageViewScanned.setImage(wImage);
    }

    @FXML
    public void secondColour(MouseEvent event) {
        Arrays.fill(colour2, -1);
        colourToCompare2 = pixelReader.getColor((int) event.getSceneX() - 30, (int) event.getSceneY() - 50);
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (compareColour(colourToCompare2, x, y)) {
                    if (colour2[i] == -1)
                        colour2[i] = i;
                    if (x + 1 < width && compareColour(colourToCompare2, x + 1, y) && i + 1 < colour2.length) {
                        if (colour2[i + 1] == -1)
                            colour2[i + 1] = i;
                        else Set.union(colour2, colour2[i + 1], colour2[i]);
                    }
                    if (y + 1 < height && compareColour(colourToCompare2, x, y + 1) && i + width < colour2.length)
                        colour2[i + width] = i;
                    if (y > 0 && compareColour(colourToCompare2, x, y - 1))
                        Set.union(colour2, colour2[i - width], colour2[i]);
                }
                i++;
            }
        }
        Arrays.fill(counter2, 0);
        for (int j : colour2) {
            if (j != -1)
                counter2[Set.find(colour2, j)]++;
        }
        numOfPills2 = 0;
        totalPills = 0;
        String sizeOfPills2 = "";
        for (int j : counter2)
            if (j > minimum) {
                numOfPills2++;
                sizeOfPills2 += j + ", ";
            }
        setColour(WHITE);
        totalPills = numOfPills1 + numOfPills2;
        pills.setText("Number of Pills: " + totalPills);
        if(sizeOfPills2.length() > 1)
            pillsText2.setText("Size of Pill Group 2: " + sizeOfPills2.substring(0, sizeOfPills2.length() - 2));
        imageViewScanned.setImage(wImage);
    }

    public boolean compareColour(Color colourToCompare, int x, int y) {
        return (Math.abs((pixelReader.getColor(x, y).getRed() * 255) - (colourToCompare.getRed() * 255)) <= red && Math.abs((pixelReader.getColor(x, y).getGreen() * 255) - (colourToCompare.getGreen() * 255)) <= blue && Math.abs((pixelReader.getColor(x, y).getBlue() * 255) - (colourToCompare.getBlue() * 255)) <= green);
    }

    public void setColour(Color colour, Color... otherColours) {
        int i = 0;
        Color secondaryColour;
        secondaryColour = otherColours.length > 0 && otherColours[0] != null ? otherColours[0] : colour;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (colour1[i] > 0 && counter1[Set.find(colour1, i)] > minimum)
                    pwImage.setColor(x, y, colour);
                else if (colour2[i] > 0 && counter2[Set.find(colour2, i)] > minimum)
                    pwImage.setColor(x, y, secondaryColour);
                else pwImage.setColor(x, y, BLACK);
                i++;
            }
        }
    }

    public void info() {
        Rectangle rectangle = new Rectangle();
        Label label = new Label();
        rectangle.setTranslateX(-600);
        rectangle.setTranslateY(-300);
        rectangle.setHeight(20);
        rectangle.setWidth(20);
        rectangle.setFill(TRANSPARENT);
        rectangle.setStrokeWidth(1.5);
        rectangle.setStroke(BLACK);
        stackPane.getChildren().add(rectangle);
    }

    public void setRandomColour() {
        Random rnd = new Random();
        setColour(Color.color(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()), Color.color(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()));
        imageViewScanned.setImage(wImage);
    }
}