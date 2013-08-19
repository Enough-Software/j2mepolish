package de.enough.polish.ant.emulator;

import java.util.ArrayList;

public class FilterSettings {
	public ArrayList<FilterSetting> settings;
	
	public void addConfiguredFilter(FilterSetting setting)
	{
		if (settings == null)
		{
			settings = new ArrayList<FilterSetting>();
		}
		settings.add(setting);
	}
}
