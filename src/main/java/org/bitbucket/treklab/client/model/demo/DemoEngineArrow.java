package org.bitbucket.treklab.client.model.demo;

public class DemoEngineArrow {
    private static int COUNTER = 0;

    private int id;
    private String name;

    public DemoEngineArrow() {
        this.id = COUNTER++;
        this.name = "Group #" + this.id;
    }

    public DemoEngineArrow(String name) {
        this.id = COUNTER++;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
