package com.thingtrack.dontpush.openlayers.test;

import java.util.Collection;

import org.vaadin.vol.Bounds;
import org.vaadin.vol.GoogleStreetMapLayer;
import org.vaadin.vol.OpenLayersMap;
import org.vaadin.vol.OpenStreetMapLayer;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Window;

public class MyVaadinApplication extends Application {

	@Override
	public void init() {

		TestWindow testWindow = new TestWindow();
		setMainWindow(testWindow);

		final OpenLayersMap map = new OpenLayersMap();
		OpenStreetMapLayer osm = new OpenStreetMapLayer();
		GoogleStreetMapLayer googleStreets = new GoogleStreetMapLayer();
		map.addLayer(osm);
		map.addLayer(googleStreets);
		map.setCenter(22.30083, 60.452541);
//		map.zoomToExtent(new Bounds(null));
		testWindow.addComponent(map);
	}

	@Override
	public Window getWindow(String name) {
		Window w = super.getWindow(name);
		if (w == null) {
			w = new TestWindow();
			addWindow(w);
			w.open(new ExternalResource(w.getURL()));
			return w;
		}
		return w;
	}

	@Override
	public void close() {
		super.close();
		Collection<Window> windows2 = getWindows();
		for (Window window : windows2) {
			TestWindow.unregister((TestWindow) window);
		}
	}

}
