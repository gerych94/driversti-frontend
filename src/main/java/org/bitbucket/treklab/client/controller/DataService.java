package org.bitbucket.treklab.client.controller;

import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.User;

import java.util.List;

public interface DataService {

    User authenticated();

    User login(String login, String password);

    void login(String login, String password, LoginController loginController);

    boolean logout();

    User register(String login, String password);

    void register(String login, String password, LoginController loginController);

    User getUserSession();

    List<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    User removeUser(User user);

    List<Device> getDevices();

    Device addDevice(Device device);

    Device updateDevice(Device device);

    Device removeDevice(Device device);

    void setUser(User user);

    String getUserAuthentication();

    void setUserAuthentication(String userPasswordBase64);
}
