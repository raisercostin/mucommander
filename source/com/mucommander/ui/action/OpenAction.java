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

package com.mucommander.ui.action;

import com.mucommander.PlatformManager;
import com.mucommander.file.AbstractFile;
import com.mucommander.file.FileFactory;
import com.mucommander.file.FileProtocols;
import com.mucommander.file.impl.local.LocalFile;
import com.mucommander.job.TempExecJob;
import com.mucommander.text.Translator;
import com.mucommander.ui.MainFrame;
import com.mucommander.ui.ProgressDialog;
import com.mucommander.ui.table.FileTable;

import java.util.Hashtable;

/**
 * This action 'opens' the currently selected file or folder in the active FileTable.
 * This means different things depending on the kind of file that is currently selected:
 * <ul>
 * <li>For browsable files (directory, archive...): shows file contents
 * <li>For local file that are not an archive or archive entry: executes file with native file associations
 * <li>For any other file type, remote or local: copies file to a temporary local file and executes it with native file associations
 * </ul>
 *
 * @author Maxence Bernard
 */
public class OpenAction extends MucoAction {

    public OpenAction(MainFrame mainFrame, Hashtable properties) {
        super(mainFrame, properties);
    }

    public void performAction() {
        FileTable fileTable = mainFrame.getActiveTable();
        AbstractFile selectedFile = fileTable.getSelectedFile(true);

        if(selectedFile==null)
            return;

        // Browsable file: show file contents
        if(selectedFile.isBrowsable())
            fileTable.getFolderPanel().tryChangeCurrentFolder(selectedFile);
        // Local file that is not an archive or archive entry: execute file with native file associations
        else if(selectedFile.getURL().getProtocol().equals(FileProtocols.FILE) && (selectedFile instanceof LocalFile))
            PlatformManager.open(selectedFile);
        // Any other file remote or local: copy file to a temporary local file and execute it with native file associations
        else {
            ProgressDialog progressDialog = new ProgressDialog(mainFrame, Translator.get("copy_dialog.copying"));
            TempExecJob job = new TempExecJob(progressDialog, mainFrame, selectedFile, FileFactory.getTemporaryFile(selectedFile.getName(), true));
            progressDialog.start(job);
        }
    }
}
