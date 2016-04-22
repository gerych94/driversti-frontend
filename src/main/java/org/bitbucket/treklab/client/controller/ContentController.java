package org.bitbucket.treklab.client.controller;

import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * Родительский интерфейс для всех контроллеров.
 * Умеет возвращать корневой виджет отображения и выполнять запланированное дейстивие при старте
 */
public interface ContentController {
    ContentPanel getView();

    void run();
}
