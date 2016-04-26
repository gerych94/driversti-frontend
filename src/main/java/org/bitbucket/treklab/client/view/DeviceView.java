package org.bitbucket.treklab.client.view;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.PositionData;
import org.bitbucket.treklab.client.controller.StateController;
import org.bitbucket.treklab.client.model.*;
import org.bitbucket.treklab.client.resources.Resources;
import org.bitbucket.treklab.client.state.DeviceFollowHandler;
import org.bitbucket.treklab.client.state.DeviceVisibilityHandler;
import org.bitbucket.treklab.client.util.LoggerHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeviceView {
    interface DeviceViewUiBinder extends UiBinder<Widget, DeviceView> {

    }

    private static DeviceViewUiBinder ourUiBinder = GWT.create(DeviceViewUiBinder.class);

    /**
     * методы этого интерфейса выполняются по нажатию соответствующих кнопок на панели управления.
     * его реализует DeviceController
     */
    public interface DeviceHandler {
        void onAdd();

        void onUpdate(Device selectedItem);

        void onRefresh();

        void onRemove(Device selectedItem);

        void onShowHistory(Device selectedItem);

        void onSelected();

        void doubleClicked(Device selectedItem);

        //метод который реагирует при смене чекбокса;
        void deviceCheckBoxActionVisible(Device device);

        void deviceCheckBoxActionFollow(Device device);
    }

    @UiField
    ContentPanel contentPanel;

    public ContentPanel getView() {
        return contentPanel;
    }

    @UiField(provided = true)
    TabPanel tabPanel;

    @UiField(provided = true)
    StoreFilterField<Device> deviceFilter;
    @UiField
    TextButton refreshDevicesButton;
    @UiField
    TextButton addDeviceButton;

    @UiField
    Grid<Device> deviceGrid;
    @UiField(provided = true)
    ListStore<Device> deviceStore;
    @UiField(provided = true)
    ColumnModel<Device> deviceCM;
    @UiField
    GroupingView<Device> deviceView;


    @UiField(provided = true)
    StoreFilterField<Event> eventFilter;
    @UiField
    TextButton refreshEventButton;

    @UiField
    Grid<Event> eventGrid;
    @UiField(provided = true)
    ListStore<Event> eventStore;
    @UiField(provided = true)
    ColumnModel<Event> eventCM;
    @UiField
    GroupingView<Event> eventView;

    private final DeviceHandler deviceHandler;
    private final StateController stateController;
    private final DeviceVisibilityHandler deviceVisibilityHandler;
    private final DeviceFollowHandler deviceFollowHandler;

    final ColumnConfig<Device, Boolean> colDeviceVisible;
    final ColumnConfig<Device, Boolean> colDeviceFollow;

    private static final DeviceProperties deviceProp = GWT.create(DeviceProperties.class);
    private static final EventProperties eventProp = GWT.create(EventProperties.class);
    Resources resources = GWT.create(Resources.class);
    HeaderIconTemplate headerTemplate = GWT.create(HeaderIconTemplate.class);

    // получаем имя класса для Логера
    private static final String className = DeviceView.class.getSimpleName();

    public DeviceView(final DeviceHandler deviceHandler,
                      ListStore<Device> globalDeviceStore,
                      ListStore<Event> globalEventStore,
                      final StateController sController,
                      final DeviceVisibilityHandler deviceVisibilityHandler,
                      final DeviceFollowHandler deviceFollowHandler) {
        this.deviceHandler = deviceHandler;
        this.stateController = sController;
        this.deviceVisibilityHandler = deviceVisibilityHandler;
        this.deviceFollowHandler = deviceFollowHandler;
        this.deviceStore = globalDeviceStore;
        this.deviceStore.setAutoCommit(true);

        this.eventStore = globalEventStore;
        this.eventStore.setAutoCommit(true);

        tabPanel = new TabPanel();

        deviceFilter = new StoreFilterField<Device>() {
            @Override
            protected boolean doSelect(Store<Device> store, Device parent, Device item, String filter) {
                return filter.trim().isEmpty() ||
                        item.getName().toLowerCase().contains(filter.toLowerCase());
            }
        };
        deviceFilter.bind(this.deviceStore);

        /** Если пользователь воспользовался фильтром, таблица с устройствами теряет выбранное стройство.
         *  При этом в StateView дополниельная информация об устройстве продолжала отображаться.
         *  Этот хэндлер очищает таблицу в StateView, таки образом решая проблему  */
        deviceFilter.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                stateController.getStateView().getRowStore().clear();
            }
        });

        // столбец для выбора отображаемых устройств

        colDeviceVisible = new ColumnConfig<>(new ValueProvider<Device, Boolean>() {
            @Override
            public Boolean getValue(Device device) {
                return deviceVisibilityHandler.isVisible(device);
            }

            @Override
            public void setValue(Device device, Boolean value) {
                deviceVisibilityHandler.setVisible(device, value);
                //если чекбокс visible отключаем то отключаем и follow
                if (!value) {
                    colDeviceFollow.getValueProvider().setValue(device, value);
                }
                //метод который реагирует при смене чекбокса
                deviceHandler.deviceCheckBoxActionVisible(device);

            }

            @Override
            public String getPath() {
                return "visible";
            }
        }, 40, headerTemplate.render(AbstractImagePrototype.create(resources.eye()).getSafeHtml()));
        colDeviceVisible.setCell(new CheckBoxCell());
        colDeviceVisible.setFixed(true);
        colDeviceVisible.setResizable(false);
        colDeviceVisible.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        colDeviceVisible.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        // столбец для выбора устройств, которые будут отслеживаться на карте
        // TODO: 3/20/16 реализовать логику при выборе чекбокса
        colDeviceFollow = new ColumnConfig<>(new ValueProvider<Device, Boolean>() {
            @Override
            public Boolean getValue(Device device) {
                return deviceFollowHandler.isFollow(device);
            }

            @Override
            public void setValue(Device device, Boolean value) {
                deviceFollowHandler.setFollow(device, value);
                //если чекбокс follow true тогда включаем и чекбокс visible
                if (value) {
                    colDeviceVisible.getValueProvider().setValue(device, value);
                }
                //метод который реагирует при смене чекбокса
                deviceHandler.deviceCheckBoxActionFollow(device);
            }

            @Override
            public String getPath() {
                return "follow";
            }
        }, 40, headerTemplate.render(AbstractImagePrototype.create(resources.follow()).getSafeHtml()));
        colDeviceFollow.setCell(new CheckBoxCell());
        colDeviceFollow.setFixed(true);
        colDeviceFollow.setResizable(false);
        colDeviceFollow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        colDeviceFollow.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        //ColumnConfig<Device, String> colDeviceName = new ColumnConfig<>(deviceProp.name(), 100, "Name");
        // этот метод в колонку "Имя" добавляем имя устройства и дату последнего обновления в две строки
        ColumnConfig<Device, Device> colDeviceName = new ColumnConfig<>(deviceProp.device(), 100, "Name");
        colDeviceName.setCell(new AbstractCell<Device>() {
            @Override
            public void render(Context context, Device device, SafeHtmlBuilder sb) {
                sb.appendEscapedLines(device.getName());
                sb.appendEscapedLines("\n");
                if (device.getLastUpdate() != null) {
                    sb.appendEscapedLines(device.getDateTime());
                } else {
                    sb.appendEscapedLines("null");
                }
            }
        });
        colDeviceName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        colDeviceName.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        ColumnConfig<Device, ImageResource> colDeviceStatus = new ColumnConfig<>(deviceProp.statusImage(), 30, headerTemplate.render(AbstractImagePrototype.create(resources.wifi()).getSafeHtml()));
        colDeviceStatus.setCell(new ImageResourceCell());
        colDeviceStatus.setResizable(false);
        colDeviceStatus.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        colDeviceStatus.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        ColumnConfig<Device, String> colDeviceSettings = new ColumnConfig<>(deviceProp.empty(), 45, "");
        TextButtonCell button = new TextButtonCell();
        button.setIcon(resources.cogWheel());
        Menu menu = new Menu();
        final MenuItem edit = new MenuItem("Редактировать");
        edit.setId("Редактировать");
        menu.add(edit);
        final MenuItem history = new MenuItem("Показать историю");
        history.setId("Показать историю");
        menu.add(history);
        final MenuItem remove = new MenuItem("Удалить");
        remove.setId("Удалить");
        menu.add(remove);
        final MenuItem showMaxSpeed = new MenuItem("Show MAX Speed");
        showMaxSpeed.setId("Show MAX Speed");
        menu.add(showMaxSpeed);
        final MenuItem showAverageSpeed = new MenuItem("Show average Speed");
        showAverageSpeed.setId("Show average Speed");
        menu.add(showAverageSpeed);
        menu.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                LoggerHelper.log(className, "Selected item id: " + event.getSelectedItem().getId());
                switch (event.getSelectedItem().getId()) {
                    case "Редактировать":
                        LoggerHelper.log(className, "'Edit' has been choose in menu");
                        deviceHandler.onUpdate(deviceGrid.getSelectionModel().getSelectedItem());
                        break;
                    case "Показать историю":
                        LoggerHelper.log(className, "'Show history' has been choose in menu");
                        deviceHandler.onShowHistory(deviceGrid.getSelectionModel().getSelectedItem());
                        break;
                    case "Удалить":
                        LoggerHelper.log(className, "'Remove' has been choose in menu");
                        deviceHandler.onRemove(deviceGrid.getSelectionModel().getSelectedItem());
                        break;
                    case "Show MAX Speed":
                        LoggerHelper.log(className, "'Show MAX Speed' has been choose in menu");
                        showMaxSpeed();
                        break;
                    case "Show average Speed":
                        LoggerHelper.log(className, "'Show average Speed' has been choose in menu");
                        showAverageSpeed();
                        break;
                }
            }
        });
        button.setMenu(menu);
        button.setArrowAlign(ButtonCell.ButtonArrowAlign.RIGHT);
        colDeviceSettings.setCell(button);
        colDeviceSettings.setResizable(true);
        colDeviceSettings.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        colDeviceSettings.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        List<ColumnConfig<Device, ?>> deviceList = new ArrayList<>();
        deviceList.add(colDeviceVisible);
        deviceList.add(colDeviceFollow);
        deviceList.add(colDeviceName);
        deviceList.add(colDeviceStatus);
        deviceList.add(colDeviceSettings);

        this.deviceCM = new ColumnModel<>(deviceList);

        eventFilter = new StoreFilterField<Event>() {
            @Override
            protected boolean doSelect(Store<Event> store, Event parent, Event item, String filter) {
                return filter.trim().isEmpty() ||
                        item.getDeviceName().toLowerCase().contains(filter.toLowerCase()) ||
                        item.getMessage().toLowerCase().contains(filter.toLowerCase());
            }
        };
        eventFilter.bind(this.eventStore);

        ColumnConfig<Event, String> colEventTime = new ColumnConfig<>(eventProp.time(), 60, "Time");
        ColumnConfig<Event, String> colEventDeviceName = new ColumnConfig<>(eventProp.deviceName(), 100, "Object");
        ColumnConfig<Event, String> colEventMessage = new ColumnConfig<>(eventProp.message(), 60, "Event");

        List<ColumnConfig<Event, ?>> eventList = new ArrayList<>();
        eventList.add(colEventTime);
        eventList.add(colEventDeviceName);
        eventList.add(colEventMessage);

        this.eventCM = new ColumnModel<>(eventList);

        ourUiBinder.createAndBindUi(this);

        //this.removeDeviceButton.setEnabled(false);

        deviceView.setAutoExpandColumn(colDeviceName);
        //deviceView.setAutoFill(true);
        deviceView.setStripeRows(true);

        deviceGrid.addCellClickHandler(new CellClickEvent.CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                deviceHandler.onSelected();
            }
        });
        deviceGrid.addCellDoubleClickHandler(new CellDoubleClickEvent.CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                //removeDeviceButton.setEnabled(true);
                deviceHandler.doubleClicked(deviceGrid.getSelectionModel().getSelectedItem());
            }
        });

        eventView.setAutoExpandColumn(colEventMessage);
        eventView.setStripeRows(true);
    }

    private void showMaxSpeed() {
        final PositionData positionData = new PositionData();
        try {
            positionData.getPositions(deviceGrid.getSelectionModel().getSelectedItem(),
                    new Date(116, 2, 1),
                    new Date(),
                    new BaseRequestCallback() {
                        @Override
                        public void onResponseReceived(Request request, Response response) {
                            JsArray<Position> positionJsArray = JsonUtils.safeEval(response.getText());
                            double maxSpeed = 0;
                            for (int i = 0; i < positionJsArray.length(); i++) {
                                if (maxSpeed < positionJsArray.get(i).getSpeed())
                                    maxSpeed = positionJsArray.get(i).getSpeed();
                            }
                            new AlertMessageBox("Info", "Max speed: " + maxSpeed).show();
                        }
                    });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private void showAverageSpeed() {
        final PositionData positionData = new PositionData();
        try {
            positionData.getPositions(deviceGrid.getSelectionModel().getSelectedItem(),
                    new Date(116, 2, 1),
                    new Date(),
                    new BaseRequestCallback() {
                        @Override
                        public void onResponseReceived(Request request, Response response) {
                            JsArray<Position> positionJsArray = JsonUtils.safeEval(response.getText());
                            double speed = 0;
                            int notZeroValue = 0;
                            for (int i = 0; i < positionJsArray.length(); i++) {
                                if (positionJsArray.get(i).getSpeed() != 0) {
                                    speed += positionJsArray.get(i).getSpeed();
                                    notZeroValue++;
                                }
                            }
                            new AlertMessageBox("Info", "Average speed: " + speed / notZeroValue).show();
                        }
                    });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @UiHandler("tabPanel")
    public void onTabSelected(SelectionEvent<Widget> event) {
        stateController.onTabSelected(event);
        deviceGrid.getSelectionModel().deselectAll();
    }

    @UiHandler("refreshDevicesButton")
    public void onRefreshDevicesButtonClicked(SelectEvent event) {
        deviceHandler.onRefresh();
    }

    /**
     * обработчик кнопки добавления нового устройства
     *
     * @param event
     */
    @UiHandler("addDeviceButton")
    public void onAddDeviceButtonClicked(SelectEvent event) {
        deviceHandler.onAdd();
    }

    public Grid<Device> getDeviceGrid() {
        return deviceGrid;
    }

    public DeviceVisibilityHandler getDeviceVisibilityHandler() {
        return deviceVisibilityHandler;
    }

    public DeviceFollowHandler getDeviceFollowHandler() {
        return deviceFollowHandler;
    }

    interface HeaderIconTemplate extends XTemplates {
        @XTemplate("<div style=\"text-align:center;\">{img}</div>")
        SafeHtml render(SafeHtml img);
    }
}