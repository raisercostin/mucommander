package com.mucommander.ui.action;

import com.mucommander.file.util.FileSet;
import com.mucommander.ui.MainFrame;
import com.mucommander.ui.dnd.ClipboardSupport;
import com.mucommander.ui.dnd.TransferableFileSet;

import java.util.Hashtable;


/**
 * This action copies the filename(s) of the currently selected / marked files(s) to the system clipboard.
 *
 * @author Maxence Bernard
 */
public class CopyFileNamesAction extends SelectedFilesAction {

    public CopyFileNamesAction(MainFrame mainFrame, Hashtable properties) {
        super(mainFrame, properties);
    }

    public void performAction() {
        FileSet selectedFiles = mainFrame.getActiveTable().getSelectedFiles();

        if(selectedFiles.size()>0) {
            // Create a TransferableFileSet and make DataFlavour.stringFlavor (text) the only DataFlavour supported
            TransferableFileSet tfs = new TransferableFileSet(selectedFiles);

            // Disable DataFlavor.javaFileListFlavor support
            tfs.setJavaFileListDataFlavorSupported(true);

            // Disable FileSetDataFlavor support because:
            // a/ we don't want it
            // b/ it would otherwise throw an exception because the data is not serializable
            tfs.setFileSetDataFlavorSupported(false);

            // Transfer filenames, not file paths
            tfs.setStringDataFlavourTransfersFilename(true);

            ClipboardSupport.setClipboardContents(tfs);
        }
    }
}