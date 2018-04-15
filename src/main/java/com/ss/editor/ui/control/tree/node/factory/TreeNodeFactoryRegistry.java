package com.ss.editor.ui.control.tree.node.factory;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.node.factory.impl.MaterialSettingsTreeNodeFactory;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.impl.*;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The registry of available tree node factories.
 *
 * @author JavaSaBr
 */
public class TreeNodeFactoryRegistry {

    @NotNull
    private static final TreeNodeFactoryRegistry INSTANCE = new TreeNodeFactoryRegistry();

    /**
     * The node id generator.
     */
    @NotNull
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @NotNull
    public static TreeNodeFactoryRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of available factories.
     */
    @NotNull
    private final Array<TreeNodeFactory> factories;

    private TreeNodeFactoryRegistry() {
        this.factories = ArrayFactory.newArray(TreeNodeFactory.class);
        register(new PrimitiveTreeNodeFactory());
        register(new AnimationTreeNodeFactory());
        register(new CollisionTreeNodeFactory());
        register(new ControlTreeNodeFactory());
        register(new DefaultParticlesTreeNodeFactory());
        register(new DefaultTreeNodeFactory());
        register(new LightTreeNodeFactory());
        register(new MaterialSettingsTreeNodeFactory());
    }

    /**
     * Register a new tree node factory.
     *
     * @param factory the tree node factory.
     */
    @FxThread
    public void register(@NotNull final TreeNodeFactory factory) {
        this.factories.add(factory);
        this.factories.sort(TreeNodeFactory::compareTo);
    }

    /**
     * Get all available tree node factories.
     *
     * @return the list of available tree node factories.
     */
    @FxThread
    private @NotNull Array<TreeNodeFactory> getFactories() {
        return factories;
    }

    /**
     * Create a tree node for an element.
     *
     * @param <T>     the type of an element.
     * @param <V>     the type of a tree node
     * @param element the element
     * @return the tree node.
     */
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element) {

        if (element instanceof TreeNode) {
            return unsafeCast(element);
        }

        final long objectId = ID_GENERATOR.incrementAndGet();

        V result = null;

        final Array<TreeNodeFactory> factories = getFactories();
        for (final TreeNodeFactory factory : factories) {
            result = factory.createFor(element, objectId);
            if (result != null) {
                break;
            }
        }

        return result;
    }
}
