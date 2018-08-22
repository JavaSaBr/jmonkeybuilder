package com.ss.builder.ui.control.choose;

import static com.ss.editor.FileExtensions.TEXTURE_EXTENSIONS;
import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.JavaFxImageManager;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.builder.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.builder.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.builder.ui.css.CssClasses;
import com.ss.builder.ui.tooltip.ImageChannelPreview;
import com.ss.builder.ui.util.DynamicIconSupport;
import com.ss.builder.ui.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.JavaFxImageManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to choose textures.
 *
 * @author JavaSaBr
 */
public class ChooseTextureControl extends HBox {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    @NotNull
    private static final JavaFxImageManager IMAGE_MANAGER = JavaFxImageManager.getInstance();

    /**
     * The image channels preview.
     */
    @Nullable
    private ImageChannelPreview textureTooltip;

    /**
     * The image preview.
     */
    @Nullable
    private ImageView texturePreview;

    /**
     * The label for of path to a texture.
     */
    @Nullable
    private Label textureLabel;

    /**
     * The wrapper.
     */
    @Nullable
    private HBox wrapper;

    /**
     * The selected file.
     */
    @Nullable
    private Path textureFile;

    /**
     * The handler.
     */
    @Nullable
    private Runnable changeHandler;

    public ChooseTextureControl() {
        createComponents();
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        reload();
        FXUtils.addClassesTo(this, CssClasses.DEF_HBOX, CssClasses.CHOOSE_TEXTURE_CONTROL);
    }

    /**
     * Handle dropped files to editor.
     */
    @FxThread
    private void dragDropped(@NotNull final DragEvent dragEvent) {
        UiUtils.handleDroppedFile(dragEvent, this, ChooseTextureControl::setTextureFile);
    }

    /**
     * Handle drag over.
     */
    @FxThread
    private void dragOver(@NotNull final DragEvent dragEvent) {
        UiUtils.acceptIfHasFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS);
    }

    /**
     * Set the change handler.
     *
     * @param changeHandler the handler.
     */
    @FxThread
    public void setChangeHandler(@Nullable final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * Get the change handler.
     *
     * @return the change handler.
     */
    @FxThread
    private @Nullable Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * Create components.
     */
    @FxThread
    protected void createComponents() {

        textureLabel = new Label();
        textureTooltip = new ImageChannelPreview();

        final VBox previewContainer = new VBox();

        texturePreview = new ImageView();
        texturePreview.fitHeightProperty().bind(previewContainer.heightProperty());
        texturePreview.fitWidthProperty().bind(previewContainer.widthProperty());

        Tooltip.install(texturePreview, textureTooltip);

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());

        wrapper = new HBox(textureLabel, previewContainer, addButton, removeButton);
        wrapper.prefWidthProperty().bind(widthProperty());

        textureLabel.prefWidthProperty().bind(wrapper.widthProperty());

        FXUtils.addToPane(wrapper, this);
        FXUtils.addToPane(texturePreview, previewContainer);

        FXUtils.addClassTo(textureLabel, CssClasses.CHOOSE_TEXTURE_CONTROL_TEXTURE_LABEL);
        FXUtils.addClassTo(previewContainer, CssClasses.CHOOSE_TEXTURE_CONTROL_PREVIEW);
        FXUtils.addClassesTo(wrapper, CssClasses.TEXT_INPUT_CONTAINER, CssClasses.DEF_HBOX);
        FXUtils.addClassesTo(addButton, removeButton, CssClasses.FLAT_BUTTON,
                CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(addButton, removeButton);

        removeButton.disableProperty().bind(texturePreview.imageProperty().isNull());
    }

    /**
     * Get the texture label.
     *
     * @return the label for the path to a texture.
     */
    @FxThread
    protected @NotNull Label getTextureLabel() {
        return notNull(textureLabel);
    }

    /**
     * Get the wrapper.
     *
     * @return the wrapper.
     */
    @FxThread
    protected @NotNull HBox getWrapper() {
        return notNull(wrapper);
    }

    /**
     * Get the image channels preview.
     *
     * @return the image channels preview.
     */
    @FxThread
    private @NotNull ImageChannelPreview getTextureTooltip() {
        return notNull(textureTooltip);
    }

    /**
     * Add a new texture.
     */
    @FxThread
    private void processAdd() {
        UiUtils.openFileAssetDialog(this::setTextureFile, FileExtensions.TEXTURE_EXTENSIONS, ACTION_TESTER);
    }

    /**
     * Get the texture file.
     *
     * @return the selected file.
     */
    @FxThread
    public @Nullable Path getTextureFile() {
        return textureFile;
    }

    /**
     * Set the texture file.
     *
     * @param textureFile the selected file.
     */
    @FxThread
    public void setTextureFile(@Nullable final Path textureFile) {
        this.textureFile = textureFile;
        reload();
        final Runnable changeHandler = getChangeHandler();
        if (changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Remove the texture.
     */
    @FxThread
    private void processRemove() {
        setTextureFile(null);
    }

    /**
     * Get the image preview.
     *
     * @return the image preview.
     */
    @FxThread
    private @NotNull ImageView getTexturePreview() {
        return notNull(texturePreview);
    }

    /**
     * Reload.
     */
    @FxThread
    protected void reload() {

        final ImageChannelPreview textureTooltip = getTextureTooltip();
        final Label textureLabel = getTextureLabel();
        final ImageView preview = getTexturePreview();

        final Path textureFile = getTextureFile();

        if (textureFile == null) {
            textureLabel.setText(Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE);
            preview.setImage(null);
            textureTooltip.clean();
            return;
        }

        final Path assetFile = notNull(EditorUtils.getAssetFile(textureFile));

        textureLabel.setText(assetFile.toString());
        preview.setImage(IMAGE_MANAGER.getImagePreview(textureFile, 28, 28));
        textureTooltip.showImage(textureFile);
    }
}
