package org.bitbucket.treklab.client.util;

/**
 * Created by driversti on 19.05.2016.
 *
 * @author driversti
 * @version 1.0
 * @since 19.05.2016
 */
public interface Observable {

    void registerObserver(Observer o);

    void removeObserver(Observer o);

    void notifyObservers();
}
