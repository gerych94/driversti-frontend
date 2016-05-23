package org.bitbucket.treklab.client.util;

import java.util.ArrayList;

/**
 * Created by driversti on 18.05.2016.
 *
 * @author driversti
 * @version 1.0
 * @since 18.05.2016
 */
public class ServerDataHolder implements Observable {

    private static final String className = ServerDataHolder.class.getSimpleName();

    private volatile static ServerDataHolder instance;
    private ArrayList<Observer> observers;
    private String key;
    private String value;

    private ServerDataHolder() {
        this.observers = new ArrayList<>();
    }

    public static ServerDataHolder getInstance() {
        if (instance == null) {
            synchronized (ServerDataHolder.className) {
                if (instance == null) {
                    instance = new ServerDataHolder();
                }
            }
        }
        return instance;
    }

    /**
     * Этот метод парсит полученную от сервера по сокетах строку (по своей сути является Map<String, String>)
     * на ключ и значение. В зависимости от ключа, заполняем данными поля device и positions
     *
     * @param data - данные из сервера
     */
    public void parse(String data) {
        // разделяем ключ и значение
        String[] array = data.split(":", 2);
        String tempKey = array[0];
        key = tempKey.replaceAll("\\W", ""); // в ключе оставляем только буквенное значение, удаляя лишние знаки
        array[0] = key;
        String tempValue = array[1];
        value = tempValue.substring(0, tempValue.length() - 1); // в значении удаляем лишнюю закрывающую фигурную скобку
        array[1] = value;

        notifyObservers();
        LoggerHelper.log(className, "Receive data: " + key);
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        int i = observers.indexOf(o);
        if (i >= 0) {
            observers.remove(i);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(key, value);
        }
    }

}
