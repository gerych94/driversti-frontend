package org.bitbucket.treklab.client.model.demo;

/**
 * Этот класс используется для создания объектов "Стрелка при остановке автомобиля" - что-то типа такого
 * Необходимо, чтобы Толя разжевал что это и как должно работать
 */
public class DemoStoppedArrow {
    private static int COUNTER = 0;

    private int id;
    private String name;

    public DemoStoppedArrow() {
        this.id = COUNTER++;
        this.name = "Group #" + this.id;
    }

    public DemoStoppedArrow(String name) {
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
