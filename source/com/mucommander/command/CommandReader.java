/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (c) 2002-2007 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with muCommander; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.mucommander.command;

import com.mucommander.Debug;
import com.mucommander.xml.parser.ContentHandler;
import com.mucommander.xml.parser.Parser;

import java.io.InputStream;
import java.util.Hashtable;

/**
 * Class used to parse custom commands XML files.
 * <p>
 * Command file parsing is done through the {@link #read(InputStream,CommandBuilder,String) read} method, which is
 * the only way to interact with this class.
 * </p>
 * <p>
 * Note that while this class knows how to read the content of an command XML file, its role is not to interpret it. This
 * is done by instances of {@link CommandBuilder}.
 * </p>
 * @see    CommandsXmlConstants
 * @see    CommandBuilder
 * @see    CommandWriter
 * @author Nicolas Rinaudo
 */
public class CommandReader implements ContentHandler, CommandsXmlConstants {
    // - Instance variables --------------------------------------------------
    // -----------------------------------------------------------------------
    /** Where to send building messages. */
    private CommandBuilder builder;



    // - Initialisation ------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Creates a new command reader.
     * @param b where to send custom command events.
     */
    private CommandReader(CommandBuilder b) {builder = b;}



    // - XML interaction -----------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Parses the content of the specified input stream.
     * <p>
     * This is a convenience method, and is equivalent to calling <code>CommandReader.read(in, b, "UTF-8")</code>.
     * </p>
     * @param  in        where to read command data from.
     * @param  b         where to send building events to.
     * @throws Exception thrown if any error occurs.
     * @see    #read(InputStream,CommandBuilder,String)
     */
    public static void read(InputStream in, CommandBuilder b) throws Exception {read(in, b, "UTF-8");}

    /**
     * Parses the content of the specified input stream.
     * <p>
     * This method will go through the specified input stream and notify the builder of any new command declaration it
     * encounters. Note that parsing is done in a very lenient fashion, and perfectly invalid XML files might not raise
     * an exception. This is not a flaw in the parser, and both allows muCommander to be error resilient and the commands
     * file format to be extended without having to rewrite most of this code.
     * </p>
     * <p>
     * Note that even if an error occurs, both of the builder's {@link CommandBuilder#startBuilding()} and
     * {@link CommandBuilder#endBuilding()} methods will still be called. Parsing will stop at the first error
     * however, so while the builder is guaranteed to receive correct messages, it might not receive all declared
     * commands.
     * </p>
     * @param  in        where to read command data from.
     * @param  b         where to send building events to.
     * @param  encoding  encoding used by <code>in</code>.
     * @throws Exception thrown if any error occurs.
     * @see    #read(InputStream,CommandBuilder)
     */
    public static void read(InputStream in, CommandBuilder b, String encoding) throws Exception {
        if(Debug.ON) Debug.trace("Starting to load custom commands.");
        b.startBuilding();
        try {new Parser().parse(in, new CommandReader(b), encoding);}
        finally {b.endBuilding();}
        if(Debug.ON) Debug.trace("Custom commands succesfully loaded.");
    }



    // - XML methods ---------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * This method is public as an implementation side effect and should not be called directly.
     */
    public void startElement(String uri, String name, Hashtable attributes, Hashtable attURIs) throws Exception {
        // New custom command declaration.
        if(name.equals(ELEMENT_COMMAND)) {
            String  alias;
            String  command;
            int     type;
            String  display;
            Command buffer;

            // Makes sure the required attributes are there.
            if(((alias = (String)attributes.get(ATTRIBUTE_ALIAS)) == null) || ((command = (String)attributes.get(ATTRIBUTE_VALUE)) == null)) {
                if(Debug.ON) Debug.trace("Missing attribute(s) to command declaration, ignoring.");
                return;
            }
            type    = parseCommandType((String)attributes.get(ATTRIBUTE_TYPE));
            display = (String)attributes.get(ATTRIBUTE_DISPLAY);


            // Creates the command and passes it to the builder.
            builder.addCommand(buffer = CommandParser.getCommand(alias, command, type, display));
        }
        else if(Debug.ON) Debug.trace("Unexpected start of element " + name + ", ignoring.");
    }



    // - Unused XML methods --------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * This method is public as an implementation side effect and should not be called directly.
     */
    public void startDocument() {}

    /**
     * This method is public as an implementation side effect and should not be called directly.
     */
    public void endDocument() {}

    /**
     * This method is public as an implementation side effect and should not be called directly.
     */
    public void characters(String s) {}

    /**
     * This method is public as an implementation side effect and should not be called directly.
     */
    public void endElement(String uri, String name) {}



    // - Misc. ---------------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Returns the integer value of the specified command type.
     * <p>
     * Note that this method is not strict in the arguments it receives:
     * <ul>
     *   <li>If <code>type</code> equals {CommandsXmlConstants#VALUE_SYSTEM}, {@link Command#SYSTEM_COMMAND} will be returned.</li>
     *   <li>If <code>type</code> equals {CommandsXmlConstants#VALUE_INVISIBLE}, {@link Command#INVISIBLE_COMMAND} will be returned.</li>
     *   <li>In any other case, {@link Command.NORMAL_COMMAND} will be returned.</li>
     * </ul>
     * </p>
     * @param  type type to analyse.
     * @return      <code>type</code>'s integer equivalent.
     */
    private static int parseCommandType(String type) {
        if(type == null)
            return Command.NORMAL_COMMAND;
        if(type.equals(VALUE_SYSTEM))
            return Command.SYSTEM_COMMAND;
        if(type.equals(VALUE_INVISIBLE))
           return Command.INVISIBLE_COMMAND;
        return Command.NORMAL_COMMAND;
    }
}
