package org.bitbucket.treklab.client.view;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import org.bitbucket.treklab.client.controller.GeofenceController;
import org.bitbucket.treklab.client.controller.StateController;
import org.bitbucket.treklab.client.model.*;
import org.bitbucket.treklab.client.resources.Resources;
import org.bitbucket.treklab.client.state.DeviceFollowHandler;
import org.bitbucket.treklab.client.state.DeviceVisibilityHandler;
import org.bitbucket.treklab.client.util.LoggerHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        void onProperties(Device selectedItem);
        void onRefresh();
        void onRemove(Device selectedItem);
        void onShowHistory(Device selectedItem);
        void onSelected();
        void doubleClicked(Device selectedItem);
        //метод который реагирует при смене чекбокса;
        void deviceCheckBoxActionVisible(Device device);
        void deviceCheckBoxActionFollow(Device device, boolean flag, boolean historyFlag);
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
    StoreFilterField<OverSpeedEvent> eventFilter;
    @UiField
    TextButton refreshEventButton;

    @UiField
    Grid<OverSpeedEvent> eventGrid;
    @UiField(provided = true)
    ListStore<OverSpeedEvent> eventStore;
    @UiField(provided = true)
    ColumnModel<OverSpeedEvent> eventCM;
    @UiField
    GroupingView<OverSpeedEvent> eventView;


    @UiField(provided = true)
    StoreFilterField<Geofence> geofenceFilter;

    @UiField
    Grid<Geofence> geofenceGrid;
    @UiField(provided = true)
    ListStore<Geofence> geofenceStore;
    @UiField(provided = true)
    ColumnModel<Geofence> geofenceCM;
    @UiField
    GroupingView<Geofence> geofenceView;

    private final DeviceHandler deviceHandler;
    private final StateController stateController;
    private final DeviceVisibilityHandler deviceVisibilityHandler;
    private final DeviceFollowHandler deviceFollowHandler;
    private final MapView.GeofenceHandler geofenceHandler;

    private final ColumnConfig<Device, Boolean> colDeviceVisible;
    private final ColumnConfig<Device, Boolean> colDeviceFollow;

    private static final DeviceProperties deviceProp = GWT.create(DeviceProperties.class);
    private static final OverSpeedEventProperties eventProp = GWT.create(OverSpeedEventProperties.class);
    private static final GeofenceProperties geoProp = GWT.create(GeofenceProperties.class);
    private Resources resources = GWT.create(Resources.class);
    private HeaderIconTemplate headerTemplate = GWT.create(HeaderIconTemplate.class);
    private HashMap<Integer, Boolean> flagMap = new HashMap<>();
    private HashMap<Integer, Boolean> historyFlagMap = new HashMap<>();

    // получаем имя класса для Логера
    private static final String className = DeviceView.class.getSimpleName();

    public DeviceView(final DeviceHandler deviceHandler,
                      ListStore<Device> globalDeviceStore,
                      ListStore<OverSpeedEvent> globalEventStore,
                      ListStore<Geofence> globalGeofenceStore,
                      final StateController sController,
                      GeofenceController geofenceController,
                      final DeviceVisibilityHandler deviceVisibilityHandler,
                      final DeviceFollowHandler deviceFollowHandler) {
        this.deviceHandler = deviceHandler;
        this.stateController = sController;
        this.geofenceHandler = geofenceController;
        this.deviceVisibilityHandler = deviceVisibilityHandler;
        this.deviceFollowHandler = deviceFollowHandler;
        this.deviceStore = globalDeviceStore;
        this.deviceStore.setAutoCommit(true);

        this.eventStore = globalEventStore;
        this.eventStore.setAutoCommit(true);

        this.geofenceStore = globalGeofenceStore;
        this.geofenceStore.setAutoCommit(true);

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
                if (!value && deviceFollowHandler.isFollow(device)) {
                    flagMap.put(device.getId(), true);
                    // flag=true;
                    colDeviceFollow.getValueProvider().setValue(device, value);
                    historyFlagMap.put(device.getId(), true);
                    // historyFlag=true;
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
                if (value&&!deviceVisibilityHandler.isVisible(device)) {
                    colDeviceVisible.getValueProvider().setValue(device, value);
                    deviceHandler.deviceCheckBoxActionVisible(device);
                }
                if(historyFlagMap.get(device.getId())==null){
                    historyFlagMap.put(device.getId(),false);
                    flagMap.put(device.getId(),false);
                }else if(historyFlagMap.get(device.getId())){
                    flagMap.put(device.getId(),false);
                }
                if(!value){
                    historyFlagMap.put(device.getId(),false);
                }
                //метод который реагирует при смене чекбокса
                deviceHandler.deviceCheckBoxActionFollow(device,flagMap.get(device.getId()),historyFlagMap.get(device.getId()));
                //            deviceHandler.deviceCheckBoxActionFollow(device,flag,historyFlag);
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
        final MenuItem edit = new MenuItem("Свойства");
        edit.setId("Свойства");
        menu.add(edit);
        final MenuItem history = new MenuItem("Показать историю");
        history.setId("Показать историю");
        menu.add(history);
        final MenuItem remove = new MenuItem("Удалить");
        remove.setId("Удалить");
        menu.add(remove);
        menu.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                LoggerHelper.log(className, "Selected item id: " + event.getSelectedItem().getId());
                switch (event.getSelectedItem().getId()) {
                    case "Свойства":
                        LoggerHelper.log(className, "'Properties' has been choose in menu");
                        deviceHandler.onProperties(deviceGrid.getSelectionModel().getSelectedItem());
                        break;
                    case "Показать историю":
                        LoggerHelper.log(className, "'Show history' has been choose in menu");
                        deviceHandler.onShowHistory(deviceGrid.getSelectionModel().getSelectedItem());
                        break;
                    case "Удалить":
                        LoggerHelper.log(className, "'Remove' has been choose in menu");
                        deviceHandler.onRemove(deviceGrid.getSelectionModel().getSelectedItem());
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

        eventFilter = new StoreFilterField<OverSpeedEvent>() {
            @Override
            protected boolean doSelect(Store<OverSpeedEvent> store, OverSpeedEvent parent, OverSpeedEvent item, String filter) {
                return false;
                /*return filter.trim().isEmpty() ||
                        item.getSpeed().toLowerCase().contains(filter.toLowerCase()) ||
                        item.getMessage().toLowerCase().contains(filter.toLowerCase());*/
            }
        };
        eventFilter.bind(this.eventStore);

        ColumnConfig<OverSpeedEvent, Date> colEventTime = new ColumnConfig<>(eventProp.time(), 60, "Time");
        ColumnConfig<OverSpeedEvent, Integer> colEventDeviceId = new ColumnConfig<>(eventProp.deviceId(), 100, "Object");
        ColumnConfig<OverSpeedEvent, Double> colEventSpeed = new ColumnConfig<>(eventProp.speed(), 60, "OverSpeedEvent");

        List<ColumnConfig<OverSpeedEvent, ?>> eventList = new ArrayList<>();
        eventList.add(colEventTime);
        eventList.add(colEventDeviceId);
        eventList.add(colEventSpeed);

        this.eventCM = new ColumnModel<>(eventList);

        geofenceFilter = new StoreFilterField<Geofence>() {
            @Override
            protected boolean doSelect(Store<Geofence> store, Geofence parent, Geofence item, String filter) {
                return filter.trim().isEmpty() ||
                        item.getName().toLowerCase().contains(filter.toLowerCase());
            }
        };
        geofenceFilter.bind(this.geofenceStore);

        ColumnConfig<Geofence, Boolean> colGeofenceVisible = new ColumnConfig<>(new ValueProvider<Geofence, Boolean>() {

            @Override
            public Boolean getValue(Geofence geofence) {
                return true;
            }

            @Override
            public void setValue(Geofence geofence, Boolean aBoolean) {

            }

            @Override
            public String getPath() {
                return "Visible";
            }
        }, 40, headerTemplate.render(AbstractImagePrototype.create(resources.eye()).getSafeHtml()));
        colGeofenceVisible.setCell(new CheckBoxCell());
        colGeofenceVisible.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        colGeofenceVisible.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        ColumnConfig<Geofence, String> colGeofenceName = new ColumnConfig<>(geoProp.name(), 100, "Geofence");
        colGeofenceName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        colGeofenceName.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        ColumnConfig<Geofence, String> colGeofenceSettings = new ColumnConfig<>(geoProp.empty(), 45, "");
        TextButtonCell geofenceButton = new TextButtonCell();
        geofenceButton.setIcon(resources.cogWheel());
        Menu geofenceMenu = new Menu();
        final MenuItem geofenceEdit = new MenuItem("Свойства");
        geofenceEdit.setId("Свойства");
        geofenceMenu.add(geofenceEdit);
        final MenuItem geofenceRemove = new MenuItem("Удалить");
        geofenceRemove.setId("Удалить");
        geofenceMenu.add(geofenceRemove);
        geofenceMenu.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                switch (event.getSelectedItem().getId()) {
                    case "Свойства":
                        // TODO: 12.05.2016 реализовать метод
                        Info.display("Ошибка", "Эта фича на стадии разработки. Наслаждайтесь другими ;)");
                        break;
                    case "Удалить":
                        LoggerHelper.log(className, "'Remove' has been choose in menu");
                        geofenceHandler.onRemove(geofenceGrid.getSelectionModel().getSelectedItem());
                        break;
                }
            }
        });
        geofenceButton.setMenu(geofenceMenu);
        geofenceButton.setArrowAlign(ButtonCell.ButtonArrowAlign.RIGHT);
        colGeofenceSettings.setCell(geofenceButton);
        colGeofenceSettings.setResizable(false);
        colGeofenceSettings.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        colGeofenceSettings.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        List<ColumnConfig<Geofence, ?>> geofenceList = new ArrayList<>();
        geofenceList.add(colGeofenceVisible);
        geofenceList.add(colGeofenceName);
        geofenceList.add(colGeofenceSettings);

        this.geofenceCM = new ColumnModel<>(geofenceList);

        ourUiBinder.createAndBindUi(this);

        deviceView.setAutoExpandColumn(colDeviceName);
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

        eventView.setAutoExpandColumn(colEventSpeed);
        eventView.setStripeRows(true);

        geofenceView.setAutoExpandColumn(colGeofenceName);
        geofenceView.setStripeRows(true);
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