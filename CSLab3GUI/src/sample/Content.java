package sample;

public class Content {
    private String columnContent = null;
    private String rowContent = null;

    public Content() {
    }

    public Content(String columnContent, String rowContent) {
        this.columnContent = columnContent;
        this.rowContent = rowContent;
    }

    public Content(String s, String s1, String s2) {

    }

    public String getColumnContent() {
        return columnContent;
    }

    public void setColumnContent(String columnContent) {
        this.columnContent = columnContent;
    }

    public String getRowContent() {
        return rowContent;
    }

    public void setRowContent(String rowContent) {
        this.rowContent = rowContent;
    }
}
