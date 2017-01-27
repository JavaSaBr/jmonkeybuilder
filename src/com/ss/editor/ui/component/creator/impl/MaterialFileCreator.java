package com.ss.editor.ui.component.creator.impl;

import static com.ss.editor.FileExtensions.JME_MATERIAL;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.AutoCompleteComboBoxListener;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

/**
 * The creator for creating a new material.
 *
 * @author JavaSaBr
 */
public class MaterialFileCreator extends AbstractFileCreator {

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();
    public static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    public static final String PBR_MAT_DEF = "Common/MatDefs/Light/PBRLighting.j3md";
    public static final String LIGHTING_MAT_DEF = "Common/MatDefs/Light/Lighting.j3md";

    static {
        DESCRIPTION.setFileDescription(Messages.MATERIAL_FILE_CREATOR_FILE_DESCRIPTION);
        DESCRIPTION.setConstructor(MaterialFileCreator::new);
    }

    /**
     * The list of available definitions.
     */
    private Array<String> definitions;

    /**
     * The combo box.
     */
    private ComboBox<String> materialTypeComboBox;

    public MaterialFileCreator() {
        super();
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
    public ComboBox<String> getMaterialTypeComboBox() {
        return materialTypeComboBox;
    }

    @Override
    protected void createSettings(@NotNull final VBox root) {
        super.createSettings(root);

        final HBox materialTypeContainer = new HBox();

        final Label materialTypeLabel = new Label(Messages.MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL + ":");
        materialTypeLabel.setId(CSSIds.FILE_CREATOR_LABEL);

        materialTypeComboBox = new ComboBox<>();
        materialTypeComboBox.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);
        materialTypeComboBox.prefWidthProperty().bind(root.widthProperty());

        final TextField editor = materialTypeComboBox.getEditor();
        editor.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);

        AutoCompleteComboBoxListener.install(materialTypeComboBox);

        final ObservableList<String> items = materialTypeComboBox.getItems();

        definitions = RESOURCE_MANAGER.getAvailableMaterialDefinitions();
        definitions.forEach(items::add);

        final SingleSelectionModel<String> selectionModel = materialTypeComboBox.getSelectionModel();

        if (definitions.contains(PBR_MAT_DEF)) {
            selectionModel.select(PBR_MAT_DEF);
        } else if (definitions.contains(LIGHTING_MAT_DEF)) {
            selectionModel.select(LIGHTING_MAT_DEF);
        } else {
            selectionModel.select(definitions.first());
        }

        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFileName());

        FXUtils.addToPane(materialTypeLabel, materialTypeContainer);
        FXUtils.addToPane(materialTypeComboBox, materialTypeContainer);
        FXUtils.addToPane(materialTypeContainer, root);

        FXUtils.addClassTo(materialTypeComboBox, CSSClasses.TRANSPARENT_COMBO_BOX);
        FXUtils.addClassTo(materialTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(materialTypeComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(materialTypeContainer, FILE_NAME_CONTAINER_OFFSET);
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
    protected void processCreate() {
        super.processCreate();

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
