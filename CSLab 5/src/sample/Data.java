package sample;

import java.util.stream.Stream;

public class Data {
    private String[][] data = new String[363][30];
    private int column = 1;
    private int line = 1;

    private String[] keyword = {"type", "description", "info", "Note", "Enabled", "Disabled",
            "Default on workstations and servers", "Default on domain controllers", "Default",
            "Warning", "Policy Path", "Policy Setting Name", "Name", "see_also", "value_type",
            "value_data", "password_policy", "right_type", "reg_key", "reg_item", "reg_option", "lockout_policy", "audit_policy_subcategory", "reference"};

    public String[][] getData(String reader) {

        String string = reader;
        String name = null;
        while (string.length() != 1) {
            if (string.startsWith("<name>")) {
                int n = 0;
                string = string.substring(6);
                while (string.charAt(n) != '<') {
                    n++;
                }
                name = string.substring(0, n);
                break;
            } else {
                string = string.substring(1);
            }
        }
        name += " ";
        while (string.length() != 1 ) {
            if (string.startsWith("<version>")) {
                int n = 0;
                string = string.substring(9);
                while (string.charAt(n) != '<') {
                    n++;
                }
                name += string.substring(0, n);
                break;
            } else {
                string = string.substring(1);
            }
        }

        for(int i = 0; i < keyword.length; i++){
            data[0][i+1] = keyword[i];
        }
        while (!string.startsWith("</report>")){
            string = string.substring(1);
        }
        int count = 0;
        while (string.length()>1) {
            if(string.startsWith("type:\"WARNING\"")){
                break;
            }
            while (!Stream.of(keyword).anyMatch(string::startsWith)) {
                string = string.substring(1);
            }
            string = add_data(string);
            count++;
        }

        int i = 0;
        data[0][0] = "id";
        while(data[i][1]!=null){
            if(i!=0)
                data[i][0] = "" + i;
            for(int j= 0; j <= keyword.length; j++){
                System.out.print(data[i][j] + " | ");
            }
            System.out.println();
            System.out.println("___________________________________________________________________________________________________________________________________________________");
            i++;
        }

        return data;
    }

    public String add_data(String string){
        column = 1;
        while (!string.startsWith(data[0][column]) ){
            column++;
        }
        int i = 0;
        while (string.charAt(i)!=':')
            i++;
        string = string.substring(i+2);
        String s = "";
        while (string.length()>1 && !Stream.of(keyword).anyMatch(string::startsWith)){
            if (string.charAt(0) != '\"' && string.charAt(0) != '\n' && string.charAt(0) != '\r' && (string.length() <= 1 || string.charAt(0) != ' ' || string.charAt(1) != ' ')) {
                s += string.charAt(0);
            }
            else if(string.charAt(0) == '\n' && string.charAt(0) == '\r'){
                s+= ". ";
            }
            string = string.substring(1);
        }
        if(string.startsWith("Notes")){
            string = string.substring(6);
            while (string.length()>1 && !Stream.of(keyword).anyMatch(string::startsWith)){
                if (string.charAt(0) != '\n' && string.charAt(0) != '\r' && (string.length() <= 1 || string.charAt(0) != ' ' || string.charAt(1) != ' ')) {
                    s += string.charAt(0);
                }
                if(string.charAt(0) == '\n' || string.charAt(0) == '\r'){
                    s+= ". ";
                }
                string = string.substring(1);
            }
        }
        if(s.contains("</custom_item>")){
            int count = 0;
            while (s.charAt(count)!='<')
                count++;
            s = s.substring(0,count);
            if(data[line][column]==null)
                data[line][column] = s;
            else{
                data[line][column] += " " + s;
            }
            line++;
        }
        else{
            if(data[line][column]==null)
                data[line][column] = s;
            else{
                data[line][column] += " " + s;
            }
        }
        return string;
    }
}