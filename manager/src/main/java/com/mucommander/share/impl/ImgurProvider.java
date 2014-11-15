package com.mucommander.share.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.impl.local.LocalFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.share.ShareProvider;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathias
 */
public class ImgurProvider implements ShareProvider {

    public final static String DISPLAY_NAME = "imgur.com";
    public final static String API_ENDPOINT = "https://api.imgur.com/3/";
    public final static String API_IMAGE = API_ENDPOINT + "image";

    public List<String> supportedFiletypes = Arrays.asList("JPEG", "JPG", "GIF", "PNG", "APNG", "TIFF", "BMP", "PDF", "XCF");

    private final static String API_KEY = "28ee9ddb765a2b0";

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
    public void handleFiles(FileSet selectedFiles) {
        Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "Connecting to " + API_IMAGE);

        for (AbstractFile af : selectedFiles) {

            File file = null;

            //Local file - no need to buffer
            if (af instanceof LocalFile) {
                file = new File(af.getAbsolutePath());
            } else {
                //Remote file - buffer first
                Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "Buffering remote file");
                try {
                    File tmpFile = File.createTempFile(af.getBaseName(), ".tmp");
                    FileUtils.copyInputStreamToFile(af.getInputStream(), tmpFile);
                    file = tmpFile;
                } catch (IOException ex) {
                    Logger.getLogger(ImgurProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (file != null) {
                uploadFile(file);
            }
        }

    }

    private void openUrlInDefaultBrowser(String url) {
        Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "Opening url {0}", url);
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException ex) {
                Logger.getLogger(ImgurProvider.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(ImgurProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException ex) {
                Logger.getLogger(ImgurProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void uploadFile(File file) {
        Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "Uploading file {0}", file.getAbsoluteFile());
        Future<HttpResponse<JsonNode>> future = Unirest.post(API_IMAGE)
                .header("Authorization", "Client-ID " + API_KEY)
                .header("Content-Type", "multipart/form-data")
                .field("image", file).asJsonAsync(new Callback<JsonNode>() {

                    @Override
                    public void completed(HttpResponse<JsonNode> response) {

                        JSONArray array = response.getBody().getArray();
                        JSONObject jsonResponse = array.getJSONObject(0);

                        Boolean success = (Boolean) jsonResponse.get("success");
                        Integer status = (Integer) jsonResponse.getInt("status");

                        if (success && status == 200) {
                            Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "File upload to imgur completed");
                            JSONObject jsonData = jsonResponse.getJSONObject("data");
                            if (jsonData != null) {
                                String url = (String) jsonData.get("link");
                                openUrlInDefaultBrowser(url);
                            }
                        } else {
                            Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "success returned {0}", success);
                            Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "Status returned {0}", status);
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "File upload to imgur failed");
                    }

                    @Override
                    public void cancelled() {
                        Logger.getLogger(ImgurProvider.class.getName()).log(Level.INFO, "File upload to imgur canceled");
                    }
                });
    }

}
