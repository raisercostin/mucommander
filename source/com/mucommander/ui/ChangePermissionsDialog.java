package com.mucommander.ui;

import com.mucommander.file.AbstractFile;
import com.mucommander.file.FilePermissions;
import com.mucommander.file.util.FileSet;
import com.mucommander.job.ChangeFileAttributesJob;
import com.mucommander.text.Translator;
import com.mucommander.ui.comp.dialog.DialogToolkit;
import com.mucommander.ui.comp.dialog.FocusDialog;
import com.mucommander.ui.comp.dialog.YBoxPanel;
import com.mucommander.ui.comp.text.SizeConstrainedDocument;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * This dialog allows the user to change the permissions of the currently selected/marked file(s). The permissions can be
 * selected either by clicking individual read/write/executable checkboxes for each of the user/group/other accesses,
 * or by entering an octal permission value. 
 *
 * @author Maxence Bernard
 */
public class ChangePermissionsDialog extends FocusDialog implements FilePermissions, ActionListener, ItemListener, DocumentListener {

    private MainFrame mainFrame;

    private FileSet files;

    private JCheckBox permCheckBoxes[][];

    private JTextField octalPermTextField;

    private JCheckBox recurseDirCheckBox;

    /** If true, ItemEvent events should be ignored */
    private boolean ignoreItemEvent;
    /** If true, DocumentEvent events should be ignored */
    private boolean ignoreDocumentEvent;

    private JButton okButton;
    private JButton cancelButton;


    public ChangePermissionsDialog(MainFrame mainFrame, FileSet files) {
        super(mainFrame, Translator.get(com.mucommander.ui.action.ChangePermissionsAction.class.getName()+".label"), mainFrame);

        this.mainFrame = mainFrame;
        this.files = files;

        YBoxPanel yBoxPanel = new YBoxPanel();

        yBoxPanel.add(new JLabel(Translator.get(com.mucommander.ui.action.ChangePermissionsAction.class.getName()+".tooltip")+" :"));
        yBoxPanel.addSpace(10);

        JPanel gridPanel = new JPanel(new GridLayout(4, 4));
        permCheckBoxes = new JCheckBox[5][5];
        JCheckBox permCheckBox;

        AbstractFile destFile = files.size()==1?files.fileAt(0):files.getBaseFolder();
        int permSetMask = destFile.getPermissionSetMask();
        int defaultPerms = destFile.getPermissions();

        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel(Translator.get("permissions.read")));
        gridPanel.add(new JLabel(Translator.get("permissions.write")));
        gridPanel.add(new JLabel(Translator.get("permissions.executable")));

        for(int a= USER_ACCESS; a>=OTHER_ACCESS; a--) {
            gridPanel.add(new JLabel(Translator.get(a== USER_ACCESS ?"permissions.user":a==GROUP_ACCESS?"permissions.group":"permissions.other")));

            for(int p=READ_PERMISSION; p>=EXECUTE_PERMISSION; p=p>>1) {
//                permCheckBox = new JCheckBox(p==READ_PERMISSION?"read":p==WRITE_PERMISSION?"write":"executable");
                permCheckBox = new JCheckBox();

                permCheckBox.setSelected((defaultPerms & (p<<a*3))!=0);

                // Enable the checkbox only if the permission can be set in the destination
                if((permSetMask & (p<<a*3))==0)
                    permCheckBox.setEnabled(false);
                else
                    permCheckBox.addItemListener(this);

                gridPanel.add(permCheckBox);
                permCheckBoxes[a][p] = permCheckBox;
            }
        }

        yBoxPanel.add(gridPanel);

        octalPermTextField = new JTextField(3);
        // Constrains text field to 3 digits, from 0 to 7 (octal base)
        Document doc = new SizeConstrainedDocument(3) {
            public void insertString(int offset, String str, AttributeSet attributeSet) throws BadLocationException {
                int strLen = str.length();
                char c;
                for(int i=0; i<strLen; i++) {
                    c = str.charAt(i);
                    if(c<'0' || c>'7')
                        return;
                }

                super.insertString(offset, str, attributeSet);
            }
        };
        octalPermTextField.setDocument(doc);
        // Initializes the field's value
        updateOctalPermTextField();

