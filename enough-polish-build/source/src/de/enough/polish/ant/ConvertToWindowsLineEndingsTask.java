package de.enough.polish.ant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class ConvertToWindowsLineEndingsTask
  extends Task
{
	private static final boolean DEBUG = false;

	private Vector filesets = new Vector();
  
	public void execute() throws BuildException
	{
		log("Converting files to windows line endings");
		Iterator it = this.filesets.iterator();
    
		while (it.hasNext())
		{
			FileSet fileSet = (FileSet) it.next();
			DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
			File dir = fileSet.getDir(getProject());
			String[] sourceFiles = directoryScanner.getIncludedFiles();

			if (DEBUG) {
				log("File count: " + sourceFiles.length);
			}
	
			for (int i = 0; i < sourceFiles.length; i++) {
				try {
					File file = new File(dir, sourceFiles[i]);

					if (DEBUG) {
						log(file.getPath());
					}

					processFile(file);
				}
				catch (IOException e) {
					e.printStackTrace();

					BuildException be = new BuildException("failed to convert file " + sourceFiles[i]);
					be.initCause(e);
					throw be;
				}
			}
		}
	}

	public void addFileSet(FileSet fileSet)
	{
		this.filesets.add(fileSet);
	}

	private void processFile(File file)
		throws IOException
	{
		String line;
		LinkedList lines = new LinkedList();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}

		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		Iterator it = lines.iterator();

		while (it.hasNext()) {
			writer.write((String) it.next());
			writer.write("\r\n");
		}

		writer.close();
	}
}
