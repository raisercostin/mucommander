package com.mucommander.ui.action;

import com.mucommander.ui.MainFrame;

/**
 * This action swaps both FileTable's current folders: the left table's current folder becomes the right table's one
 * and vice versa.
 *
 * @author Maxence Bernard
 */
public class SwapFoldersAction extends MucoAction {

    public SwapFoldersAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    public void performAction() {
        mainFrame.swapFolders();
    }
}
