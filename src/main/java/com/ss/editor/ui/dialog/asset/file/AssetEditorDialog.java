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
import com.ss.rlib.fx.util.FxControlUtils;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of the {@link EditorDialog} to choose the object from an asset folder.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public class AssetEditorDialog<C> extends BaseAssetEditorDialog<ResourceElement, C> {

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
    @NotNull
    private final ResourceTree resourceTree;

    public AssetEditorDialog(@NotNull Consumer<C> consumer) {
        this(consumer, null);
    }

    public AssetEditorDialog(@NotNull Consumer<C> consumer, @Nullable Validator<C> validator) {
        super(consumer, validator);
        this.waitedFilesToSelect = ArrayFactory.newArray(Path.class);
        this.resourceTree = new ResourceTree(this::processOpen, false);
    }

    /**
     * Set the list of available extensions.
     *
     * @param extensionFilter the list of available extensions.
     */
    @FromAnyThread
    public void setExtensionFilter(@NotNull Array<String> extensionFilter) {
        getResourceTree().setExtensionFilter(extensionFilter);
    }

    /**
     * Set the action tester.
     *
     * @param actionTester the action tester.
     */
    @FromAnyThread
    public void setActionTester(@Nullable Predicate<Class<?>> actionTester) {
        getResourceTree().setActionTester(actionTester);
    }

    /**
     * Set true if need to show only folders.
     *
     * @param onlyFolders true if need to show only folders.
     */
    @FromAnyThread
    public void setOnlyFolders(boolean onlyFolders) {
        getResourceTree().setOnlyFolders(onlyFolders);
    }

    @Override
    @FxThread
    protected @NotNull Region buildFirstPart(@NotNull HBox container) {
        FxControlUtils.onSelectedItemChange(resourceTree, this::processSelected);
        return resourceTree;
    }

    /**
     * The process of opening the element.
     *
     * @param element the element
     */
    @FxThread
    protected void processOpen(@NotNull ResourceElement element) {
        hide();
    }

    @Override
    @FxThread
    public void show(@NotNull Window owner) {
        super.show(owner);

        var currentAsset = EditorConfig.getInstance()
                .requiredCurrentAsset();

        getResourceTree().fill(currentAsset);

        FxEventManager.getInstance()
                .addEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler)
                .addEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle)
                .addEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        ExecutorManager.getInstance()
                .addFxTask(resourceTree::requestFocus);
    }

    /**
     * Handle creating file event.
     */
    @FxThread
    private void processEvent(@NotNull CreatedFileEvent event) {

        var file = event.getFile();

        var waitedFilesToSelect = getWaitedFilesToSelect();
        var waitedSelect = waitedFilesToSelect.contains(file);

        var resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (waitedSelect) {
            waitedFilesToSelect.fastRemove(file);
        }

        if (waitedSelect || event.isNeedSelect()) {
            resourceTree.expandTo(file, true);
        }
    }

    /**
     * Handle deleting file event.
     */
    @FxThread
    private void processEvent(@NotNull DeletedFileEvent event) {
        getResourceTree().notifyDeleted(event.getFile());
    }

    /**
     * Handle selecting file event.
     */
    @FxThread
    private void processEvent(@NotNull RequestSelectFileEvent event) {

        var file = event.getFile();

        var resourceTree = getResourceTree();
        var element = createFor(file);
        var treeItem = findItemForValue(resourceTree.getRoot(), element);

        if (treeItem == null) {
            getWaitedFilesToSelect().add(file);
            return;
        }

        resourceTree.expandTo(treeItem, true);
    }

    /**
     * Get the list of waited files to select.
     *
     * @return the list of waited files to select.
     */
    @FromAnyThread
    private @NotNull Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    @Override
    @FxThread
    protected @Nullable Path getRealFile(@NotNull ResourceElement element) {
        return element.getFile();
    }

    @Override
    @FxThread
    public void hide() {

        FxEventManager.getInstance()
                .removeEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler)
                .removeEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle)
                .removeEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        super.hide();
    }

    @Override
    @FxThread
    protected @NotNull ObservableBooleanValue buildAdditionalDisableCondition() {
        return getResourceTree().getSelectionModel()
                .selectedItemProperty()
                .isNull();
    }

    /**
     * Get the tree with all resources.
     *
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

        var selectedItem = getResourceTree().getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            hide();
            return;
        }

        processOpen(selectedItem.getValue());
    }
}
