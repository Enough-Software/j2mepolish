/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */
package de.enough.polish.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

/** A minimalistic XML pull parser, similar to kXML, but
    not supporting namespaces or legacy events. If you need
    support for namespaces, or access to XML comments or
    processing instructions, please use kXML(2) instead. */

public class XmlPullParser implements SimplePullParser {

    static final private String UNEXPECTED_EOF =
        "Unexpected EOF";

    // general

    public boolean relaxed = false;
    private Hashtable entityMap;
    private int depth;
    private String[] elementStack = new String[4];

    // source

    private Reader reader;
    private boolean allowEntitiesInAttributes;

    private char[] srcBuf =
        new char[Runtime.getRuntime().freeMemory() >= 1048576
            ? 8192
            : 128];

    private int srcPos;
    private int srcCount;

    private boolean eof;

    private int line;
    private int column;

    private int peek0;
    private int peek1;

    // txtbuffer

    private char[] txtBuf = new char[128];
    private int txtPos;

    // Event-related

    private int type;
    private String text;
    private boolean isWhitespace;
    private String name;

    private boolean degenerated;
    private int attributeCount;
    private String[] attributes = new String[16];

    private String[] TYPES =
        {
            "Start Document",
            "End Document",
            "Start Tag",
            "End Tag",
            "Text" };

	private boolean doNewline;

    private int read() throws IOException {

        int r = this.peek0;
        this.peek0 = this.peek1;

        if(this.doNewline) {
        	this.line++;
            this.column = 0;
            this.doNewline = false;
        }
        
        if (this.peek0 == -1) {
            this.eof = true;
            return r;
        }
        else if (r == '\n' || (r == '\r' && this.peek0 != '\n')) {
        	// If we are truly at the end of a line, update the line/column information.
        	// We do not swallow any newline characters as XML processors always return any characters.
        	// Its up to the client to trim any unwanted whitespace.
            this.doNewline = true;
        }
        this.column++;

        if (this.srcPos >= this.srcCount) {
            this.srcCount = this.reader.read(this.srcBuf, 0, this.srcBuf.length);
            if (this.srcCount <= 0) {
                this.peek1 = -1;
                return r;
            }
            this.srcPos = 0;
        }

        this.peek1 = this.srcBuf[this.srcPos++];
        return r;
    }

    private void exception(String desc)
        throws IOException {
        throw new IOException(
            desc + " pos: " + getPositionDescription());
    }

    private void push(int c) {
        if (c == 0)
            return;

        if (this.txtPos == this.txtBuf.length) {
            char[] bigger = new char[this.txtPos * 4 / 3 + 4];
            System.arraycopy(this.txtBuf, 0, bigger, 0, this.txtPos);
            this.txtBuf = bigger;
        }

        this.txtBuf[this.txtPos++] = (char) c;
    }

    private void read(char c) throws IOException {
        if (read() != c) {
            if (this.relaxed) {
                if (c <= 32) {
                    skip(); 
                read(); 
            }
            }
            else {
            exception("expected: '" + c + "'");
            }
        }
    }

    private void skip() throws IOException {

        while (!this.eof && this.peek0 <= ' ')
            read();
    }

    private String pop(int pos) {
        String result = new String(this.txtBuf, pos, this.txtPos - pos);
        this.txtPos = pos;
        return result;
    }

