package org.bitbucket.treklab.client.model.demo;

public class DemoGroup {

    private static int COUNTER = 0;

    private int id;
    private String name;

    public DemoGroup() {
        this.id = COUNTER++;
        this.name = "Group #" + this.id;
    }

    public DemoGroup(String name) {
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
