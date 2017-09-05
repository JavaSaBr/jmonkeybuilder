package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.FileExtensions.TEXTURE_EXTENSIONS;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
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

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The implementation of the {@link PropertyControl} to edit {@link com.jme3.texture.Texture2D} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public class Texture2DPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Texture2D> {

    /**
     * The constant NO_TEXTURE.
     */
    @NotNull
    protected static final String NO_TEXTURE = Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE;

    @NotNull
    protected static final Point DIALOG_SIZE = new Point(600, -1);

    @NotNull
    protected static final String PROP_FLIP = "flip";

    @NotNull
    protected static final String PROP_WRAP_MODE_S = "wrapModeS";

    @NotNull
    protected static final String PROP_WRAP_MODE_T = "wrapModeT";

    @NotNull
    protected static final String PROP_MAG_FILTER = "magFilter";

    @NotNull
    protected static final String PROP_MIN_FILTER = "minFilter";

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

    public Texture2DPropertyControl(@Nullable final Texture2D propertyValue, @NotNull final String propertyName,
                                    @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
    }

    /**
     * Handle dropped files to editor.
     */
    protected void dragDropped(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, TEXTURE_EXTENSIONS, this, Texture2DPropertyControl::setTexture);
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

        final Button settingsButton = new Button();
        settingsButton.setGraphic(new ImageView(Icons.SETTINGS_16));
        settingsButton.setOnAction(event -> openSettings());
        settingsButton.disableProperty().bind(buildDisableRemoveCondition());

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
                .subtract(settingsButton.widthProperty())
                .subtract(addButton.widthProperty()));

        FXUtils.addToPane(textureLabel, container);
        FXUtils.addToPane(previewContainer, container);
        FXUtils.addToPane(addButton, container);
        FXUtils.addToPane(settingsButton, container);
        FXUtils.addToPane(removeButton, container);
        FXUtils.addToPane(texturePreview, previewContainer);

        FXUtils.addClassesTo(container, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(previewContainer, CSSClasses.ABSTRACT_PARAM_CONTROL_PREVIEW_CONTAINER);
        FXUtils.addClassTo(textureLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);
        FXUtils.addClassesTo(settingsButton, addButton, removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);
    }

    protected @NotNull BooleanBinding buildDisableRemoveCondition() {
        return getTexturePreview().imageProperty().isNull();
    }

    private @NotNull Label getTextureLabel() {
        return notNull(textureLabel);
    }

    private @NotNull ImageView getTexturePreview() {
        return notNull(texturePreview);
    }

    private @NotNull ImageChannelPreview getTextureTooltip() {
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
        UIUtils.openFileAssetDialog(this::setTexture, TEXTURE_EXTENSIONS, DEFAULT_ACTION_TESTER);
    }

    /**
     * Process to open texture's settings.
     */
    protected void openSettings() {

        final Texture2D texture = notNull(getPropertyValue());
        final TextureKey key = (TextureKey) texture.getKey();
        final boolean flipY = key.isFlipY();
        final Texture.WrapMode wrapS = texture.getWrap(Texture.WrapAxis.S);
        final Texture.WrapMode wrapT = texture.getWrap(Texture.WrapAxis.T);
        final Texture.MagFilter magFilter = texture.getMagFilter();
        final Texture.MinFilter minFilter = texture.getMinFilter();

        final Array<PropertyDefinition> properties = ArrayFactory.newArray(PropertyDefinition.class);
        properties.add(new PropertyDefinition(EditablePropertyType.BOOLEAN, Messages.MATERIAL_MODEL_PROPERTY_CONTROL_FLIP_Y, PROP_FLIP, flipY));
        properties.add(new PropertyDefinition(EditablePropertyType.ENUM, Messages.MATERIAL_MODEL_PROPERTY_CONTROL_WRAP_MODE_S, PROP_WRAP_MODE_S, wrapS));
        properties.add(new PropertyDefinition(EditablePropertyType.ENUM, Messages.MATERIAL_MODEL_PROPERTY_CONTROL_WRAP_MODE_T, PROP_WRAP_MODE_T, wrapT));
        properties.add(new PropertyDefinition(EditablePropertyType.ENUM, Messages.MATERIAL_MODEL_PROPERTY_CONTROL_MAG_FILTER, PROP_MAG_FILTER, magFilter));
        properties.add(new PropertyDefinition(EditablePropertyType.ENUM, Messages.MATERIAL_MODEL_PROPERTY_CONTROL_MIN_FILTER, PROP_MIN_FILTER, minFilter));

        final GenericFactoryDialog dialog = new GenericFactoryDialog(properties, this::applyChanges);
        dialog.setTitle(Messages.MATERIAL_MODEL_PROPERTY_CONTROL_TEXTURE_SETTINGS);
        dialog.setButtonOkText(Messages.SIMPLE_DIALOG_BUTTON_APPLY);
        dialog.setButtonCloseText(Messages.SIMPLE_DIALOG_BUTTON_CANCEL);
        dialog.configureSize(DIALOG_SIZE);
        dialog.show();
    }

    /**
     * Apple new changes if need.
     *
     * @param varTable the var table.
     */
    private void applyChanges(@NotNull final VarTable varTable) {

        final Texture2D texture = notNull(getPropertyValue());
        final TextureKey key = (TextureKey) texture.getKey();
        final boolean flipY = key.isFlipY();
        final Texture.WrapMode wrapS = texture.getWrap(Texture.WrapAxis.S);
        final Texture.WrapMode wrapT = texture.getWrap(Texture.WrapAxis.T);
        final Texture.MagFilter magFilter = texture.getMagFilter();
        final Texture.MinFilter minFilter = texture.getMinFilter();

        final boolean needFlipY = varTable.getBoolean(PROP_FLIP);
        final Texture.WrapMode needWrapS = varTable.getEnum(PROP_WRAP_MODE_S, Texture.WrapMode.class);
        final Texture.WrapMode needWrapT = varTable.getEnum(PROP_WRAP_MODE_T, Texture.WrapMode.class);
        final Texture.MagFilter needMagFilter = varTable.getEnum(PROP_MAG_FILTER, Texture.MagFilter.class);
        final Texture.MinFilter needMinFilter = varTable.getEnum(PROP_MIN_FILTER, Texture.MinFilter.class);

        if (flipY == needFlipY && wrapS == needWrapS && wrapT == needWrapT && magFilter == needMagFilter &&
                minFilter == needMinFilter) {
            return;
        }

        final TextureKey newKey = new TextureKey(key.getName());
        newKey.setFlipY(needFlipY);

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.deleteFromCache(key);

        final Texture2D loadedTexture = (Texture2D) assetManager.loadTexture(newKey);
        loadedTexture.setWrap(Texture.WrapAxis.S, needWrapS);
        loadedTexture.setWrap(Texture.WrapAxis.T, needWrapT);
        loadedTexture.setMagFilter(needMagFilter);
        loadedTexture.setMinFilter(needMinFilter);

        changed(loadedTexture, texture);
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

            final EditorConfig config = EditorConfig.getInstance();
            final Path assetFile = notNull(getAssetFile(file));
            final TextureKey textureKey = new TextureKey(toAssetPath(assetFile));
            textureKey.setFlipY(config.isDefaultUseFlippedTexture());

            final AssetManager assetManager = EDITOR.getAssetManager();
            final Texture2D texture = (Texture2D) assetManager.loadTexture(textureKey);
            texture.setWrap(Texture.WrapMode.Repeat);

            changed(texture, getPropertyValue());
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
            preview.setDisable(true);
            preview.setMouseTransparent(true);
        } else {

            preview.setDisable(false);
            preview.setMouseTransparent(false);

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
