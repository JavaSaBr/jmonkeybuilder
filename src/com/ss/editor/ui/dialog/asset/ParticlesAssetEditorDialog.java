package com.ss.editor.ui.dialog.asset;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import tonegod.emitter.material.ParticlesMaterial;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;

/**
 * The implementation of the {@link AssetEditorDialog} for choosing the {@link ParticlesMaterial}
 * from asset.
 *
 * @author JavaSaBr
 */
public class ParticlesAssetEditorDialog extends AssetEditorDialog<ParticlesMaterial> {

    /**
     * The label 'Texture parameter name'.
     */
    private Label textureParamNameLabel;

    /**
     * The label 'Apply lighting transform'.
     */
    private Label applyLightingTransformLabel;

    /**
     * The combo box with texture parameter name.
     */
    private ComboBox<String> textureParamNameComboBox;

    /**
     * The check box for applying the lighting transform.
     */
    private CheckBox applyLightingTransformCheckBox;

    public ParticlesAssetEditorDialog(@NotNull final Consumer<ParticlesMaterial> consumer) {
        super(consumer);
    }

    @NotNull
    @Override
    protected Parent buildSecondPart(final HBox container) {

        final GridPane settingsContainer = new GridPane();
        settingsContainer.setId(CSSIds.PARTICLES_ASSET_EDITOR_DIALOG_SETTINGS_CONTAINER);

        textureParamNameLabel = new Label(Messages.PARTICLE_ASSET_EDITOR_DIALOG_TEXTURE_PARAM_LABEL + ":");
        textureParamNameLabel.setId(CSSIds.PARTICLES_ASSET_EDITOR_DIALOG_LABEL);

        applyLightingTransformLabel = new Label(Messages.PARTICLE_ASSET_EDITOR_DIALOG_LIGHTING_TRANSFORM_LABEL + ":");
        applyLightingTransformLabel.setId(CSSIds.PARTICLES_ASSET_EDITOR_DIALOG_LABEL);

        textureParamNameComboBox = new ComboBox<>();
        textureParamNameComboBox.setId(CSSIds.PARTICLES_ASSET_EDITOR_DIALOG_CONTROL);

        applyLightingTransformCheckBox = new CheckBox();
        applyLightingTransformCheckBox.setId(CSSIds.PARTICLES_ASSET_EDITOR_DIALOG_CONTROL);

        final VBox previewContainer = new VBox();
        previewContainer.setId(CSSIds.PARTICLES_ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);

        imageView = new ImageView();
        imageView.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        imageView.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        FXUtils.addToPane(imageView, previewContainer);

        settingsContainer.add(textureParamNameLabel, 0, 0);
        settingsContainer.add(textureParamNameComboBox, 1, 0);
        settingsContainer.add(applyLightingTransformLabel, 0, 1);
        settingsContainer.add(applyLightingTransformCheckBox, 1, 1);
        settingsContainer.add(previewContainer, 0, 2, 2, 2);

        HBox.setMargin(settingsContainer, SECOND_PART_OFFSET_OFFSET);

        return settingsContainer;
    }

    /**
     * @return the combo box with texture parameter name.
     */
    private ComboBox<String> getTextureParamNameComboBox() {
        return textureParamNameComboBox;
    }

    /**
     * @return the check box for applying the lighting transform.
     */
    private CheckBox getApplyLightingTransformCheckBox() {
        return applyLightingTransformCheckBox;
    }

    @Override
    protected void processOpen(@NotNull final ResourceElement element) {
        super.processOpen(element);

        final ComboBox<String> textureParamNameBox = getTextureParamNameComboBox();
        final SingleSelectionModel<String> selectionModel = textureParamNameBox.getSelectionModel();
        final String textureParamName = selectionModel.getSelectedItem();

        final CheckBox transformBox = getApplyLightingTransformCheckBox();

        final AssetManager assetManager = EDITOR.getAssetManager();

        final Path file = element.getFile();
        final Path assetFile = getAssetFile(file);

        final Material material = assetManager.loadAsset(new MaterialKey(toAssetPath(assetFile)));

        final Consumer<ParticlesMaterial> consumer = getConsumer();
        consumer.accept(new ParticlesMaterial(material, textureParamName, transformBox.isSelected()));
    }

    @Override
    protected void validate(@NotNull final Label warningLabel, @Nullable final ResourceElement element) {

        final ComboBox<String> comboBox = getTextureParamNameComboBox();
        final ObservableList<String> items = comboBox.getItems();
        items.clear();

        final Path file = element == null ? null : element.getFile();

        if (file != null) {

            final AssetManager assetManager = EDITOR.getAssetManager();
            final Path assetFile = getAssetFile(file);

            final Material material = assetManager.loadAsset(new MaterialKey(toAssetPath(assetFile)));
            final MaterialDef materialDef = material.getMaterialDef();

            final Collection<MatParam> materialParams = materialDef.getMaterialParams();
            materialParams.stream()
                    .filter(matParam -> matParam.getVarType() == VarType.Texture2D)
                    .forEach(filtred -> items.add(filtred.getName()));

            if (!items.isEmpty()) comboBox.getSelectionModel().select(0);
        }

        super.validate(warningLabel, element);
    }
}
