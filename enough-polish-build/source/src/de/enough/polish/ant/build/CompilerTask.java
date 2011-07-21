/*
 * Created on 25-Feb-2005 at 15:29:28.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
import java.lang.reflect.Field;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.ant.ConditionalElement;
import de.enough.polish.util.ReflectionUtil;

/**
 * <p>Allows the setting of any &lt;javac&gt;-settings.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CompilerTask extends Javac {
	
	private final ConditionalElement condition;
	private boolean bootClassPathSet;
	private boolean classPathSet;
	private boolean debugLevelSet;
	private boolean destDirSet;
	private boolean sourceSet;
	private boolean debugSet;
	private boolean sourceDirSet;
	private boolean targetSet;
	private boolean taskNameSet;
	private Path bootClassPath;
	private Reference bootClassPathRef;
	private Path classPath;
	private Reference classPathRef;
	private String debugLevel;
	private File destDir;
	private String source;
	private Path sourceDir;
	private String target;
	private String internalTaskName;
	private String includes;
	private File includesFile;
	private String excludes;
	private File excludesFile;
	private Path extDirs;
	private String encoding;
	private boolean nowarn;
	private boolean optimize;
	private boolean deprecation;
	private boolean verbose;
	private boolean depend;
	private boolean includeAntRuntime;
	private boolean includeJavaRuntime;
	private boolean fork;
	private String executable;
	private String memoryInitialSize;
	private String memoryMaximumSize;
	private String compiler;
	private File tempDir;
	private ArrayList compilerArguments;

	/**
	 * Creates a new Compiler task
	 */
	public CompilerTask() {
		super();
		this.condition = new ConditionalElement();
	}
	
	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.condition.setIf( ifExpr );
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.condition.setUnless(unlessExpr);
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param currentProject The project to which this nested element belongs to.
	 * @return true when this element is valid
	 */
	public boolean isActive( Project currentProject ) {
		return this.condition.isActive( currentProject );
	}

	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @param currentProject the Ant project into which this variable is embedded
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator, Project currentProject) {
		return this.condition.isActive( evaluator, currentProject );
	}

	public void setBootclasspath(Path path) {
		this.bootClassPathSet = true;
		this.bootClassPath = path;
		super.setBootclasspath(path);
	}
	public void setDirectBootclasspath(Path path) {
		super.setBootclasspath(path);
		this.bootClassPath = path;
	}
	public void setBootClasspathRef(Reference reference) {
		this.bootClassPathSet = true;
		this.bootClassPathRef = reference;
		super.setBootClasspathRef(reference);
	}
	public void setClasspath(Path path) {
		this.classPathSet = true;
		this.classPath = path;
		super.setClasspath(path);
	}
	public void setDirectClasspath(Path path) {
		super.setClasspath(path);
	}
	public void setClasspathRef(Reference reference) {
		this.classPathSet = true;
		this.classPathRef = reference;
		super.setClasspathRef(reference);
	}
	public void setDirectClasspathRef(Reference reference) {
		super.setClasspathRef(reference);
	}
	public void setDebug(boolean enable) {
		this.debugSet = true;
		super.setDebug(enable);
	}
	public void setDirectDebug(boolean enable) {
		super.setDebug(enable);
	}
	public void setDebugLevel(String level) {
		this.debugLevelSet = true;
		this.debugLevel = level;
		super.setDebugLevel(level);
	}
	public void setDirectDebugLevel(String level) {
		super.setDebugLevel(level);
	}
	public void setDestdir(File file) {
		this.destDirSet = true;
		this.destDir = file;
		super.setDestdir(file);
	}
	public void setDirectDestdir(File file) {
		super.setDestdir(file);
	}
	public void setSource(String source) {
		this.sourceSet = true;
		this.source = source;
		super.setSource(source);
	}
	public void setDirectSource(String source) {
		super.setSource(source);
	}
	public void setSrcdir(Path path) {
		this.sourceDirSet = true;
		this.sourceDir = path;
		super.setSrcdir(path);
	}
	public void setDirectSrcdir(Path path) {
		super.setSrcdir(path);
	}
	public void setTarget(String target) {
		this.targetSet = true;
		this.target = target;
		super.setTarget(target);
	}
	public void setDirectTarget(String target) {
		super.setTarget(target);
	}
	public void setTaskName( String name ) {
		this.taskNameSet = true;
		this.internalTaskName = name;
		super.setTaskName(name);
	}
	public void setDirectTaskName( String name ) {
		super.setTaskName(name);
	}
	public void setIncludes( String includes ) {
		this.includes = includes;
		super.setIncludes(includes);
	}
	public void setIncludesfile( File includes ) {
		this.includesFile = includes;
		super.setIncludesfile(includes);
	}
	public void setExcludes( String excludes ) {
		this.excludes = excludes;
		super.setExcludes(excludes);
	}
	public void setExcludesfile( File excludes ) {
		this.excludesFile = excludes;
		super.setExcludesfile(excludes);
	}
	public void setExtdirs( Path path ) {
		this.extDirs = path;
		super.setExtdirs( path );
	}
	public void setEncoding( String encoding ) {
		this.encoding = encoding;
		super.setEncoding(encoding);
	}
	public void setNowarn( boolean nowarn ) {
		this.nowarn = nowarn;
		super.setNowarn(nowarn);
	}
	public void setOptimize( boolean optimize ) {
		this.optimize = optimize;
		super.setOptimize(optimize);
	}
	public void setDeprecation( boolean deprecation ) {
		this.deprecation = deprecation;
		super.setDeprecation(deprecation);
	}
	public void setVerbose( boolean verbose ) {
		this.verbose = verbose;
		super.setVerbose( verbose );
	}
	public void setDepend( boolean depend ) {
		this.depend = depend;
		super.setDepend(depend);
	}
	public void setIncludeantruntime( boolean include ) {
		this.includeAntRuntime = include;
		super.setIncludeantruntime(include);
	}
	public void setIncludejavaruntime( boolean include ) {
		this.includeJavaRuntime = include;
		super.setIncludejavaruntime(include);
	}
	public void setFork( boolean fork ) {
		this.fork = fork;
		super.setFork( fork );
	}
	public void setExecutable( String exec ) {
		this.executable = exec;
		super.setExecutable( exec );
	}
	public void setMemoryInitialSize( String size ) {
		this.memoryInitialSize = size;
		super.setMemoryInitialSize(size);
	}
	public void setMemoryMaximumSize( String size ) {
		this.memoryMaximumSize = size;
		super.setMemoryMaximumSize(size);
	}
	public void setFailonerror( boolean fail ) {
		this.failOnError = fail;
		super.setFailonerror(fail);
	}
	public void setCompiler( String compiler ) {
		this.compiler = compiler;
		super.setCompiler(compiler);
	}
	public void setListfiles( boolean list ) {
		this.listFiles = list;
		super.setListfiles(list);
	}
	public void setTempdir( File dir ) {
		this.tempDir = dir;
		super.setTempdir( dir );
	}
	
	public ImplementationSpecificArgument createCompilerArg() {
		ImplementationSpecificArgument argument =  super.createCompilerArg();
		if (this.compilerArguments == null) {
			this.compilerArguments = new ArrayList();
		}
		this.compilerArguments.add( argument );
		return argument;
	}
	
	public boolean isBootClassPathSet() {
		return this.bootClassPathSet;
	}
	public boolean isClassPathSet() {
		return this.classPathSet;
	}
	public ConditionalElement getCondition() {
		return this.condition;
	}
	public boolean isDebugLevelSet() {
		return this.debugLevelSet;
	}
	public boolean isDebugSet() {
		return this.debugSet;
	}
	public boolean isDestDirSet() {
		return this.destDirSet;
	}
	public boolean isSourceDirSet() {
		return this.sourceDirSet;
	}
	public boolean isSourceSet() {
		return this.sourceSet;
	}
	public boolean isTargetSet() {
		return this.targetSet;
	}
	public boolean isTaskNameSet() {
		return this.taskNameSet;
	}
	
	
	public void execute() throws BuildException {
        super.execute();
	}

	/**
	 * Creates a copy of this task.
	 * This is needed because otherwise Duplicate Class Definition errors are encountered
	 * when the base javac task is reused.
	 * 
	 * @return a copy of this task.
	 */
	public CompilerTask copy() {
		CompilerTask copy = new CompilerTask();
		if (this.bootClassPath != null) {
			copy.setBootclasspath(this.bootClassPath);
		}
		if (this.bootClassPathRef != null) {
			copy.setBootClasspathRef( this.bootClassPathRef );
		}
		if (this.classPath != null) {
			copy.setClasspath(this.classPath);
		}
		if (this.classPathRef != null) {
			copy.setClasspathRef(this.classPathRef);
		}
		if (this.debugLevel != null) {
			copy.setDebug( this.debugSet );
			copy.setDebugLevel(this.debugLevel);
		} else if (this.debugSet) {
			copy.setDebug( this.debugSet );
		}
		if (this.destDir != null) {
			copy.setDestdir(this.destDir);
		}
		if (this.source != null) {
			copy.setSource( this.source );
		}
		if (this.sourceDir != null) {
			copy.setSourcepath( this.sourceDir );
		}
		if (this.target != null) {
			copy.setTarget( this.target );
		}
		if (this.internalTaskName != null ) {
			copy.setTaskName(this.internalTaskName);
		}
		if (this.includes != null) {
			copy.setIncludes(this.includes);
		}
		if (this.includesFile != null) {
			copy.setIncludesfile(this.includesFile);
		}
		if (this.excludes != null) {
			copy.setExcludes(this.excludes);
		}
		if (this.excludesFile != null) {
			copy.setExcludesfile(this.excludesFile);
		}
		if (this.extDirs != null) {
			copy.setExtdirs( this.extDirs );
		}
		if (this.encoding != null) {
			copy.setEncoding(this.encoding);
		}
		copy.setNowarn(this.nowarn);
		copy.setOptimize(this.optimize);
		copy.setDeprecation(this.deprecation);
		copy.setVerbose( this.verbose );
		copy.setDepend(this.depend);
		copy.setIncludeantruntime(this.includeAntRuntime);
		copy.setIncludejavaruntime(this.includeJavaRuntime);
		copy.setFork( this.fork );
		if (this.executable != null) {
			copy.setExecutable( this.executable );
		}
		if (this.memoryInitialSize != null) {
			super.setMemoryInitialSize( this.memoryInitialSize);
		}
		if (this.memoryMaximumSize != null) {
			copy.setMemoryMaximumSize(this.memoryMaximumSize);
		}
		copy.setFailonerror(this.failOnError);
		if (this.compiler != null) {
			copy.setCompiler(this.compiler);
		}
		copy.setListfiles(this.listFiles);
		if (this.tempDir != null) {
			super.setTempdir( this.tempDir );
		}
		if (this.compilerArguments != null) {
			ImplementationSpecificArgument[] arguments = (ImplementationSpecificArgument[]) this.compilerArguments.toArray( new ImplementationSpecificArgument[ this.compilerArguments.size() ] );
			for (int i = 0; i < arguments.length; i++) {
				ImplementationSpecificArgument argument = arguments[i];
				try {
					Field field = ReflectionUtil.getField( copy, "facade" );
					FacadeTaskHelper facade = (FacadeTaskHelper) field.get( copy );
					facade.addImplementationArgument(argument);
 				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to set compiler arguments of nested <compiler>-task: " + e.toString(), e );
				}

			}
		}
		return copy;
	}
}
