package org.bitbucket.treklab.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.GeofenceData;
import org.bitbucket.treklab.client.model.Geofence;
import org.bitbucket.treklab.client.util.LoggerHelper;
import org.discotools.gwt.leaflet.client.layers.ILayer;

public class GeofenceAddDialog extends Composite {

    private static GeoFenceAddDialogUiBinder uiBinder = GWT.create(GeoFenceAddDialogUiBinder.class);
    interface GeoFenceAddDialogUiBinder extends UiBinder<Widget, GeofenceAddDialog> {
    }

    private static final String className = GeofenceAddDialog.class.getSimpleName();
    private Geofence geofence;
    private ListStore<Geofence> geofenceStore;
    private final GeofenceData geofenceData = new GeofenceData();
    private final MapView mapView;

    private final ILayer layer;
    @UiField
    Window window;
    @UiField
    VerticalLayoutContainer container;
    @UiField
    TextField nameField;

    @UiField
    TextArea descriptionArea;

    public GeofenceAddDialog(Geofence geofence,
                             ListStore<Geofence> geofenceStore,
                             MapView mapView,
                             ILayer layer) {
        this.geofence = geofence;
        this.geofenceStore = geofenceStore;
        this.mapView = mapView;
        this.layer = layer;
        uiBinder.createAndBindUi(this);
    }

    public Window getWindow() {
        return window;
    }

    public void show() {
        window.show();
    }

    public void hide() {
        window.hide();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(SelectEvent event) {
        geofence.setName(nameField.getText());
        geofence.setDescription(descriptionArea.getText());
        // TODO: 27.05.2016 userId получать из текущего юзера!!!!!!!!!!!!!!!!!
        geofence.setUserId(1);
        try {
            geofenceData.addGeofence(geofence, new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    // TODO: 06.05.2016 реализовать метод
                    if (200 == response.getStatusCode()) {
                        JsArray<Geofence> geofenceJsArray = JsonUtils.safeEval("[" + response.getText()+ "]");
                        geofence = geofenceJsArray.get(0);
                        layer.getJSObject().setProperty("_leaflet_id", geofence.getId());
                        geofenceStore.add(geofence);
                        LoggerHelper.log(className, layer.getJSObject().getProperty("_leaflet_id") + "");
                    } else {
                        LoggerHelper.log(className, "Response code: " + response.getStatusCode());
                        removeLayer();
                    }
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
        hide();
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(SelectEvent event) {
        removeLayer();
        hide();
    }

    private void removeLayer() {
        mapView.getMap().removeLayer(layer);
    }
}