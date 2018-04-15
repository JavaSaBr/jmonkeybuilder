package com.ss.editor.util;

import com.ss.editor.manager.PluginManager;
import com.ss.rlib.common.util.ref.Reference;
import com.ss.rlib.common.util.ref.ReferenceFactory;
import com.ss.rlib.common.util.ref.ReferenceType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * The implementation of serialization with additional class loaders.
 *
 * @author JavaSaBr
 */
public class ExtObjectInputStream extends ObjectInputStream {

    public ExtObjectInputStream(@NotNull final InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(@NotNull final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return super.resolveClass(desc);
        } catch (final ClassNotFoundException e) {

            final String name = desc.getName();

            try (final Reference ref = ReferenceFactory.takeFromTLPool(ReferenceType.OBJECT)) {

                final PluginManager pluginManager = PluginManager.getInstance();
                pluginManager.handlePlugins(plugin -> {

                    if (ref.getObject() != null) return;

                    final ClassLoader classLoader = plugin.getClassLoader();
                    try {
                        ref.setObject(classLoader.loadClass(name));
                    } catch (final ClassNotFoundException ex) {
                        // ignore this exception
                    }
                });

                if (ref.getObject() != null) {
                    return (Class<?>) ref.getObject();
                }
            }

            throw e;
        }
    }
}
