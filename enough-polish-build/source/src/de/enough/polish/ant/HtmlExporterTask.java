/*
 * Created on 29-Jun-2004 at 21:40:55.
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
package de.enough.polish.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.JDOMException;

import de.enough.polish.Device;
import de.enough.polish.Variable;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.devices.Bug;
import de.enough.polish.devices.BugManager;
import de.enough.polish.devices.Configuration;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.IdentifierComparator;
import de.enough.polish.devices.Library;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.devices.Platform;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Exports the device database to a HTML format.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        29-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HtmlExporterTask extends Task {
	private static final String[] CSS_TABLE_ROW_CLASSES = new String[]{"oddRow", "evenRow" };

//	private File wtkHome = new File( "/home/enough/dev/WTK2.1" );
	private String targetDir = "../enough-polish-website/tmp/devices/";
	private HashMap deviceLinks = new HashMap();
	private Comparator caseInsensitiveComparator = new CaseInsensitiveComparator();
	private File polishHome = new File("../enough-polish-build/" );
	private LibraryManager libraryManager;
	private BugManager bugManager;

	//private DeviceManager deviceManager;

	private DeviceDatabase database;
	
	/**
	 * Creates a new uninitialised task
	 */
	public HtmlExporterTask() {
		super();
	}
	
	public void execute() throws BuildException {
		// load device database:
		try {
			this.database = new DeviceDatabase( this.polishHome );
			this.libraryManager = this.database.getLibraryManager();
//			CapabilityManager capabilityManager = new CapabilityManager( getProject().getProperties(),  open( "capabilities.xml" ) );
//			ConfigurationManager configurationManager = new ConfigurationManager( capabilityManager, open("configurations.xml"));
//			PlatformManager platformManager = new PlatformManager( capabilityManager, open("platforms.xml"));
//			this.libraryManager = new LibraryManager(getProject().getProperties(), new File( "import"), this.wtkHome, open( "apis.xml" ) );
//			DeviceGroupManager groupManager = new DeviceGroupManager( open("groups.xml"), capabilityManager ); 
//			VendorManager vendorManager = new VendorManager( null, open("vendors.xml"), capabilityManager, groupManager );
//			this.deviceManager = new DeviceManager( configurationManager, platformManager, vendorManager, groupManager, this.libraryManager, capabilityManager, open("devices.xml") );
			this.bugManager = new BugManager( getProject().getProperties(), open("bugs.xml"));
//			Device[] devices = this.deviceManager.getDevices();
			
			Device[] devices = this.database.getDevices();

			
			// create detailed device pages:
			for (int i = 0; i < devices.length; i++) {
				Device device = devices[i];
				writeDevicePage( device );
			}
			
			// sort by vendor and name:
			Arrays.sort( devices, new IdentifierComparator() );
			process( "Devices by Vendor", "devices-vendor.html", 
					devices, 
					new VendorIndexGenerator(),
					null, "vendor");
			
			// generate Index of used APIs:
			HashMap apisByName = new HashMap();
			for (int i = 0; i < devices.length; i++) {
				Device device = devices[i];
				String[] apis = device.getSupportedApis();
				if (apis != null) {
					for (int j = 0; j < apis.length; j++) {
						String api = apis[j];
						apisByName.put( api, Boolean.TRUE );
					}
				}
			}
			String[] apis = (String[]) apisByName.keySet().toArray( new String[ apisByName.size()] );
			//Arrays.sort( apis );
			ArrayList apiLinksList = new ArrayList( apis.length * 2 );
			//String[] apiLinks = new String[ apis.length ];
			
		 	// get the devices for each api:
			IndexGenerator indexGenerator = new NoIndexGenerator();
			for (int i = 0; i < apis.length; i++) {
				String api = apis[i];
				Requirements requirements = new Requirements();
				requirements.addConfiguredRequirement( new Variable("JavaPackage", api ));
				Device[] filteredDevices = requirements.filterDevices(devices);
				Library lib = this.libraryManager.getLibrary( api );
				String fullApiName;
				String introText = null;
				if (lib != null) {
					fullApiName = lib.getFullName();
					introText = lib.getDescription();
				} else {
					if (api.indexOf("api") == -1) {
						fullApiName = api + " API";
					} else {
						fullApiName = api;
					}
				}
				String fileName = "devices-" + clean( api ) + ".html";
				process( "Devices supporting the " + fullApiName,  fileName,
						filteredDevices, indexGenerator, introText, "api" );
				apiLinksList.add(  "<a id=\"" + fullApiName + "\" href=\"" + fileName + "\" class=\"h2index\">" + fullApiName + " (" + filteredDevices.length + ")</a><br/>" );
				if (lib != null) {
					String[] names = lib.getNames();
					for (int j = 0; j < names.length; j++) {
						String name = names[j];
						if (name.startsWith("JSR-")) {
							apiLinksList.add(  "<a id=\"" + name + "\" href=\"" + fileName + "\" class=\"h2index\">" + name + ": " + fullApiName +  " (" + filteredDevices.length + ")</a><br/>" );
							break;
						}
					}
				}
			}
			String[] apiLinks = (String[]) apiLinksList.toArray( new String[ apiLinksList.size() ] );
			Arrays.sort( apiLinks );
			writeApiOverview( apiLinks );
			
			// now select devices by platform:
			Platform[] platforms = this.database.getPlatforms();
			String[] platformsLinks = new String[ platforms.length ];
			for (int i = 0; i < platforms.length; i++) {
				Platform platform = platforms[i];
				String identifier = platform.getIdentifier();
				Device[] filteredDevices = this.database.getDevices( platform );
				String page = "platform_" + getPageFromIdentifier(identifier);
				platformsLinks[i] = "<a href=\"" + page + "\">" + identifier + " (" + filteredDevices.length + ")</a>";
		 		process( identifier + " Devices", page, filteredDevices, indexGenerator, 
						"The following devices support the " + identifier + " platform", "platform");
			}
			Configuration[] configurations = this.database.getConfigurations();
			String[] configurationsLinks = new String[ configurations.length ];
			for (int i = 0; i < configurations.length; i++) {
				Configuration configuration = configurations[i];
				String identifier = configuration.getIdentifier();
				Device[] filteredDevices = this.database.getDevices( configuration );
		 		String page = "configuration_" + getPageFromIdentifier(identifier);
		 		configurationsLinks[i] =  "<a href=\"" + page + "\">" + identifier + " (" + filteredDevices.length + ")</a>";
				process( identifier + " Devices", page, filteredDevices, indexGenerator, 
						"The following devices support the " + identifier + " configuration", "configuration");
				
			}
			writePlatformOverview(platformsLinks, configurationsLinks);
			
			// write known issues:
			writeKnownIssues();
			
		} catch (de.enough.polish.BuildException e) {
			throw new BuildException( e.getMessage() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("unable to read device database: " + e.getMessage(), e );
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new BuildException("unable to read device database: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			e.printStackTrace();
			throw new BuildException("unable to create device database: " + e.getMessage(), e );
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("unable to read device database: " + e.getMessage(), e );
		}
	}
	
	/**
	 * @throws IOException
	 * 
	 */
	private void writeKnownIssues() 
	throws IOException 
	{
		String fileName = "issues.html";
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection.issues %>");
		lines.add("<%set title = J2ME Polish: Issues Database %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
		//lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">The Known Issues Database</h1>" );
		lines.add("<%index %>");
		lines.add( "<p>Some J2ME devices show unexpected behavior - this list provides an overview.</p>");
		lines.add( "<p>Please <a href=\"mailto:j2mepolish@enough.de?subject=Known_Issue\">email</a> us updates or new known issues stating the area, the device, the description and the solution if possible.</p>");
		String[] areas = this.bugManager.getAreas();
		for (int i = 0; i < areas.length; i++) {
			String area = areas[i];
			lines.add("<h2 id=\"area-" + area + "\">" + area + "</h2>" );
			lines.add("<p>");
			Bug[] issues = this.bugManager.getBugsForArea(area);
			for (int j = 0; j < issues.length; j++) {
				Bug issue = issues[j];
				String name = issue.getName();
				lines.add("<a href=\"issue-" + name + ".html\">" + name + "</a><br />");
			}
		}
		lines.add("</p>");

		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
		
		// now write the detailed description for each bug:
		Bug[] issues = this.bugManager.getAllBugs();
		for (int i = 0; i < issues.length; i++) {
			Bug issue = issues[i];
			writeKnowIssue( issue );
		}
	}

	/**
	 * @param issue
	 * @throws IOException
	 */
	private void writeKnowIssue(Bug issue) throws IOException {
		String name = issue.getName();
		String fileName = "issue-" + name + ".html";
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection.issues %>");
		lines.add("<%set title = J2ME Polish: Issues Database %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
//		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">Known Issue: " + name + "</h1>" );
		lines.add("<%index %>");
		lines.add( "<p>Please <a href=\"mailto:j2mepolish@enough.de?subject=Issue-" + name + "\">email</a> us for updates of this known issue.</p>");
		String[] areas = issue.getAreas();
		if (areas.length == 1) {
			lines.add("<h2 id=\"areas\">Area</h2>" );			
		} else {
			lines.add("<h2 id=\"areas\">Areas</h2>" );
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("<p>");
		for (int i = 0; i < areas.length; i++) {
			String area = areas[i];
			buffer.append( "<a href=\"issues.html#area-" ).append( area ).append("\">").append( area ).append("</a>");
			if ( i < areas.length - 1  ) {
				buffer.append(", ");
			}
		}
		buffer.append("</p>");
		lines.add( buffer.toString() );
		lines.add("<h2 id=\"description\">Description</h2>" );
		lines.add("<p>" + issue.getDescription() + "</p>");
		lines.add("<h2 id=\"workaround\">Workaround</h2>" );
		String solution = issue.getSolution();
		if (solution != null && solution.length() > 1) {
			lines.add("<p>" + solution + "</p>");			
		} else {
			lines.add("<p>No workaround known.</p>");
		}
		lines.add("<h2 id=\"preprocessing\">Preprocessing Access</h2>" );
		lines.add("<p><pre>");
		lines.add("//#ifdef polish.Bugs." + name );
		lines.add("	// implement workaround");
		lines.add("//#endif" );
		lines.add("</pre></p>");
		Device[] devices = issue.getDevices( this.database.getDeviceManager() );
		if (devices.length == 1) {
			lines.add("<h2 id=\"devices\">Affected Device</h2>" );
			
		} else if (devices.length == 0 ) {
			lines.add("<h2 id=\"devices\">Affected Devices</h2>" );
			lines.add("<p>No devices affected.</p>" );
		} else {
			lines.add("<h2 id=\"devices\">Affected Devices</h2>" );
		}
		lines.add("<p>");
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			lines.add("<a href=\"" + device.getVendorName()  + "/" + device.getName() + ".html\">" + device.getIdentifier() + "</a><br />");
			
		}
		lines.add("</p>");
		
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
	}

	/**
	 * @param device
	 * @throws IOException
	 * @throws InvalidComponentException
	 */
	private void writeDevicePage(Device device) 
	throws IOException, InvalidComponentException 
	{
		String vendor = clean( device.getVendorName() );
		String name = clean( device.getName() );
		String fileName = vendor + "/" + name + ".html";
		this.deviceLinks.put( device.getIdentifier(), fileName);
		
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%set title = J2ME Polish: " + device.getIdentifier() +" J2ME Specification %>");
		lines.add("<%set basedir = ../../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
//		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">" + device.getIdentifier() + "</h1>" );
		lines.add("<p>");
		lines.add("<%index %>");
		lines.add("<br/></p>");
		if (device.hasFeature("polish.isVirtual")) {
			lines.add( "<p>This device is a virtual device which combines several features of real devices. A virtual device represents a group of devices.</p>");
		}
		addDisplayCapabilities( lines, device );
		addPlatformCapabilities( lines, device );
		addMemoryCapabilities( lines, device );
		addMultimediaCapabilities( lines, device );
		addKeyCapabilities( lines, device );
		addDeviceIssues( lines, device );
		//addDeviceOverview(lines, device, "oddRow", false);		
		String[] groups = device.getGroupNames();
		if (groups != null && groups.length > 0) {
			lines.add("<h2 id=\"groups\">Groups</h2>");
			lines.add("<p>Groups can be used to assemble the resources (like images or sound-files) for an application." +
					"<br/>Have a look at the " +
					"<a href=\"<%= basedir %>docs/resource-assembling.html\">documentation</a> for more info.</p>");
			lines.add("<table class=\"borderedTable\"><tr><th>Group</th><th>Resource Folder</th></tr>");
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				lines.add("<tr><td>" + group + "</td><td>resources/" + group + "</td></tr>");
			}
			lines.add("</table>");
		}
		
		// add capabilities:
		HashMap capabilitiesByName = device.getCapabilities();
		String[] capabilities = (String[]) capabilitiesByName.keySet().toArray( new String[ capabilitiesByName.size()] );
		Arrays.sort( capabilities, this.caseInsensitiveComparator );
		lines.add("<h2 id=\"capabilities\">Preprocessing Capabilities</h2>");
		lines.add("<p>Capabilities can be used with the &quot;//#=&quot; " +
				"preprocessing directive and can be compared with the &quot;//#if&quot; " +
				"directive. For each defined capability a preprocessing symbol " +
				"with the same name will be defined." +
				"<br/>Have a look at the <a href=\"<%= basedir %>docs/preprocessing.html\">preprocessing documentation</a> " +
				"for more information.</p>");
		lines.add("<p>Examples:");
		lines.add("<pre>");
		lines.add("//#if polish.Vendor == " + device.getVendorName() );
		lines.add("	// this is a " + device.getVendorName() + " device");
		lines.add("//#endif");
		lines.add("//#ifdef polish.ScreenWidth:defined");
		lines.add("	//#= int screenWidth = ${ polish.ScreenWidth };");
		lines.add("//#else");
		lines.add("	int screenWidth = UNKNOWN_WIDTH;");
		lines.add("//#endif");
		lines.add("</pre></p>");
		lines.add("<table class=\"borderedTable\"><tr><th>Capability</th><th>Value</th></tr>");
		for (int i = 0; i < capabilities.length; i++) {
			String cap = capabilities[i];
			String value = (String) capabilitiesByName.get( cap );
			lines.add("<tr><td>" + cap + "</td><td>" + value + "</td></tr>");
		}
		lines.add("</table>");
		
		// add symbols:
		HashMap symbolsByName = device.getFeatures();
		String[] symbols = (String[]) symbolsByName.keySet().toArray( new String[ symbolsByName.size()] );
		Arrays.sort( symbols, this.caseInsensitiveComparator );
		lines.add("<h2 id=\"symbols\">Preprocessing Symbols</h2>");
		lines.add("<p>Symbols can be evaluated with the &quot;//#ifdef&quot; " +
				"and related preprocessing directives." +
				"<br/>Have a look at the <a href=\"<%= basedir %>docs/preprocessing.html\">preprocessing documentation</a> " +
				"for more information.</p>");
		lines.add("<p>Example:");
		lines.add("<pre>");
		lines.add("//#if polish.api.mmapi || polish.midp2" );
		lines.add("	// this device supports the Mobile Media API");
		lines.add("//#endif");
		lines.add("</pre></p>");
		lines.add("<table class=\"borderedTable\"><tr><th>Symbol</th></tr>");
		for (int i = 0; i < symbols.length; i++) {
			String symbol = symbols[i];
			lines.add("<tr><td>" + symbol + "</td></tr>");
		}
		lines.add("</table>");
		
		
		// add the end:
		lines.add("<%include end.txt %>");		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );	
	}

	private void addDisplayCapabilities(ArrayList lines, Device device) {
		String screenSize = device.getCapability("polish.ScreenSize");
		String canvasSize = device.getCapability("polish.CanvasSize");
		String fullCanvasSize = device.getCapability("polish.FullCanvasSize");
		String bitsPerPixel = device.getCapability("polish.BitsPerPixel");
		lines.add( "<h2 id=\"screen\">Display</h2>" );
		lines.add("<table width=\"100%\" class=\"borderedTable\">");
		lines.add( "<tr><th>Property</th><th>Value</th><th>Preprocessing Access</th></tr>" );
		int row = 0;
		String cssStyle;
		if (screenSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Screen-Size (width x height)</td><td>" + screenSize + "</td><td>polish.ScreenSize, polish.ScreenWidth, polish.ScreenHeight</td></tr>" );
		}
		if (canvasSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Canvas-Size (width x height)</td><td>" + canvasSize + "</td><td>polish.CanvasSize, polish.CanvasWidth, polish.CanvasHeight</td></tr>" );
		}
		if (fullCanvasSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Canvas-Size in fullscreen mode</td><td>" + fullCanvasSize + "</td><td>polish.FullCanvasSize, polish.FullCanvasWidth, polish.FullCanvasHeight</td></tr>" );
		}
		if (bitsPerPixel != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			int bits = Integer.parseInt(bitsPerPixel);
			int colors = (int) Math.pow( 2, bits);
			NumberFormat formatter = NumberFormat.getInstance( Locale.ENGLISH );
			String colorsStr = formatter.format(colors);
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Bits per Pixel</td><td>" + bitsPerPixel + "<br/>(" + colorsStr + " colors)</td><td>polish.BitsPerPixel</td></tr>" );
		}
		boolean hasPointerEvents = device.hasFeature("polish.hasPointerEvents");
		String answer = hasPointerEvents ? "yes" : "no";
		cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
		row++;
		lines.add( "<tr class=\"" + cssStyle + "\"><td>Has Pointer Events (Stylus)</td><td>" + answer + "</td><td>polish.hasPointerEvents</td></tr>" );
		
		lines.add("</table>");
		
	}

	private void addPlatformCapabilities(ArrayList lines, Device device) {
		String platform = device.getCapability("polish.JavaPlatform");
		String configuration = device.getCapability("polish.JavaConfiguration");
		String apis = device.getSupportedApisAsString();
		String os = device.getCapability("polish.OS");
		lines.add( "<h2 id=\"platform\">Platform</h2>" );
		lines.add("<table width=\"100%\" class=\"borderedTable\">");
		lines.add( "<tr><th>Property</th><th>Value</th><th>Preprocessing Access</th></tr>" );
		int row = 0;
		String cssStyle;
		cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
		row++;
		lines.add( "<tr class=\"" + cssStyle + "\"><td>Vendor</td><td><a href=\"../devices-vendor.html#" + device.getVendorName() + "\">" + device.getVendorName() + "</a></td><td>polish.Vendor" );
		if (os != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>OS</td><td>" + os + "</td><td>polish.OS" );
		}
		if (platform != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			String platformRow = "<tr class=\"" + cssStyle + "\"><td>Platform</td><td>"; 
			if (device.isMidp1()) {
				platformRow +=  "<a href=\"../midp1.html\">" + platform + "</a>";
			} else if (device.isMidp2()) {
				platformRow +=  "<a href=\"../midp2.html\">" + platform + "</a>";
			} else {
				platformRow += platform;
			}
			platformRow += "</td><td>polish.JavaPlatform";
			if (device.isMidp1()) {
				platformRow +=  ", polish.midp1</td></tr>";
			} else if (device.isMidp2()) {
				platformRow +=  ", polish.midp2</td></tr>";
			} else {
				platformRow +=  "</td></tr>";
			}
			lines.add( platformRow );
		}
		if (configuration != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			String platformRow = "<tr class=\"" + cssStyle + "\"><td>Configuration</td><td>";
			if (device.isCldc10()) {
				platformRow +=  "<a href=\"../cldc10.html\">" + configuration + "</a></td><td>polish.JavaConfiguration";
			} else if (device.isCldc11()) {
				platformRow +=  "<a href=\"../cldc11.html\">" + configuration + "</a></td><td>polish.JavaConfiguration";
			} else {
				platformRow +=  configuration + "</td><td>polish.JavaConfiguration";
			}
			if (device.isCldc10()) {
				platformRow +=  ", polish.cldc1.0</td></tr>";
			} else if (device.isCldc11()) {
				platformRow +=  ", polish.cldc1.1</td></tr>";
			} else {
				platformRow +=  "</td></tr>";
			}
			lines.add( platformRow );
		}

		if (apis != null) {
			String[] apiNames = StringUtil.splitAndTrim( apis, ',');
			ArrayList apisList = new ArrayList( apiNames.length + 5 );
			for (int i = 0; i < apiNames.length; i++) {
				String apiName = apiNames[i];
				String[] symbols = this.libraryManager.getSymbols(apiName);
				if (symbols == null) {
					apisList.add( apiName );
				} else {
					for (int j = 0; j < symbols.length; j++) {
						String symbol = symbols[j];
						apisList.add( symbol );
					}
				}
			}
			apiNames = (String[]) apisList.toArray( new String[ apisList.size() ]);
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			StringBuffer apisRow = new StringBuffer();
			apisRow.append( "<tr class=\"" ).append( cssStyle )
				.append("\"><td>Supported APIs</td><td>" );
			StringBuffer preprocessingSymbols = new StringBuffer();
			for (int i = 0; i < apiNames.length; i++) {
				String apiName = apiNames[i];
				apisRow.append( "<a href=\"../devices-" ).append( apiName ).append(".html\">")
				   .append( apiName ).append("</a>");
				preprocessingSymbols.append("polish.api.").append( apiName );
				if (i != apiNames.length -1 ) {
					apisRow.append(", ");
					preprocessingSymbols.append(", ");
				}
			}
			apisRow.append( "</td><td>polish.JavaPackage, " )
				.append( preprocessingSymbols ).append("</td></tr>" );
			lines.add( apisRow.toString() );
		}
		// does this device support the J2ME Polish GUI?
		cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
		row++;
		String support = "no";
		if (device.supportsPolishGui()) {
			support = "yes&nbsp;&nbsp;<img src=\"<%= basedir %>images/checked.png\" width=\"17\" height=\"17\" />";
		}
		lines.add( "<tr class=\"" + cssStyle + "\"><td>Meets the Recommended Capablities for the J2ME Polish GUI</td><td>" + support + "</td><td>--</td></tr>" );

		lines.add("</table>");		
	}

	private void addMemoryCapabilities(ArrayList lines, Device device) {
		String heapSize = device.getCapability("polish.HeapSize");
		String maxJarSize = device.getCapability("polish.MaxJarSize");
		String storageSize = device.getCapability("polish.StorageSize");
		String maxRecordStoreSize = device.getCapability("polish.MaxRecordStoreSize");
		String maxRecordSize = device.getCapability("polish.RMS.MaxRecordSize");
		if (heapSize == null && maxJarSize == null && storageSize == null && maxRecordStoreSize == null && maxRecordSize == null) {
			return;
		}
		lines.add( "<h2 id=\"memory\">Memory</h2>" );
		lines.add("<table width=\"100%\" class=\"borderedTable\">");
		lines.add( "<tr><th>Property</th><th>Value</th><th>Preprocessing Variable</th></tr>" );
		int row = 0;
		String cssStyle;
		if (heapSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Heap-Size</td><td>" + heapSize + "</td><td>polish.HeapSize</td></tr>" );
		}
		if (maxJarSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Maximum Jar Size</td><td>" + maxJarSize + "</td><td>polish.MaxJarSize</td></tr>" );
		}
		if (storageSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Available Storage Size</td><td>" + storageSize + "</td><td>polish.StorageSize</td></tr>" );
		}
		if (maxRecordStoreSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Maximum Size of the Record-Store (RMS)</td><td>" + maxRecordStoreSize + "</td><td>polish.MaxRecordStoreSize</td></tr>" );
		}
		if (maxRecordSize != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Maximum Size of One Record (RMS)</td><td>" + maxRecordSize + "</td><td>polish.polish.RMS.MaxRecordSize</td></tr>" );
		}
		lines.add("</table>");		
	}
	
	private void addMultimediaCapabilities(ArrayList lines, Device device) {
		String soundFormat = device.getCapability("polish.SoundFormat");
		String videoFormat = device.getCapability("polish.VideoFormat");
		if (soundFormat == null && videoFormat == null ) {
			return;
		}
		lines.add( "<h2 id=\"multimedia\">Multimedia</h2>" );
		lines.add("<table width=\"100%\" class=\"borderedTable\">");
		lines.add( "<tr><th>Property</th><th>Value</th><th>Preprocessing Access</th></tr>" );
		int row = 0;
		String cssStyle;
		if (soundFormat != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			String[] formats = StringUtil.splitAndTrim(soundFormat.toLowerCase(), ',');
			StringBuffer rowText = new StringBuffer();
			rowText.append("<tr class=\"");
			rowText.append(cssStyle);
			rowText.append("\"><td>Supported Audio Formats</td><td>");
			rowText.append(soundFormat + "</td><td>polish.SoundFormat, ");
			for (int i = 0; i < formats.length; i++) {
				String format = formats[i];
				rowText.append("polish.audio.");
				rowText.append(format);
				if (i != formats.length - 1) {
					rowText.append(", ");
				}
			}
			rowText.append("</td></tr>"); 
			lines.add( rowText.toString() );
		}
		if (videoFormat != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			String[] formats = StringUtil.splitAndTrim(videoFormat.toLowerCase(), ',');
			String rowText = "<tr class=\"" + cssStyle + "\"><td>Supported Video Formats</td><td>" + videoFormat + "</td><td>polish.VideoFormat, ";
			for (int i = 0; i < formats.length; i++) {
				String format = formats[i];
				rowText += "polish.video." + format;
				if (i != formats.length - 1) {
					rowText += ", ";
				}
			}
			rowText += "</td></tr>"; 
			lines.add( rowText );
		}
		lines.add("</table>");		
	}
	
	private void addKeyCapabilities(ArrayList lines, Device device) {
		String leftSoftKey = device.getCapability("polish.key.LeftSoftKey");
		String middleSoftKey = device.getCapability("polish.key.MiddleSoftKey");
		String rightSoftKey = device.getCapability("polish.key.RightSoftKey");
		String clearKey = device.getCapability("polish.key.ClearKey");
		String changeInputModeKey = device.getCapability("polish.key.ChangeInputModeKey");
		if (leftSoftKey == null && middleSoftKey == null && rightSoftKey == null && clearKey == null && changeInputModeKey == null) {
			return;
		}
		lines.add( "<h2 id=\"keys\">Keys</h2>" );
		lines.add("<table width=\"100%\" class=\"borderedTable\">");
		lines.add( "<tr><th>Key</th><th>Value</th><th>Preprocessing Variable</th></tr>" );
		int row = 0;
		String cssStyle;
		if (leftSoftKey != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Left Soft Key</td><td>" + leftSoftKey+ "</td><td>polish.key.LeftSoftKey</td></tr>" );
		}
		if (middleSoftKey != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Middle Soft Key</td><td>" + middleSoftKey+ "</td><td>polish.key.MiddleSoftKey</td></tr>" );
		}
		if (rightSoftKey != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Right Soft Key</td><td>" + rightSoftKey+ "</td><td>polish.key.RightSoftKey</td></tr>" );
		}
		if (clearKey != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Clear Key</td><td>" + clearKey+ "</td><td>polish.key.ClearKey</td></tr>" );
		}
		if (changeInputModeKey != null) {
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr class=\"" + cssStyle + "\"><td>Change Text-Input-Mode Key</td><td>" + changeInputModeKey+ "</td><td>polish.key.ChangeInputModeKey</td></tr>" );
		}
		
		lines.add("</table>");
		
	}


	private void addDeviceIssues(ArrayList lines, Device device) 
	throws InvalidComponentException 
	{
		Bug[] bugs = this.bugManager.getBugs(device);
		if (bugs == null ) {
			return;
		}
		lines.add( "<h2 id=\"issues\">Known Issues</h2>" );
		lines.add("<table width=\"100%\" class=\"borderedTable\">");
		int row = 0;
		String cssStyle;
		for (int i = 0; i < bugs.length; i++) {
			Bug bug = bugs[i];
			cssStyle = CSS_TABLE_ROW_CLASSES[ row % 2 ];
			row++;
			lines.add( "<tr><th>Issue " + (i + 1) + "</th><th>Area</th><th>Description</th></tr>" );
			StringBuffer buffer = new StringBuffer();
			buffer.append( "<tr class=\"" ).append( cssStyle ).append("\"><td>")
			  .append("<a href=\"../issue-" + bug.getName() + ".html\">")
			  .append( bug.getName() ).append("</a></td><td>");
			String[] areas = bug.getAreas();
			for (int j = 0; j < areas.length; j++) {
				String area = areas[j];
				buffer.append( "<a href=\"../issues.html#area-").append( area ).append("\">" ).append( area ).append("</a>");
				if (j != areas.length -1 ) {
					buffer.append( ", ");
				}
			}
			buffer.append("</td><td>");
			buffer.append( bug.getDescription() );
			buffer.append("</td><td></tr>");
			String solution = bug.getSolution();
			if (solution != null) {
				buffer.append( "\n<tr><th colspan=\"3\">Solution</td></tr>\n" );
				buffer.append( "<tr class=\"" ).append( cssStyle ).append("\"><td colspan=\"3\">");
				if (solution.indexOf("\\n") != -1) {
					String[] solutionLines = StringUtil.splitAndTrim(solution, "\\n");
					for (int j = 0; j < solutionLines.length; j++) {
						buffer.append( solutionLines[j] ).append("<br/>\n");						
					}
					buffer.append("</td><td>");
				} else {
					buffer.append( solution ).append("</td></tr>");
				}
			}
			buffer.append( "\n<tr><th colspan=\"3\">Preprocessing Symbol</td></tr>\n" );
			buffer.append( "<tr class=\"" ).append( cssStyle ).append("\"><td colspan=\"3\">");
			buffer.append( "polish.Bugs.").append( bug.getName() )
				.append( "<br/>&nbsp;</td></tr>" ); 
			lines.add( buffer.toString() );
		}

		lines.add("</table>");		
	}
	
	/**
	 * @param configurations 
	 * @param platforms 
	 * @throws IOException
	 * 
	 */
	private void writePlatformOverview(String[] platforms, String[] configurations) throws IOException {
		String fileName = "platform.html";
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection.platform %>");
		lines.add("<%set title = J2ME Polish: Device Database %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
//		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">Device by Platforms</h1>" );
		lines.add( "<p>Following platforms are supported by mobile devices:</p><ul>");
		for (int i = 0; i < platforms.length; i++) {
			lines.add("<li>" + platforms[i]  + "</li>");
		}
//		lines.add("<li><a href=\"midp1.html\">MIDP/1.0</a></li>");
//		lines.add("<li><a href=\"midp2.html\">MIDP/2.0</a></li>");
		lines.add("</ul>");
		lines.add( "<p>Following configurations are supported by mobile devices:</p><ul>");
		for (int i = 0; i < configurations.length; i++) {
			lines.add("<li>" + configurations[i]  + "</li>");
		}
//		lines.add("<li><a href=\"cldc10.html\">CLDC/1.0</a></li>");
//		lines.add("<li><a href=\"cldc11.html\">CLDC/1.1</a></li>");
		lines.add("</ul>");
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
	}
	
	String getPageFromIdentifier( String identifier ) {
		identifier = identifier.replace('/', '_' );
		identifier += ".html";
		return identifier;
	}

	/**
	 * @param apiLinks
	 * @throws IOException
	 */
	private void writeApiOverview(String[] apiLinks) throws IOException {
		String fileName = "apis.html";
		System.out.println("Creating " + fileName);
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection.api %>");
		lines.add("<%set title = J2ME Polish: APIs %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
		//lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">Device by APIs</h1>" );
		lines.add( "<p>Following APIs are supported by J2ME devices:</p><p>");
		for (int i = 0; i < apiLinks.length; i++) {
			lines.add( apiLinks[i] );
		}
		lines.add("</p>");
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
	}
	

	private String clean( String fileName) {
		fileName = StringUtil.replace( fileName, ' ', '_');
		fileName = StringUtil.replace( fileName, '/', '_');
		return fileName;
	}

	/**
	 * Opens a file
	 * 
	 * @param fileName the name of the file which should be opened
	 * @return an input stream of the specified file
	 * @throws FileNotFoundException when the specified file was not found
	 */
	private InputStream open(String fileName) 
	throws FileNotFoundException 
	{
		File file = new File( this.polishHome, fileName );
		return new FileInputStream( file );
	}
	
	private void process( String heading, String fileName, Device[] devices, 
			IndexGenerator indexGenerator,
			String introText, String subSection ) 
	throws IOException 
	{
		System.out.println("Creating " + fileName);
		String[] cssRowClasses = new String[]{"oddRow", "evenRow" };
		ArrayList lines = new ArrayList();
		lines.add("<%define inDevicesSection %>");
		lines.add("<%define inDevicesSection." + subSection + " %>");
		lines.add("<%set title = J2ME Polish: " + heading + " %>");
		lines.add("<%set basedir = ../ %>");
		lines.add("<%include start.txt %>" );
		lines.add("");
//		lines.add("<div id=\"content\">" );
		lines.add("<h1 id=\"top\">" + heading + "</h1>" );
		if (introText != null) {
			lines.add("<p>" + introText + "</p>");
		}
		if (devices.length == 0) {
			lines.add("<p>There are not known devices in this list yet.</p>");
		} else if (devices.length > 1) {
			lines.add("<p>There are " + devices.length + " devices in this list.</p>");
		}
		int indexRow = lines.size();
		startTable( lines );
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			indexGenerator.update(device, lines);
			String cssClass = cssRowClasses[ i % 2 ];
			addDeviceOverview(lines, device, cssClass, true);
		}
		endTable( lines );
		
		// now insert the index:
		String[] index = indexGenerator.generateIndex();
		for (int i = 0; i < index.length; i++) {
			String line = index[i];
			lines.add(indexRow, line);
			indexRow++;
		}
		
		// add the end:
		lines.add("<%include end.txt %>");
		
		// write the file:
		String[] htmlCode = (String[] ) lines.toArray( new String[ lines.size() ] );
		FileUtil.writeTextFile( new File( this.targetDir + fileName), htmlCode );
	}
	
	private void addDeviceOverview( ArrayList lines, Device device, String cssClass, boolean addDeviceLink ) {
		lines.add("<tr class=\"" + cssClass + "\">");
		StringBuffer buffer = new StringBuffer();
		buffer.append( "<td>" ).append( device.getVendorName() ).append( "</td><td>" );
		if (addDeviceLink) {
			String link = (String) this.deviceLinks.get( device.getIdentifier());
			buffer.append("<a href=\"").append( link ).append("\">");
		}
		buffer.append( device.getName() );
		if (addDeviceLink) {
			buffer.append("</a></td><td>" );
		} else {
			buffer.append("</td><td>" );			
		}
		if (device.supportsPolishGui()) {
			buffer.append("<img src=\"<%= basedir %>images/checked.png\" width=\"17\" height=\"17\" />");
		} else {
			buffer.append("-");
		}
		buffer.append( "</td><td>" );
		buffer.append( device.getCapability("JavaPlatform") );
		String configuration = device.getCapability( "JavaConfiguration" );
		if ( configuration != null) {
			buffer.append( " ").append( configuration );
		}
		buffer.append( "</td><td>" );
		String apis = device.getSupportedApisAsString();
		if (apis == null) {
			apis = "-";
		}
		buffer.append( apis  ).append( "</td><td>" );
		
		String screenSize = device.getCapability("ScreenSize");
		if (screenSize != null) {
			buffer.append( screenSize );
		} else {
			buffer.append( "-");
		}
		buffer.append("</td><td>");
		
		String bitsPerPixel = device.getCapability( "BitsPerPixel" );
		if (bitsPerPixel != null) {
			buffer.append( bitsPerPixel );
		} else {
			buffer.append( "-");
		}
		buffer.append("</td><td>");
		
		String features = device.getFeaturesAsString();
		if (features != null) {
			buffer.append( features );
		} else {
			buffer.append( "-");
		}
		buffer.append("</td>");
		
		lines.add( buffer.toString() );
		lines.add("</tr>");
	}
	
	private void endTable( ArrayList list ) {
		list.add("</table><p>GUI: device supports the J2ME Polish GUI.</p>");
	}
	private void startTable( ArrayList list ) {
		list.add("<table width=\"100%\" class=\"borderedTable\">");
		list.add("<tr><th>Vendor</th><th>Device</th><th>GUI</th><th>Platform</th><th>APIs</th>" +
				"<th>ScreenSize</th><th>BitsPerPixel</th><th>Features</th></tr>");
	}
	
	abstract class IndexGenerator {
		protected ArrayList indeces = new ArrayList();
		
		public abstract void update( Device device, ArrayList list);
		
		protected void addIndex( String index, ArrayList list ) {
			boolean firstEntry = this.indeces.size() == 0;
			this.indeces.add( index );
			if (!firstEntry) {
				endTable( list );
			}
			list.add("<h2 id=\"" + index + "\">" + index + "</h2>");
			if (!firstEntry) {
				startTable( list );
			}
		}
		
		public String[] generateIndex(){
			String[] index = new String[ this.indeces.size() ];
			for (int i = 0; i < index.length; i++) {
				String heading = (String) this.indeces.get( i );
				index[i] = "<a href=\"#" + heading + "\" class=\"h2index\">" + heading + "</a><br/>";
			}
			return index;
	 	}
	}
	
	class VendorIndexGenerator extends IndexGenerator {
		
		private String lastVendor;
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ant.HtmlExporterTask.IndexGenerator#update(de.enough.polish.Device, java.util.ArrayList)
		 */
		public void update(Device device, ArrayList list) {
			String vendor = device.getVendorName();
			if (!vendor.equals( this.lastVendor)) {
				addIndex( vendor, list );
				this.lastVendor = vendor;
			}
		}

	}
	
	class NoIndexGenerator extends IndexGenerator {

		/* (non-Javadoc)
		 * @see de.enough.polish.ant.HtmlExporterTask.IndexGenerator#update(de.enough.polish.Device, java.util.ArrayList)
		 */
		public void update(Device device, ArrayList list) {
			// nothing to update
		}
		
	}

	class CaseInsensitiveComparator implements Comparator {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
		}
		
	}

}
