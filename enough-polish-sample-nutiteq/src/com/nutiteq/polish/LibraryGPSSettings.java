package com.nutiteq.polish;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;

import com.nutiteq.components.PlaceIcon;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.location.providers.LocationAPIProvider;
import com.nutiteq.location.providers.LocationDataConnectionProvider;
import com.nutiteq.location.providers.SonyEricssonCellIdLocationProvider;
import com.nutiteq.polish.screens.MapItem;

public class LibraryGPSSettings extends Form implements CommandListener {
	static final int PROVIDER_LOCATION_API = 0;
	static final int PROVIDER_BLUETOOTH = 1;
	static final int PROVIDER_CELLID = 2;

	private final Command cmdBack;
	private final Command cmdOk;
	private final ChoiceGroup track;
	private final Displayable canvas;
	private final MapItem mapComponet;
	private final Image gpsImage;
	private final Image connectionLost;
	private final int selectedImplementation;
	private String url;
	private LocationDataConnectionProvider provider;
	private LocationSource source;

	public LibraryGPSSettings(final int selectedImplementation,
			final MapItem mapComponet, final Displayable canvas,
			final Image gpsImage, final Image connectionLost) {
		//#style serviceList
		super("Library GPS");
		this.selectedImplementation = selectedImplementation;
		this.mapComponet = mapComponet;
		this.canvas = canvas;
		this.gpsImage = gpsImage;
		this.connectionLost = connectionLost;
		this.cmdBack = new Command("Back", Command.BACK, 0);
		this.cmdOk = new Command("OK", Command.OK, 0);
		//#style serviceListItem
		this.track = new ChoiceGroup("Track GPS", ChoiceGroup.MULTIPLE);
		//#style serviceListItem
		this.track.append("Yes", null);

		addCommand(this.cmdBack);
		addCommand(this.cmdOk);
		append(this.track);

		setCommandListener(this);
	}

	public void commandAction(final Command cmd, final Displayable d) {
		if (cmd == this.cmdBack) {
			J2MEPolishSample.instance.show(this.canvas);
		} else if (cmd == this.cmdOk) {
			final LocationSource dataSource = getLocationSource(this.selectedImplementation);
			if (dataSource == null) {
				return;
			}

			final LocationMarker marker = new NutiteqLocationMarker(
					new PlaceIcon(this.gpsImage, 4, 16), new PlaceIcon(
							this.connectionLost, 4, 16), 3000, 
							this.track.isSelected(0));
			dataSource.setLocationMarker(marker);
			this.mapComponet.setLocationSource(dataSource);
			J2MEPolishSample.instance.show(this.canvas);
		}
	}

	private LocationSource getLocationSource(final int impl) {
		switch (impl) {
		case PROVIDER_LOCATION_API:
			return new LocationAPIProvider(5000);
		case PROVIDER_BLUETOOTH:
			return this.source;
		case PROVIDER_CELLID:
			return new SonyEricssonCellIdLocationProvider();
		default:
			return new LocationAPIProvider(5000);
		}
	}

	public void setBluetoothStuff(final LocationSource lSource) {
		this.source = lSource;
	}
}
