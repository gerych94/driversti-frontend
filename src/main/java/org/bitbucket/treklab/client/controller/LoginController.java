package org.bitbucket.treklab.client.controller;

import com.google.gwt.user.client.Cookies;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import org.bitbucket.treklab.client.Application;
import org.bitbucket.treklab.client.communication.Base64;
import org.bitbucket.treklab.client.model.User;
import org.bitbucket.treklab.client.view.LoginDialog;

public class LoginController implements LoginDialog.LoginHandler, DataServiceController.UserHandler {

    private LoginHandler loginHandler;
    private LoginDialog loginDialog;

    private boolean validate(String login, String password) {
        if (login == null || login.isEmpty()) {
            new AlertMessageBox("Ошибка авторизации", "Имя пользователя не заполено").show();
            return false;
        } else if (password == null || password.isEmpty()) {
            new AlertMessageBox("Ошибка авторизации", "Пароль не заполен").show();
            return false;
        }
        return true;
    }

    public void login(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;

        User user = Application.getDataServiceController().authenticated();

        if (user != null) {
            this.loginHandler.onLogin();
        } else {
            loginDialog = new LoginDialog(LoginController.this);
            loginDialog.show();
        }
    }

    @Override
    public void onLogin(String login, String password) {
        if (validate(login, password)) {
            //User user = Application.getDataServiceController().getUserSession();
            Application.getDataServiceController().login(login, password, LoginController.this);
        } else {
            new AlertMessageBox("Ошибка логгирования", "Неверное значение логина/пароля");
        }
    }

    @Override
    public void onLogin(User user) {
        if (loginHandler != null) {
            Application.getDataServiceController().setUser(user);
            String userPassword = user.getEmail() + ":" + user.getPassword();
            Cookies.setCookie("loginIDTrekLab", user.getName());
            Cookies.setCookie("passwordTrekLab", user.getPassword());
            Cookies.setCookie("emailTrekLab", user.getEmail());
            String userPasswordBase64 = new Base64().encode(userPassword.getBytes());
            Application.getDataServiceController().setUserAuthentication(userPasswordBase64);
            loginDialog.hide();
            loginHandler.onLogin();
        } else {
            new AlertMessageBox("Ошибка авторизации", "Неверное значение логина/пароля");
        }
    }

    @Override
    public void onRegister(String login, String password) {

        if (validate(login, password)) {
            //Заглушка на метод регистрации
            //Application.getDataServiceController().register(login, password, LoginController.this);
        } else {
            new AlertMessageBox("Ошибка авторизации", "Некорректное значение логина/пароля");
        }

    }

    @Override
    public void onRegister(User user) {
        Application.getDataServiceController().setUser(user);
        loginDialog.hide();
        loginHandler.onLogin();
    }

    public interface LoginHandler {
        void onLogin();
    }

}