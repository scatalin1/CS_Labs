package sample;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main extends Application {

    private static String Directory;
    private static String[][] data;
    private static  String[] head;
    private static String[][] final_data = new String[363][30];
    private static String[][] comp_audits = new String[363][5];

    ObservableList<String[]> list = FXCollections.observableArrayList();

    @FXML
    TextField sizeField;
    public TextField nameField;
    GridPane matrix = new GridPane();
    Scene matrixScene;

    TableView tableView = new TableView();
    Scene tableScene;

    @Override
    public void start(Stage primaryStage) throws IOException {

//        ObservableList<String[]> list = FXCollections.observableArrayList();
//        list.addAll(Arrays.asList(data));
//        list.remove(0);

        GridPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Check policies");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public ObservableList<ObservableList<String>> buildData(String[][] dataArray) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        head = dataArray[0];
        String[][] dataAr = dataArray;
        for(int i=0;i<dataAr.length-1;i++){
            dataAr[i] = dataAr[i+1];
        }
        for (String[] row : dataAr) {
            data.add(FXCollections.observableArrayList(row));
        }

        return data;
    }

    public TableView<ObservableList<String>> createTableView(String[][] dataArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setItems(buildData(dataArray));

        for (int i = 0; i < 24; i++) {
            final int curCol = i;
            //TableColumn<String, Content> column = new TableColumn<>(dataArray[i]
            //new TableColumn(dataArray[i].toString());
            final TableColumn<ObservableList<String>, String> column = new TableColumn<>(head[i]);
            column.setCellValueFactory(
                    param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol))
            );
            tableView.getColumns().add(column);
        }

        return tableView;
    }

//    public TableView<ObservableList<CheckBoxTableCell>> checkBoxColumn(CheckBoxTableCell[] check) {
//        TableView<ObservableList<CheckBoxTableCell>> boxTable = new TableView<>();
//
//    }


    @FXML
    void onButtonClicked(ActionEvent event) throws IOException {
        final String[] tmp = {""};
        String path = nameField.getText();
        System.out.println("working");
        Data dat = new Data();
        Reader reader = new Reader();
        String str = reader.getReader(path);
        data = dat.getData(str);
//        int i=0;
        write_to_file();
//        int size = 50;
//        createTableView(data);
//        for (int x = 0; x <= 24; x++) {
//            for (int y = 0; y <=326; y++) { //326
//                TableColumn<String, Content> c = new TableColumn<>(data[x][y]);
//                tableView.getColumns().addAll(c);
//
//            }
//            i++;
//        }
//
//        tableView.setItems(list);
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        tableView.getColumns().addAll();

//        VBox vbox = new VBox(tableView);
//        tableScene = new Scene(vbox);
        //TableView<ObservableList<String>> tableView = createTableView(data);
        VBox box = new VBox();
        Button export = new Button("   export   ");
        Button apply = new Button("   find id   ");
        TextField search = new TextField("search for...");
        TextField ids = new TextField();
        ids.setMinWidth(200);
        ids.setMaxWidth(200);
        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList("id", "type", "Policy Path", "Policy Setting Name", "value_type", "value_data", "reg_key", "reg_item"));
        search.setMaxWidth(300);
        CheckBox checkbox = new CheckBox("Select all");
        final String[] searchfor = {new String()};
        final String[] header = {new String()};
        ids.visibleProperty().bind(Bindings.isNotNull(cb.valueProperty()));
        EventHandler<ActionEvent> search_apply = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e)
            {
                ids.setText("");
                String[][] new_data = new String[data.length][data[0].length];
                searchfor[0] = search.getText();
                System.out.println("search for " + searchfor[0]);
                header[0] = cb.getValue().toString();
                System.out.println("header " + header[0]);
                new_data[0] = data[0];
                int j = 0;
                if(search.getText() == null || search.getText().equals("search for...")){
                    ids.setText("error! Insert the search field");
//                    ids.setText(tmp[0]);
                }
                else {
                    while (j < head.length && head[j] != header[0]) {
                        j++;
                    }
                    System.out.println("j = " + j);

                    int k = 0;
                    if (header[0].equals("id")) {
                        for (int i = 1; i < data.length - 1; i++) {
                            if (data[i][j] != null && data[i][j].equals(searchfor[0])) {
                                new_data[k] = data[i];
                                k++;
                            }
                        }
                    } else {
                        for (int i = 1; i < data.length - 1; i++) {
                            if (data[i][j] != null && data[i][j].contains(searchfor[0])) {
                                new_data[k] = data[i];
                                k++;
                            }
                        }
                    }
                    int i = 0;
                    while (new_data[i][1] != null) {
                        for (j = 0; j <= 25; j++) {
                            System.out.print(new_data[i][j] + " | ");
                        }
                        System.out.println();
                        System.out.println("_________________________________________________________________________________________________________________________________________________");
                        i++;
                    }

//                create_matrix(new_data);
                    i = 0;
                    tmp[0] = "";
                    while (new_data[i][0] != null) {
//                    for(i=0;i<new_data.length ;i++){
                        tmp[0] += new_data[i][0] + " ";
                        i++;
                    }
                    System.out.println(tmp[0]);
                    ids.setText("");
                    ids.setText(tmp[0]);
                }
            }
        };

        EventHandler<ActionEvent> export_action = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e)
            {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("D:\\new\\audit.txt"));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    String ls = System.getProperty("line.separator");
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append(ls);
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    reader.close();

                    String audits = stringBuilder.toString();
