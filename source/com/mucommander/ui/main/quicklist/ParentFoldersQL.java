/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2008 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.ui.main.quicklist;

import java.util.Vector;

import javax.swing.ImageIcon;

import com.mucommander.file.AbstractFile;
import com.mucommander.file.FileFactory;
import com.mucommander.ui.event.LocationEvent;
import com.mucommander.ui.event.LocationListener;
import com.mucommander.ui.icon.FileIcons;
import com.mucommander.ui.icon.IconManager;
import com.mucommander.ui.main.FolderPanel;
import com.mucommander.ui.quicklist.QuickListWithIcons;
import com.mucommander.text.Translator;

/**
 * This quick list shows the parent folders of the current location in the FileTable.
 * 
 * @author Arik Hadas
 */
public class ParentFoldersQL extends QuickListWithIcons implements LocationListener {
	protected Vector parents = new Vector();
	protected boolean updated = true;
		
	public ParentFoldersQL(FolderPanel folderPanel) {
		super(Translator.get("parent_folders_popup.title"), Translator.get("parent_folders_popup.empty_message"));
		
		folderPanel.getLocationManager().addLocationListener(this);		
	}
	
	protected void acceptListItem(String item) {
		folderPanel.tryChangeCurrentFolder(item);
	}
	
	public void locationChanged(LocationEvent locationEvent) {
		updated = false;
	}
	
	protected void populateParentFolders(AbstractFile folder) {
		parents = new Vector();
				
		while((folder=folder.getParentSilently())!=null)
            parents.add(folder.getAbsolutePath());
    }
	
	public void locationCancelled(LocationEvent locationEvent) {}

	public void locationChanging(LocationEvent locationEvent) {}

	public void locationFailed(LocationEvent locationEvent) {}

	protected ImageIcon getImageIcon(String value) {
		AbstractFile file = FileFactory.getFile(value);
		if (file != null)
			return IconManager.getImageIcon(FileIcons.getFileIcon(file));
		return null;
	}

	public Object[] getData() {
		if (!updated ? (updated = true) : false)
			populateParentFolders(folderPanel.getCurrentFolder());
		
		return parents.toArray();
	}
}
