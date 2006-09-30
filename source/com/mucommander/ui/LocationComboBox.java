
package com.mucommander.ui;

import com.mucommander.bookmark.Bookmark;
import com.mucommander.bookmark.BookmarkManager;
import com.mucommander.file.AbstractFile;
import com.mucommander.file.RootFolders;
import com.mucommander.ui.comp.progress.ProgressTextField;
import com.mucommander.ui.event.LocationEvent;
import com.mucommander.ui.event.LocationListener;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.*;
import java.awt.event.*;


public class LocationComboBox extends JComboBox implements LocationListener, ActionListener, KeyListener, FocusListener {

    /** FolderPanel this combo box is displayed in */
    private FolderPanel folderPanel;
    /** Text field used to type in a location */
    private ProgressTextField locationField;

    /** When true, any action or key events received will be ignored */
    private boolean ignoreEvents = true;    // Events are ignored until location is changed for the first time

    /** Semi-transparent color used to display progress in the location field */
    private final static Color PROGRESS_COLOR = new Color(0, 255, 255, 64);


    /**
     * Creates a new LocationComboBox for use in the given FolderPanel.
     *
     * @param folderPanel FolderPanel this combo box is displayed in
     */
    public LocationComboBox(FolderPanel folderPanel) {
        this.folderPanel = folderPanel;

        // Use a custom text field that can display loading progress when changing folders
        this.locationField = new ProgressTextField(0, PROGRESS_COLOR);
        setEditor(new BasicComboBoxEditor() {
                public Component getEditorComponent() {
                    return LocationComboBox.this.locationField;
                }
            });

        // Make this combo box editable
        setEditable(true);

        // Listen to action events generated by the combo box (popup menu selection)
        addActionListener(this);
        // Listen to key events generated by the text field (enter and escape)
        locationField.addKeyListener(this);
        // Listen to location changes (to update popup menu choices)
        folderPanel.getLocationManager().addLocationListener(this);

        // Listen to focus events to temporarily disable the MainFrame's JMenuBar when this component has the keyboard focus.
        // Not doing so would trigger unwanted menu bar actions when typing.
        locationField.addFocusListener(this);
        addFocusListener(this);

        // Prevent up/down keys from firing ActionEvents (default behavior is plain stupid)
        // Java 1.3
        putClientProperty("JComboBox.lightweightKeyboardNavigation","Lightweight");
        // Java 1.4 and up
        putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    }


    /**
     * Returns the text field used to type in a location.
     */
    public ProgressTextField getLocationField() {
        return this.locationField;
    }


    ////////////////////////
    // Overridden methods //
    ////////////////////////

    /**
     * Overrides this method to ignore events received when this component is disabled.
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.ignoreEvents = !enabled;
    }

    
    //////////////////////////////
    // LocationListener methods //
    //////////////////////////////

    public void locationChanged(LocationEvent e) {
        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("called");

        // Remove all choices corresponding to previous current folder
        removeAllItems();

        // Add choices corresponding to the new current folder
        // /!\ Important note: combo box seems to fire action events when items
        // are added so it's necessary to ignore events while items are being added
        AbstractFile folder = e.getFolderPanel().getCurrentFolder();
        // Start by adding current folder, and all parent folders up to root
        do {
            addItem(folder);
        }
        while((folder=folder.getParent())!=null);

        // Re-enable component and stop ignoring events
        setEnabled(true);
    }

    public void locationChanging(LocationEvent e) {
        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("called");

        // Disable component and ignore events until folder has been changed (or cancelled)
        // Note: setEnabled(false) will have already been called if folder was changed by this component 
        if(isEnabled())
            setEnabled(false);
    }

    public void locationCancelled(LocationEvent e) {
        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("called");

        // Re-enable component and stop ignoring events
        setEnabled(true);
    }


    ////////////////////////////
    // ActionListener methods //
    ////////////////////////////

    public void actionPerformed(ActionEvent e) {
        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("called, "+"selectedIndex="+getSelectedIndex()+", selectedItem="+getSelectedItem()+" ignoreEvents="+ignoreEvents);

        // Return if events should be ignored
        if(ignoreEvents)
            return;

        Object selectedItem = getSelectedItem();
        // If a folder was selected in the combo popup menu, change current folder to the selected one
        if(selectedItem!=null) {
            // Disable component and ignore events until folder has been changed (or cancelled)
            setEnabled(false);
            // Explicitely hide popup, seems to be necessary under Windows/Java 1.5
            hidePopup();
            // Change folder
            folderPanel.trySetCurrentFolder((AbstractFile)selectedItem);
        }
    }


    /////////////////////////
    // KeyListener methods //
    /////////////////////////

    public void keyPressed(KeyEvent e) {
        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("called, keyCode="+e.getKeyCode()+" ignoreEvents="+ignoreEvents);

        // Return if events should be ignored or if popup is visible (events would pertain to combo popup, not text field)
        if(ignoreEvents || isPopupVisible())
            return;

        // ESC cancels location change, restores original location in the field and transfers focus to file table
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            locationField.setText(folderPanel.getCurrentFolder().getAbsolutePath());
            folderPanel.getFileTable().requestFocus();
        }
        // ENTER changes current folder to the folder entered in the location field,
        // or to a bookmark or root folder that match the entered string
        else if(e.getKeyCode()==KeyEvent.VK_ENTER) {
            // Disable component and ignore events until folder has been changed (or cancelled)
            setEnabled(false);

            String locationText = locationField.getText();

            // Look for a bookmark which name is the entered string (case insensitive)
            Bookmark b = BookmarkManager.getBookmark(locationText);
            if(b!=null) {
                // Change the current folder to the bookmark's location
                folderPanel.trySetCurrentFolder(b.getURL());
                return;
            }

            // Look for a root folder which name is the entered string (case insensitive)
            AbstractFile rootFolders[] = RootFolders.getRootFolders();
            for(int i=0; i<rootFolders.length; i++) {
                if(rootFolders[i].getName().equalsIgnoreCase(locationText)) {
                    // Change the current folder to the root folder
                    folderPanel.trySetCurrentFolder(rootFolders[i]);
                    return;
                }
            }

            // Change folder
            folderPanel.trySetCurrentFolder(locationText);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }


    ///////////////////////////
    // FocusListener methods //
    ///////////////////////////

    public void focusGained(FocusEvent e) {
        // Disable menu bar when this component has gained focus
        folderPanel.getMainFrame().getJMenuBar().setEnabled(false);
    }

    public void focusLost(FocusEvent e) {
        // Enable menu bar when this component has lost focus
        folderPanel.getMainFrame().getJMenuBar().setEnabled(true);
    }
}
