package org.bitbucket.treklab.client;

import com.google.gwt.core.client.EntryPoint;
import org.bitbucket.treklab.client.controller.LoginController;

public class Frontend implements EntryPoint, LoginController.LoginHandler {

    @Override
    public void onModuleLoad() {
        new LoginController().login(Frontend.this);
        //new Application().run();
    }

    @Override
    public void onLogin() {
        new Application().run();
    }
}
