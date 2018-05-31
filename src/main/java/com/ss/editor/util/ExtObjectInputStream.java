package com.ss.editor.util;

import com.ss.editor.manager.PluginManager;
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

    public ExtObjectInputStream(@NotNull InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(@NotNull ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return super.resolveClass(desc);
        } catch (ClassNotFoundException e) {

            var name = desc.getName();

            try (var ref = ReferenceFactory.takeFromTLPool(ReferenceType.OBJECT)) {

                var pluginManager = PluginManager.getInstance();
                pluginManager.handlePluginsNow(plugin -> {

                    if (ref.getObject() != null) {
                        return;
                    }

                    var classLoader = plugin.getClassLoader();
                    try {
                        ref.setObject(classLoader.loadClass(name));
                    } catch (ClassNotFoundException ex) {
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
