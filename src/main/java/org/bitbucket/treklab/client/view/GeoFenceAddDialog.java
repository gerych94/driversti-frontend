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
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import org.bitbucket.treklab.client.communication.BaseRequestCallback;
import org.bitbucket.treklab.client.communication.GeofenceData;
import org.bitbucket.treklab.client.model.Geofence;

public class GeofenceAddDialog extends Composite {
    interface GeoFenceAddDialogUiBinder extends UiBinder<Widget, GeofenceAddDialog> {
    }

    private static GeoFenceAddDialogUiBinder uiBinder = GWT.create(GeoFenceAddDialogUiBinder.class);

    private static final String className = GeofenceAddDialog.class.getSimpleName();

    private Geofence geofence;
    private ListStore<Geofence> geofenceStore;
    private GeofenceData geofenceData;

    @UiField
    Window window;

    public Window getWindow() {
        return window;
    }

    @UiField
    VerticalLayoutContainer container;
    @UiField
    TextField nameField;
    @UiField
    TextArea descriptionArea;

    public GeofenceAddDialog(Geofence geofence, ListStore<Geofence> geofenceStore) {
        this.geofence = geofence;
        this.geofenceStore = geofenceStore;
        uiBinder.createAndBindUi(this);
    }

    public void show() {
        window.show();
    }

    public void hide() {
        window.hide();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(SelectEvent event) {
        Geofence tempGeofence = (Geofence) Geofence.createObject();
        tempGeofence.setName(nameField.getText());
        tempGeofence.setDescription(descriptionArea.getText());
        tempGeofence.setType(geofence.getType());
        tempGeofence.setCoordinates(geofence.getCoordinates());
        GeofenceData geofenceData = new GeofenceData();
        try {
            geofenceData.addGeofence(tempGeofence, new BaseRequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    // TODO: 06.05.2016 реализовать метод
                    if (200 == response.getStatusCode()) {
                        JsArray<Geofence> geofences = JsonUtils.safeEval(response.getText());
                        geofence = geofences.get(0);
                        geofenceStore.add(geofence);
                    } else {
                        new AlertMessageBox("Error", "Response code: " + response.getStatusCode()).show();
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
        hide();
    }
}