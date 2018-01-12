package com.ss.editor.remote.control.client;

import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import com.ss.rlib.network.ConnectionOwner;
import com.ss.rlib.network.annotation.PacketDescription;
import com.ss.rlib.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The command to load local libraries in jMB.
 *
 * @author JavaSaBr
 */
@PacketDescription(id = 3)
public class LoadLocalClassesClientCommand extends ClientCommand {

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final JmeApplication JME_APPLICATION = JmeApplication.getInstance();

    @Override
    @BackgroundThread
    protected void readImpl(@NotNull final ConnectionOwner owner, @NotNull final ByteBuffer buffer) {

        final String outputPath = readString(buffer);
        final Path output = StringUtils.isEmpty(outputPath) ? null : Paths.get(outputPath);
        final ClasspathManager classpathManager = ClasspathManager.getInstance();
        classpathManager.loadLocalClasses(output);

        EXECUTOR_MANAGER.addJmeTask(() -> JME_APPLICATION.getAssetManager().clearCache());
    }
}
