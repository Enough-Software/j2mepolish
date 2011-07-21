package de.enough.polish.buildlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.tools.ant.BuildException;

import de.enough.polish.ant.ConditionalTask;

public class HtmlGeneratorTask
	extends ConditionalTask
{
	private static final String DEFAULT_BUILDLIST = "dist/buildlist.txt";
	private static final String DEFAULT_OUTPUTFILE = "dist/index.html";
	private static final String DEFAULT_TITLE = "Device list";
	private static final String DEFAULT_INTRO = "";

	protected String buildlist = DEFAULT_BUILDLIST;
	protected String title = DEFAULT_TITLE;
	protected String intro = DEFAULT_INTRO;
	protected String outputFile = DEFAULT_OUTPUTFILE;

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException
	{
		File buildListFile = new File(this.buildlist);
		
		if (!buildListFile.exists()) {
			throw new BuildException("buildlist file \"" + this.buildlist + "\" not found");
		}
		
		try
		{
			HashMap devicesByVendor = readDeviceList(buildListFile);

			generateVendorList(devicesByVendor);
			Object[] vendors = devicesByVendor.keySet().toArray();
			for (int i = 0; i < vendors.length; i++) {
				String vendorName = (String) vendors[i];
				generateVendorFile(vendorName, devicesByVendor);
			}
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
	}

	private HashMap readDeviceList(File buildListFile)
		throws IOException
	{
		//System.out.println("trying to read build list file " + buildListFile.getAbsolutePath() );
		HashMap devicesByVendor = new HashMap();
		BufferedReader reader = new BufferedReader(new FileReader(buildListFile));
		String line;

		while ((line = reader.readLine()) != null) {
			int pos = line.indexOf('/');
			StringTokenizer st = new StringTokenizer(line, "\t");
			String deviceName = (String) st.nextElement();
			st.nextElement();
			st.nextElement();
			String jadName = (String) st.nextElement();
			String vendorName = deviceName.substring(0, pos);
			//System.out.println("found device=" + deviceName );
			TreeMap deviceList = (TreeMap) devicesByVendor.get(vendorName);

			if (deviceList == null) {
				deviceList = new TreeMap();
				//System.out.println("adding map for vendor " + vendorName );
				devicesByVendor.put(vendorName, deviceList);
			}
			deviceList.put(deviceName, jadName);
		}

		reader.close();
		//System.out.println("returning " + devicesByVendor);
		return devicesByVendor;
	}

	private void generateVendorList(HashMap devicesByVendor)
		throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile));

		writeDeviceListHeader(writer);
		Object[] vendors = devicesByVendor.keySet().toArray();
		Arrays.sort( vendors );

		for (int i = 0; i < vendors.length; i++) {
			String vendorName =(String) vendors[i];
			writeDeviceListEntry(writer, vendorName);
		}
		
		writeDeviceListFooter(writer);
		writer.close();
	}

	protected void writeDeviceListHeader(BufferedWriter writer) throws IOException
	{
		writer.write("<html>\n");
		writer.write("<head>\n");
		writer.write("<title>\n");
		writer.write(this.title);
		writer.write("\n</title>\n");
		writer.write("</head>\n");
		writer.write("<body>\n");
		writer.write("<p>");
		writer.write(this.intro);
		writer.write("</p>");
		writer.write("<p>");
	}
	
	protected void writeDeviceListEntry(BufferedWriter writer, String vendorName) throws IOException
	{
		writer.write("<a href=\"");
		writer.write(vendorName);
		writer.write(".html\">");
		writer.write(vendorName);
		writer.write("</a><br />\n");
	}
	
	protected void writeDeviceListFooter(BufferedWriter writer) throws IOException
	{
		writer.write("</p>");
		writer.write("</body>\n");
		writer.write("</html>\n");
	}
	
	private void generateVendorFile(String vendorName, HashMap devicesByVendor)
		throws IOException
	{
		TreeMap deviceList = (TreeMap) devicesByVendor.get(vendorName);
		//System.out.println("using " + devicesByVendor);
		if (deviceList == null) {
			System.out.println("Warning: unable to build map for vendor " + vendorName + ": no treemap" );
			return;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter("dist/" + vendorName + ".html"));
		writeVendorFileHeader(writer);

		Iterator it = deviceList.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String deviceName = (String) entry.getKey();
			String jadName = (String) entry.getValue();
			writeVendorFileEntry(writer, deviceName, jadName);
		}

		writeVendorFileFooter(writer);
		writer.close();
	}

	protected void writeVendorFileHeader(BufferedWriter writer) throws IOException
	{
		writer.write("<html>\n");
		writer.write("<head>\n");
		writer.write("<title>\n");
		writer.write(this.title);
		writer.write("\n</title>\n");
		writer.write("</head>\n");
		writer.write("<body>\n");
		writer.write("<p>\n");
	}
	
	protected void writeVendorFileEntry(BufferedWriter writer, String deviceName, String jadName) throws IOException
	{
		writer.write("<a href=\"");
		writer.write(jadName);
		writer.write("\">");
		writer.write(deviceName);
		writer.write("</a><br />\n");
	}

	protected void writeVendorFileFooter(BufferedWriter writer) throws IOException
	{
		writer.write("</p>\n");
		writer.write("<p>\n");
		writer.write("<a href=\"index.html\">Back</a>");
		writer.write("</p>\n");
		writer.write("</body>\n");
		writer.write("</html>\n");
	}
	
	public String getBuildlist()
	{
		return this.buildlist;
	}

	public void setBuildlist(String buildlist)
	{
		this.buildlist = buildlist;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getIntro()
	{
		return this.intro;
	}

	public void setIntro(String intro)
	{
		this.intro = intro;
	}

	public String getOutputFile()
	{
		return this.outputFile;
	}

	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}
}