        // Disable text field if no permission bit can be set
        if(permSetMask==0)
            octalPermTextField.setEnabled(false);
        else {
            setInitialFocusComponent(octalPermTextField);
            doc.addDocumentListener(this);
        }

        yBoxPanel.addSpace(10);
        JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tempPanel.add(new JLabel(Translator.get("permissions.octal_notation")));
        tempPanel.add(octalPermTextField);
        yBoxPanel.add(tempPanel);

        yBoxPanel.addSpace(15);

        recurseDirCheckBox = new JCheckBox(Translator.get("recurse_directories"));
        yBoxPanel.add(recurseDirCheckBox);

        Container contentPane = getContentPane();
        contentPane.add(yBoxPanel, BorderLayout.NORTH);

        okButton = new JButton(Translator.get("ok"));
        cancelButton = new JButton(Translator.get("cancel"));

        contentPane.add(DialogToolkit.createOKCancelPanel(okButton, cancelButton, this), BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        setResizable(false);
    }


    /**
     * Creates and returns a permissions int using the values of the permission checkboxes.
     */
    private int getPermInt() {
        JCheckBox permCheckBox;
        int perms = 0;

        for(int a= USER_ACCESS; a>=OTHER_ACCESS; a--) {
            for(int p=READ_PERMISSION; p>=EXECUTE_PERMISSION; p=p>>1) {
                permCheckBox = permCheckBoxes[a][p];

                if(permCheckBox.isSelected())
                    perms |= (p<<a*3);
            }
        }

        return perms;
    }


    /**
     * Updates the octal permissions text field's value to reflect the permission checkboxes' values.
     */
    private void updateOctalPermTextField() {
        String octalStr = Integer.toOctalString(getPermInt());
        int len = octalStr.length();
        for(int i=len; i<3; i++)
            octalStr = "0"+octalStr;

        octalPermTextField.setText(octalStr);
    }


    /**
     * Updates the permission checkboxes' values to reflect the octal permissions text field.
     */
    private void updatePermCheckBoxes() {
        JCheckBox permCheckBox;
        String octalStr = octalPermTextField.getText();

        int perms = octalStr.equals("")?0:Integer.parseInt(octalStr, 8);

        for(int a= USER_ACCESS; a>=OTHER_ACCESS; a--) {
            for(int p=READ_PERMISSION; p>=EXECUTE_PERMISSION; p=p>>1) {
                permCheckBox = permCheckBoxes[a][p];

//                if(permCheckBox.isEnabled())
                permCheckBox.setSelected((perms & (p<<a*3))!=0);
            }
        }

    }


    ///////////////////////////////////
    // ActionListener implementation //
    ///////////////////////////////////

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source==okButton) {
            dispose();

            // Starts copying files
            ProgressDialog progressDialog = new ProgressDialog(mainFrame, Translator.get("progress_dialog.processing_files"));
            ChangeFileAttributesJob job = new ChangeFileAttributesJob(progressDialog, mainFrame, files, getPermInt(), recurseDirCheckBox.isSelected());
            progressDialog.start(job);
        }
        else if(source==cancelButton) {
            dispose();
        }
    }


    /////////////////////////////////
    // ItemListener implementation //
    /////////////////////////////////

    // Update the octal permission text field whenever one of the permission checkboxes' value has changed

    public void itemStateChanged(ItemEvent e) {
        if(ignoreItemEvent)
            return;

        ignoreDocumentEvent = true;
        updateOctalPermTextField();
        ignoreDocumentEvent = false;
    }


    //////////////////////////////
    // DocumentListener methods //
    //////////////////////////////

    // Update the permission checkboxes' values whenever the octal permission text field has changed

    public void changedUpdate(DocumentEvent e) {
        if(ignoreDocumentEvent)
            return;

        ignoreItemEvent = true;
        updatePermCheckBoxes();
        ignoreItemEvent = false;
    }

    public void insertUpdate(DocumentEvent e) {
        if(ignoreDocumentEvent)
            return;

        ignoreItemEvent = true;
        updatePermCheckBoxes();
        ignoreItemEvent = false;
    }

    public void removeUpdate(DocumentEvent e) {
        if(ignoreDocumentEvent)
            return;

        ignoreItemEvent = true;
        updatePermCheckBoxes();
        ignoreItemEvent = false;
    }
}
