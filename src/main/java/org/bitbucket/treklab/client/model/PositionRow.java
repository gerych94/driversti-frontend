package org.bitbucket.treklab.client.model;

public class PositionRow {

    private static int COUNTER = 0;

    private int id;
    private String name;
    private String value;

    public PositionRow() {
        this.id = COUNTER++;
    }

    public PositionRow(int id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
