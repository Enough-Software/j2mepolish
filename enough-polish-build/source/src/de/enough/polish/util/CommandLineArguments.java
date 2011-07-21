/*
 * Created on 24-Nov-2003 at 15:41:09
 */
package de.enough.polish.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Parses arguments given from the command line.</p>
 * <p></p>
 * <p>copyright Enough Software 2003</p>
 * <pre>
 *    history
 *       24-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CommandLineArguments {
	
	private HashMap allowedFlagsByName;
	private HashMap allowedParametersByName;
	
	private HashMap flags;
	private HashMap parameters;
	private String[] arguments;
	
	private String[] commandLineArguments; 

	/**
	 * Parses the given arguments.
	 * @param args the arguments given in a String array.
	 * @param allowedFlags array of allowed flags, must not be null
	 * @param allowedLongFlags array of allowed flags in the long format, must not be null
	 * @param allowedParameters array of allowed parameters, must not be null
	 * @param allowedLongParameters array of allowed parameters in the long format, must not be null
	 * @throws IllegalArgumentException when there is an invalid argument which has not been specified.
	 */
	public CommandLineArguments( String[] args, String[] allowedFlags, String[] allowedLongFlags, String[] allowedParameters, String[] allowedLongParameters  ) {
		this.allowedFlagsByName = new HashMap();
		populateMap(this.allowedFlagsByName, allowedFlags );
		populateMap(this.allowedFlagsByName, allowedLongFlags );
		this.allowedParametersByName = new HashMap();
		populateMap(this.allowedParametersByName, allowedParameters);
		populateMap(this.allowedParametersByName, allowedLongParameters);
		this.flags = new HashMap();
		this.parameters = new HashMap();
		parseArguments( args );
	}
	
	private static void populateMap( HashMap map, String[] keys ) {
		for (int i = 0; i < keys.length; i++) {
			map.put( keys[i], Boolean.TRUE );
		}
	}
	
	/**
	 * Parses the given arguments.
	 * @param args the arguments given in a String array.
	 * @throws IllegalArgumentException when there is an invalid argument which has not been specified.
	 */
	private void parseArguments(String[] args)
	throws IllegalArgumentException 
	{
		this.flags.clear();
		this.parameters.clear();
		this.commandLineArguments = args;
		ArrayList argumentsList = new ArrayList();
		boolean argumentsStarted = false;
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (this.allowedFlagsByName.get( argument ) != null) {
				if (argumentsStarted) {
					throw new IllegalArgumentException( "flag [" + argument + "] cannot come after argument [" + args[ i -1 ] + "].");
				}
				this.flags.put( argument, Boolean.TRUE );
			} else if ( this.allowedParametersByName.get( argument) != null) {
				if (argumentsStarted) {
					throw new IllegalArgumentException( "parameter [" + argument + "] cannot come after argument [" + args[ i -1 ] + "].");
				}
				i++;
				try {
					String value = args[ i ];
					this.parameters.put( argument, value );
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException( "parameter [" + argument + "] needs a value.");
				}
			} else {
				argumentsStarted = true;
				argumentsList.add( argument );
			}
		}
		this.arguments = (String[]) argumentsList.toArray( new String[ argumentsList.size() ] );
	}
	
	/**
	 * Retrieves all remaining arguments that are given.
	 * 
	 * @return an arrary of normal arguments that were given on the command line.
	 */
	public String[] getArguments() {
		return this.arguments;
	}
	
	public boolean hasFlag( String name, String longName ) {
		if ( this.flags.get( name ) != null) {
			return true;
		} else if ( this.flags.get( longName ) != null ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasParameter( String name, String longName ) {
		if ( this.parameters.get( name ) != null) {
			return true;
		} else if ( this.parameters.get( longName ) != null ) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getParameter( String name, String longName ) {
		String value = (String) this.parameters.get( name );
		if (value == null) {
			value = (String) this.parameters.get( longName ); 
		}
		return value;
	}
	
	public String[] getCommandLineArguments() {
		return this.commandLineArguments;
	}

}
