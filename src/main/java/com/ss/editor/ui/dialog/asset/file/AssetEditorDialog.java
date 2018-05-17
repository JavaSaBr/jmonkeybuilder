package com.ss.editor.ui.dialog.asset.file;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UiUtils.findItemForValue;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.editor.ui.dialog.asset.BaseAssetEditorDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.EventHandler;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The implementation of the {@link EditorDialog} to choose the object from an asset folder.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public class AssetEditorDialog<C> extends BaseAssetEditorDialog<ResourceElement, C> {

    /**
     * The executing manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The event manager.
     */
    @NotNull
    protected static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    /**
     * The handler created files events.
     */
    @NotNull
    private final EventHandler<CreatedFileEvent> createdFileHandler = this::processEvent;

    /**
     * The handler selected file events.
     */
    @NotNull
    private final EventHandler<RequestSelectFileEvent> selectFileHandle = this::processEvent;

    /**
     * The handler deleted file events,
     */
    @NotNull
    private final EventHandler<DeletedFileEvent> deletedFileHandler = this::processEvent;

    /**
     * The list of waited files to select.
     */
    @NotNull
    private final Array<Path> waitedFilesToSelect;

    /**
     * The tree with all resources.
     */
    @Nullable
    private ResourceTree resourceTree;

    public AssetEditorDialog(@NotNull final Consumer<C> consumer) {
        this(consumer, null);
    }

    public AssetEditorDialog(@NotNull final Consumer<C> consumer, @Nullable final Function<C, String> validator) {
        super(consumer, validator);
        this.waitedFilesToSelect = ArrayFactory.newArray(Path.class);
    }

    /**
     * Sets extension filter.
     *
     * @param extensionFilter the list of available extensions.
     */
    @FromAnyThread
    public void setExtensionFilter(@NotNull final Array<String> extensionFilter) {
        getResourceTree().setExtensionFilter(extensionFilter);
    }

    /**
     * Sets action tester.
     *
     * @param actionTester the action tester.
     */
    @FromAnyThread
    public void setActionTester(@Nullable final Predicate<Class<?>> actionTester) {
        getResourceTree().setActionTester(actionTester);
    }

    /**
     * Sets only folders.
     *
     * @param onlyFolders true if need to show only folders.
     */
    @FromAnyThread
    public void setOnlyFolders(final boolean onlyFolders) {
        getResourceTree().setOnlyFolders(onlyFolders);
    }

    @Override
    @FxThread
    protected @NotNull Region buildFirstPart(@NotNull final HBox container) {

        resourceTree = new ResourceTree(this::processOpen, false);
        resourceTree.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> processSelected(newValue));

        return resourceTree;
    }

    /**
     * The process of opening the element.
     *
     * @param element the element
     */
    @FxThread
    protected void processOpen(@NotNull final ResourceElement element) {
        hide();
    }

    @Override
    @FxThread
    public void show(@NotNull final Window owner) {
        super.show(owner);

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ResourceTree resourceTree = getResourceTree();
        final Path currentAsset = notNull(editorConfig.getCurrentAsset());

        resourceTree.fill(currentAsset);

        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler);
        FX_EVENT_MANAGER.addEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle);
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        EXECUTOR_MANAGER.addFxTask(resourceTree::requestFocus);
    }

    /**
     * Handle creating file event.
     */
    @FxThread
    private void processEvent(@NotNull final CreatedFileEvent event) {

        final Path file = event.getFile();

        final Array<Path> waitedFilesToSelect = getWaitedFilesToSelect();
        final boolean waitedSelect = waitedFilesToSelect.contains(file);

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (waitedSelect) waitedFilesToSelect.fastRemove(file);
        if (waitedSelect || event.isNeedSelect()) resourceTree.expandTo(file, true);
    }

    /**
     * Handle deleting file event.
     */
    @FxThread
    private void processEvent(@NotNull final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);
    }

    /**
     * Handle selecting file event.
     */
    @FxThread
    private void processEvent(@NotNull final RequestSelectFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        final ResourceElement element = createFor(file);
        final TreeItem<ResourceElement> treeItem = findItemForValue(resourceTree.getRoot(), element);

        if (treeItem == null) {
            getWaitedFilesToSelect().add(file);
            return;
        }

        resourceTree.expandTo(treeItem, true);
    }

    /**
     * @return the list of waited files to select.
     */
    @FromAnyThread
    private @NotNull Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    @Override
    @FxThread
    protected @Nullable Path getRealFile(@NotNull final ResourceElement element) {
        return element.getFile();
    }

    @Override
    @FxThread
    public void hide() {

        FX_EVENT_MANAGER.removeEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler);
        FX_EVENT_MANAGER.removeEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle);
        FX_EVENT_MANAGER.removeEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        super.hide();
    }

    @Override
    @FxThread
    protected @NotNull ObservableBooleanValue buildAdditionalDisableCondition() {
        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final ReadOnlyObjectProperty<TreeItem<ResourceElement>> selectedItemProperty = selectionModel.selectedItemProperty();
        return selectedItemProperty.isNull();
    }

    /**
     * @return the tree with all resources.
     */
    @FxThread
    private @NotNull ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            hide();
            return;
        }

        processOpen(selectedItem.getValue());
    }
}
