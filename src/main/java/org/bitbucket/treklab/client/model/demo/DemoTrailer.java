package org.bitbucket.treklab.client.model.demo;

public class DemoTrailer {
    private static int COUNTER = 0;

    private int id;
    private String name;

    public DemoTrailer() {
        this.id = COUNTER++;
        this.name = "Group #" + this.id;
    }

    public DemoTrailer(String name) {
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
