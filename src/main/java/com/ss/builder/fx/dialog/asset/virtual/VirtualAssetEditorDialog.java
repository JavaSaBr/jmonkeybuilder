package com.ss.builder.fx.dialog.asset.virtual;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.component.virtual.tree.VirtualResourceTree;
import com.ss.builder.fx.component.virtual.tree.resource.VirtualResourceElement;
import com.ss.builder.fx.component.virtual.tree.resource.VirtualResourceElementFactory;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.component.virtual.tree.VirtualResourceTree;
import com.ss.builder.fx.component.virtual.tree.resource.VirtualResourceElement;
import com.ss.builder.fx.component.virtual.tree.resource.VirtualResourceElementFactory;
import com.ss.builder.fx.dialog.asset.BaseAssetEditorDialog;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxControlUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The implementation of the {@link BaseAssetEditorDialog} to choose the object from a virtual asset.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public class VirtualAssetEditorDialog<C> extends BaseAssetEditorDialog<VirtualResourceElement<?>, C> {

    /**
     * The all resources.
     */
    @NotNull
    private final Array<C> resources;

    /**
     * The tree with all resources.
     */
    @NotNull
    private final VirtualResourceTree<C> resourceTree;

    public VirtualAssetEditorDialog(@NotNull Consumer<C> consumer, @NotNull Array<C> resources) {
        this(consumer, null, resources);
    }

    public VirtualAssetEditorDialog(
            @NotNull Consumer<C> consumer,
            @Nullable Validator<C> validator,
            @NotNull Array<C> resources
    ) {
        super(consumer, validator);
        this.resources = resources;
        this.resourceTree = new VirtualResourceTree<>(getObjectsType());
    }

    /**
     * @param pathFunction the path function.
     * @see VirtualResourceTree#setPathFunction(Function)
     */
    @FromAnyThread
    public void setPathFunction(@Nullable Function<C, String> pathFunction) {
        getResourceTree().setPathFunction(pathFunction);
    }

    @Override
    @FxThread
    protected @Nullable String getAssetPath(@NotNull VirtualResourceElement<?> element) {
        return getResourceTree().getPath(element.getObject());
    }

    @Override
    @FxThread
    protected @NotNull Region buildFirstPart(@NotNull HBox container) {
        FxControlUtils.onSelectedItemChange(resourceTree, this::processSelected);
        return resourceTree;
    }

    /**
     * Get the type of presented objects.
     *
     * @return the type of presented objects.
     */
    @FromAnyThread
    protected @NotNull Class<C> getObjectsType() {
        throw new RuntimeException("unsupported");
    }

    @Override
    @FxThread
    public void show(@NotNull final Window owner) {
        super.show(owner);

        var resourceTree = getResourceTree();
        var newRoot = VirtualResourceElementFactory.build(resources, resourceTree);

        resourceTree.fill(newRoot);
        resourceTree.expandAll();

        ExecutorManager.getInstance()
                .addFxTask(resourceTree::requestFocus);
    }

    @Override
    @FxThread
    protected @Nullable C getObject(@NotNull VirtualResourceElement<?> element) {
        var object = element.getObject();
        var type = getObjectsType();
        return type.isInstance(object) ? type.cast(object) : null;
    }

    /**
     * Get the tree with all resources.
     *
     * @return the tree with all resources.
     */
    @FxThread
    private @NotNull VirtualResourceTree<C> getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    @FxThread
    protected @NotNull ObservableBooleanValue buildAdditionalDisableCondition() {

        var resourceTree = getResourceTree();
        var selectedItemProperty = resourceTree.getSelectionModel()
                .selectedItemProperty();

        var type = getObjectsType();
        var typeCondition = new BooleanBinding() {

            @Override
            protected boolean computeValue() {
                var treeItem = selectedItemProperty.get();
                return treeItem == null || !type.isInstance(treeItem.getValue().getObject());
            }

            @Override
            public Boolean getValue() {
                return computeValue();
            }
        };

        return Bindings.or(selectedItemProperty.isNull(), typeCondition);
    }

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();

        var selectedItem = getResourceTree()
                .getSelectionModel()
                .getSelectedItem();

        if (selectedItem == null) {
            hide();
            return;
        }

        var element = selectedItem.getValue();
        var object = element.getObject();
        var type = getObjectsType();

        if (type.isInstance(object)) {
            getConsumer().accept(type.cast(object));
        }
    }
}
