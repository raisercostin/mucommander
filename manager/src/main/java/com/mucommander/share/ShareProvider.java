package com.mucommander.share;

import com.mucommander.commons.file.util.FileSet;
import java.util.Set;

/**
 *
 * @author Mathias
 */
public interface ShareProvider {
    /**
     * Returns true if the provider supports one or more of the selected file type(s).
     * Extensions are without dot (.) in front.
     * @param extensions List of file extensions
     * @return 
     */
    public boolean supportsFiletypes(Set<String> extensions);
    
    /**
     * Returns the name of the service to be displayed
     * @return The display name
     */
    public String getDisplayName();
    
    /**
     * Handles the selected files
     * @param selectedFiles The files that have been selected for sharing
     */
    public void handleFiles(FileSet selectedFiles);

}
