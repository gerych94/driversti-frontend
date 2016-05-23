package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;

public class LoginDialog extends Composite {
    private static LoginDialogUiBinder uiBinder = GWT.create(LoginDialogUiBinder.class);
    @UiField
    Window window;
    @UiField
    TextField login;
    @UiField
    PasswordField password;
    @UiField
    TextButton registerButton;
    private LoginHandler loginHandler;
    public LoginDialog(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;

        uiBinder.createAndBindUi(this);
    }

    public void show() {
        window.show();
    }

    public void hide() {
        window.hide();
    }

    private void login() {
        loginHandler.onLogin(login.getText(), password.getText());
    }

    @UiHandler("loginButton")
    public void onLoginClicked(SelectEvent event) {
        login();
    }

    @UiHandler("registerButton")
    public void onRegisterClicked(SelectEvent event) {
        loginHandler.onRegister(login.getText(), password.getText());
    }

    @UiHandler({"login", "password"})
    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            login();
        }
    }

    interface LoginDialogUiBinder extends UiBinder<Window, LoginDialog> {
    }

    public interface LoginHandler {
        void onLogin(String login, String password);

        void onRegister(String login, String password);
    }
}