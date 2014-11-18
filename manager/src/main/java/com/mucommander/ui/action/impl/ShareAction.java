package com.mucommander.ui.action.impl;

import com.mucommander.commons.file.util.FileSet;
import com.mucommander.share.ShareProvider;
import com.mucommander.ui.action.MuAction;
import com.mucommander.ui.main.MainFrame;
import java.util.Map;

/**
 *
 * @author Mathias
 */
public class ShareAction extends MuAction {

    private ShareProvider provider;

    public ShareAction(MainFrame mainFrame, Map<String, Object> properties, ShareProvider provider) {
        super(mainFrame, properties);
        this.provider = provider;
        setLabel(provider.getDisplayName());
    }

    @Override
    public void performAction() {

        FileSet selectedFiles;

        // Retrieves the current selection.
        selectedFiles = mainFrame.getActiveTable().getSelectedFiles();

        // If no files are either selected or marked, aborts.
        if (selectedFiles.size() == 0) {
            return;
        }
        provider.handleFiles(selectedFiles);
    }

}
