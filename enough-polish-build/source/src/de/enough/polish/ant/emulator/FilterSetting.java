package de.enough.polish.ant.emulator;

import java.util.ArrayList;

import de.enough.polish.ExtensionSetting;

public class FilterSetting extends ExtensionSetting
{
	private ArrayList<String> filterPackageNames;
	private Character filterMandatoryMessageChar;
	
	public ArrayList<String> getFilterPackageNames() {
		return filterPackageNames;
	}
	public void setFilterPackageNames(ArrayList<String> filterPackageNames) {
		this.filterPackageNames = filterPackageNames;
	}
	public Character getFilterMandatoryMessageChar() {
		return filterMandatoryMessageChar;
	}
	public void setFilterMandatoryMessageChar(Character filterMandatoryMessageChar) {
		this.filterMandatoryMessageChar = filterMandatoryMessageChar;
	}
	
	public void addConfiguredPackage(PackageNameSetting packageSetting)
	{
		if (filterPackageNames == null)
		{
			filterPackageNames = new ArrayList<String>();
		}
		filterPackageNames.add(packageSetting.getName());
	}
}
 