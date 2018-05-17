package com.ss.editor.remote.control.client;

import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.bar.action.OpenAssetAction;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.AssetComponentLoadedEvent;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.network.ConnectionOwner;
import com.ss.rlib.common.network.annotation.PacketDescription;
import javafx.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The command to open a file.
 *
 * @author JavaSaBr
 */
@PacketDescription(id = 1)
public class OpenFileClientCommand extends ClientCommand {

    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @Override
    @BackgroundThread
    protected void readImpl(@NotNull ConnectionOwner owner, @NotNull ByteBuffer buffer) {

        var assetPath = Paths.get(readString(buffer));
        var fileToOpen = Paths.get(readString(buffer));

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();

        if (currentAsset != null && assetPath.equals(currentAsset)) {
            EXECUTOR_MANAGER.addFxTask(() -> openFile(fileToOpen));
        } else {
            EXECUTOR_MANAGER.addFxTask(() -> {

                final OpenAssetAction action = new OpenAssetAction();
                action.openAssetFolder(assetPath);

                var eventHandler = new EventHandler<AssetComponentLoadedEvent>() {

                    @Override
                    public void handle(@NotNull AssetComponentLoadedEvent event) {
                        FX_EVENT_MANAGER.removeEventHandler(AssetComponentLoadedEvent.EVENT_TYPE, this);
                        openFile(fileToOpen);
                    }
                };

                FX_EVENT_MANAGER.addEventHandler(AssetComponentLoadedEvent.EVENT_TYPE, eventHandler);
            });

        }
    }

    /**
     * Open the file.
     *
     * @param fileToOpen the file.
     */
    private void openFile(@NotNull Path fileToOpen) {
        FX_EVENT_MANAGER.notify(new RequestedOpenFileEvent(fileToOpen));
        EditorUtil.requestFxFocus();
    }
}
