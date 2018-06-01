package com.ss.editor.test.internal.plugin;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * The test to check registering plugin extensions.
 */
public class RegisterPluginExtensionsTest {

    @NotNull
    //FIXME
    //private static final InitializationManager MANAGER = InitializationManager.getInstance();

    @Test
    public void registerFilter() {
       /* MANAGER.addOnAfterCreateJmeContext(() -> {
            final FXAAFilter filter = new FXAAFilter();
            final RenderFilterExtension filterExtension = RenderFilterExtension.getInstance();
            filterExtension.register(filter);
            filterExtension.setOnRefresh(filter, Object::notify);
        });*/
    }
}
