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
import org.bitbucket.treklab.client.model.PositionRow;
import org.bitbucket.treklab.client.model.PositionRowProperties;

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
    Grid<PositionRow> rowGrid;
    @UiField(provided = true)
    final ListStore<PositionRow> rowStore;
    @UiField(provided = true)
    final ColumnModel<PositionRow> rowCM;
    @UiField
    GroupingView<PositionRow> rowView;

    public ContentPanel getView() {
        return contentPanel;
    }

    private static final PositionRowProperties prop = GWT.create(PositionRowProperties.class);

    public StateView(ListStore<PositionRow> rowStore) {
        // TODO: 20.04.2016 Этот список инициализировать в контроллере
        this.rowStore = rowStore;

        ColumnConfig<PositionRow, String> colName = new ColumnConfig<>(prop.name(), 100, "Параметр");
        colName.setResizable(true);
        ColumnConfig<PositionRow, String> colValue = new ColumnConfig<>(prop.value(), 100, "Значение");
        colValue.setResizable(true);

        List<ColumnConfig<PositionRow, ?>> configList = new ArrayList<>();
        configList.add(colName);
        configList.add(colValue);

        this.rowCM = new ColumnModel<>(configList);

        ourUiBinder.createAndBindUi(this);

        this.rowView.setAutoExpandColumn(colValue);
        this.rowView.setStripeRows(true);
    }

    public ListStore<PositionRow> getRowStore() {
        return rowStore;
    }

    public ColumnModel<PositionRow> getRowCM() {
        return rowCM;
    }
}