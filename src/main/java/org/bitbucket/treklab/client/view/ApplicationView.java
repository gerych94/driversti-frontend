package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

public class ApplicationView extends Composite {
    private static ApplicationViewUiBinder uiBinder = GWT.create(ApplicationViewUiBinder.class);
    @UiField(provided = true)
    final ContentPanel navPanel;
    @UiField(provided = true)
    final ContentPanel westPanel;
    @UiField(provided = true)
    final ContentPanel centerPanel;
    @UiField
    BorderLayoutContainer container;
    @UiField
    BorderLayoutContainer.BorderLayoutData northData;
    @UiField
    BorderLayoutContainer.BorderLayoutData westData;
    public ApplicationView(ContentPanel navPanel,
                           final ContentPanel westPanel,
                           final ContentPanel centerPanel) {
        this.navPanel = navPanel;
        this.westPanel = westPanel;
        this.centerPanel = centerPanel;

        initWidget(uiBinder.createAndBindUi(this));

        this.northData.setCollapseMini(true);
        this.northData.setCollapsed(true);
        this.northData.setCollapseHidden(true);
        this.westData.setCollapseMini(true);
        this.westPanel.setAnimationDuration(3000);

        /** Западную панель создаём скрытой. Через 5мс разворачиваемю
         * Это позволяет исправить следующий баг: если изначально западная панель раскрыта,
         * то после скрытия с правой стороны браузера не подгружалась часть карты на ширину до
         * ширины западной панели*/
        /*this.westData.setCollapsed(true);

        Timer t = new Timer() {
            @Override
            public void run() {
                container.expand(Style.LayoutRegion.WEST);
            }
        };
        t.schedule(10);*/
    }

    interface ApplicationViewUiBinder extends UiBinder<Widget, ApplicationView> {
    }
}