package com.ss.editor.test.internal;

import com.ss.editor.JfxApplication;
import com.ss.editor.config.CommandLineConfig;
import com.ss.editor.manager.InitializationManager;
import com.ss.rlib.concurrent.util.ThreadUtils;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

/**
 * The set up test.
 *
 * @author JavaSaBr
 */
public class SetUpApplicationTest {

    @NotNull
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    @Nullable
    private static JfxApplication jfxApplication;

    protected static synchronized void waitForApp() {
        if (jfxApplication != null) return;
        try {

            final Path asset = Files.createTempDirectory("asset");
            final InitializationManager initializationManager = InitializationManager.getInstance();
            initializationManager.addOnFinishLoading(COUNT_DOWN_LATCH::countDown);

            JfxApplication.main(ArrayFactory.toArray(
                    CommandLineConfig.PREF_EDITOR_ASSET_FOLDER + "=" + asset.toString()));

            COUNT_DOWN_LATCH.await();

            jfxApplication = JfxApplication.getInstance();

        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static synchronized void runApplication() {
        waitForApp();
        ThreadUtils.sleep(100000);
    }
}
