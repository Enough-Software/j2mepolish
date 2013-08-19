package de.enough.polish.emulator;

import java.util.Locale;

import org.apache.tools.ant.Project;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionDefinition;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ExtensionSetting;
import de.enough.polish.ExtensionTypeDefinition;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.ant.emulator.FilterSetting;

public abstract class EmulatorOutputFilter extends Extension 
{
	public enum FilterResult
	{
		NotProcessed, DoNotPrint, Print
	}

	public static final String EXTENSION_TYPE = "EmulatorFilter";
	
	
	@Override
	protected void init(ExtensionTypeDefinition typeDefinition,
			ExtensionDefinition definition, ExtensionSetting setting,
			Project project, ExtensionManager manager, Environment env) 
	{
		super.init(typeDefinition, definition, setting, project, manager, env);
		configure((FilterSetting) setting);
	}

	@Override
	public void execute(Device device, Locale locale, Environment env)
			throws BuildException {
		// ignore, filter(String) is called directly
		
	}

	protected abstract void configure(FilterSetting filterSetting);

	public abstract FilterResult filter(String message);
}
