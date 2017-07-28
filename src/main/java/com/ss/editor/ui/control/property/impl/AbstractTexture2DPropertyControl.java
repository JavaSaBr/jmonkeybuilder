package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.FileExtensions.TEXTURE_EXTENSIONS;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Texture2D;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit {@link com.jme3.texture.Texture2D} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public abstract class AbstractTexture2DPropertyControl<C extends ChangeConsumer, T> extends
        AbstractPropertyControl<C, T, Texture2D> {

    /**
     * The constant NO_TEXTURE.
     */
    @NotNull
    protected static final String NO_TEXTURE = Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE;

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
     * Instantiates a new Abstract vector 3 f property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractTexture2DPropertyControl(@Nullable final Texture2D propertyValue, @NotNull final String propertyName,
                                            @NotNull final C changeConsumer,
                                            @NotNull final SixObjectConsumer<C, T, String, Texture2D, Texture2D, BiConsumer<T, Texture2D>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
    }

    /**
     * Handle dropped files to editor.
     */
    protected void dragDropped(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, TEXTURE_EXTENSIONS, this, AbstractTexture2DPropertyControl::setTexture);
    }

    /**
     * Handle drag over.
     */
    protected void dragOver(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, TEXTURE_EXTENSIONS);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        textureLabel = new Label(NO_TEXTURE);
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
        removeButton.disableProperty().bind(buildDisableRemoveCondition());

        textureLabel.prefWidthProperty().bind(widthProperty()
                .subtract(removeButton.widthProperty())
                .subtract(previewContainer.widthProperty())
                .subtract(addButton.widthProperty()));

        FXUtils.addToPane(textureLabel, container);
        FXUtils.addToPane(previewContainer, container);
        FXUtils.addToPane(addButton, container);
        FXUtils.addToPane(removeButton, container);
        FXUtils.addToPane(texturePreview, previewContainer);

        FXUtils.addClassesTo(container, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(previewContainer, CSSClasses.ABSTRACT_PARAM_CONTROL_PREVIEW_CONTAINER);
        FXUtils.addClassTo(textureLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);
        FXUtils.addClassesTo(addButton, removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);
    }

    @NotNull
    protected BooleanBinding buildDisableRemoveCondition() {
        return getTexturePreview().imageProperty().isNull();
    }

    @NotNull
    private Label getTextureLabel() {
        return notNull(textureLabel);
    }

    @NotNull
    private ImageView getTexturePreview() {
        return notNull(texturePreview);
    }

    @NotNull
    private ImageChannelPreview getTextureTooltip() {
        return notNull(textureTooltip);
    }

    /**
     * Process to remove the current texture.
     */
    protected void processRemove() {
        setTexture(null);
    }

    /**
     * Process to add a new texture.
     */
    protected void processAdd() {
        UIUtils.openAssetDialog(this::setTexture, TEXTURE_EXTENSIONS, DEFAULT_ACTION_TESTER);
    }

    /**
     * Sets new texture to this property.
     *
     * @param file the file to new texture.
     */
    protected void setTexture(@Nullable final Path file) {

        if (file == null) {
            changed(null, getPropertyValue());
        } else {

            final Path assetFile = notNull(getAssetFile(file));
            final TextureKey textureKey = new TextureKey(toAssetPath(assetFile));

            final AssetManager assetManager = EDITOR.getAssetManager();
            final Texture2D texture = (Texture2D) assetManager.loadTexture(textureKey);

            changed(texture, getPropertyValue());
        }

        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    @Override
    protected void reload() {

        final Texture2D texture2D = getPropertyValue();
        final AssetKey key = texture2D == null ? null : texture2D.getKey();

        final Label textureLabel = getTextureLabel();
        textureLabel.setText(key == null ? NO_TEXTURE : key.getName());

        final ImageChannelPreview textureTooltip = getTextureTooltip();
        final ImageView preview = getTexturePreview();

        if (key == null) {
            preview.setImage(null);
            textureTooltip.clean();
        } else {

            final Path realFile = notNull(getRealFile(key.getName()));

            if (Files.exists(realFile)) {
                preview.setImage(IMAGE_MANAGER.getImagePreview(realFile, 24, 24));
                textureTooltip.showImage(realFile);
            } else {
                preview.setImage(IMAGE_MANAGER.getImagePreview(key.getName(), 24, 24));
                textureTooltip.showImage(key.getName());
            }
        }
    }
}
