package com.ss.editor.ui.dialog.asset;

import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;
import static com.ss.editor.manager.JavaFXImageManager.isImage;
import static java.nio.file.Files.isDirectory;

/**
 * The implementation of the {@link EditorDialog} for choosing the object from asset.
 *
 * @author JavaSaBr
 */
public class AssetEditorDialog<C> extends EditorDialog {

    protected static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    protected static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    protected static final Insets SECOND_PART_OFFSET_OFFSET = new Insets(0, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);

    protected static final Point DIALOG_SIZE = new Point(1200, 700);

    protected static final JavaFXImageManager JAVA_FX_IMAGE_MANAGER = JavaFXImageManager.getInstance();
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The function for handling the choose.
     */
    protected final Consumer<C> consumer;

    /**
     * The function for validating the choose.
     */
    protected final Function<C, String> validator;

    /**
     * The tree with all resources.
     */
    protected ResourceTree resourceTree;

    /**
     * The image preview.
     */
    protected ImageView imageView;

    /**
     * The label with any warning.
     */
    protected Label warningLabel;

    public AssetEditorDialog(@NotNull final Consumer<C> consumer) {
        this(consumer, null);
    }

    public AssetEditorDialog(@NotNull final Consumer<C> consumer, @Nullable final Function<C, String> validator) {
        this.consumer = consumer;
        this.validator = validator;
    }

    /**
     * @param extensionFilter the list of available extensions.
     */
    public void setExtensionFilter(final Array<String> extensionFilter) {
        resourceTree.setExtensionFilter(extensionFilter);
    }

    @Override
    protected void createContent(final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        resourceTree = new ResourceTree(this::processOpen, true);
        resourceTree.prefHeightProperty().bind(root.heightProperty());
        resourceTree.prefWidthProperty().bind(root.widthProperty());
        resourceTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processSelected(newValue));

        final Parent secondPart = buildSecondPart(container);

        FXUtils.addToPane(resourceTree, container);
        FXUtils.addToPane(secondPart, container);
        FXUtils.addToPane(container, root);

        root.setOnKeyReleased(this::processKeyEvent);
    }

    @NotNull
    protected Parent buildSecondPart(final HBox container) {

        final VBox previewContainer = new VBox();
        previewContainer.setId(CSSIds.ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);

        imageView = new ImageView();
        imageView.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        imageView.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        FXUtils.addToPane(imageView, previewContainer);

        HBox.setMargin(previewContainer, SECOND_PART_OFFSET_OFFSET);

        return previewContainer;
    }

    /**
     * The process of opening the element.
     */
    protected void processOpen(@NotNull final ResourceElement element) {
        hide();
    }

    private void processKeyEvent(final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processSelect();
        }
    }

    @Override
    public void show(final Window owner) {
        super.show(owner);

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(editorConfig.getCurrentAsset());

        EXECUTOR_MANAGER.addFXTask(resourceTree::requestFocus);
    }

    /**
     * @return The image preview.
     */
    private ImageView getImageView() {
        return imageView;
    }

    /**
     * @return the function for validating the choose.
     */
    protected Function<C, String> getValidator() {
        return validator;
    }

    /**
     * @return the label with any warning.
     */
    private Label getWarningLabel() {
        return warningLabel;
    }

    /**
     * Handle selected element in the tree.
     */
    private void processSelected(final TreeItem<ResourceElement> newValue) {

        final ResourceElement element = newValue == null ? null : newValue.getValue();
        final Path file = element == null ? null : element.getFile();

        validate(getWarningLabel(), element);
        updatePreview(file);
    }

    /**
     * Update the preview of the file.
     *
     * @param file the file for preview or null.
     */
    protected void updatePreview(@Nullable final Path file) {

        final ImageView imageView = getImageView();

        if (file == null || isDirectory(file) || !isImage(file)) {
            imageView.setImage(null);
            return;
        }

        final Image preview = JAVA_FX_IMAGE_MANAGER.getTexturePreview(file, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        imageView.setImage(preview);
    }

    /**
     * Validate the resource element.
     *
     * @param element the element.
     */
    protected void validate(@NotNull final Label warningLabel, @Nullable final ResourceElement element) {
    }

    @Override
    protected void createActions(final VBox root) {

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        warningLabel = new Label();
        warningLabel.setId(CSSIds.EDITOR_DIALOG_LABEL_WARNING);
        warningLabel.setGraphic(new ImageView(Icons.WARNING_24));
        warningLabel.setVisible(false);

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final ReadOnlyObjectProperty<TreeItem<ResourceElement>> selectedItemProperty = selectionModel.selectedItemProperty();

        final Button okButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processSelect());
        okButton.disableProperty().bind(warningLabel.visibleProperty()
                .or(selectedItemProperty.isNull()));

        final Button cancelButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addClassTo(warningLabel, CSSClasses.MAIN_FONT_15_BOLD);

        FXUtils.addToPane(warningLabel, container);
        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return the function for handling the choose.
     */
    protected Consumer<C> getConsumer() {
        return consumer;
    }

    /**
     * @return the tree with all resources.
     */
    private ResourceTree getResourceTree() {
        return resourceTree;
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

    @Override
    protected String getTitleText() {
        return ASSET_EDITOR_DIALOG_TITLE;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
