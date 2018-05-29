package com.ss.editor.plugin.api.messages;

import static java.util.Collections.emptyEnumeration;
import static java.util.ResourceBundle.getBundle;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.plugin.EditorPlugin;
import com.ss.rlib.common.util.PropertyLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The factory of messages resources bundles for plugins.
 *
 * @author JavaSaBr
 */
public class MessagesPluginFactory {

    /**
     * The empty resource bundle.
     */
    private static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ResourceBundle() {

        @Override
        protected Object handleGetObject(@NotNull String key) {
            return null;
        }

        @NotNull
        @Override
        public Enumeration<String> getKeys() {
            return emptyEnumeration();
        }
    };

    /**
     * Create a resource bundle of messages for the plugin. You need to put a 'messages.properties'
     * file to your resources folder by path '/resources/messages/'.
     *
     * @param pluginClass the plugin class.
     * @return the resource bundle.
     */
    @FromAnyThread
    public static @NotNull ResourceBundle getResourceBundle(@NotNull Class<? extends EditorPlugin> pluginClass) {
        return getResourceBundle(pluginClass, Messages.BUNDLE_NAME);
    }

    /**
     * Create a resource bundle of messages for the plugin.
     *
     * @param pluginClass the plugin class.
     * @param bundleName  the bundle name.
     * @return the resource bundle.
     */
    @FromAnyThread
    public static @NotNull ResourceBundle getResourceBundle(
            @NotNull Class<? extends EditorPlugin> pluginClass,
            @NotNull String bundleName
    ) {

        var locale = Locale.getDefault();
        var classLoader = pluginClass.getClassLoader();
        var resourceBundle = getBundle(bundleName, locale, classLoader, PropertyLoader.getInstance());

        if (resourceBundle == null) {
            return EMPTY_RESOURCE_BUNDLE;
        }

        return resourceBundle;
    }
}
