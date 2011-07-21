package de.enough.polish.buildlist;

import java.io.BufferedWriter;
import java.io.IOException;

public class WmlGeneratorTask
	extends HtmlGeneratorTask
{
	private static final String DEFAULT_OUTPUTFILE = "dist/index.wml";

	public WmlGeneratorTask()
	{
		this.outputFile = DEFAULT_OUTPUTFILE;
	}

	protected void writeDeviceListHeader(BufferedWriter writer) throws IOException
	{
		writer.write("<wm1>\n");
		writer.write("<card>\n");
		writer.write("<p>\n");
		writer.write(this.title);
		writer.write("\n</p>\n");
		writer.write("<p>\n");
		writer.write(this.intro);
		writer.write("\n</p>\n");
		writer.write("<p>\n");
	}
	
	protected void writeDeviceListEntry(BufferedWriter writer, String vendorName) throws IOException
	{
		writer.write("<a href=\"\"");
		writer.write(vendorName);
		writer.write(".wml\">");
		writer.write(vendorName);
		writer.write("</a>\n");
	}
	
	protected void writeDeviceListFooter(BufferedWriter writer) throws IOException
	{
		writer.write("</p>\n");
		writer.write("<card>\n");
		writer.write("<wml>\n");
	}

	protected void writeVendorFileHeader(BufferedWriter writer) throws IOException
	{
		writer.write("<wm1>\n");
		writer.write("<card>\n");
		writer.write("<p>\n");
	}

	protected void writeVendorFileEntry(BufferedWriter writer, String deviceName, String jadName) throws IOException
	{
		writer.write("<a href=\"\"");
		writer.write(jadName);
		writer.write(".wml\">");
		writer.write(deviceName);
		writer.write("</a>\n");
	}

	protected void writeVendorFileFooter(BufferedWriter writer) throws IOException
	{
		writer.write("</p>\n");
		writer.write("<p>\n");
		writer.write("<a href=\"index.wml\">\n");
		writer.write("</p>\n");
		writer.write("<card>\n");
		writer.write("<wml>\n");
	}
}
