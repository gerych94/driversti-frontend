package org.bitbucket.treklab.client.controller;

import com.sencha.gxt.widget.core.client.ContentPanel;
import org.bitbucket.treklab.client.view.ScheduleView;

/**
 * Этот контроллер отвечает за действия южной (история передвижений) панели
 */
public class ScheduleController implements ContentController {

    private final ScheduleView scheduleView;

    @Override
    public ContentPanel getView() {
        return scheduleView.getView();
    }

    @Override
    public void run() {

    }

    public ScheduleController() {
        this.scheduleView = new ScheduleView();
    }
}
