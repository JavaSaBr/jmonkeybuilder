package com.ss.builder.remote.control.client;

import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.manager.ClasspathManager;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.network.ConnectionOwner;
import com.ss.rlib.common.network.annotation.PacketDescription;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.file.Paths;

/**
 * The command to load local libraries in jMB.
 *
 * @author JavaSaBr
 */
@PacketDescription(id = 3)
public class LoadLocalClassesClientCommand extends ClientCommand {

    @Override
    @BackgroundThread
    protected void readImpl(@NotNull ConnectionOwner owner, @NotNull ByteBuffer buffer) {

        var outputPath = readString(buffer);
        var output = StringUtils.isEmpty(outputPath) ? null : Paths.get(outputPath);

        ClasspathManager.getInstance()
                .loadLocalClasses(output);

        ExecutorManager.getInstance()
                .addJmeTask(EditorUtils::clearAssetCache);
    }
}
