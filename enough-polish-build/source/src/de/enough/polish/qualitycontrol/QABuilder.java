package de.enough.polish.qualitycontrol;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.jdom.JDOMException;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.StringUtil;

public class QABuilder
implements OutputFilter
{
	private static final String VERSION = "0.8.0";
	private static String processLogCommand = "cat";

	private boolean isVerbose;
	private VariablesManager variablesManager;
	private PolishVariable[] variables;
	
	private final ArrayList processOutput;
	
	private Random random;
	private int maxPermutations;


	public QABuilder(File polishHome, boolean random, int maxPermutations) throws JDOMException, IOException, InvalidComponentException {
		this.variablesManager = new VariablesManager( polishHome );
		this.processOutput = new ArrayList();
		
		if (random) {
			this.random = new Random();
		}

		this.maxPermutations = maxPermutations;
		this.variables = this.variablesManager.getVisibleVariables();
	}
	
	private void buildProjects(String[] projects) {
		int numberOfPermutations = getNumberOfPermutations( getNumberOfBooleanVariables() );
		int numberOfBuildsPerProject = numberOfPermutations;
		if (this.maxPermutations != -1) {
			numberOfBuildsPerProject = this.maxPermutations;
		}
		int currentBuildRound = 0;
		int currentPermutationIndex = -1;
		String[] polishVariables = null;
		int totalBuilds = numberOfBuildsPerProject * projects.length;
		int executedBuilds = 0;
		
		boolean atLeastOneProjectFailed = false;
		boolean[] ignoreProjects = new boolean[ projects.length ];
		while (currentBuildRound < numberOfBuildsPerProject) {
			for (int i = 0; i < projects.length; i++) {
				boolean ignore = ignoreProjects[i];
				if (!ignore) {
					File projectHome = new File( projects[i] );
					if (polishVariables == null) {
						System.out.println("Building " + projectHome.getName() + " in the default configuration (" + (executedBuilds+1) + "/" + totalBuilds + ")...");
					} else {
						System.out.println("Building " + projectHome.getName() + " using variables permutation " + currentPermutationIndex + " (" + (executedBuilds+1) + "/" + totalBuilds + ")...");
					}
					boolean ignoreForFutureBuilds = build( projectHome, currentPermutationIndex, numberOfBuildsPerProject, polishVariables );
					if (ignoreForFutureBuilds) {
						totalBuilds -= numberOfBuildsPerProject + currentBuildRound + 1;
						atLeastOneProjectFailed = true;
						ignoreProjects[i] = true;
					}
					executedBuilds++;
				}
			}
			if (this.random != null) {
				currentPermutationIndex = this.random.nextInt(numberOfPermutations);
			} else {
				currentPermutationIndex++;
			}
			currentBuildRound++;
			polishVariables = getVariablePermutation(currentPermutationIndex);
		}
		// Signal general failure.
		if (atLeastOneProjectFailed) {
			System.exit(1);
		}
	}

	/**
	 * Builds a project
	 * @param projectHome
	 * @param permutationsIndex
	 * @param numberOfBuilds
	 * @param polishVariables
	 * @return true when the build failed
	 */ 
	private boolean build(File projectHome, int permutationsIndex, int numberOfBuilds, String[] polishVariables) {
		ArrayList args = new ArrayList();
		File errorFile = new File( projectHome, "errors.log");
		if (errorFile.exists()) {
			boolean success = errorFile.delete();
			if (!success) {
				handleConfigError( "Unable to delete " + errorFile.getAbsolutePath() );
			}
		}
		args.add("ant");
		args.add("cleanbuild");
		args.add("-Dpolish.buildcontrol.errorhandler=de.enough.polish.qualitycontrol.FileErrorHandler");
		args.add("-Dfileerrorhandler.file=" + errorFile.getAbsolutePath());
		if (polishVariables != null) {
			args.add("-Dpolish.buildcontrol.variables.enabled=false");
			for (int j = 0; j < polishVariables.length; j++) {
				String polishVariable = polishVariables[j];
				args.add("-D" + polishVariable );
			}
		}
		try {
			this.processOutput.clear();
			int res = ProcessUtil.exec(args, "", true, this, projectHome );
			if (res != 0 || errorFile.exists()) {
				String[] summary = new String[0];
				if (errorFile.exists()) {
					try {
						summary = FileUtil.readTextFile(errorFile);
					} catch (Exception e) {
						System.out.println("Unable to load error file " + errorFile.getAbsolutePath() + ": " + e.toString() );
						e.printStackTrace();
					}
				}
				handleBuildError( projectHome, polishVariables, summary, (String[])this.processOutput.toArray( new String[ this.processOutput.size() ]) );
				// after one build error we don't try to build the project any more:
				return true;
			}
		} catch (Exception e) {
			String message = "Unable to build project or handle build error: " + e;
			System.err.println(message);
			e.printStackTrace();
			handleConfigError(message);
			return true;
		}
		return false;
	}

	protected void handleBuildError(File projectHome, String[] polishVariables, String[] summary, String[] output) 
	{
		if (summary != null && summary.length > 2) {
			System.out.println("Encountered build error: " + summary[2]);
		}
		ArrayList lines = new ArrayList();
		lines.add("Build error in " + projectHome.getName() + ":  " + projectHome.getAbsolutePath() );
		lines.add("On " + (new Date()).toString() );
		lines.add("");
		lines.add("*****  Summary:  *****");
		lines.add("");		
		for (int i = 0; i < summary.length; i++) {
			String line = summary[i];
			lines.add(line);
		}
		lines.add("");
		lines.add("*****  Complete output:  *****");
		lines.add("");
		for (int i = 0; i < output.length; i++) {
			String line = output[i];
			lines.add(line);
		}
		if (polishVariables == null) {
			lines.add("");
			lines.add("*****  Configuration:  *****");
			lines.add("");			
			lines.add("<standard project configuration>");			
		} else {
			lines.add("");
			lines.add("*****  Configuration:  *****");
			lines.add("");
			for (int i = 0; i < polishVariables.length; i++) {
				String line = polishVariables[i];
				lines.add(line);
			}
		}
		
		String logFileName = "build-error-" + projectHome.getName() + "-" + System.currentTimeMillis() + ".log";
		File logFile = new File( logFileName );
		try {
			FileUtil.writeTextFile( logFile, lines );
			executeProcessLogCommand(logFile);
		} catch (Exception e) {
			String message = "Unable to store log " + logFile.getAbsolutePath() + ": " + e;
			System.err.println(message);
			e.printStackTrace();
			handleConfigError(message);
		}
	}
	

	public void filter(String message, PrintStream output) {
		this.processOutput.add(message);
		if (this.isVerbose) {
			output.println(message);
		}
	}

	
	private int getNumberOfBooleanVariables() {
		int numberOfBooleanVariables = 0;
		for (int i=0; i<this.variables.length; i++) {
			PolishVariable var = this.variables[i];
			if ("boolean".equals(var.getType())) {
				numberOfBooleanVariables++;
			}
		}
		return numberOfBooleanVariables;
	}
	
	private int getNumberOfPermutations( int numberOfBooleanVariables ) {
		int numberOfBooleanPermuations = 1;
		for (int i=1; i<=numberOfBooleanVariables; i++) {
			numberOfBooleanPermuations *= i;
		}
		return numberOfBooleanPermuations;
	}
	
	private String[] getVariablePermutation( int permutationIndex ) {
		int booleanIndex = 0;
		String[] perm = new String[this.variables.length];
		ArrayList conditionsList = null;
		for (int i=0; i<this.variables.length; i++) {
			PolishVariable var = this.variables[i];
			String permutation;
			if ("boolean".equals(var.getType())) {
				boolean isOn = getBooleanPermuationValue( permutationIndex, booleanIndex );
				if (isOn) {
					permutation = var.getName() + "=true";
					PolishVariableCondition[] conditions = var.getConditions();
					if (conditions != null) {
						if (conditionsList == null) {
							conditionsList = new ArrayList();
						}
						for (int j = 0; j < conditions.length; j++) {
							PolishVariableCondition condition = conditions[j];
							conditionsList.add( condition.getVariableName() +"=" + condition.getVariableValue());
						}
					}
				} else {
					permutation = var.getName() + "=false";
				}
				booleanIndex++;
			} else {
				permutation = var.getName() + "=" + var.getDefaultValue();
			}
			perm[i] = permutation;
		}
		if (conditionsList == null) {
			return perm;
		} else {
			String[] result = new String[ perm.length + conditionsList.size() ];
			System.arraycopy(perm, 0, result, 0, perm.length );
			for (int i=0; i<conditionsList.size(); i++) {
				result[ perm.length + i] = (String) conditionsList.get(i);
			}
			return result;
		}
	}


	/*
	private String[][] getVariablePermuations() {
		int numberOfBooleanVariables = 0;
		int numberOfBooleanPermuations = 1;
		for (int i=0; i<this.variables.length; i++) {
			PolishVariable var = this.variables[i];
			if ("boolean".equals(var.getType())) {
				numberOfBooleanVariables++;
				numberOfBooleanPermuations *= numberOfBooleanVariables;
			}
		}
		System.out.println("Number of permutations: " + numberOfBooleanPermuations + ", numberOfBooleanVariables=" + numberOfBooleanVariables);
		String[][] perms = new String[numberOfBooleanPermuations][];
		for (int i = 0; i < perms.length; i++) {
			int booleanIndex = 0;
			String[] perm = new String[this.variables.length];
			for (int j=0; j<this.variables.length; j++) {
				PolishVariable var = this.variables[j];
				if ("boolean".equals(var.getType())) {
					perm[j] = var.getName() + "=" + getBooleanPermuationValue( i, booleanIndex );
					booleanIndex++;
				} else {
					perm[j] = var.getName() + "=" + var.getDefaultValue();
				}
			}
			
			perms[i] = perm;
		}
		
		return perms;
	}
	*/


	private boolean getBooleanPermuationValue(int permutationIndex, int variableIndex ) 
	{
		return (permutationIndex & (1 << variableIndex)) != 0;
	}


	private static void handleConfigError(String text) {
		System.err.println("Configuration error: " + text);
		File logFile = new File("configuration-error-" + System.currentTimeMillis() + ".log");
		try {
			FileUtil.writeTextFile(logFile, new String[]{ text } );
			executeProcessLogCommand(logFile);
		} catch (Exception e) {
			System.err.println("Unable to handle config error: " + e );
			e.printStackTrace();
		}
	}
	
	private static void executeProcessLogCommand( File logFile ) {
		try {
			ProcessUtil.exec( new String[] { processLogCommand, logFile.getAbsolutePath() }, "", false );
		} catch (Exception e) {
			System.err.println("Unable to execute process log command: " + e);
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidComponentException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args)  {
		System.out.println("J2ME Polish QA Builder " + VERSION );
		try {
			String polishHome = null;
			String projectsHome = null;
			boolean isVerbose = false;
			boolean random = false;
			int maxPermutations = -1;
			if (args.length > 1) {
				for (int i = 0; i < args.length; i++) {
					String arg = args[i];
					if (arg.startsWith("polish.home=")) {
						polishHome = arg.substring("polish.home=".length());
					} else if (arg.startsWith("project=")) {
						projectsHome = arg.substring("project=".length());
					} else if (arg.startsWith("projects=")) {
						projectsHome = arg.substring("projects=".length());
					} else if (arg.startsWith("-random")) {
						random = true;
					} else if (arg.startsWith("permutations=")) {
						maxPermutations = Integer.parseInt( arg.substring("permutations=".length()) );
					} else if (arg.startsWith("command=")) {
						processLogCommand = arg.substring("command=".length());
					} else if (arg.equals("-verbose")) {
						isVerbose = true;
					}
				}
			} else {
				usage();
				System.exit(1);
			}
			if (polishHome == null || projectsHome == null ) {
				usage();
				System.exit(1);
			}
			if (isVerbose) {
				System.out.println("QABuilder launching...");
			}
			QABuilder builder = new QABuilder( new File( polishHome ), random, maxPermutations );
			builder.isVerbose = isVerbose;
			if (isVerbose) {
				System.out.println("Starting with project...");
			}
			String[] projects = StringUtil.split(projectsHome, File.pathSeparatorChar);
			builder.buildProjects( projects );
		} catch (Exception e) {
			handleConfigError( "Unexpected configuration problem: " + e.toString() );
			System.err.println("Configuration error: " + e.toString() );
			e.printStackTrace();
			System.exit(1);
		}
	}



	private static void usage() {
		System.out.println("Usage:");
		System.out.println("java de.enough.polish.qualitycontrol.QABuilder [-verbose] polish.home=${polish.home} project(s)=${path to project(s)} command=${command that should be executed upon build error}");
		System.out.println("Order of parameters does not matter, parameter can also be given as environment variables.");
		System.out.println("The command receives the path to a log file containing all information about the encountered error.");
		System.out.println("");
		System.out.println("Invocation example: "); 
		System.out.println("java -cp lib/ant.jar:lib/jdom.jar:bin/classes/ de.enough.polish.qualitycontrol.QABuilder polish.home=. project=../enough-polish-sample-rssbrowser:../enough-polish-sample-email/ -random permutations=6 command=sendmailtoteam");
	}


}
