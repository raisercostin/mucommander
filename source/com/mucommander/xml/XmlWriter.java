/**
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2007 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.xml;

import java.io.Writer;
import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Used to write pretty-printed XML content.
 * <p>
 * Application writers should keep in mind that this class does not perform any sort
 * of coherency check, and will not prevent them from closing elements they haven't opened yet,
 * or any other thing that would make the XML output invalid.
 * </p>
 * @author Nicolas Rinaudo
 */
public class XmlWriter {
    // - Constants -------------------------------------------------------
    // -------------------------------------------------------------------
    /** Number of space characters used for one level of indentation. */
    private static final int    OFFSET_INCREMENT    = 4;
    /** Identifier for publicly accessible objects. */
    public  static final String AVAILABILITY_PUBLIC = "PUBLIC";
    /** Identifier for system resources. */
    public  static final String AVAILABILITY_SYSTEM = "SYSTEM";
    /** Default output encoding. */
    public static final String  DEFAULT_ENCODING    = "UTF-8";



    // - XML standard entities -------------------------------------------
    // -------------------------------------------------------------------
    /** Forbiden XML characters. */
    private final static String[] ENTITIES            = new String[] {"&", "\"" , "'", "<", ">"};
    /** What to replace forbiden XML characters with. */
    private final static String[] ENTITY_REPLACEMENTS = new String[] {"&amp;", "&quot;", "&apos;", "&lt;", "&gt;"};



    // - Instance fields -------------------------------------------------
    // -------------------------------------------------------------------
    /** Where to write the XML content to. */
    private PrintWriter out;
    /** Current indentation offset. */
    private int         offset;
    /** Whether the next element opening or closing operation should be indented. */
    private boolean     printIndentation;



    // - Initialisation --------------------------------------------------
    // -------------------------------------------------------------------
    /**
     * Creates an <code>XmlWriter</code> that will write to the specified file.
     * <p>
     * This is a convenience constructor and is strictly equivalent to
     * <code>{@link #XmlWriter(OutputStream,String) XmlWriter}(new FileOutputStream(file), {@link #DEFAULT_ENCODING})</code>.
     * </p>
     * @param  file                  where to write XML output to.
     * @throws FileNotFoundException if <code>file</code> could not be found.
     */
    public XmlWriter(File file) throws FileNotFoundException {this(new FileOutputStream(file));}

    /**
     * Creates an <code>XmlWriter</code> that will write to the specified file using the specified encoding.
     * <p>
     * This is a convenience constructor and is strictly equivalent to
     * <code>{@link #XmlWriter(OutputStream,String) XmlWriter}(new FileOutputStream(file), encoding)</code>.
     * </p>
     * @param  file                         where to write XML output to.
     * @param  encoding                     encoding to use when writing the XML content.
     * @throws FileNotFoundException        if <code>file</code> could not be found.
     * @throws UnsupportedEncodingException if <code>encoding</code> is not supported.
     */
    public XmlWriter(File file, String encoding) throws FileNotFoundException, UnsupportedEncodingException {this(new FileOutputStream(file), encoding);}

    /**
     * Creates an <code>XmlWriter</code> that will write to the specified output stream.
     * <p>
     * This is a convenience constructor and is strictly equivalent to
     * <code>{@link #XmlWriter(OutputStream,String) XmlWriter}(stream, {@link #DEFAULT_ENCODING})</code>.
     * </p>
     * @param stream where to write XML output to.
     */
    public XmlWriter(OutputStream stream) {
        try {init(new OutputStreamWriter(stream, DEFAULT_ENCODING), DEFAULT_ENCODING);}
        // UTF-8 is guaranteed to be supported by the Java specifications,
        // we can safely ignore this exception.
        catch(UnsupportedEncodingException e) {}
    }

    /**
     * Creates an <code>XmlWriter</code> that will write to the specified stream using the specified encoding.
     * @param  stream                       where to write XML output to.
     * @param  encoding                     encoding to use when writing the XML content.
     * @throws UnsupportedEncodingException if <code>encoding</code> is not supported.
     */
    public XmlWriter(OutputStream stream, String encoding) throws UnsupportedEncodingException {init(new OutputStreamWriter(stream, encoding), encoding);}

    private void init(Writer writer, String encoding) {
        out = new PrintWriter(writer, true);
        out.print("<?xml version=\"1.0\" encoding=\"");
        out.print(encoding);
        out.println("\"?>");
    }


    // - Element operations --------------------------------------------------
    // -------------------------------------------------------------------
    /**
     * Writes the document type declaration of the XML file.
     * <p>
     * For the generated XML content to be valid, application writers should make sure that this
     * is the very first method they call. This class doesn't ensure coherency and won't
     * complain if the <code>DOCTYPE</code> statement is the last in the file.
     * </p>
     * <p>
     * Both <code>description</code> and <code>url</code> can be set to <code>null</code>.
     * If so, they will just be ignored.
     * </p>
     * @param topElement   label of the top element in the XML file.
     * @param availability availability of the document (expected to be either {@link #AVAILABILITY_PUBLIC}
     *                     or {@link #AVAILABILITY_SYSTEM}, but this is not enforced).
     * @param description  description of the file (see DOCTYPE specifications for more information).
     * @param url          URL at which the DTD of the XML file can be downloaded.
     */
    public void writeDocType(String topElement, String availability, String description, String url) {
        // Writes the compulsory bits.
        out.print("<!DOCTYPE ");
        out.print(topElement);
        out.print(' ');
        out.print(availability);

        // Writes the description if present.
        if(description != null) {
            out.print(' ');
            out.print('\"');
            out.print(description);
            out.print('\"');
        }

        // Writes the DTD url if present.
        if(url != null) {
            out.print(' ');
            out.print('\"');
            out.print(url);
            out.print('\"');
        }
        out.println('>');
    }

