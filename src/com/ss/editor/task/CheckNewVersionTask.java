package com.ss.editor.task;

import static org.apache.http.impl.client.HttpClients.createMinimal;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.config.Config;
import com.ss.editor.util.AppVersion;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * The task to check new versions of the Editor.
 *
 * @author JavaSaBr
 */
public class CheckNewVersionTask implements Runnable {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(CheckNewVersionTask.class);

    @NotNull
    private static final String APP_VERSION_URL = "https://api.bitbucket.org/1.0/repositories/javasabr/" +
            "jme3-spaceshift-editor/raw/master/app.version";

    @NotNull
    private static final String DOWNLOAD_APP_PATH_URL = "https://api.bitbucket.org/1.0/repositories/javasabr/" +
            "jme3-spaceshift-editor/raw/master/download.app.path";

    @Override
    public void run() {

        try (final CloseableHttpClient httpClient = createMinimal()) {

            CloseableHttpResponse response = httpClient.execute(new HttpGet(APP_VERSION_URL));
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != 200) {
                return;
            }

            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            Header encoding = entity.getContentEncoding();
            String enc = encoding == null ? "UTF-8" : encoding.getValue();

            final String targetVersion = IOUtils.toString(content, enc)
                    .trim()
                    .replace("v.", "");

            if (Config.APP_VERSION.compareTo(new AppVersion(targetVersion)) <= 0) {
                return;
            }

            response = httpClient.execute(new HttpGet(DOWNLOAD_APP_PATH_URL));
            statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != 200) {
                return;
            }

            entity = response.getEntity();
            content = entity.getContent();
            encoding = entity.getContentEncoding();
            enc = encoding == null ? "UTF-8" : encoding.getValue();

            final String targetLink = IOUtils.toString(content, enc).trim();

            Platform.runLater(() -> {

                final JFXApplication jfxApplication = JFXApplication.getInstance();
                final HostServices hostServices = jfxApplication.getHostServices();

                final Hyperlink hyperlink = new Hyperlink(Messages.CHECK_NEW_VERSION_DIALOG_HYPERLINK + targetLink);
                hyperlink.setOnAction(event -> hostServices.showDocument(targetLink));

                final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(Messages.CHECK_NEW_VERSION_DIALOG_TITLE);
                alert.setHeaderText(Messages.CHECK_NEW_VERSION_DIALOG_HEADER_TEXT + targetVersion);

                final DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setContent(hyperlink);

                alert.show();
            });

        } catch (final IOException e) {
            LOGGER.warning(e);
        }
    }
}
