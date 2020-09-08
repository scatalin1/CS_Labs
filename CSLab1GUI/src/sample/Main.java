package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main extends Application {

    private static String Directory;
    private static String[][] dat;
    @FXML
    TextField sizeField;
    public TextField nameField;
    GridPane matrix = new GridPane();
    Scene matrixScene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        GridPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Load policies");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

//


    @FXML
    void onButtonClicked(ActionEvent event) throws IOException {
        Directory = nameField.getText();
        System.out.println(Directory);
        String path = Directory;
        Data data = new Data();
        Reader reader = new Reader();
        String str = reader.getReader(path);
        dat = data.getData(str);
        int i=0;

        FileWriter newfile = new FileWriter("MSCT Windows 10 1903 1.19.9.txt");
        while(dat[i][1]!=null){
            for(int j= 0; j <= 25; j++){
                newfile.write(dat[i][j] + " | ");
            }
            newfile.write('\n');
            i++;
        }
        newfile.close();
        int size = 10;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
//                Random rand = new Random();
//                int value = rand.nextInt(2);

                // Create a new TextField in each Iteration
                TextField tf = new TextField();
                tf.setPrefHeight(50);
                tf.setPrefWidth(50);
                tf.setAlignment(Pos.CENTER);
                tf.setEditable(false);
                tf.setText(String.valueOf(dat[y][x]));
                GridPane.setRowIndex(tf, y);
                GridPane.setColumnIndex(tf, x);
                matrix.getChildren().add(tf);
            }
        }
        matrixScene = new Scene(matrix);
        Stage stage = ((Stage)((Button)(event.getSource())).getScene().getWindow());
        stage.sizeToScene();
        stage.setScene(matrixScene);
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
