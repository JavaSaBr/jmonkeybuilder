package com.ss.builder.fx.control.tree.node.factory;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.node.factory.impl.*;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.fx.control.tree.node.factory.impl.*;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The registry of available tree node factories.
 *
 * @author JavaSaBr
 */
public class TreeNodeFactoryRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(TreeNodeFactoryRegistry.class);

    /**
     * @see TreeNodeFactory
     */
    public static final String EP_FACTORIES = "TreeNodeFactoryRegistry#factories";

    private static final ExtensionPoint<TreeNodeFactory> FACTORIES =
            ExtensionPointManager.register(EP_FACTORIES);

    /**
     * The node's id generator.
     */
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private static final TreeNodeFactoryRegistry INSTANCE = new TreeNodeFactoryRegistry();

    @FromAnyThread
    public static @NotNull TreeNodeFactoryRegistry getInstance() {
        return INSTANCE;
    }

    private TreeNodeFactoryRegistry() {

        FACTORIES.register(new PrimitiveTreeNodeFactory())
                .register(new LegacyAnimationTreeNodeFactory())
                .register(new CollisionTreeNodeFactory())
                .register(new ControlTreeNodeFactory())
                .register(new DefaultParticlesTreeNodeFactory())
                .register(new DefaultTreeNodeFactory())
                .register(new LightTreeNodeFactory())
                .register(new MaterialSettingsTreeNodeFactory())
                .register(new AnimationTreeNodeFactory());

        LOGGER.info("initialized.");
    }

    /**
     * Create a tree node for the element.
     *
     * @param <T>     the element's type.
     * @param <V>     the tree node's type.
     * @param element the element
     * @return the created tree node or null.
     */
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable T element) {

        if (element instanceof TreeNode) {
            return unsafeCast(element);
        }

        var factories = FACTORIES.getExtensions();
        var objectId = ID_GENERATOR.incrementAndGet();

        V result = null;

        for (var factory : factories) {
            result = factory.createFor(element, objectId);
            if (result != null) {
                break;
            }
        }

        return result;
    }
}
