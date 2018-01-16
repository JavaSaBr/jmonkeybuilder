package com.ss.editor.test.plugin;

import com.jme3.post.filters.FXAAFilter;
import com.ss.editor.manager.InitializationManager;
import com.ss.editor.plugin.api.RenderFilterExtension;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * The test to check registering plugin extensions.
 */
public class RegisterPluginExtensionsTest {

    @NotNull
    private static final InitializationManager MANAGER = InitializationManager.getInstance();

    @Test
    public void registerFilter() {
        MANAGER.addOnAfterCreateJmeContext(() -> {
            final FXAAFilter filter = new FXAAFilter();
            final RenderFilterExtension filterExtension = RenderFilterExtension.getInstance();
            filterExtension.register(filter);
            filterExtension.setOnRefresh(filter, Object::notify);
        });
    }
}
