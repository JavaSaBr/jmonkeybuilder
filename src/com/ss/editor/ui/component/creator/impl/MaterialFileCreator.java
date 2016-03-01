package com.ss.editor.ui.component.creator.impl;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.css.CSSIds;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

import static com.ss.editor.FileExtensions.JME_MATERIAL;

/**
 * Реализация создателя новых материалов.
 *
 * @author Ronn
 */
public class MaterialFileCreator extends AbstractFileCreator {

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();
    public static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    static {
        DESCRIPTION.setFileDescription(Messages.MATERIAL_FILE_CREATOR_FILE_DESCRIPTION);
        DESCRIPTION.setConstructor(MaterialFileCreator::new);
    }

    /**
     * Список с выбором типов материалов.
     */
    private ComboBox<String> materialTypeComboBox;

    public MaterialFileCreator() {
    }

    @Override
    protected String getTitleText() {
        return Messages.MATERIAL_FILE_CREATOR_TITLE;
    }

    @Override
    protected String getFileExtension() {
        return JME_MATERIAL;
    }

    /**
     * @return список с выбором типов материалов.
     */
    public ComboBox<String> getMaterialTypeComboBox() {
        return materialTypeComboBox;
    }

    @Override
    protected void createSettings(final VBox root) {
        super.createSettings(root);

        final HBox materialTypeContainer = new HBox();

        final Label materialTypeLabel = new Label(Messages.MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL + ":");
        materialTypeLabel.setId(CSSIds.FILE_CREATOR_LABEL);

        materialTypeComboBox = new ComboBox<>();
        materialTypeComboBox.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);
        materialTypeComboBox.prefWidthProperty().bind(root.widthProperty());

        final ObservableList<String> items = materialTypeComboBox.getItems();

        final Array<String> definitions = RESOURCE_MANAGER.getAvailableMaterialDefinitions();
        definitions.forEach(items::add);

        final SingleSelectionModel<String> selectionModel = materialTypeComboBox.getSelectionModel();

        if (definitions.contains("Common/MatDefs/Light/PBRLighting.j3md")) {
            selectionModel.select("Common/MatDefs/Light/PBRLighting.j3md");
        } else if (definitions.contains("Common/MatDefs/Light/Lighting.j3md")) {
            selectionModel.select("Common/MatDefs/Light/Lighting.j3md");
        } else {
            selectionModel.select(definitions.first());
        }

        FXUtils.addToPane(materialTypeLabel, materialTypeContainer);
        FXUtils.addToPane(materialTypeComboBox, materialTypeContainer);
        FXUtils.addToPane(materialTypeContainer, root);

        VBox.setMargin(materialTypeContainer, FILE_NAME_CONTAINER_OFFSET);
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
            LOGGER.warning(this, e);
        }

        notifyFileCreated(fileToCreate, true);
    }
}
