package com.mucommander.share.impl;

import com.mucommander.commons.file.util.FileSet;
import com.mucommander.job.FileJob;
import com.mucommander.share.ShareProvider;
import com.mucommander.ui.dialog.file.ProgressDialog;
import com.mucommander.ui.main.MainFrame;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Mathias
 */
public class ImgurProvider implements ShareProvider {

    public final static String DISPLAY_NAME = "imgur.com";
    public final static String API_ENDPOINT = "https://api.imgur.com/3/";
    public final static String API_IMAGE = API_ENDPOINT + "image";

    public List<String> supportedFiletypes = Arrays.asList("JPEG", "JPG", "GIF", "PNG", "APNG", "TIFF", "BMP", "PDF", "XCF");

    public final static String API_KEY = "28ee9ddb765a2b0";

    @Override
    public boolean supportsFiletypes(Set<String> extensions) {
        for (String extension : extensions) {
            for (String supportedExtension : supportedFiletypes) {
                if (supportedExtension.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean supportsFiletype(String extension) {

        for (String supportedExtension : supportedFiletypes) {
            if (supportedExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public FileJob getJob(ProgressDialog progressDialog, MainFrame mainFrame, FileSet selectedFiles) {
        return new ImgurJob(progressDialog, mainFrame, selectedFiles);
    }

}
