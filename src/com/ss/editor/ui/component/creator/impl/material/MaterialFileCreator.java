package com.ss.editor.ui.component.creator.impl.material;

import static com.ss.editor.FileExtensions.JME_MATERIAL;
import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.AutoCompleteComboBoxListener;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create a new material.
 *
 * @author JavaSaBr
 */
public class MaterialFileCreator extends AbstractFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    private static final String PBR_MAT_DEF = "Common/MatDefs/Light/PBRLighting.j3md";
    private static final String LIGHTING_MAT_DEF = "Common/MatDefs/Light/Lighting.j3md";

    static {
        DESCRIPTION.setFileDescription(Messages.MATERIAL_FILE_CREATOR_FILE_DESCRIPTION);
        DESCRIPTION.setConstructor(MaterialFileCreator::new);
    }

    /**
     * The list of available definitions.
     */
    @NotNull
    private Array<String> definitions;

    /**
     * The combo box.
     */
    @Nullable
    private ComboBox<String> materialTypeComboBox;

    private MaterialFileCreator() {
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.MATERIAL_FILE_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return JME_MATERIAL;
    }

    /**
     * @return the combo box.
     */
    @NotNull
    private ComboBox<String> getMaterialTypeComboBox() {
        return requireNonNull(materialTypeComboBox);
    }

    @Override
    protected void createSettings(@NotNull final GridPane root) {
        super.createSettings(root);

        final Label materialTypeLabel = new Label(Messages.MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL + ":");
        materialTypeLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        materialTypeLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        materialTypeComboBox = new ComboBox<>();
        materialTypeComboBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        materialTypeComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final TextField editor = materialTypeComboBox.getEditor();
        editor.setId(CSSIds.EDITOR_DIALOG_FIELD);

        AutoCompleteComboBoxListener.install(materialTypeComboBox);

        definitions = RESOURCE_MANAGER.getAvailableMaterialDefinitions();

        final ObservableList<String> items = materialTypeComboBox.getItems();
        items.clear();
        items.addAll(definitions);

        final SingleSelectionModel<String> selectionModel = materialTypeComboBox.getSelectionModel();

        if (definitions.contains(PBR_MAT_DEF)) {
            selectionModel.select(PBR_MAT_DEF);
        } else if (definitions.contains(LIGHTING_MAT_DEF)) {
            selectionModel.select(LIGHTING_MAT_DEF);
        } else {
            selectionModel.select(definitions.first());
        }

        selectionModel.selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validateFileName());

        root.add(materialTypeLabel, 0, 1);
        root.add(materialTypeComboBox, 1, 1);

        FXUtils.addClassTo(materialTypeComboBox, CSSClasses.TRANSPARENT_COMBO_BOX);
        FXUtils.addClassTo(materialTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(materialTypeComboBox, CSSClasses.SPECIAL_FONT_14);
    }

    @Override
    protected void validateFileName() {
        super.validateFileName();

        final Button okButton = getOkButton();
        if (okButton.isDisable()) return;

        final ComboBox<String> materialTypeComboBox = getMaterialTypeComboBox();
        final SingleSelectionModel<String> selectionModel = materialTypeComboBox.getSelectionModel();

        final String selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            okButton.setDisable(true);
            return;
        }

        okButton.setDisable(!definitions.contains(selectedItem));
    }

    @Override
    protected void processOk() {
        super.processOk();

        final AssetManager assetManager = EDITOR.getAssetManager();

        final ComboBox<String> materialTypeComboBox = getMaterialTypeComboBox();
        final SingleSelectionModel<String> selectionModel = materialTypeComboBox.getSelectionModel();

        final Material material = new Material(assetManager, selectionModel.getSelectedItem());
        material.getAdditionalRenderState();

        final String materialContent = MaterialSerializer.serializeToString(material);
        final Path fileToCreate = getFileToCreate();

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(fileToCreate))) {
            out.print(materialContent);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(fileToCreate, true);
    }
}
