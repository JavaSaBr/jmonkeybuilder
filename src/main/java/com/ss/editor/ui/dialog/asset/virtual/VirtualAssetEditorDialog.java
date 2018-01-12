package com.ss.editor.ui.dialog.asset.virtual;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.virtual.tree.VirtualResourceTree;
import com.ss.editor.ui.component.virtual.tree.resource.RootVirtualResourceElement;
import com.ss.editor.ui.component.virtual.tree.resource.VirtualResourceElement;
import com.ss.editor.ui.component.virtual.tree.resource.VirtualResourceElementFactory;
import com.ss.editor.ui.dialog.asset.BaseAssetEditorDialog;
import com.ss.rlib.util.array.Array;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
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
    @Nullable
    private VirtualResourceTree<C> resourceTree;

    public VirtualAssetEditorDialog(@NotNull final Consumer<C> consumer, @NotNull final Array<C> resources) {
        this(consumer, null, resources);
    }

    public VirtualAssetEditorDialog(@NotNull final Consumer<C> consumer,
                                    @Nullable final Function<@NotNull C, @Nullable String> validator,
                                    @NotNull final Array<C> resources) {
        super(consumer, validator);
        this.resources = resources;
    }

    /**
     * @param pathFunction the path function.
     * @see VirtualResourceTree#setPathFunction(Function)
     */
    @FromAnyThread
    public void setPathFunction(@Nullable final Function<C, String> pathFunction) {
        getResourceTree().setPathFunction(pathFunction);
    }

    @Override
    @FxThread
    protected @Nullable String getAssetPath(@NotNull final VirtualResourceElement<?> element) {
        return getResourceTree().getPath(element.getObject());
    }

    @Override
    @FxThread
    protected @NotNull Region buildFirstPart(@NotNull final HBox container) {

        resourceTree = new VirtualResourceTree<>(getObjectsType());
        resourceTree.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> processSelected(newValue));

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

        final VirtualResourceTree<C> resourceTree = getResourceTree();
        final RootVirtualResourceElement newRoot =
                VirtualResourceElementFactory.build(resources, resourceTree);

        resourceTree.fill(newRoot);
        resourceTree.expandAll();

        EXECUTOR_MANAGER.addFXTask(resourceTree::requestFocus);
    }

    @Override
    @FxThread
    protected @Nullable C getObject(@NotNull final VirtualResourceElement<?> element) {
        final Object object = element.getObject();
        final Class<C> type = getObjectsType();
        return type.isInstance(object) ? type.cast(object) : null;
    }

    /**
     * @return the tree with all resources.
     */
    @FxThread
    private @NotNull VirtualResourceTree<C> getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    @FxThread
    protected @NotNull ObservableBooleanValue buildAdditionalDisableCondition() {

        final VirtualResourceTree<C> resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<VirtualResourceElement<?>>> selectionModel = resourceTree.getSelectionModel();
        final ReadOnlyObjectProperty<TreeItem<VirtualResourceElement<?>>> selectedItemProperty = selectionModel.selectedItemProperty();

        final Class<C> type = getObjectsType();
        final BooleanBinding typeCondition = new BooleanBinding() {

            @Override
            protected boolean computeValue() {
                final TreeItem<VirtualResourceElement<?>> treeItem = selectedItemProperty.get();
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

        final VirtualResourceTree<C> resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<VirtualResourceElement<?>>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<VirtualResourceElement<?>> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            hide();
            return;
        }

        final VirtualResourceElement<?> element = selectedItem.getValue();
        final Object object = element.getObject();
        final Class<C> type = getObjectsType();

        if (type.isInstance(object)) {
            getConsumer().accept(type.cast(object));
        }
    }
}
