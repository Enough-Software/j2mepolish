/*
 * Created on May 30, 2008 at 2:42:18 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.obfuscate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.enough.polish.util.FileUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ObfuscatedSourcesFilter
{
	
	private final FilterConfiguration cfg;



	public ObfuscatedSourcesFilter( FilterConfiguration cfg ) {
		this.cfg = cfg;
		
	}
	

	/**
	 * @throws IOException 
	 * 
	 */
	public void filter() throws IOException
	{
		ProGuardObfuscationMap map = new ProGuardObfuscationMap( this.cfg.getObfuscationMapFile() );
		String[] classNames = map.getClassNames();
		System.out.println("There are " + classNames.length + " classes in " + this.cfg.getObfuscationMapFile().getAbsolutePath() );
		for (int i = 0; i < classNames.length; i++)
		{
			String className = classNames[i];
			copy( className );
		}
	}




	/**
	 * @param className
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void copy(String className) throws FileNotFoundException, IOException
	{
		String classFileName = className.replace('.', '/') + ".java";
		File in = new File( this.cfg.getInDir(), classFileName );
		if (!in.exists()) {
			if (className.indexOf('$') != -1) {
				// this is an inner class, ignore
				return;
			}
			throw new IOException("Unable to resolve class " + className + ", should be in " + in.getAbsolutePath() );
		}
		File out = new File( this.cfg.getOutDir(), classFileName );
		FileUtil.copy(in, out);
	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		FilterConfiguration cfg = new FilterConfiguration();
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			int splitPos = arg.indexOf('=');
			if (splitPos == -1) {
				usage();
				System.exit(1);
			}
			String name = arg.substring(0, splitPos);
			String value = arg.substring(splitPos + 1 );
			if (name.equals("-in")) {
				cfg.setInDir( new File( value ));
			} else if (name.equals("-out")) {
				cfg.setOutDir( new File( value ));
			} else if (name.equals("-map")) {
				cfg.setObfuscationMapFile(new File(value));
			} else {
				usage();
				System.exit(1);
			}
		}
		if (!cfg.isValid()) {
			usage();
			System.exit(1);
		}
	
		ObfuscatedSourcesFilter filter = new ObfuscatedSourcesFilter( cfg );
		filter.filter();

	}
	

	
	
	/**
	 * 
	 */
	private static void usage()
	{
		System.out.println("Usage: java -cp classpath de.enough.polish.obfuscate.ObfuscatedSourcesFilter -in=/path/indir -out=/path/outdir -map=/path/to/obfuscation-map.txt");
		
	}



	static class FilterConfiguration {
		private File inDir;
		private File outDir;
		private File obfuscationMapFile;
		
		public FilterConfiguration() {
			// nothing to set up
		}
		
		public boolean isValid() {
			boolean isValid = true;
			if (this.inDir == null ) {
				System.out.println("Missing parameter -in");
				isValid = false;
			} else if (!this.inDir.exists()) {
				System.out.println("Unable to resolve " + this.inDir.getAbsolutePath() + " - check the -in parameter");
				isValid = false;
			}
			if (this.outDir == null) {
				System.out.println("Missing parameter -out");
				isValid = false;
			}
			if (this.obfuscationMapFile == null) {
				System.out.println("Missing parameter -map");
				isValid = false;
			} else if (!this.obfuscationMapFile.exists()) {
				System.out.println("Unable to resolve " + this.obfuscationMapFile.getAbsolutePath() + " - check the -map parameter");
				isValid = false;
			}
			return isValid;
		}
		
		/**
		 * @return the inDir
		 */
		public File getInDir() {
		return this.inDir;}
		
		/**
		 * @param inDir the inDir to set
		 */
		public void setInDir(File inDir)
		{
			this.inDir = inDir;
		}
		/**
		 * @return the outDir
		 */
		public File getOutDir() {
		return this.outDir;}
		
		/**
		 * @param outDir the outDir to set
		 */
		public void setOutDir(File outDir)
		{
			this.outDir = outDir;
		}
		/**
		 * @return the obfuscationMapFile
		 */
		public File getObfuscationMapFile() {
		return this.obfuscationMapFile;}
		
		/**
		 * @param obfuscationMapFile the obfuscationMapFile to set
		 */
		public void setObfuscationMapFile(File obfuscationMapFile)
		{
			this.obfuscationMapFile = obfuscationMapFile;
		}
		
	
	}
	
	
}


