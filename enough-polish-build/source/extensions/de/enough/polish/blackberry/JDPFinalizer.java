package de.enough.polish.blackberry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.Jad;
import de.enough.polish.ant.blackberry.JDPTask;
import de.enough.polish.descriptor.DescriptorCreator;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.jar.Packager;
import de.enough.polish.manifest.ManifestCreator;
import de.enough.polish.util.FileUtil;

/**
 * <p>Creates a Blackberry JDE project file from the preprocessed resources and sources for debugging etc.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        21-Mar-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class JDPFinalizer extends Finalizer{

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env) {
		JDPTask task = new JDPTask();
		
		String name = env.getVariable("MIDlet-Name");
		
		// add JAD file to JAR, so that MIDlet.getAppProperty() works later onwards:
		// TODO: we should remove all unneeded MIDlet attributes that are not going to be read anyhow
		String mainClassName = env.getVariable( "blackberry.main");
		boolean usePolishGui = env.hasSymbol( "polish.usePolishGui" );
		if (mainClassName == null && usePolishGui) {
			try {
				File classesDir = new File( device.getClassesDir() );
				storeJadProperties(jadFile, classesDir, device.getResourceDir(), env);
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to store JAD file in BlackBerry JAR file: " + e.toString() );
			}
		}
		
		task.setName(name);
		task.setPath(device.getBaseDir());
		task.setSources(device.getBaseDir());
		
		task.execute();
	}

	/**
	 * Writes the JAD properties in a way so that the MIDlet can load them.
	 * 
	 * @param jadFile the JAD file
	 * @param classesDir the classes directory to which the JAD properties should be saved
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void storeJadProperties(File jadFile, File classesDir, File resouresDir, Environment env) 
	throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		File txtJadFile = new File( classesDir, jadFile.getName().substring( 0, jadFile.getName().length() - ".jad".length() ) + ".txt");
		//System.out.println("CREATING " + txtJadFile.getAbsolutePath() );
		//FileUtil.copy( jadFile, txtJadFile );
		Attribute[] descriptorAttributes = (Attribute[]) env.get(ManifestCreator.MANIFEST_ATTRIBUTES_KEY);
		Jad jad = new Jad( env );
		jad.setAttributes( descriptorAttributes );
		descriptorAttributes = (Attribute[]) env.get(DescriptorCreator.DESCRIPTOR_ATTRIBUTES_KEY);
		jad.addAttributes( descriptorAttributes );
		String[] jadPropertiesLines = jad.getContent();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < jadPropertiesLines.length; i++)
		{
			String line = jadPropertiesLines[i];
			buffer.append(line).append('\n');
		}
		FileOutputStream fileOut = new FileOutputStream(  txtJadFile );
		fileOut.write( buffer.toString().getBytes("UTF-8") );
		fileOut.flush();
		fileOut.close();
		// copy to resources folder:
		FileUtil.copy(txtJadFile, new File( resouresDir, txtJadFile.getName()) );
	}

}