    private String readName() throws IOException {

        int pos = this.txtPos;
        int c = this.peek0;
        if ((c < 'a' || c > 'z')
            && (c < 'A' || c > 'Z')
            && c != '_'
            && c != ':'
            && !this.relaxed)
            exception("name expected");

        do {
            push(read());
            c = this.peek0;
        }
        while ((c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || (c >= '0' && c <= '9')
            || c == '_'
            || c == '-'
            || c == ':'
            || c == '.');

        return pop(pos);
    }

    private void parseLegacy(boolean push)
        throws IOException {

        String req = "";
        int term;

        read(); // <
        int c = read();

        if (c == '?') {
            term = '?';
        }
        else if (c == '!') {
            if (this.peek0 == '-') {
                req = "--";
                term = '-';
            }
            else if(this.peek0 == '[')
            {
            	//TODO hack for <![CDATA[]]
            	req = "[CDATA[";
                term = ']';
                this.type = SimplePullParser.TEXT;
                push = true;
            }
            else {
                req = "DOCTYPE";
                term = -1;
            }
        }
        else {
            if (c != '[')
                exception("cantreachme: " + c);
            req = "CDATA[";
            term = ']';
        }

        for (int i = 0; i < req.length(); i++)
            read(req.charAt(i));

        if (term == -1)
            parseDoctype();
        else {
            while (true) {
                if (this.eof)
                    exception(UNEXPECTED_EOF);

                c = read();
                if (push)
                    push(c);

                if ((term == '?' || c == term)
                    && this.peek0 == term
                    && this.peek1 == '>')
                    break;
            }
            read();
            read();

            if (push && term != '?')
                pop(this.txtPos - 1);
        }
    }

    /** precondition: &lt! consumed */

    private void parseDoctype() throws IOException {

        int nesting = 1;

        while (true) {
            int i = read();
            switch (i) {

                case -1 :
                    exception(UNEXPECTED_EOF);
                    break;

                case '<' :
                    nesting++;
                    break;

                case '>' :
                    if ((--nesting) == 0)
                        return;
                    break;
            }
        }
    }

    /* precondition: &lt;/ consumed */

    private void parseEndTag() throws IOException {

        read(); // '<'
        read(); // '/'
        this.name = readName();
        if (this.depth == 0 && !this.relaxed)
            exception("element stack empty");
            
        if (this.name.equals(this.elementStack[this.depth-1]))
          this.depth--;
        else if (!this.relaxed)
            exception("expected: " + this.elementStack[this.depth]);
        skip();
        read('>');
    }

    private int peekType() {
        switch (this.peek0) {
            case -1 :
                return SimplePullParser.END_DOCUMENT;
            case '&' :
                return SimplePullParser.ENTITY_REF;
            case '<' :
                switch (this.peek1) {
                    case '/' :
                        return SimplePullParser.END_TAG;
                    case '[' :
                        return SimplePullParser.CDSECT;
                    case '?' :
                    case '!' :
                        return SimplePullParser.LEGACY;
                    default :
                        return SimplePullParser.START_TAG;
                }
            default :
                return SimplePullParser.TEXT;
        }
    }

    private static String[] ensureCapacity(
        String[] arr,
        int required) {
        if (arr.length >= required)
            return arr;
        String[] bigger = new String[required + 16];
        System.arraycopy(arr, 0, bigger, 0, arr.length);
        return bigger;
    }

    /** Sets name and attributes */

    private void parseStartTag() throws IOException {

        read(); // <
        this.name = readName();
        this.elementStack = ensureCapacity(this.elementStack, this.depth + 1);
        this.elementStack[this.depth++] = this.name;

        while (true) {
            skip();

            int c = this.peek0;

            if (c == '/') {
                this.degenerated = true;
                read();
                skip();
                read('>');
                break;
            }

            if (c == '>') {
                read();
                break;
            }

            if (c == -1)
                exception(UNEXPECTED_EOF);

            String attrName = readName();

            if (attrName.length() == 0)
                exception("attr name expected");

            skip();
            read('=');
            

            skip();
            int delimiter = read();

            if (delimiter != '\'' && delimiter != '"') {
                if (!this.relaxed)
                    exception(
                        "<"
                            + this.name
                            + ">: invalid delimiter: "
                            + (char) delimiter);

                delimiter = ' ';
            }

            int i = (this.attributeCount++) << 1;

            this.attributes = ensureCapacity(this.attributes, i + 4);

            this.attributes[i++] = attrName;

            int p = this.txtPos;

            if (this.allowEntitiesInAttributes) {
            	pushText(delimiter);
            }
            else {
            	pushTextAttribute(delimiter);
            }

            this.attributes[i] = pop(p);

            if (delimiter != ' ')
                read(); // skip endquote
        }
    }

    /** result: isWhitespace; if the setName parameter is set,
    the name of the entity is stored in "name" */

    public final boolean pushEntity() throws IOException {

        read(); // &

        int pos = this.txtPos;

        while (!this.eof && this.peek0 != ';') {
            push(read());
        }

        String code = pop(pos);

        read();

        if (code.length() > 0 && code.charAt(0) == '#') {
            int c =
                (code.charAt(1) == 'x'
                    ? Integer.parseInt(code.substring(2), 16)
                    : Integer.parseInt(code.substring(1)));
            push(c);
            return c <= ' ';
        }

        String result = (String) this.entityMap.get(code);
        boolean whitespace = true;

        if (result == null) {
            result = "&" + code + ";";
        }

        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if (c > ' ') {
                whitespace = false;
            }
            push(c);
        }

        return whitespace;
    }

