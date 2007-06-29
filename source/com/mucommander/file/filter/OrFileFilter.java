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

package com.mucommander.file.filter;

import com.mucommander.file.AbstractFile;

/**
 * AndFileFilter is a {@link ChainedFileFilter} that must statisfy only one of the registered filters'
 * {@link FileFilter#accept(AbstractFile)} methods. If any of those methods returns true, the file will be accepted.
 *
 * If this {@link ChainedFileFilter} contains no filter, {@link #accept(AbstractFile)} will always return true.
 *
 * @author Maxence Bernard
 */
public class OrFileFilter extends ChainedFileFilter {

    /**
     * Creates a new AndFileFilter that initially contains no {@link FileFilter}.
     */
    public OrFileFilter() {
    }

    
    ///////////////////////////////
    // FileFilter implementation //
    ///////////////////////////////

    /**
     * Calls the registered filters' {@link FileFilter#accept(AbstractFile)} methods, and returns true if one of them
     * accepted the given AbstractFile (i.e. returned true). Returns false if none of them accepted the file.
     *
     * <p>If this {@link ChainedFileFilter} contains no filter, true will always be returned.
     *
     * @param file the file to test against the registered filters
     * @return if the file was accepted by one filter, false if it was rejected by one filter
     */
    public synchronized boolean accept(AbstractFile file) {
        int nbFilters = filters.size();

        if(nbFilters==0)
            return true;

        for(int i=0; i<nbFilters; i++)
            if(((FileFilter)filters.elementAt(i)).accept(file))
                return true;

        return false;
    }
}