    /**
     * Writes an element opening sequence.
     * <p>
     * Elements opened using this method will not have any attribute, and will
     * need to be closed using an {@link #endElement(String) endElement} call.
     * </p>
     * @param name name of the element to open.
     * @see        #startElement(String,XmlAttributes)
     * @see        #writeStandAloneElement(String)
     * @see        #writeStandAloneElement(String,XmlAttributes)
     */
    public void startElement(String name) {startElement(name, false, null, false);}

    public void startElement(String name, boolean lineBreak) {startElement(name, false, null, lineBreak);}

    /**
     * Writes a stand-alone element.
     * <p>
     * Elements opened using this method will not have any attributes, and will be
     * closed immediately.
     * </p>
     * @param name name of the element to write.
     * @see        #startElement(String,XmlAttributes)
     * @see        #startElement(String)
     * @see        #writeStandAloneElement(String)
     */
    public void writeStandAloneElement(String name) {startElement(name, true, null, true);}

    /**
     * Writes an element opening sequence.
     * <p>
     * Elements opened using this method will need to be closed using an {@link #endElement(String) endElement} call.
     * </p>
     * @param name       name of the element to open.
     * @param attributes attributes that this element will have.
     * @see              #startElement(String)
     * @see              #writeStandAloneElement(String)
     * @see              #writeStandAloneElement(String,XmlAttributes)
     */
    public void startElement(String name, XmlAttributes attributes) {startElement(name, false, attributes, false);}

    public void startElement(String name, XmlAttributes attributes, boolean lineBreak) {startElement(name, false, attributes, lineBreak);}

    /**
     * Writes a stand-alone element.
     * <p>
     * Elements opened using this method will not need to be closed
     * </p>
     * @param name       name of the element to write.
     * @param attributes attributes that this element will be closed immediately.
     * @see              #startElement(String)
     * @see              #startElement(String,XmlAttributes)
     * @see              #writeStandAloneElement(String)
     */
    public void writeStandAloneElement(String name, XmlAttributes attributes) {startElement(name, true, attributes, true);}

    /**
     * Writes an element opening sequence.
     * @param name         name of the element to open.
     * @param isStandAlone whether or not this element should be closed immediately.
     * @param attributes   XML attributes for this element.
     */
    private void startElement(String name, boolean isStandAlone, XmlAttributes attributes, boolean lineBreak) {
        // Prints indentation if necessary.
        indent();

        // Opens the element.
        out.print('<');
        out.print(name);

        // Writes attributes, if any.
        if(attributes != null) {
            Iterator names;
            String   attName;

            names = attributes.names();
            while(names.hasNext()) {
                attName = (String)names.next();
                out.print(' ');
                out.print(attName);
                out.print("=\"");
                out.print(escape(attributes.getValue(attName)));
                out.print("\"");
            }
        }

        // Closes the element if necessary.
        if(isStandAlone)
            out.print('/');
        else
            offset += OFFSET_INCREMENT;

        // Finishes the element opening sequence.
        out.print('>');

        // Stand-alone elements are followed by a line break.
        if(lineBreak)
            println();
    }

    /**
     * Writes an element closing sequence.
     * @param name name of the element to close.
     */
    public void endElement(String name) {
        // Updates the indentation, and prints it if necessary.
        offset -= OFFSET_INCREMENT;
        indent();

        // Writes the element closing sequence.
        out.print("</");
        out.print(name);
        out.print('>');
        println();
    }



    // - CDATA handling --------------------------------------------------
    // -------------------------------------------------------------------
    /**
     * Writes the specified CDATA to the XML stream.
     * @param cdata content to write to the XML stream.
     */
    public void writeCData(String cdata) {
        indent();
        out.print(escape(cdata));
    }

    /**
     * Escapes XML content, replacing special characters by their proper value.
     * @param  data data to escape.
     * @return the escaped content.
     */
    private String escape(String data) {
        int position;

        for(int i = 0; i < ENTITIES.length; i++) {
            position = 0;
            while((position = data.indexOf(ENTITIES[i], position)) != -1) {
                data = data.substring(0, position) + ENTITY_REPLACEMENTS[i] +
                    (position == data.length() -1 ? "" : data.substring(position + 1, data.length()));
                position = position + ENTITY_REPLACEMENTS[i].length();
            }
        }

        return data;
    }



    // - Indentation handling --------------------------------------------
    // -------------------------------------------------------------------
    /**
     * Prints a line break.
     */
    public void println() {
        out.println();
        printIndentation = true;
    }

    /**
     * If necessary, prints indentation.
     */
    private void indent() {
        if(printIndentation) {
            for(int i = 0; i < offset; i++)
                out.print(' ');
            printIndentation = false;
        }
    }



    // - Misc. -----------------------------------------------------------
    // -------------------------------------------------------------------
    /**
     * Closes the XML stream.
     */
    public void close() throws IOException {out.close();}
}
