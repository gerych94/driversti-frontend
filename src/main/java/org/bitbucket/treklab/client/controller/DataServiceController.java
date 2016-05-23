package org.bitbucket.treklab.client.controller;

import org.bitbucket.treklab.client.model.Device;
import org.bitbucket.treklab.client.model.User;

import java.util.List;

public final class DataServiceController implements DataService {

    private static volatile DataServiceController dataServiceInstance;

    //    private LoginController loginController;
    private UserController userController;
    private User user;
    private String userAuthentication = null;

    private DataServiceController() {
        userController = new UserController();
    }

    public static DataServiceController getInstance() {
        // скопировали ссылку на инстанс, чтобы гарантированно получить ее в наш стек
        // каждый поток имеет свой стек
        // еще тут могут быть несколько потоков
        // и в самом начале кто-то из них получит null,
        // а кто-то уже ссылку на реальный экземпляр
        DataServiceController local = dataServiceInstance;
        // потому мы проверим, а не null ли экземпляр
        // что не гарантирует, что экземпляр не создан уже
        // оно лишь говорит, что в момоент получения
        // ссылки dataServiceInstance в local оно таки было null
        // может и сейчас так, а может и нет :)
        if (local == null) {
            // критическая секция только на записи
            // тут выстраиваются в очередь все потоки, которые определили,
            // что на момент копирования ссылки из instance в local она была null
            // то есть они соревнуются за первенство - кто же всетаки создаст экземпляр
            synchronized (DataServiceController.class) {
                // тут всегда только один поток - это критическая секция
                // лочимся на объекте класса dataServiceInstance, а потому точно для всех потоков

                // копируем еще раз ссылку, потому что может быть пока мы стояли в очереди
                // перед критической секцией, другой поток уже успел проинициализировать
                // экземпляр и он уже не null
                local = dataServiceInstance;
                if (local == null) {
                    // если мы первые - инстанциируем
                    // присваивая сразу и полю и локальной переменной ссылку на экземпляр
                    dataServiceInstance = local = new DataServiceController();
                }
            }

        }
        // возвращаем ссылку на экземпляр, тут она точно будет не null
        // потому что либо мы сами ее инициализировали - если первые
        return local;
    }

    @Override
    public User authenticated() {
        return user == null ? null : getUser();
    }

    @Override
    public void login(String login, String password, LoginController loginController) {
        userController.login(login, password, loginController);
    }

    @Override
    public User login(String login, String password) {
        return getUser();
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public User register(String login, String password) {
        return getUser();
    }

    public void register(String login, String password, LoginController loginController) {
        userController.register(login, password, loginController);
    }

    @Override
    public User getUserSession() {
        userController.getSessionUser();
        //setUser(sessionUser);
        return getUser();
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User removeUser(User user) {
        return null;
    }

    @Override
    public List<Device> getDevices() {
        return null;
    }

    @Override
    public Device addDevice(Device device) {
        return null;
    }

    @Override
    public Device updateDevice(Device device) {
        return null;
    }

    @Override
    public Device removeDevice(Device device) {
        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getUserAuthentication() {
        return this.userAuthentication;
    }

    @Override
    public void setUserAuthentication(String userPasswordBase64) {
        this.userAuthentication = userPasswordBase64;
    }

    public interface UserHandler {
        void onRegister(User user);

        void onLogin(User user);
    }


}
