package com.mucommander.share.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.impl.ProxyFile;
import com.mucommander.commons.file.impl.local.LocalFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.job.TransferFileJob;
import static com.mucommander.share.impl.ImgurProvider.API_IMAGE;
import static com.mucommander.share.impl.ImgurProvider.API_KEY;
import com.mucommander.text.Translator;
import com.mucommander.ui.dialog.file.ProgressDialog;
import com.mucommander.ui.main.MainFrame;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
public class ImgurJob extends TransferFileJob {

    private String currentFile;

    public ImgurJob(ProgressDialog progressDialog, MainFrame mainFrame, FileSet files) {
        super(progressDialog, mainFrame, files);
    }

    @Override
    protected boolean hasFolderChanged(AbstractFile folder) {
        // This job does not modify anything
        return false;
    }

    @Override
    protected boolean processFile(AbstractFile af, Object recurseParams) {
        Logger.getLogger(ImgurJob.class.getName()).log(Level.INFO, af.getBaseName());
        currentFile = af.getBaseName();

        File file = null;

        //Local file - no need to buffer
        if (af instanceof LocalFile || af instanceof ProxyFile) {
            file = new File(af.getAbsolutePath());
        } else {
            //Remote file - buffer first
            Logger.getLogger(ImgurJob.class.getName()).log(Level.INFO, "Buffering remote file");
            try {
                File tmpFile = File.createTempFile(af.getBaseName(), "." + af.getExtension());
                FileUtils.copyInputStreamToFile(af.getInputStream(), tmpFile);
                file = tmpFile;
            } catch (IOException ex) {
                Logger.getLogger(ImgurJob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (file != null) {
            Future future = uploadFile(file);

            while (future.isCancelled() || !future.isDone()) {
                Thread.yield();
            }

            return true;

        }

        return false;
    }

    @Override
    public String getStatusString() {

        String status;
        if (currentFile == null) {
            status = Translator.get("share_dialog.connecting");
        } else {
            status = Translator.get("share_dialog.uploading",currentFile);
        }

        return status;
    }

    private Future uploadFile(File file) {
        Logger.getLogger(ImgurJob.class.getName()).log(Level.INFO, "Uploading file {0}", file.getAbsoluteFile());

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
        return future;

    }

    private void openUrlInDefaultBrowser(String url) {
        Logger.getLogger(ImgurJob.class.getName()).log(Level.INFO, "Opening url {0}", url);
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException ex) {
                Logger.getLogger(ImgurJob.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(ImgurJob.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException ex) {
                Logger.getLogger(ImgurJob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
