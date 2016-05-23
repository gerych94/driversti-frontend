package org.bitbucket.treklab.client.controller;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.ServerData;
import org.bitbucket.treklab.client.communication.SessionData;
import org.bitbucket.treklab.client.communication.UserData;
import org.bitbucket.treklab.client.model.User;

public class UserController extends ServerData {

    private final UserData userData = new UserData();
    private final SessionData sessionData = new SessionData();

    private LoginController loginController;

    public void login(String login, final String password, LoginController loginController) {

        this.loginController = loginController;

        try {
            sessionData.setSession(login, password, new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    //new AlertMessageBox("Запрос получения сессии пользователя sessionData.setSession", response.getStatusCode() + "").show();
                    if (200 == response.getStatusCode()) {
                        //new AlertMessageBox("Список пользователей", response.getText()).show();
                        User userSession = JsonUtils.safeEval(response.getText());
                        userSession.setPassword(password);
                        onLogin(userSession);

                    } else {
                        new AlertMessageBox("Ошибка авторизации", "Сессия пользователя не получена. Код ошибки: " + response.getStatusCode() + "").show();
                        onLogin(null);
                    }
                }
            });
//            userData.getUsers(new BaseRequestCallback() {
//                @Override
//                public void onResponseReceived(Request request, Response response) {
//                    new AlertMessageBox("Запрос список пользователей", response.getStatusCode() + "").show();
//                    if (200 == response.getStatusCode()) {
//                        new AlertMessageBox("Список пользователей", response.getText()).show();
//                        JsArray<User> data = JsonUtils.safeEval(response.getText());
//                        for (int i = 0; i < data.length(); i++) {
//                            User curUser = data.get(i);
//                            if (curUser.getName().equals(loginChek.trim()) && curUser.getPassword().equals(passChek.trim())) {
//                                chekUser[0] = true;
//                                onLogin(curUser);
//                                break;
//                            }
//                        }
//
//                        if (!chekUser[0]) {
//                            new AlertMessageBox("Ошибка авторизации", "Имя пользователя или пароль введены неверно").show();
//                            onLogin(null);
//                        }
//
//                    } else {
//                        new AlertMessageBox("Ошибка авторизации: Список пользователей не получен", response.getStatusCode() + "").show();
//                        onLogin(null);
//                    }
//                }
//            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    public void register(String login, String password, LoginController loginController) {

        this.loginController = loginController;

        //User user = получаем объект из БД - нужно реализовать регистрацию пользователя в Базе Данных
        final User newUser = (User) User.createObject();
        newUser.setName(login);
        newUser.setPassword(password);
        //newUser.setId(22);
        newUser.setLanguage("en");

        final User[] registerUser = new User[1];

        try {
            userData.addUser(newUser, new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        //new AlertMessageBox("add User", response.getStatusCode() + "").show();
                        registerUser[0] = JsonUtils.safeEval(response.getText());
                        onRegister(registerUser[0]);
                    } else {
                        new AlertMessageBox("Ошибка: User не добавлен", response.getStatusCode() + "").show();
                        registerUser[0] = (User) User.createObject();
                        onRegister(newUser);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getSessionUser() {

        try {
            sessionData.getSession(new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        //new AlertMessageBox("getSessionUser() ", response.getStatusCode() + "").show();
                        User sUser = (User) JsonUtils.safeEval(response.getText());
                    } else {
                        new AlertMessageBox("Ошибка: getSessionUser()", response.getStatusCode() + "").show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onLogin(User user) {
        loginController.onLogin(user);
    }

    private void onRegister(User user) {
        loginController.onRegister(user);
    }

    public interface UserHandler {
        void onRegister(User user);
    }

}
