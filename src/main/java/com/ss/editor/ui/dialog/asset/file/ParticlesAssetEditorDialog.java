package com.ss.editor.ui.dialog.asset.file;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.material.ParticlesMaterial;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * The implementation of the {@link AssetEditorDialog} to chose the {@link ParticlesMaterial} from asset.
 *
 * @author JavaSaBr
 */
public class ParticlesAssetEditorDialog extends AssetEditorDialog<ParticlesMaterial> {

    /**
     * The label 'Texture parameter name'.
     */
    @Nullable
    private Label textureParamNameLabel;

    /**
     * The label 'Apply lighting transform'.
     */
    @Nullable
    private Label applyLightingTransformLabel;

    /**
     * The combo box with texture parameter name.
     */
    @Nullable
    private ComboBox<String> textureParamNameComboBox;

    /**
     * The check box for applying the lighting transform.
     */
    @Nullable
    private CheckBox applyLightingTransformCheckBox;

    public ParticlesAssetEditorDialog(@NotNull final Consumer<ParticlesMaterial> consumer) {
        super(consumer);
    }

    @Override
    protected @NotNull Region buildSecondPart(@NotNull final HBox container) {

        final Region preview = super.buildSecondPart(container);

        textureParamNameLabel = new Label(Messages.PARTICLE_ASSET_EDITOR_DIALOG_TEXTURE_PARAM_LABEL + ":");
        textureParamNameLabel.prefWidthProperty().bind(container.widthProperty().multiply(0.25));

        applyLightingTransformLabel = new Label(Messages.PARTICLE_ASSET_EDITOR_DIALOG_LIGHTING_TRANSFORM_LABEL + ":");
        applyLightingTransformLabel.prefWidthProperty().bind(container.widthProperty().multiply(0.25));

        textureParamNameComboBox = new ComboBox<>();
        textureParamNameComboBox.prefWidthProperty().bind(container.widthProperty().multiply(0.25));

        applyLightingTransformCheckBox = new CheckBox();
        applyLightingTransformCheckBox.prefWidthProperty().bind(container.widthProperty().multiply(0.25));

        final GridPane settingsContainer = new GridPane();
        settingsContainer.add(textureParamNameLabel, 0, 0);
        settingsContainer.add(textureParamNameComboBox, 1, 0);
        settingsContainer.add(applyLightingTransformLabel, 0, 1);
        settingsContainer.add(applyLightingTransformCheckBox, 1, 1);
        settingsContainer.add(preview, 0, 2, 2, 1);

        FXUtils.addClassTo(settingsContainer, CSSClasses.DEF_GRID_PANE);

        return settingsContainer;
    }

    /**
     * @return the combo box with texture parameter name.
     */
    @FXThread
    private @NotNull ComboBox<String> getTextureParamNameComboBox() {
        return notNull(textureParamNameComboBox);
    }

    /**
     * @return the check box for applying the lighting transform.
     */
    @FXThread
    private @NotNull CheckBox getApplyLightingTransformCheckBox() {
        return notNull(applyLightingTransformCheckBox);
    }

    @Override
    @FXThread
    protected void processOpen(@NotNull final ResourceElement element) {
        super.processOpen(element);

        final ComboBox<String> textureParamNameBox = getTextureParamNameComboBox();
        final SingleSelectionModel<String> selectionModel = textureParamNameBox.getSelectionModel();
        final String textureParamName = selectionModel.getSelectedItem();

        final CheckBox transformBox = getApplyLightingTransformCheckBox();

        final AssetManager assetManager = JME_APPLICATION.getAssetManager();

        final Path file = element.getFile();
        final Path assetFile = getAssetFile(file);

        if (assetFile == null) {
            throw new RuntimeException("AssetFile can't be null.");
        }

        final Material material = assetManager.loadAsset(new MaterialKey(toAssetPath(assetFile)));

        final Consumer<ParticlesMaterial> consumer = getConsumer();
        consumer.accept(new ParticlesMaterial(material, textureParamName, transformBox.isSelected()));
    }

    @Override
    @FXThread
    protected @NotNull ObservableBooleanValue buildAdditionalDisableCondition() {
        final ComboBox<String> comboBox = getTextureParamNameComboBox();
        final SingleSelectionModel<String> selectionModel = comboBox.getSelectionModel();
        final ReadOnlyObjectProperty<String> itemProperty = selectionModel.selectedItemProperty();
        final ObservableBooleanValue parent = super.buildAdditionalDisableCondition();
        return Bindings.and(parent, itemProperty.isNull().or(itemProperty.isEqualTo("")));
    }

    @Override
    @FXThread
    protected void validate(@NotNull final Label warningLabel, @Nullable final ResourceElement element) {

        final ComboBox<String> comboBox = getTextureParamNameComboBox();
        final ObservableList<String> items = comboBox.getItems();
        items.clear();

        final Path file = element == null ? null : element.getFile();

        if (file != null && !Files.isDirectory(file)) {

            final AssetManager assetManager = JME_APPLICATION.getAssetManager();
            final Path assetFile = getAssetFile(file);

            if (assetFile == null) {
                throw new RuntimeException("AssetFile can't be null.");
            }

            final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));
            final Material material = assetManager.loadAsset(materialKey);
            final MaterialDef materialDef = material.getMaterialDef();

            final Collection<MatParam> materialParams = materialDef.getMaterialParams();
            materialParams.stream()
                    .filter(param -> param.getVarType() == VarType.Texture2D)
                    .filter(matParam -> material.getTextureParam(matParam.getName()) != null)
                    .forEach(filtred -> items.add(filtred.getName()));

            final SingleSelectionModel<String> selectionModel = comboBox.getSelectionModel();

            if (!items.isEmpty()) {
                selectionModel.select(0);
            } else {
                selectionModel.select(null);
            }
        }

        super.validate(warningLabel, element);
    }
}
