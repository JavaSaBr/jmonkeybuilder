package com.ss.editor.ui.dialog.asset;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;
import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.util.Objects.requireNonNull;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JMEFilePreviewManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Files;
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
public class AssetEditorDialog<C> extends EditorDialog {

    /**
     * The constant DIALOG_SIZE.
     */
    @NotNull
    protected static final Point DIALOG_SIZE = new Point(-1, -1);

    /**
     * The constant JAVA_FX_IMAGE_MANAGER.
     */
    @NotNull
    protected static final JavaFXImageManager JAVA_FX_IMAGE_MANAGER = JavaFXImageManager.getInstance();

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The constant FX_EVENT_MANAGER.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    @NotNull
    private final EventHandler<Event> createdFileHandler = event -> processEvent((CreatedFileEvent) event);

    @NotNull
    private final EventHandler<Event> selectFileHandle = event -> processEvent((RequestSelectFileEvent) event);

    @NotNull
    private final EventHandler<Event> deletedFileHandler = event -> processEvent((DeletedFileEvent) event);

    /**
     * The list of waited files to select.
     */
    @NotNull
    private final Array<Path> waitedFilesToSelect;

    /**
     * The function for handling the choose.
     */
    @NotNull
    protected final Consumer<C> consumer;

    /**
     * The action tester.
     */
    @Nullable
    protected Predicate<Class<?>> actionTester;

    /**
     * The function for validating the choose.
     */
    @Nullable
    private final Function<C, String> validator;

    /**
     * The tree with all resources.
     */
    @Nullable
    private ResourceTree resourceTree;

    /**
     * The image preview.
     */
    @Nullable
    ImageView imageView;

    /**
     * The preview of text files.
     */
    @Nullable
    TextArea textView;

    /**
     * The label with any warning.
     */
    @Nullable
    private Label warningLabel;

    /**
     * The OK button.
     */
    @Nullable
    protected Button okButton;

    /**
     * Instantiates a new Asset editor dialog.
     *
     * @param consumer the consumer
     */
    public AssetEditorDialog(@NotNull final Consumer<C> consumer) {
        this(consumer, null);
    }

    /**
     * Instantiates a new Asset editor dialog.
     *
     * @param consumer  the consumer
     * @param validator the validator
     */
    public AssetEditorDialog(@NotNull final Consumer<C> consumer, @Nullable final Function<C, String> validator) {
        this.waitedFilesToSelect = ArrayFactory.newArray(Path.class);
        this.consumer = consumer;
        this.validator = validator;
    }

    /**
     * Sets extension filter.
     *
     * @param extensionFilter the list of available extensions.
     */
    public void setExtensionFilter(@NotNull final Array<String> extensionFilter) {
        getResourceTree().setExtensionFilter(extensionFilter);
    }

    /**
     * Sets action tester.
     *
     * @param actionTester the action tester.
     */
    public void setActionTester(@Nullable final Predicate<Class<?>> actionTester) {
        getResourceTree().setActionTester(actionTester);
    }

    /**
     * Sets only folders.
     *
     * @param onlyFolders true if need to show only folders.
     */
    void setOnlyFolders(final boolean onlyFolders) {
        getResourceTree().setOnlyFolders(true);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {

        final HBox container = new HBox();

        resourceTree = new ResourceTree(this::processOpen, false);
        resourceTree.prefHeightProperty().bind(root.heightProperty());
        resourceTree.prefWidthProperty().bind(root.widthProperty().multiply(0.5));
        resourceTree.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> processSelected(newValue));

        final Region secondPart = buildSecondPart(container);
        secondPart.prefHeightProperty().bind(root.heightProperty());
        secondPart.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        FXUtils.addToPane(resourceTree, container);
        FXUtils.addToPane(secondPart, container);
        FXUtils.addToPane(container, root);

        root.setOnKeyReleased(this::processKeyEvent);

        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(root, CSSClasses.ASSET_EDITOR_DIALOG);
    }

    /**
     * Build second part parent.
     *
     * @param container the container
     * @return the parent
     */
    @NotNull
    protected Region buildSecondPart(@NotNull final HBox container) {

        final StackPane previewContainer = new StackPane();

        imageView = new ImageView();
        imageView.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        imageView.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        textView = new TextArea();
        textView.setEditable(false);
        textView.prefWidthProperty().bind(previewContainer.widthProperty().subtract(2));
        textView.prefHeightProperty().bind(previewContainer.heightProperty().subtract(2));

        FXUtils.addToPane(imageView, previewContainer);
        FXUtils.addToPane(textView, previewContainer);

        FXUtils.addClassTo(previewContainer, CSSClasses.ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);
        FXUtils.addClassTo(textView, CSSClasses.TRANSPARENT_TEXT_AREA);

        return previewContainer;
    }

    /**
     * Gets ok button.
     *
     * @return the ok button.
     */
    @NotNull
    public Button getOkButton() {
        return notNull(okButton);
    }

