/*
 * Created on Oct 16, 2008 at 7:43:23 PM.
 * 
 * Copyright (c) 2007 Andre Schmidt / Enough Software
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
package de.enough.polish.android.precompiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Creates the stub directories for the activity and
 * copies the source files to the stubs</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ActivityPreCompiler extends PreCompiler
implements OutputFilter
{
	
	public final static String project = ".project";
	
	public final static String classpath = ".classpath";
	
	public final static String placeholder = "@@NAME@@";

	private String errorMessage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.precompile.PreCompiler#preCompile(java.io.File,
	 * de.enough.polish.Device)
	 */
	public void preCompile(File classesDir, Device device)
			throws BuildException {
		Environment env = device.getEnvironment();
		ArrayList arguments;
		String name;
		String android = ArgumentHelper.android(env);
		if (android != null) {
			name = "android";
			arguments = getAndroidArguments(android,env);
		} else {
			String activityCreator = ArgumentHelper.activityCreator(env);
			if (activityCreator == null) {
				throw new BuildException("Unable to resolve android path, please check your \"android.home\" setting, which currently points to \"" + env.getVariable("android.home") + "\"." );
			}
			name = "activityCreator";
			arguments = getActivityCreatorArguments(activityCreator,env);
		}
		File directory = new File(ArgumentHelper.getPlatformTools(env));
		
		try {
			// Delete the previously build activity
			FileUtil.delete(new File(ArgumentHelper.getActivity(env)));
			
			// Create a project out of the activity
			createProject(env);
			
			// Create the activity
			this.errorMessage = null;
			int result = ProcessUtil.exec( arguments, name + ": ", true, this, directory );
			if (result != 0 || this.errorMessage != null) {
				String message = "Unable to create activity / project: " + name + " returned " + result;
				if (this.errorMessage != null) {
					message += " and error message " + this.errorMessage;
					if (this.errorMessage.indexOf(" id ") != -1) {
						message += " Used ID was [" + ArgumentHelper.getTargetId(env) + "].";
						String[] args = new String[]{ (String)arguments.get(0), "list", "targets" };
						try {
							ProcessUtil.exec( args, name + ": ", true, null, directory );
						} catch (Throwable t) {
							// ignore
						}
					}
					System.out.println("arguments were:");
					System.out.println(ProcessUtil.toString(arguments));
				}
				throw new BuildException(message ); 
			}
			
			// Copy the preprocessed sources
			FileUtil.copyDirectoryContents(new File(device.getSourceDir()), ArgumentHelper.getSrc(env), false);
			// generate Activity:
			generateActivityClass( ArgumentHelper.getSrc(env), env );

			new File(ArgumentHelper.getClasses(env)).mkdir();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create activity / project: " + e);
		}
	}
	
	private void generateActivityClass(String src, Environment env) throws IOException {
		String[] midlets = env.getBuildSetting().getMidletClassNames(env);
		String midlet = midlets[0] + "Activity";
		String packageName = "";
		String className = midlet;
		int packageEnd = midlet.lastIndexOf('.');
		if (packageEnd != -1) {
			packageName = midlet.substring(0, packageEnd);
			className = midlet.substring( packageEnd + 1 );
		}
		String[] code = new String[]{
			"package " + packageName + ";",
			"public class " + className + " extends de.enough.polish.android.midlet.MidletBridge {",
			"	public " + className + "(){ super(); }",
			"}"
		};
		midlet = StringUtil.replace(midlet, '.', File.separatorChar );
		FileUtil.writeTextFile( new File(src + File.separator + midlet + ".java"), code);
	}

	private ArrayList getAndroidArguments(String executable, Environment env) {
		String[] midlets = env.getBuildSetting().getMidletClassNames(env);
		String midlet = midlets[0];
		String packageName = "";
		int packageEnd = midlet.lastIndexOf('.');
		if (packageEnd != -1) {
			packageName = midlet.substring(0, packageEnd);
			midlet = midlet.substring( packageEnd + 1 ) + "Activity";
		}

		String base = ArgumentHelper.getActivity(env);
		ArrayList arguments = new ArrayList();
		arguments.add(executable);
		if ("android".equals(executable)) {
			arguments.add("update");
		} else {
			arguments.add("create");
		}
		arguments.add("project");
		arguments.add("--target");
		//System.out.println("using targetId " + ArgumentHelper.getTargetId(env) );
		arguments.add(ArgumentHelper.getTargetId(env));
		arguments.add("--path");
		arguments.add(base);
		arguments.add("--activity");
		arguments.add(midlet);
		arguments.add("--package");
		arguments.add(packageName);
		return arguments;
	}

	/**
	 * Returns the default arguments for executable
	 * @param executable the executable
	 * @param env the environment
	 * @return the ArrayList
	 */
	static ArrayList getActivityCreatorArguments(String executable, Environment env)
	{
		String[] midlets = env.getBuildSetting().getMidletClassNames(env);
		String midlet = midlets[0] + "Activity";
		String base = ArgumentHelper.getActivity(env);
		ArrayList arguments = new ArrayList();
		arguments.add(executable);
		arguments.add("--out");
		arguments.add(base);
		arguments.add(midlet);
		return arguments;
	}
	
	/**
	 * Copies the .project and .classpath file to
	 * create a eclipse project from the activity 
	 * @param env the environment
	 * @throws IOException if a file error occurs
	 */
	void createProject(Environment env) throws IOException
	{
		String android = ArgumentHelper.android(env);
		if (android != null) {
			return;
		}
		File activity = new File(ArgumentHelper.getActivity(env));
		File projectFile = new File(ArgumentHelper.getBuild(env) + File.separator + project);
		File classpathFile = new File(ArgumentHelper.getBuild(env) + File.separator + classpath);
		
		String[] lines = FileUtil.readTextFile(projectFile);
		
		lines = replace(ArgumentHelper.getName(env),lines);
		
		FileUtil.writeTextFile(new File(ArgumentHelper.getActivity(env) + File.separator + project), lines);
		
		FileUtil.copy(classpathFile,activity);
	}
	
	/**
	 * Replaces the placeholder in .project with the project name
	 * @param name the project name
	 * @param lines the lines of .project
	 * @return the altered lines
	 */
	String[] replace(String name, String[] lines)
	{
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].replace(placeholder, name);
		}
		
		return lines;
	}

	public void filter(String message, PrintStream output) {
		if (message.indexOf("Error") != -1) {
			this.errorMessage = message;
		}
		output.println(message);
	}
	
}
