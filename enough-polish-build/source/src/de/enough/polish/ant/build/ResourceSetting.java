/*
 * Created on 10-Sep-2004 at 19:18:54.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.ant.Setting;
import de.enough.polish.resources.ResourcesFileSet;
import de.enough.polish.util.StringUtil;

/**
 * <p>Stores all settings made in the &lt;resources&gt;-element.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceSetting extends Setting {

	private final ArrayList localizationSettings;
	private ArrayList fileSets;
	private boolean useDefaultExcludes = true;
	private String[] excludes = new String[0];
	private String baseDir;
	private ArrayList copierSettings;
	private boolean filterZeroLengthFiles = true;
	private ArrayList filterSettings;
	private ArrayList rootSettings;
	private boolean forceUpdate;


	/**
	 * Creates a new empty Resource Setting.
	 * 
	 * @param baseDir the base directory of the corresponding project
	 */
	public ResourceSetting( File baseDir ) {
		super();
		this.baseDir = baseDir.getAbsolutePath() + File.separator;
		this.localizationSettings = new ArrayList();
		this.fileSets = new ArrayList();
	}
	
	
	public void addConfiguredLocalization( LocalizationSetting setting ) {
		if ( !setting.isValid()) {
			throw new BuildException("Invalid <localisation>-element in build.xml: please specify the attribute \"locales\" or add <locale> subelements." );
		}
		this.localizationSettings.add( setting );
	}
	
	public void addConfiguredFileset( ResourcesFileSet fileSet ) {
		this.fileSets.add( fileSet );
	}
	
	public void addConfiguredCopier( ResourceCopierSetting setting ) {
		if (this.copierSettings == null) {
			this.copierSettings = new ArrayList();
		}
		this.copierSettings.add( setting );
	}
	
	public void addConfiguredRoot( RootSetting setting ) {
		if (setting.getDirDefinition() == null) {
			throw new BuildException("Invalid <root> element in build.xml: please specify the \"dir\" attribute.");
		}
		if (this.rootSettings == null) {
			this.rootSettings = new ArrayList();
		}
		this.rootSettings.add( setting );
	}
	
	public void addConfiguredFilter( ResourceFilterSetting setting ) {
		if (setting.getExcludePatterns() == null) {
			throw new BuildException("Each <filter> element within the <resources> element needs to define one \"excludes\" attribute.");
		}
		if ( this.filterSettings == null ) {
			this.filterSettings = new ArrayList();
		}
		this.filterSettings.add( setting );
	}
	
	public void setDir( File dir ) {
		if (!dir.exists()) { 
			throw new BuildException("The specified resources-directory [" + dir.getAbsolutePath() + "] does not exist. Please adjust the \"dir\" attribute of the <resources> element.");
		}
		addConfiguredRoot( new RootSetting( dir ) );
	}
	
	public RootSetting[] getRootDirectories( Environment env ) {
		if (this.rootSettings == null) {
			return new RootSetting[]{ 
				new RootSetting( new File( this.baseDir + "resources" ) )
			};
		}
		RootSetting[] settings = (RootSetting[]) this.rootSettings.toArray( new RootSetting[ this.rootSettings.size() ] );
		ArrayList rootDirsList = new ArrayList( settings.length );
		BooleanEvaluator evaluator = env.getBooleanEvaluator();
		for (int i = 0; i < settings.length; i++) {
			RootSetting setting = settings[i];
			if (setting.isActive(evaluator)) {
				rootDirsList.add( setting );
			}
		}
		RootSetting[] rootDirs = (RootSetting[]) rootDirsList.toArray( new RootSetting[ rootDirsList.size()] );
		if (rootDirs.length == 0) {
			throw new BuildException( "Unable to build for device [" + env.getDevice().getIdentifier() + "]: no resource directories fit. Check the conditions of your <root> elements in the build.xml script.");
		}
		return rootDirs;
	}
	
	public void setDefaultexcludes( boolean useExcludes ) {
		this.useDefaultExcludes = useExcludes;
	}
	
	public boolean useDefaultExcludes() {
		return this.useDefaultExcludes;
	}
	
	public void setExcludes( String excludes ) {
		this.excludes = StringUtil.splitAndTrim( excludes, ',' );
	}
	
	public String[] getExcludes() {
		return this.excludes;
	}
	
	public void setLocales( String locales ) {
		if (locales.length() == 0) {
			// ignore this setting
			return;
		}
		LocalizationSetting setting = new LocalizationSetting();
		setting.setLocales(locales);
		this.localizationSettings.add( setting );
	}
	
	public void setLocale( String locale ) {
		setLocales( locale );
	}
	
	/**
	 * Retrieves the first active localization setting or null when none was found.
	 * 
	 * @param env the environment
	 * @return the first active localization setting or null when none was found.
	 */
	public LocalizationSetting getLocalizationSetting( Environment env ) {
		for (Iterator iter = this.localizationSettings.iterator(); iter.hasNext();) {
			LocalizationSetting localizationSetting = (LocalizationSetting) iter.next();
			if (localizationSetting.isActive( env )) {
				return localizationSetting;
			}
		}
		// return default setting:
		LocalizationSetting setting = new LocalizationSetting();
		setting.setLocale( Locale.getDefault().toString() );
		return setting;
	}
	
	/**
	 * Retrieves an array of file-sets which should either include or exclude files.
	 * 
	 * @param env the environment
	 * @return an array of the appropriate ResourcesFileSet, can be empty but not null
	 */
	public ResourcesFileSet[] getFileSets( Environment env ) {
		if (this.fileSets.size() == 0) {
			return new ResourcesFileSet[0];
		}
		ArrayList list = new ArrayList( this.fileSets.size());
		for (Iterator iter = this.fileSets.iterator(); iter.hasNext();) {
			ResourcesFileSet set = (ResourcesFileSet) iter.next();
			if (set.isActive(env )) {
				//System.out.println("Adding <fileset> " + set.getDir(this.project)  + " (condition=" + set.getCondition() + ")");
				list.add( set );
			//} else {
			//	System.out.println("Skipping <fileset> " + set.getDir(this.project) + " (condition=" + set.getCondition() + ")");
			}
			
		}
		ResourcesFileSet[] sets = (ResourcesFileSet[]) list.toArray( new ResourcesFileSet[list.size()] );
		return sets;
	}
	
	public ResourceCopierSetting getCopier( Environment env ) {
		if (this.copierSettings == null) {
			return null;
		}
		ResourceCopierSetting[] settings = (ResourceCopierSetting[]) this.copierSettings.toArray( new ResourceCopierSetting[ this.copierSettings.size()]  );
		for (int i = 0; i < settings.length; i++) {
			ResourceCopierSetting setting = settings[i];
			if (setting.isActive(env)) {
				return setting;
			}
		}
		return null;
	}

	public ResourceCopierSetting[] getCopiers(Environment env) {
		if (this.copierSettings == null) {
			return null;
		}
		ArrayList copiers = new ArrayList();
		ResourceCopierSetting[] settings = (ResourceCopierSetting[]) this.copierSettings.toArray( new ResourceCopierSetting[ this.copierSettings.size()]  );
		for (int i = 0; i < settings.length; i++) {
			ResourceCopierSetting setting = settings[i];
			if (setting.isActive(env)) {
				copiers.add( setting );
			}
		}
		return (ResourceCopierSetting[]) copiers.toArray( new ResourceCopierSetting[ copiers.size() ] );
	}
	
	public ResourceFilterSetting[] getFilters(Environment env) {
		if (this.filterSettings == null) {
			return null;
		}
		
		ArrayList filters = new ArrayList();
		ResourceFilterSetting[] settings = (ResourceFilterSetting[]) this.filterSettings.toArray( new ResourceFilterSetting[ this.filterSettings.size()]  );
		for (int i = 0; i < settings.length; i++) {
			ResourceFilterSetting setting = settings[i];
			if (setting.isActive(env)) {
				//System.out.println("ResourceSetting: adding filter with pattern " + setting.getExcludePatterns()[0].pattern() );
				filters.add( setting );
//			} else {
//				System.out.println("ResourceSetting: NOT adding filter with pattern " + setting.getExcludePatterns()[0].pattern() + " and condition " + setting.getCondition() );
			}
		}
		return (ResourceFilterSetting[]) filters.toArray( new ResourceFilterSetting[ filters.size() ] );
	}

	
	public void setFilterZeroLengthFiles( boolean filter ) {
		this.filterZeroLengthFiles = filter;
	}

	public boolean filterZeroLengthFiles() {
		return this.filterZeroLengthFiles ;
	}


	/**
	 * Determines whether resources should be copied in any case.
	 * This can make sense, for example, when a binary library file contains resources
	 * that should be overwritten by normal project specific resources.
	 * 
	 * @return true when resources should be copied even when newer resources already exist at the target destination
	 */
	public boolean isForceUpdate() {
		return this.forceUpdate;
	}
	


	/**
	 * Sets whether resources should be copied in any case.
	 * This can make sense, for example, when a binary library file contains resources
	 * that should be overwritten by normal project specific resources.
	 * 
	 * @param forceUpdate true when resources should be copied even when newer resources already exist at the target destination
	 */
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}


}
