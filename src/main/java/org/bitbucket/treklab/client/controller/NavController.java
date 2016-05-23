package org.bitbucket.treklab.client.controller;

import com.sencha.gxt.widget.core.client.ContentPanel;
import org.bitbucket.treklab.client.view.NavView;

/**
 * Этот контроллер отвечает за действия северной (навигационной) панели
 */
public class NavController implements ContentController {

    private final NavView navView;

    public NavController() {
        this.navView = new NavView();
    }

    @Override
    public ContentPanel getView() {
        return navView.getView();
    }

    @Override
    public void run() {

    }
}
