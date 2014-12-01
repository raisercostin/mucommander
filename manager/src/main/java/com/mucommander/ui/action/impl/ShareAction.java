package com.mucommander.ui.action.impl;

import com.mucommander.commons.file.util.FileSet;
import com.mucommander.job.FileJob;
import com.mucommander.share.ShareProgressDialog;
import com.mucommander.share.ShareProvider;
import com.mucommander.text.Translator;
import com.mucommander.ui.action.MuAction;
import com.mucommander.ui.dialog.file.ProgressDialog;
import com.mucommander.ui.main.MainFrame;
import java.util.Map;

/**
 *
 * @author Mathias
 */
public class ShareAction extends MuAction {

    private ShareProvider provider;
    private MainFrame mainFrame;

    public ShareAction(MainFrame mainFrame, Map<String, Object> properties, ShareProvider provider) {
        super(mainFrame, properties);
        this.provider = provider;
        this.mainFrame = mainFrame;
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
        // Starts sharing files
        ProgressDialog progressDialog = new ShareProgressDialog(mainFrame, Translator.get("share_dialog.sharing"));
        FileJob job = provider.getJob(progressDialog, mainFrame, selectedFiles);
        progressDialog.start(job);
    }

}