    /** types:
    '<': parse to any token (for nextToken ())
    '"': parse to quote
    ' ': parse to whitespace or '>'
    */

    private boolean pushText(int delimiter)
        throws IOException {

        boolean whitespace = true;
        int next = this.peek0;

        while (!this.eof
            && next != delimiter) { // covers eof, '<', '"'

            if (delimiter == ' ')
                if (next <= ' ' || next == '>')
                    break;

            if (next == '&') {
                if (!pushEntity())
                    whitespace = false;

            }
            else {
                if (next > ' ')
                    whitespace = false;

                push(read());
            }

            next = this.peek0;
        }

        return whitespace;
    }

    private boolean pushTextAttribute(int delimiter)
    	throws IOException
    {
    	boolean whitespace = true;
    	int next = this.peek0;
    	
    	while (!this.eof
    			&& next != delimiter) { // covers eof, '<', '"'
    		
    		if (delimiter == ' ')
    			if (next <= ' ' || next == '>')
    				break;
    		
    		if (next > ' ')
    			whitespace = false;
    		
    		push(read());
    		next = this.peek0;
    	}
    	
    	return whitespace;
    }

    //--------------- public part starts here... ---------------


    public XmlPullParser(Reader reader)
    	throws IOException
    {
    	this(reader, true);
    }

    public XmlPullParser(Reader reader, boolean allowEntitiesInAttributes)
    	throws IOException
    {
        this.reader = reader;
        this.allowEntitiesInAttributes = allowEntitiesInAttributes;

        this.peek0 = reader.read();
        this.peek1 = reader.read();

        this.eof = this.peek0 == -1;

        this.entityMap = new Hashtable();
        this.entityMap.put("amp", "&");
        this.entityMap.put("apos", "'");
        this.entityMap.put("gt", ">");
        this.entityMap.put("lt", "<");
        this.entityMap.put("quot", "\"");

        this.line = 1;
        this.column = 1;
    }

    public void defineCharacterEntity(
        String entity,
        String value) {
        this.entityMap.put(entity, value);
    }

    public int getDepth() {
        return this.depth;
    }

    public String getPositionDescription() {

        StringBuffer buf =
            new StringBuffer(
                this.type < this.TYPES.length ? this.TYPES[this.type] : "Other");

        buf.append(" @" + this.line + ":" + this.column + ": ");

        if (this.type == SimplePullParser.START_TAG || this.type == SimplePullParser.END_TAG) {
            buf.append('<');
            if (this.type == SimplePullParser.END_TAG)
                buf.append('/');

            buf.append(this.name);
            buf.append('>');
        }
        else if (this.isWhitespace)
            buf.append("[whitespace]");
        else
            buf.append(getText());

        return buf.toString();
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.column;
    }

