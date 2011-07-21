/*
 * Created on 18-Jan-2004 at 10:14:15.
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
package de.enough.polish;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.propertyfunctions.VersionFunction;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.PropertyUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Evaluates boolean expressions based on defined (or undefined) symbols and the operators &amp;&amp;, ||, ! and ^.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BooleanEvaluator {
	
	public static final int INVALID = -1;
	public static final int NONE = 0;
	public static final int NOT = 1;
	public static final int AND = 2;
	public static final int OR = 3;
	public static final int XOR = 4;
	public static final int GREATER = 5;
	public static final int LESSER = 6;
	public static final int EQUALS = 7;
	public static final int GREATER_EQUALS = 8;
	public static final int LESSER_EQUALS = 9;
	public static final int NOT_EQUALS = 10;

	private static final String SYMBOL = "(\\w|-|:|\\.|/|_|\")+"; 
	protected static final Pattern SYMBOL_PATTERN = Pattern.compile( SYMBOL );
	
	private static final String COMPARATOR = "(==|>=|<=|>|<|!=)";
	private static final String COMPARATOR_TERM = SYMBOL + "\\s*" + COMPARATOR + "\\s*" + SYMBOL;
	protected static final Pattern COMPARATOR_PATTERN = Pattern.compile( COMPARATOR );
	protected static final Pattern COMPARATOR_TERM_PATTERN = Pattern.compile( COMPARATOR_TERM );
	
	private static final String OPERATOR = "(&&|\\^|\\|\\||==|>=|<=|>|<|!=)";
	
	protected static final Pattern OPERATOR_PATTERN = Pattern.compile( OPERATOR ); 
	private static final String TERM = "\\(\\s*!?\\s*" + SYMBOL + "\\s*(" + OPERATOR 
								       + "\\s*!?\\s*" + SYMBOL + "\\s*)+\\)";
	protected static final Pattern TERM_PATTERN = Pattern.compile( TERM );

	private Map symbols;
	private Map variables;
	private Environment environment;
	private Device device;

	/**
	 * Creates a new boolean evaluator.
	 * 
	 * @param environment the environment settings
	 */ 
	public BooleanEvaluator( Environment environment ) {
		this.environment = environment;
	}

	/**
	 * Creates a new boolean evaluator.
	 * 
	 * @param symbols a map containing all defined symbols
	 * @param variables a map containing all defined variables
	 * @throws NullPointerException when symbols or variables are null
	 */ 
	public BooleanEvaluator( Map symbols, Map variables ) {
		setEnvironment(symbols, variables);
	}

	/**
	 * Sets the environment for this evaluator.
	 * 
	 * @param symbols a map containing all defined symbols
	 * @param variables a map containing all defined variables
	 * @throws NullPointerException when symbols or variables are null
	 */
	public void setEnvironment( Map symbols, Map variables ) {
		if (symbols == null) {
			throw new NullPointerException("Got invalid symbols: [null].");
		} 
		if ( variables == null) {
			throw new NullPointerException("Got invalid variables: [null].");
		}
		this.symbols = symbols;
		this.variables = variables;
	}
	
	/**
	 * Sets the environment for this evaluator.
	 * 
	 * @param device the device
	 */
	public void setEnvironment( Device device ) {
		this.device = device;
	}

	/**
	 * Sets the environment for this evaluator.
	 * 
	 * @param environment the environment
	 */
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	
	/**
	 * Evaluates the given expression.
	 * 
	 * @param expression the expression containing defined (or undefined) symbols and the operators &amp;&amp;, ||, ! and ^.
	 *              A valid expression is for example "( symbol1 ||symbol2 ) &amp;&amp; !symbol3" 
	 * @param fileName the name of the source code file
	 * @param line the line number in the source code file (first line is 1)
	 * @return true when the expression yields to true
	 * @throws BuildException when there is a syntax error in the expression
	 */
	public boolean evaluate( String expression, String fileName, int line ) 
	throws BuildException 
	{
		// main loop: extract all simple expressions (without parenthesisses) and 
		// evaluate each of them.
		
		// replace all tabs by spaces:
		expression = expression.replace('\t', ' ');
		
		// first step: replace all properties:
		if ( this.environment != null) {
			//System.out.println("BooleanEvaluator: prev: " + expression);
			expression = this.environment.writeProperties( expression );
			//System.out.println("BooleanEvaluator: post: " + expression);
		} else {
			expression = PropertyUtil.writeProperties(expression, this.variables);
		}
		// second step: replace " and " with && and " or " with ||
		expression = StringUtil.replace( expression, " and ", " && " );
		expression = StringUtil.replace( expression, " or ", " || " );
		expression = StringUtil.replace( expression, " not ", " !" );
		expression = StringUtil.replace( expression, " xor ", " ^ " );
		if (expression.startsWith("not ")) {
			expression = "!" + expression.substring( "not ".length() );
		}
		// now extract each term:
		Matcher termMatcher = TERM_PATTERN.matcher( expression );
		try {
			boolean foundParenthesis = termMatcher.find(); 
			while ( foundParenthesis ) {
				String group = termMatcher.group();
				String term = group.substring( 1, group.length() -1 ); // the term has no parenthesis
				
				Matcher comparatorTermMatcher = COMPARATOR_TERM_PATTERN.matcher( term );
				while ( comparatorTermMatcher.find() ) {
					String comparatorTerm = comparatorTermMatcher.group();
					boolean result = evaluateTerm( comparatorTerm, fileName, line );
					term = StringUtil.replaceFirst( term, comparatorTerm, "" + result );
				}
				
				boolean result = evaluateTerm( term, fileName, line );
				expression = StringUtil.replaceFirst( expression, group, "" + result );
				
				// find next "(...)" term:
				foundParenthesis = termMatcher.find();
				if (!foundParenthesis) {
					termMatcher = TERM_PATTERN.matcher( expression );
					foundParenthesis = termMatcher.find();
				}
			}
			// now the expression is simplified to a term without parenthesis:
			// first go through any remaning comparisons:
			Matcher comparatorTermMatcher = COMPARATOR_TERM_PATTERN.matcher( expression );
			while ( comparatorTermMatcher.find() ) {
				String comparatorTerm = comparatorTermMatcher.group();
				boolean result = evaluateTerm( comparatorTerm, fileName, line );
				expression = StringUtil.replaceFirst( expression, comparatorTerm, "" + result );
			}
			// then evaluate the really simplified expression and return that value:
			return evaluateTerm( expression, fileName, line );
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Error while evaluating expression [" + expression + "].");
			throw e;
		}
	}

	/**
	 * Evaluates the given simple term.
	 * @param term the simple term without any paranthesis, e.g. "symbol1 &amp;&amp; ! symbol2"
	 * @param fileName the name of the source file
	 * @param line the line number of this term
	 * @return true when the term represents true
	 * @throws BuildException when there is a paranthesis in the term
	 */
	protected boolean evaluateTerm(String term, String fileName, int line)
	throws BuildException
	{
		//System.out.println("EvaluatingTerm: " + term);
		// check for parentheses:
		if (term.indexOf('(') != -1) {
			throw new BuildException(fileName + " line " + line 
					+ ": invalid/additional parenthesis \"(\" in term [" + term 
					+ "] (the term might be simplified)." ); 
		}
		if (term.indexOf(')') != -1) {
			throw new BuildException(fileName + " line " + line 
					+ ": invalid/additional parenthesis \")\" in term [" + term 
					+ "] (the term might be simplified)." ); 
		}
		Matcher symbolMatcher = SYMBOL_PATTERN.matcher( term );
		Matcher operatorMatcher = OPERATOR_PATTERN.matcher( term );
		int lastSymbolEnd = 0;
		boolean result = true;
		int operator = NONE;
		String symbol = null;
		String lastSymbol = null; // is needed for >, <, ==, <= and >=
		while (symbolMatcher.find()) {
			// evaluate symbol:
			symbol = symbolMatcher.group();
			int negatePos = term.indexOf( '!', lastSymbolEnd );
			boolean negate = negatePos != -1 && negatePos < symbolMatcher.start();
			lastSymbolEnd = symbolMatcher.end();
			boolean symbolResult = false;
			if ("true".equals( symbol )) {
				symbolResult = true;
			} else if ("false".equals( symbol)) {
				symbolResult = false;
			} else {
				if (this.environment != null) {
					//System.out.println("BooleanEvaluator: checking symbol " + symbol + " by environment");
					symbolResult = this.environment.hasSymbol( symbol ); // this is done by Envrionment.hasSymbol already: || "true".equals( this.environment.getVariable(symbol));
				} else if (this.device != null) {
					symbolResult = this.device.hasFeature( symbol )  || "true".equals( this.device.getCapability(symbol) );
				} else {
					//System.out.println("BooleanEvaluator: checking symbol " + symbol + " directly...");
					symbolResult = this.symbols.get( symbol ) != null || "true".equals( this.variables.get(symbol) );
				}
			}
			if (negate) {
				symbolResult = !symbolResult;
			}
			// combine temporary result with main result:
			if (operator == NONE) {
				result = symbolResult;
			} else if (operator == AND) {
				result &= symbolResult;
			} else if (operator ==  OR) {
				result |= symbolResult;
			} else if (operator == XOR) {
				result ^= symbolResult; 
			} else if (operator == INVALID) {
				throw new BuildException(fileName + " line " + line 
						+ ": found no operator before symbol [" + symbol + "] in term [" + term 
						+ "] (both symbol and term might be simplified)." );
			} else {
				//System.out.println("comparing [" + lastSymbol + "] with [" + symbol + "].");
				// this is either >, <, ==, >=, <= or !=
				String var;
				// hack for polish.identifier comparisons:
				if ( symbol.equals(getVariable( "polish.Identifier") )) {
					var = symbol;
				} else {
					var = getVariable( symbol ); 
				}
				//System.out.println( symbol + "=" + var);
				if (var == null) {
					if (hasSymbol( symbol )) {
						var = "true";
					} else {
						var = symbol;
					}
				}
				String lastVar;
				if (this.environment != null) {
					lastVar = this.environment.getVariable( lastSymbol );
				} else if (this.device != null) {
					lastVar = this.device.getCapability( lastSymbol );
				} else {
					lastVar = (String) this.variables.get( lastSymbol );
				}
				if (lastVar == null) {
					lastVar = lastSymbol;
				}
				if ( operator == EQUALS || operator == NOT_EQUALS ) {
					if ( (var.charAt( 0 ) == '\"') && (var.charAt( var.length() - 1 ) == '\"')) {
						var = var.substring( 1, var.length() - 1 );
					}
					if ( (lastVar.charAt( 0 ) == '\"') && (lastVar.charAt( lastVar.length() - 1 ) == '\"')) {
						lastVar = lastVar.substring( 1, lastVar.length() - 1 );
					}
					result = var.equals( lastVar );
					if (!result && var.indexOf('.') != -1) {
						// check if this is a version comparison:
						int separatorIndex = var.indexOf('/');
						String versionIdentifier = null;
						if (separatorIndex != -1) {
							versionIdentifier = var.substring(0, separatorIndex);
							var = var.substring(separatorIndex+1);
						}
						var = VersionFunction.process(var);
						try {
							int numVar = Integer.parseInt(var);
							// okay, a version could be parsed, continue with last value:
							lastVar = VersionFunction.process(lastVar, versionIdentifier);
							int numLastVar = Integer.parseInt( lastVar );
							result = numVar == numLastVar;
						} catch (Exception e2) {
							// ignore
						}
					}
					if ( operator == NOT_EQUALS ) {
						result = !result;
					}
					//System.out.println( var + " == " + lastVar + " = " + result);
				} else {
					// this is either >, <, >= or <= - so a numerical comparison is required
					int numVar = -1;
					int numLastVar = -1;
					try {
						boolean nonNumeric = false;
						String versionIdentifier = null;
						try {
							numVar = CastUtil.getInt( var );
						} catch (NumberFormatException e ) {
							nonNumeric = true;
							int separatorIndex = var.indexOf('/');
							if (separatorIndex != -1) {
								versionIdentifier = var.substring(0, separatorIndex);
								var = var.substring(separatorIndex+1);
							}
							if (var.indexOf('.') != -1) {
								// assuming version:
								var = VersionFunction.process(var);
								try {
									numVar = Integer.parseInt(var);
								} catch (Exception e2) {
									System.out.println("Warn: " + fileName + " line " + line + ": integer-variable [" + var + "] could not be parsed.");
								}
							} else {
								System.out.println("Warn: " + fileName + " line " + line + ": integer-variable [" + var + "] could not be parsed.");
							}
						}
						if (nonNumeric) {
							lastVar = VersionFunction.process(lastVar, versionIdentifier);
						} 
						try {
							numLastVar = CastUtil.getInt( lastVar );
						} catch (NumberFormatException e ) {
							System.out.println("Warn: " + fileName + " line " + line + ": integer-variable [" + lastVar + "] could not be parsed.");
						}
					} catch (Exception e) {
						throw new BuildException(fileName + " line " + line 
								+ ": unable to parse integer-arguments [" + symbol + "] or [" 
								+ lastSymbol + "] in term [" + term 
								+ "] (both symbols and term might be simplified)." );
					}
					if (operator == GREATER ) {
						result = numLastVar > numVar;
						//System.out.println( numLastVar + " > " + numVar + " = " + result );
					} else if (operator == LESSER) {
						result = numLastVar < numVar;
						//System.out.println( numLastVar + " < " + numVar + " = " + result );
					} else if (operator == GREATER_EQUALS) {
						result = numLastVar >= numVar;
						//System.out.println( numLastVar + " >= " + numVar + " = " + result );
					} else if (operator == LESSER_EQUALS) {
						result = numLastVar <= numVar;
						//System.out.println( numLastVar + " <= " + numVar + " = " + result );
					} else {
						throw new BuildException(fileName + " line " + line 
								+ ": unknown operator in term [" + term + "]. The term might be simplified." ); 
					}
					//System.out.println("result: " + result);
				}
			}
			
			// evaluate next operator:
			if (operatorMatcher.find()) {
				// check if operator is in the correct position:
				int operatorPos = operatorMatcher.start();
				if (operatorPos < lastSymbolEnd ) {
					throw new BuildException( fileName + " line " + line  
							+": found invalid/additional operator in [" + term +"] (that term might be simplified)." );
					
				}
				int emptyStart = lastSymbolEnd + 1;
				if ( emptyStart < operatorPos ) {
					String empty = term.substring( emptyStart, operatorPos ).trim();
					if (empty.length() > 0) {
						throw new BuildException( fileName + " line " + line  
								+": missing or invalid operator after [" + symbol +"] in term [" + term
								+"] (both symbol and term might be simplified)." );
					}
				}
				// okay operator is in correct position, so know check what operator it is:
				String operatorSymbol = operatorMatcher.group();
				if ("&&".equals( operatorSymbol)) {
					operator = AND;
				} else if ("||".equals( operatorSymbol)) {
					operator = OR;
				} else if ("^".equals( operatorSymbol)) {
					operator = XOR;
				} else if (">".equals( operatorSymbol)) {
					operator = GREATER;
				} else if ("<".equals( operatorSymbol)) {
					operator = LESSER;
				} else if ("==".equals( operatorSymbol)) {
					operator = EQUALS;
				} else if (">=".equals( operatorSymbol)) {
					operator = GREATER_EQUALS;
				} else if ("<=".equals( operatorSymbol)) {
					operator = LESSER_EQUALS;
				} else if ("!=".equals( operatorSymbol)) {
					operator = NOT_EQUALS;
				}
			} else { // no more operator found:
				operator = INVALID;
			}
			lastSymbol = symbol;
			//System.out.println("operator == " + operator);
		} // while there are more symbols
		// check if there are any more operators after the last symbol:
		if (operator != INVALID){
			throw new BuildException( fileName + " line " + line  
					+ ": found invalid/additional operator [" + operatorMatcher.group() 
					+ "] after symbol [" + symbol + "] in term [" + term 
					+ "] (both symbol and term might be simplified)." ); 
		}
		// System.out.println("term result=" + result);
		return result;
	}
	
	private String getVariable( String name ) {
		if (this.environment != null) {
			return this.environment.getVariable( name );
		} else if (this.device != null) {
			return this.device.getCapability( name );
		} else {
			return (String) this.variables.get( name );
		}
	}
	
	private boolean hasSymbol( String name ) {
		if (this.environment != null) {
			return this.environment.hasSymbol(name);
		} else if (this.device != null) {
			return this.device.hasFeature(name);
		} else {
			return this.symbols.get( name ) != null;
		}
	}

	
	public Map getSymbols() {
		return this.symbols;
	}
	
	public Map getVariables() {
		return this.variables;
	}
	
	public Environment getEnvironment() {
		return this.environment;
	}
}
