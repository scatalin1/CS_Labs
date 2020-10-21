package sample;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    private static String[][] data;
    private static  String[] head;
    private static String[][] final_data = new String[363][30];
    private static String[][] comp_audits = new String[363][3];
    private static String[][] compare_audits = new String[363][5];

    @FXML
    public TextField nameField;

    @Override
    public void start(Stage primaryStage) throws IOException {

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
        if(dataArray[0][0].equals("id")) {
            for(int i=0;i<dataAr.length-1;i++){
                dataAr[i] = dataAr[i+1];
            }
        }
        else{
            for(int i=0;i<dataAr.length;i++){
                dataAr[i] = dataAr[i];
            }
        }
        for (String[] row : dataAr) {
            data.add(FXCollections.observableArrayList(row));
        }

        return data;
    }

    public TableView<ObservableList<String>> createTableView(String[][] dataArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setItems(buildData(dataArray));
        for (int i = 0; i < head.length; i++) {
            final int curCol = i;
            final TableColumn<ObservableList<String>, String> column = new TableColumn<>(head[i]);
            column.setCellValueFactory(
                    param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol))
            );
            tableView.getColumns().add(column);
        }

        return tableView;
    }

    public TableView<ObservableList<String>> createTableViewNew(String[][] dataArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setItems(buildData(dataArray));
        String[] first_line = {"id","Name","Value_data", "Value from windows", "passed/failed"};
        for (int i = 0; i < first_line.length; i++) {
            final int curCol = i;
            final TableColumn<ObservableList<String>, String> column = new TableColumn<>(first_line[i]);
            column.setCellValueFactory(
                    param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol))
            );
            tableView.getColumns().add(column);
        }

        return tableView;
    }



    @FXML
    void onButtonClicked(ActionEvent event) throws IOException {
        final String[] tmp = {""};
        String path = nameField.getText();
        System.out.println("working");
        Data dat = new Data();
        Reader reader = new Reader();
        String str = reader.getReader(path);
        data = dat.getData(str);
        write_to_file();
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

                    i = 0;
                    tmp[0] = "";
                    while (new_data[i][0] != null) {
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
                    BufferedReader reader = new BufferedReader(new FileReader("C:\\audits.inf"));
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
                                    final_data[k] = data[Integer.parseInt(s) - 1];
                                    k++;
                            }
                        }
                    }
                    write_to_export(str,final_data);
                    create_table_audits(audits);
                    compare_audits();

                    VBox boxnew = new VBox();
                    TextField searchnew = new TextField("search for...");
                    TextField idsnew = new TextField();
                    EventHandler<ActionEvent> searching = new EventHandler<ActionEvent>() {

                        public void handle(ActionEvent e)
                        {
//                            String[][] new_data = new String[data.length][data[0].length];
                            searchfor[0] = searchnew.getText();
                            System.out.println("search for " + searchfor[0]);
                            String sear = "";
//                            header[0] = cb.getValue().toString();
//                            System.out.println("header " + header[0]);
//                            new_data[0] = data[0];
//                            int i = 0;
                            if(searchfor[0] == null || searchfor[0].equals("search for...")){
                                ids.setText("error! Insert the search field");
                            }
                            else {
                                for (int i = 0; i < compare_audits.length; i++) {
//                                    System.out.println("i=" + i);
                                    if (compare_audits[i][1] != null && compare_audits[i][1].contains(searchfor[0])) {
                                        System.out.println("yes");
                                        sear += compare_audits[i][0] + " ";
                                    }
                                }
                            }
                            idsnew.setText(sear);
                        }
                    };

                    EventHandler<ActionEvent> apply_action = new EventHandler<ActionEvent>() {

                        public void handle(ActionEvent e) {
                            String idstr = idsnew.getText();
                            int[] idint = new int[idstr.length()];
                            System.out.println(idstr);
                            if (idstr == null) {
                                final_data = null;
                                final_data = data;
                                System.out.println("null ids");
                            } else {
                                int i = 0;
                                int k = 0;
                                while (i < idstr.length()) {
                                    while (idstr.charAt(i) != ' ')
                                        i++;
                                    String s = idstr.substring(0, i);
                                    idstr = idstr.substring(i + 1);
                                    idint[k] = Integer.parseInt(s);
                                    k++;
                                }
                            }
                            try {
                                find_in_compare(idint, audits);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
//                            int k=0;
//                            while (idint[k]>0){
//                                System.out.println(idint[k]);
//                                k++;
//                            }
                        }
                    };

                    Button applynew = new Button("   apply   ");
                    applynew.setOnAction(apply_action);
                    Button find = new Button("   find id   ");
                    find.setOnAction(searching);
                    idsnew.setMinWidth(200);
                    idsnew.setMaxWidth(200);

                    Label textnew = new Label("add ids of policies you want to be added");
                    HBox hBoxnew = new HBox(searchnew, find, idsnew, textnew);
                    HBox hbnew = new HBox(createTableViewNew(compare_audits));
                    Label textapp = new Label("after click on \"apply\" open command line as administrator ");
                    Label textapp1 = new Label("and write the following command: secedit.exe /configure /db %windir%\\security\\local.sdb /cfg D:\\new_audits.inf");
                    boxnew.getChildren().addAll(hBoxnew, applynew, textapp, textapp1, hbnew);


                    Stage stage = new Stage();
                    stage.setScene(new Scene(boxnew,900,300));
                    stage.show();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        apply.setOnAction(search_apply);
        export.setOnAction(export_action);
        Label textl = new Label("add ids of policies you want to be added");
        HBox hBox = new HBox(cb, search, apply, ids, textl);
        HBox hb = new HBox(createTableView(data));
        Label textexp = new Label("before click on \"export\" open command line as administrator ");
        Label textexp1 = new Label("and write the following command: secedit.exe /export /cfg D:\\audits.inf");
        box.getChildren().addAll(hBox, textexp, textexp1, export, checkbox, hb);

        Stage stage = ((Stage)((Button)(event.getSource())).getScene().getWindow());
        stage.sizeToScene();
        stage.setScene(new Scene(box,900,300));
        stage.show();

    }

    public void find_in_compare(int[] ids, String audits) throws IOException {

        int i =0; int[] final_id = new int[ids.length]; int[] id_from_compare = new int[ids.length]; int j=0;
        while (ids[i]>0){
            if(compare_audits[ids[i]-1][3].contains("-") || compare_audits[ids[i]-1][4].equals("passed")) {
                i++;
            } else {
                int k = 0;
                System.out.println(compare_audits[ids[i] - 1][1]);
                while (k < data.length) {
                    if(data[k][12]==null)
                        k++;
                    if (data[k][12].equals(compare_audits[ids[i] - 1][1]))
                        break;
                    k++;
                }
                final_id[j] = k+1;
                id_from_compare[j] = ids[i];
                System.out.println("id_from_compare=" + id_from_compare[j] + " final id=" + final_id[j]);
                j++;
                i++;
            }
        }
        i = 0;
        while(final_id[i]>0) {
            if (final_id[i]== 230) {
                final_id[i]=2;
            } else if (final_id[i]== 249) {
                final_id[i]=3;
            } else if (final_id[i]== 64) {
                final_id[i]=14;
            } else if (final_id[i]== 61) {
                final_id[i]=15;
            } else if (final_id[i]== 62) {
                final_id[i]=16;
            } else if (final_id[i]== 54 || final_id[i]== 63 || final_id[i]== 53) {
                final_id[i]=18;
            } else if (final_id[i]== 74) {
                final_id[i]=19;
            } else if (final_id[i]== 66) {
                final_id[i]=20;
            } else if (final_id[i]== 59) {
                final_id[i]=22;
            } else if (final_id[i]== 305 || final_id[i]== 306 || final_id[i]== 307
                    || final_id[i]== 308 || final_id[i]== 309 || final_id[i]== 310 ) {
                final_id[i]=36;
            } else if (final_id[i]== 343) {
                final_id[i]=30;
            } else if (final_id[i]== 342) {
                final_id[i]=29;
            } else if (final_id[i]== 132) {
                final_id[i]=1;
            } else if (final_id[i]== 272) {
                final_id[i]=56;
            } else if (final_id[i]== 224) {
                final_id[i]=63;
            } else if (final_id[i]== 131) {
                final_id[i]=74;
            }
            comp_audits[final_id[i]][1] = compare_audits[id_from_compare[i]][2];
            i++;
        }
        System.out.println("Compare in the end:");
        i = 0;
        while (compare_audits[i][1]!=null){
            for(j=0;j<compare_audits[0].length;j++){
                System.out.print(compare_audits[i][j] + "  |  ");
            }
            System.out.println();
            i++;
        }
        export_new_audits(audits);
    }

    public void compare_audits(){
        int i = 0;
        i = 0;
        System.out.println("Comp:");
        while (comp_audits[i][0]!=null){
            for(int j=0;j<comp_audits[0].length;j++){
                System.out.print(comp_audits[i][j] + "  |  ");
            }
            System.out.println();
            i++;
        }
        System.out.println("Final:");
        i = 0;
        while (final_data[i][0]!=null){
            for(int j=0;j<final_data[0].length;j++){
                System.out.print(final_data[i][j] + "  |  ");
            }
            System.out.println();
            i++;
        }
        i = 0;
        while (final_data[i][0]!=null){
            compare_audits[i][1] = final_data[i][12];
            compare_audits[i][2] = final_data[i][16];
            i++;
        }
        i=0;
        while(compare_audits[i][1]!=null){
            compare_audits[i][0] = i+1 + "";
            i++;
        }
        int j = 0;
        while (compare_audits[j][1]!=null) {
            String s = "";
            int count=0;
            char c;
            while (count<compare_audits[j][1].length()-1){
                if(compare_audits[j][1].charAt(count)==' '){
                    count++;
                    c  = Character.toUpperCase(compare_audits[j][1].charAt(count));
                }
                else{
                    c = compare_audits[j][1].charAt(count);
                }
                s+=c;
                count++;
            }
            j++;
        }
        i=0;
        while(compare_audits[i][1]!= null) {
            if (final_data[i][0].equals("230")) {
                compare_audits[i][3] = comp_audits[2][1];
            } else if (final_data[i][0].equals("249")) {
                compare_audits[i][3] = comp_audits[3][1];
            } else if (final_data[i][0].equals("64")) {
                compare_audits[i][3] = comp_audits[14][1];
            } else if (final_data[i][0].equals("61")) {
                compare_audits[i][3] = comp_audits[15][1];
            } else if (final_data[i][0].equals("62")) {
                compare_audits[i][3] = comp_audits[16][1];
            } else if (final_data[i][0].equals("54") || final_data[i][0].equals("63") || final_data[i][0].equals("53")) {
                compare_audits[i][3] = comp_audits[18][1];
            } else if (final_data[i][0].equals("74")) {
                compare_audits[i][3] = comp_audits[19][1];
            } else if (final_data[i][0].equals("66")) {
                compare_audits[i][3] = comp_audits[20][1];
            } else if (final_data[i][0].equals("59")) {
                compare_audits[i][3] = comp_audits[22][1];
            } else if (final_data[i][0].equals("305") || final_data[i][0].equals("306") || final_data[i][0].equals("307")
                    || final_data[i][0].equals("308") || final_data[i][0].equals("309") || final_data[i][0].equals("310")) {
                compare_audits[i][3] = comp_audits[36][1];
            } else if (final_data[i][0].equals("343")) {
                compare_audits[i][3] = comp_audits[30][1];
            } else if (final_data[i][0].equals("342")) {
                compare_audits[i][3] = comp_audits[29][1];
            } else if (final_data[i][0].equals("132")) {
                compare_audits[i][3] = comp_audits[1][1];
            } else if (final_data[i][0].equals("272")) {
                compare_audits[i][3] = comp_audits[56][1];
            } else if (final_data[i][0].equals("224")) {
                compare_audits[i][3] = comp_audits[63][1];
            } else if (final_data[i][0].equals("131")) {
                compare_audits[i][3] = comp_audits[74][1];
            } else {
                compare_audits[i][3] = "-";
                compare_audits[i][4] = "was not found";
//        System.out.println("i = " + i)
            }
            i++;
        }
            i=0;
            while (compare_audits[i][3]!=null) {
                for (j = 0; j < compare_audits[i][3].length(); j++) {
                    if (compare_audits[i][3].charAt(j) == ' ' || compare_audits[i][3].charAt(j) == ',')
                        compare_audits[i][3] = compare_audits[i][3].substring(0, j) + compare_audits[i][3].substring(j + 1);
                }
                for (j = 0; j < compare_audits[i][2].length(); j++) {
                if (compare_audits[i][2].charAt(j) == ' ' || compare_audits[i][2].charAt(j) == ',')
                    compare_audits[i][2] = compare_audits[i][2].substring(0, j) + compare_audits[i][2].substring(j + 1);
            }
                if (compare_audits[i][3].equals(compare_audits[i][2]))
                    compare_audits[i][4] = "passed";
                else  if(compare_audits[i][4]== null)
                    compare_audits[i][4] = "failed";
                 else {

            }
                 i++;
        }


        System.out.println("Compare:");
        i = 0;
        while (compare_audits[i][1]!=null){
            for(j=0;j<compare_audits[0].length;j++){
                System.out.print(compare_audits[i][j] + "  |  ");
            }
            System.out.println();
            i++;
        }
    }

    public void export_new_audits(String audits) throws IOException {
        File file = new File("D:\\new_audits.inf");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter newfile = new FileWriter(file.getAbsoluteFile());
//        PrintWriter  = new PrintWriter(new File("C:\\new_audits.inf"));
        int i = 0;
//        System.out.println("audits string :" + audits);
        newfile.write(audits.substring(0,16));
        while(comp_audits[i][1]!=null){

            newfile.write(comp_audits[i][0] + " = " + comp_audits[i][1]);
//            newfile.write('\n');
            i++;
        }
        while (!audits.startsWith("[Privilege Rights]")){
            audits = audits.substring(1);
        }
        System.out.println("audits: " + audits);
        newfile.write(audits);
        newfile.close();
    }

    public void create_table_audits(String audits){
        audits = audits.substring(3);
        while (!audits.startsWith("\nMinimum")){
            audits = audits.substring(1);
        }
//        audits = audits.substring(16);

        int i = 0; int j = 0; int count = 0;
        while (count<81){
            if(audits.startsWith("[")){
                int k = 0;
                while (audits.charAt(k)!=']'){
                    k++;
                }
                audits = audits.substring(0,k);
            }
            else{
                String s = "";
                while (audits.length()>0 && !audits.startsWith("=")){
                    s +=audits.charAt(0);
                    audits = audits.substring(1);
                }
                s = s.substring(1);
//        System.out.println("s = " + s);
                comp_audits[i][j] = s;
                j++;
                s = "";
                    audits = audits.substring(3);
                while (!audits.startsWith("\n") && !audits.startsWith("\r") && !audits.startsWith("\r\n")){
//                    if(audits.charAt(0)!=',') {
                        s += audits.charAt(0);
                        audits = audits.substring(1);
//                    }
                }
                if(s.contains(" [Privilege Rights]"))
                    break;
//        System.out.println("s = " + s);
                comp_audits[i][j] = s;
                j=0;
                i++;
            }
            count++;
        }
        i=0;
        while(comp_audits[i][0]!=null){
            comp_audits[i][2] = i + "";
            i++;
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
////////  4 66 131 132 272 305
// C:\Users\Сat\Downloads