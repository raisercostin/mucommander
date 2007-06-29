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

package com.mucommander.auth;

import com.mucommander.file.FileURL;
import com.mucommander.xml.writer.XmlAttributes;
import com.mucommander.xml.writer.XmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class provides a method to write the credentials XML file.
 *
 * @author Maxence Bernard
 */
public class CredentialsWriter implements CredentialsConstants {

    /**
     * Writes the credentials XML file in the user's preferences folder.
     * This method should only be called by {@link CredentialsManager}.
     */
    static void write(OutputStream stream) throws IOException {

        XmlWriter out  = new XmlWriter(stream);

        // Root element, add the encryption method used
        XmlAttributes attributes = new XmlAttributes();
        attributes.add(ATTRIBUTE_ENCRYPTION, WEAK_ENCRYPTION_METHOD);
        out.startElement(ELEMENT_ROOT, attributes);
        out.println();

        // Add muCommander version
        out.startElement(ELEMENT_VERSION);
        out.writeCData(com.mucommander.RuntimeConstants.VERSION);
        out.endElement(ELEMENT_VERSION);

        Iterator iterator = CredentialsManager.getPersistentCredentials().iterator();
        MappedCredentials credentials;
        FileURL realm;
        Enumeration propertyKeys;
        String name;

        while(iterator.hasNext()) {
            credentials = (MappedCredentials)iterator.next();
            realm = credentials.getRealm();

            // Start credentials element
            out.startElement(ELEMENT_CREDENTIALS);
            out.println();

            // Write URL
            out.startElement(ELEMENT_URL);
            out.writeCData(realm.toString(false));
            out.endElement(ELEMENT_URL);

            // Write login
            out.startElement(ELEMENT_LOGIN);
            out.writeCData(credentials.getLogin());
            out.endElement(ELEMENT_LOGIN);

            // Write password
            out.startElement(ELEMENT_PASSWORD);
            out.writeCData(credentials.getEncryptedPassword());
            out.endElement(ELEMENT_PASSWORD);

            // Write properties, each property is stored in a separate 'property' element
            propertyKeys = realm.getPropertyKeys();
            if(propertyKeys!=null) {
                while(propertyKeys.hasMoreElements()) {
                    name = (String)propertyKeys.nextElement();
                    attributes = new XmlAttributes();
                    attributes.add(ATTRIBUTE_NAME, name);
                    attributes.add(ATTRIBUTE_VALUE, realm.getProperty(name));
                    out.startElement(ELEMENT_PROPERTY, attributes);
                    out.endElement(ELEMENT_PROPERTY);
                }
            }

            // End credentials element
            out.endElement(ELEMENT_CREDENTIALS);
        }

        // End root element
        out.endElement(ELEMENT_ROOT);
    }
}