    /**
     * The process of opening the element.
     *
     * @param element the element
     */
    protected void processOpen(@NotNull final ResourceElement element) {
        hide();
    }

    private void processKeyEvent(@NotNull final KeyEvent event) {
        final Button okButton = getOkButton();
        if (event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
            processSelect();
        }
    }

    @Override
    public void show(@NotNull final Window owner) {
        super.show(owner);

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ResourceTree resourceTree = getResourceTree();
        final Path currentAsset = requireNonNull(editorConfig.getCurrentAsset());

        resourceTree.fill(currentAsset);

        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler);
        FX_EVENT_MANAGER.addEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle);
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        EXECUTOR_MANAGER.addFXTask(resourceTree::requestFocus);
    }

    /**
     * Handle creating file event.
     */
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
    private void processEvent(@NotNull final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);
    }

    /**
     * Handle selecting file event.
     */
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
    @NotNull
    private Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    /**
     * @return the image preview.
     */
    @NotNull
    private ImageView getImageView() {
        return notNull(imageView);
    }

    /**
     * @return the text preview.
     */
    @NotNull
    private TextArea getTextView() {
        return notNull(textView);
    }

    /**
     * Gets validator.
     *
     * @return the function for validating the choose.
     */
    @Nullable
    Function<C, String> getValidator() {
        return validator;
    }

    /**
     * @return the label with any warning.
     */
    @NotNull
    private Label getWarningLabel() {
        return notNull(warningLabel);
    }

    /**
     * Handle selected element in the tree.
     */
    private void processSelected(@Nullable final TreeItem<ResourceElement> newValue) {

        final ResourceElement element = newValue == null ? null : newValue.getValue();
        final Path file = element == null ? null : element.getFile();

        validate(getWarningLabel(), element);

        try {
            updatePreview(file);
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
        }
    }

    /**
     * Update the preview of the file.
     *
     * @param file the file for preview or null.
     */
    private void updatePreview(@Nullable final Path file) {

        final ImageView imageView = getImageView();
        imageView.setVisible(false);

        final TextArea textView = getTextView();
        textView.setVisible(false);

        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        if (JMEFilePreviewManager.isJmeFile(file)) {

            final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
            previewManager.show(file, width, height);

            final ImageView sourceView = previewManager.getImageView();
            final ObjectProperty<Image> imageProperty = imageView.imageProperty();
            imageProperty.bind(sourceView.imageProperty());

            imageView.setVisible(true);

        } else if (JavaFXImageManager.isImage(file)) {

            final Image preview = JAVA_FX_IMAGE_MANAGER.getTexturePreview(file, width, height);
            imageView.setImage(preview);
            imageView.setVisible(true);

        } else if (JMEFilePreviewManager.isAudioFile(file)) {
        } else if (file != null && !Files.isDirectory(file)) {

            imageView.imageProperty().unbind();
            imageView.setImage(null);

            textView.setText(FileUtils.read(file));
            textView.setVisible(true);

        } else {
            imageView.imageProperty().unbind();
            imageView.setImage(null);
        }
    }

    @Override
    public void hide() {

        FX_EVENT_MANAGER.removeEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler);
        FX_EVENT_MANAGER.removeEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle);
        FX_EVENT_MANAGER.removeEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.clear();

        super.hide();
    }

    /**
     * Validate the resource element.
     *
     * @param warningLabel the warning label
     * @param element      the element.
     */
    protected void validate(@NotNull final Label warningLabel, @Nullable final ResourceElement element) {
    }

    @Override
    protected void createActions(@NotNull final VBox root) {

        final HBox container = new HBox();

        warningLabel = new Label();
        warningLabel.setGraphic(new ImageView(Icons.WARNING_24));
        warningLabel.setVisible(false);

        okButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processSelect());
        okButton.disableProperty().bind(buildDisableCondition());

        final Button cancelButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(warningLabel, CSSClasses.DIALOG_LABEL_WARNING);
        FXUtils.addClassTo(okButton, cancelButton, CSSClasses.DIALOG_BUTTON);
        FXUtils.addClassTo(root, CSSClasses.ASSET_EDITOR_DIALOG_ACTIONS);

        FXUtils.addToPane(warningLabel, container);
        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);
    }

    /**
     * Build disable condition boolean binding.
     *
     * @return the boolean binding
     */
    protected BooleanBinding buildDisableCondition() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final ReadOnlyObjectProperty<TreeItem<ResourceElement>> selectedItemProperty = selectionModel.selectedItemProperty();

        final Label warningLabel = getWarningLabel();
        return warningLabel.visibleProperty().or(selectedItemProperty.isNull());
    }

    /**
     * Gets consumer.
     *
     * @return the function for handling the choose.
     */
    @NotNull
    protected Consumer<C> getConsumer() {
        return consumer;
    }

    /**
     * @return the tree with all resources.
     */
    @NotNull
    private ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    /**
     * The process of choosing the element.
     */
    private void processSelect() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            hide();
            return;
        }

        processOpen(selectedItem.getValue());
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return ASSET_EDITOR_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