    public boolean isWhitespace() {
        return this.isWhitespace;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#getText()
     */
    public String getText() {

        if (this.text == null)
            this.text = pop(0);

        return this.text;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#getName()
     */
    public String getName() {
        return this.name;
    }

    public boolean isEmptyElementTag() {
        return this.degenerated;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#getAttributeCount()
     */
    public int getAttributeCount() {
        return this.attributeCount;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#getAttributeName(int)
     */
    public String getAttributeName(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[index << 1];
    }

    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#getAttributeValue(int)
     */
    public String getAttributeValue(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(index << 1) + 1];
    }
    
    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#getAttributeValue(java.lang.String)
     */
    public String getAttributeValue(String attrName) {

        for (int i = (this.attributeCount << 1) - 2;
            i >= 0;
            i -= 2) {
            if (this.attributes[i].equals(attrName))
                return this.attributes[i + 1];
        }

        return null;
    }

    public int getType() {
        return this.type;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.xml.SimplePullParser#next()
     */
    public int next() {

        try
        {
      
        if (this.degenerated) {
            this.type = SimplePullParser.END_TAG;
            this.degenerated = false;
            this.depth--;
            return this.type;
        }

        this.txtPos = 0;
        this.isWhitespace = true;

        do {
            this.attributeCount = 0;

            this.name = null;
            this.text = null;
            this.type = peekType();

            switch (this.type) {

                case SimplePullParser.ENTITY_REF :
                    this.isWhitespace &= pushEntity();
                    this.type = SimplePullParser.TEXT;
                    break;

                case SimplePullParser.START_TAG :
                    parseStartTag();
                    break;

                case SimplePullParser.END_TAG :
                    parseEndTag();
                    break;

                case SimplePullParser.END_DOCUMENT :
                    break;

                case SimplePullParser.TEXT :
                    this.isWhitespace &= pushText('<');
                    break;

                case SimplePullParser.CDSECT :
                    parseLegacy(true);
                    this.isWhitespace = false;
                    this.type = SimplePullParser.TEXT;
                    break;

                default :
                    parseLegacy(false);
            }
        }
        while (this.type > SimplePullParser.TEXT
            || this.type == SimplePullParser.TEXT
            && peekType() >= SimplePullParser.TEXT);

        this.isWhitespace &= this.type == SimplePullParser.TEXT;
        
        }
        catch (IOException e)
        {
          this.type = SimplePullParser.END_DOCUMENT;
        }
        
        return this.type;
    }

    //-----------------------------------------------------------------------------
    // utility methods to mak XML parsing easier ...

    /**
     * test if the current event is of the given type and if the
     * name do match. null will match any namespace
     * and any name. If the current event is TEXT with isWhitespace()=
     * true, and the required type is not TEXT, next () is called prior
     * to the test. If the test is not passed, an exception is
     * thrown. The exception text indicates the parser position,
     * the expected event and the current event (not meeting the
     * requirement.
     *
     * <p>essentially it does this
     * <pre>
     *  if (getType() == TEXT && type != TEXT && isWhitespace ())
     *    next ();
     *
     *  if (type != getType
     *  || (name != null && !name.equals (getName ())
     *     throw new XmlPullParserException ( "....");
     * </pre>
     */
    public void require(int eventType, String eventName)
        throws IOException {

        if (this.type == SimplePullParser.TEXT && eventType != SimplePullParser.TEXT && isWhitespace()) 
        {
            next();
        }

        if (eventType != this.type
            || (eventName != null && !eventName.equals(getName()))) 
        {
            exception("expected: " + this.TYPES[eventType] + "/" + eventName);
        }
    }

    /**
     * If the current event is text, the value of getText is
     * returned and next() is called. Otherwise, an empty
     * String ("") is returned. Useful for reading element
     * content without needing to performing an additional
     * check if the element is empty.
     *
     * <p>essentially it does this
     * <pre>
     *   if (getType != TEXT) return ""
     *    String result = getText ();
     *    next ();
     *    return result;
     *  </pre>
     */

    public String readText() throws IOException {

        if (this.type != SimplePullParser.TEXT)
            return "";

        String result = getText();
        next();
        return result;
    }

}
