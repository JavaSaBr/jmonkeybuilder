package com.ss.editor.ui.control.choose;

import static com.ss.editor.FileExtensions.TEXTURE_EXTENSIONS;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
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
    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();

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

    /**
     * Instantiates a new Choose texture control.
     */
    public ChooseTextureControl() {
        createComponents();
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        reload();
        FXUtils.addClassesTo(this, CSSClasses.DEF_HBOX, CSSClasses.CHOOSE_TEXTURE_CONTROL);
    }

    /**
     * Handle dropped files to editor.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, TEXTURE_EXTENSIONS, this, ChooseTextureControl::setTextureFile);
    }

    /**
     * Handle drag over.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, TEXTURE_EXTENSIONS);
    }

    /**
     * Sets change handler.
     *
     * @param changeHandler the handler.
     */
    public void setChangeHandler(@Nullable final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * tThe handler.
     */
    @Nullable
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * Create components.
     */
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

        FXUtils.addClassTo(textureLabel, CSSClasses.CHOOSE_TEXTURE_CONTROL_TEXTURE_LABEL);
        FXUtils.addClassTo(previewContainer, CSSClasses.CHOOSE_TEXTURE_CONTROL_PREVIEW);
        FXUtils.addClassesTo(wrapper, CSSClasses.TEXT_INPUT_CONTAINER, CSSClasses.DEF_HBOX);
        FXUtils.addClassesTo(addButton, removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(addButton, removeButton);

        removeButton.disableProperty().bind(texturePreview.imageProperty().isNull());
    }

    /**
     * Gets texture label.
     *
     * @return the label for the path to a texture.
     */
    @NotNull
    protected Label getTextureLabel() {
        return notNull(textureLabel);
    }

    /**
     * Gets wrapper.
     *
     * @return the wrapper.
     */
    @NotNull
    protected HBox getWrapper() {
        return notNull(wrapper);
    }

    /**
     * @return The image channels preview.
     */
    @NotNull
    private ImageChannelPreview getTextureTooltip() {
        return notNull(textureTooltip);
    }

    /**
     * Add new texture.
     */
    private void processAdd() {
        UIUtils.openAssetDialog(this, this::setTextureFile, TEXTURE_EXTENSIONS, ACTION_TESTER);
    }

    /**
     * Gets texture file.
     *
     * @return the selected file.
     */
    @Nullable
    public Path getTextureFile() {
        return textureFile;
    }

    /**
     * Sets texture file.
     *
     * @param textureFile the selected file.
     */
    public void setTextureFile(@Nullable final Path textureFile) {
        this.textureFile = textureFile;

        reload();

        final Runnable changeHandler = getChangeHandler();
        if (changeHandler != null) changeHandler.run();
    }

    /**
     * Remove the texture.
     */
    private void processRemove() {
        setTextureFile(null);
    }

    /**
     * @return the image preview.
     */
    @NotNull
    private ImageView getTexturePreview() {
        return notNull(texturePreview);
    }

    /**
     * Reload.
     */
    protected void reload() {

        final ImageChannelPreview textureTooltip = getTextureTooltip();
        final Label textureLabel = getTextureLabel();
        final ImageView preview = getTexturePreview();

        final Path textureFile = getTextureFile();

        if (textureFile == null) {
            textureLabel.setText(Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE);
            preview.setImage(null);
            textureTooltip.showImage(null);
            return;
        }

        final Path assetFile = notNull(getAssetFile(textureFile));

        textureLabel.setText(assetFile.toString());
        preview.setImage(IMAGE_MANAGER.getTexturePreview(textureFile, 28, 28));
        textureTooltip.showImage(textureFile);
    }
}
