package com.ss.builder.task;

import static org.apache.http.impl.client.HttpClients.createMinimal;
import com.ss.builder.Messages;
import com.ss.builder.config.Config;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.Version;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

/**
 * The task to check new versions of the editor.
 *
 * @author JavaSaBr
 */
public class CheckNewVersionTask implements Runnable {

    private static final Logger LOGGER = LoggerManager.getLogger(CheckNewVersionTask.class);

    private static final String APP_VERSION_URL = "https://api.bitbucket.org/1.0/repositories/javasabr/" +
            "jmonkey-builder/raw/master/app.version";
    private static final String DOWNLOAD_APP_PATH_URL = "https://api.bitbucket.org/1.0/repositories/javasabr/" +
            "jmonkey-builder/raw/master/download.app.path";

    @Override
    public void run() {

        try (var httpClient = createMinimal()) {

            var response = httpClient.execute(new HttpGet(APP_VERSION_URL));
            var statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != 200) {
                return;
            }

            var entity = response.getEntity();
            var content = entity.getContent();
            var encoding = entity.getContentEncoding();
            var enc = encoding == null ? "UTF-8" : encoding.getValue();

            var targetVersion = IOUtils.toString(content, enc)
                    .trim()
                    .replace("v.", "");

            if (Config.APP_VERSION.compareTo(new Version(targetVersion)) >= 0) {
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

            var targetLink = IOUtils.toString(content, enc)
                    .trim();

            Platform.runLater(() -> {

                var hostServices = EditorUtils.getHostServices();

                var hyperlink = new Hyperlink(Messages.CHECK_NEW_VERSION_DIALOG_HYPERLINK + targetLink);
                hyperlink.setOnAction(event -> hostServices.showDocument(targetLink));

                var alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(Messages.CHECK_NEW_VERSION_DIALOG_TITLE);
                alert.setHeaderText(Messages.CHECK_NEW_VERSION_DIALOG_HEADER_TEXT + targetVersion);

                var dialogPane = alert.getDialogPane();
                dialogPane.setContent(hyperlink);

                alert.show();
            });

        } catch (IOException e) {
            LOGGER.warning(e);
        }
    }
}