//                    System.out.println("audi : " + audits);
                    create_table_audits(audits);
                    if(checkbox.isSelected()){
                        final_data = null;
                        final_data = data;
                        System.out.println("check selected");
                    }
                    else{
                        String idstr = ids.getText();
                        System.out.println(idstr);
                        if(idstr == null){
                            final_data = null;
                            final_data = data;
                            System.out.println("null ids");
                        }
                        else{
                            int i = 0; int k = 0;
                            while (i<idstr.length()){
                                while (idstr.charAt(i)!=' ')
                                    i++;
                                String s = idstr.substring(0,i);
                                idstr = idstr.substring(i+1);
                                    System.out.println(s + "," + data[Integer.parseInt(s) - 1][2]);
//                                final_data = null;
//                                System.out.println(final_data[0][0]);
                                    final_data[k] = data[Integer.parseInt(s) - 1];
                                    k++;
                            }
                        }
                    }
                    write_to_export(str,final_data);
//                    String audits = "";

//                    myReader.close();
                    Label l = new Label("label");
                    VBox newVBox = new VBox(l);
                    Scene stringScene = new Scene(newVBox, 600, 300);
                    Stage stage = new Stage();
                    stage.setScene(stringScene);
                    stage.show();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        apply.setOnAction(search_apply);
        export.setOnAction(export_action);
//        ids.setText(tmp[0]);
        Label textl = new Label("add ids of policies you want to be added");
        HBox hBox = new HBox(cb, search, apply, ids, textl);
        HBox hb = new HBox(createTableView(data));
        box.getChildren().addAll(hBox, export, checkbox, hb);

        Stage stage = ((Stage)((Button)(event.getSource())).getScene().getWindow());
        stage.sizeToScene();
        stage.setScene(new Scene(box,900,300));
        stage.show();

    }

//    void printMatrix(TableView<String[]> target, String[][] source) {
//
//        target.getColumns().clear();
//        target.getItems().clear();
//
//        int numRows = source.length;
//        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
//            for (int i = target.getColumns().size(); i < source[rowIndex].length; i++);
//                TableColumn<String[], String[]> column = new TableColumn<>("column");
//                final int columnIndex = i;
//                column.setCellValueFactory(cellData -> {
//                    String[] row = cellData.getValue();
//                    String value;
//                    if (row.length <= columnIndex) {
//                        value = null;
//                    } else {
//                        value = row[columnIndex];
//                    }
//                    return new SimpleStringProperty(value);
//                });
//                target.getColumns().add(column);
//        }
//        target.getItems().add(source[rowIndex]);
//    }

    public void create_table_audits(String audits){
//        while (!audits.startsWith("[System Access]")){
//            audits = audits.substring(1);
//        }
        int i = 0; int j = 0;
        while (audits.length()>1){
            if(audits.startsWith("[")){
                int k = 0;
                while (audits.charAt(k)!=']'){
                    k++;
                }
                audits = audits.substring(0,i);
            }
            else{
                String s = "";
                while (!audits.startsWith("=")){
                    s +=audits.charAt(0);
                    audits = audits.substring(1);
                }
                comp_audits[i][j] = s;
                j++;
                s = "";
                audits = audits.substring(2);
                while (!audits.startsWith("\n") && !audits.startsWith("\r") && !audits.startsWith("\r\n")){
                    s +=audits.charAt(0);
                    audits = audits.substring(1);
                }
                comp_audits[i][j] = s;
                j=0;
                i++;
            }
        }
        for(i = 0;i<final_data.length;i++){
            comp_audits[i][2] = final_data[i][10];
            comp_audits[i][3] = final_data[i][15];
        }
        for(i=0;i<comp_audits.length;i++){
            for(j=0;j<comp_audits[i].length;j++){
                System.out.println(comp_audits[i][j] + "  |  ");
            }
            System.out.println();
        }
    }

    public void write_to_file() throws IOException {
        PrintWriter newfile = new PrintWriter("MSCT Windows 10 1903 1.19.9.json");
        int i = 1;

        while(data[i][1]!=null){
            newfile.write('{');
            newfile.write('\n');
            for(int j= 0; j < 25; j++){
                if(data[i][j]!= null && data[i][j]!= null){
                    if(!data[i][j].startsWith("\"")){
                        newfile.write("\"");
                    }
                    newfile.write(data[0][j] + "\": \"" + data[i][j] + "\",");
                    newfile.write('\n');
                }
            }
            newfile.write('}');
            newfile.write('\n');
            i++;
        }
        newfile.close();
    }

    public void write_to_export(String read, String[][] data) throws IOException {

        PrintWriter newfile = new PrintWriter("MSCT Windows 10 1903 1.19.9.audit");
        int i = 0;
        String reader = read;
        while (!reader.startsWith("<check_type"))
            reader = reader.substring(1);
        while (!reader.startsWith("</report>")) {
            newfile.write(reader.charAt(0));
            reader = reader.substring(1);
        }
        newfile.write(reader.substring(0,10));

        while(data[i][0]!=null){
            newfile.write("<custom_item>");
            newfile.write("\n\n");
            for(int j= 0; j < 25; j++){
                if(data[i][j]!= null && data[i][j]!= null){
                    newfile.write(head[j] + "  :  " + data[i][j]);
                    newfile.write('\n');
                }
            }
            newfile.write("</custom_item>");
            newfile.write("\n\n");
            i++;
        }
        reader = reader.substring(reader.indexOf("</then>")+1);
        newfile.write(reader);

        newfile.close();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}


//////path :   D:\3 curs 1 semester\criptography and security\lab 3
// C:\Users\Сat\Downloads