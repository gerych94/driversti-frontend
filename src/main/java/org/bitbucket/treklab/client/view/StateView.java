package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import org.bitbucket.treklab.client.model.InfoRow;
import org.bitbucket.treklab.client.model.InfoRowProperties;

import java.util.ArrayList;
import java.util.List;

public class StateView {
    interface StateViewUiBinder extends UiBinder<Widget, StateView> {
    }

    private static StateViewUiBinder ourUiBinder = GWT.create(StateViewUiBinder.class);

    public interface StateHandler {
        void onTabSelected(SelectionEvent<Widget> event);
    }

    @UiField
    ContentPanel contentPanel;

    @UiField
    Grid<InfoRow> rowGrid;
    @UiField(provided = true)
    final ListStore<InfoRow> rowStore;
    @UiField(provided = true)
    final ColumnModel<InfoRow> rowCM;
    @UiField
    GroupingView<InfoRow> rowView;

    public ContentPanel getView() {
        return contentPanel;
    }

    private static final InfoRowProperties prop = GWT.create(InfoRowProperties.class);

    public StateView(ListStore<InfoRow> rowStore) {
        // TODO: 20.04.2016 Этот список инициализировать в контроллере
        this.rowStore = rowStore;

        ColumnConfig<InfoRow, String> colName = new ColumnConfig<>(prop.name(), 100, "Параметр");
        colName.setResizable(true);
        ColumnConfig<InfoRow, String> colValue = new ColumnConfig<>(prop.value(), 100, "Значение");
        colValue.setResizable(true);

        List<ColumnConfig<InfoRow, ?>> configList = new ArrayList<>();
        configList.add(colName);
        configList.add(colValue);

        this.rowCM = new ColumnModel<>(configList);

        ourUiBinder.createAndBindUi(this);

        this.rowView.setAutoExpandColumn(colValue);
        this.rowView.setStripeRows(true);
    }

    public ListStore<InfoRow> getRowStore() {
        return rowStore;
    }

    public ColumnModel<InfoRow> getRowCM() {
        return rowCM;
    }
}