package com.ss.editor.ui.dialog.converter;

import static java.util.Objects.requireNonNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.choose.ChooseFolderControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The dialog with settings of converting a model.
 *
 * @author JavaSaBr
 */
public class ModelConverterDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Insets CONTAINER_OFFSET = new Insets(20, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    @NotNull
    private static final Point DIALOG_SIZE = new Point(570, 250);

    /**
     * The callback.
     */
    @NotNull
    private final Consumer<ModelConverterDialog> callback;

    /**
     * The filename field.
     */
    @Nullable
    private TextField filenameField;

    /**
     * The destination folder control.
     */
    @Nullable
    private ChooseFolderControl destinationControl;

    /**
     * The export materials check box.
     */
    @Nullable
    private CheckBox exportMaterialsCheckBox;

    /**
     * The materials destination folder.
     */
    @Nullable
    private ChooseFolderControl materialsFolderControl;

    /**
     * The overwrite materials check box.
     */
    @Nullable
    private CheckBox overwriteMaterialsCheckBox;

    public ModelConverterDialog(@NotNull final Path source, @NotNull final Path destination,
                                @NotNull final Consumer<ModelConverterDialog> callback) {
        this.callback = callback;
        getDestinationControl().setFolder(destination.getParent());
        getFilenameField().setText(destination.getFileName().toString());
    }

    @Override
    protected void createContent(@NotNull final VBox root) {

        final Label filenameLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_RESULT_NAME + ":");
        filenameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        filenameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        filenameField = new TextField();
        filenameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        filenameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        filenameField.textProperty().addListener((observable, oldValue, newValue) -> validate());

        final Label destinationLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_DESTINATION_FOLDER + ":");
        destinationLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        destinationLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        destinationControl = new ChooseFolderControl();
        filenameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        destinationControl.setChangeHandler(this::validate);

        final Label exportMaterialsLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS + ":");
        exportMaterialsLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        exportMaterialsLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        exportMaterialsCheckBox = new CheckBox();
        exportMaterialsCheckBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        exportMaterialsCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        exportMaterialsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        final Label materialsFolderLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER + ":");
        materialsFolderLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        materialsFolderLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        materialsFolderControl = new ChooseFolderControl();
        materialsFolderControl.setId(CSSIds.EDITOR_DIALOG_FIELD);
        materialsFolderControl.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        materialsFolderControl.disableProperty().bind(exportMaterialsCheckBox.selectedProperty().not());
        materialsFolderControl.setChangeHandler(this::validate);

        final Label overwiteMaterials = new Label(Messages.MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS + ":");
        overwiteMaterials.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        overwiteMaterials.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        overwriteMaterialsCheckBox = new CheckBox();
        overwriteMaterialsCheckBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        overwriteMaterialsCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        overwriteMaterialsCheckBox.disableProperty().bind(exportMaterialsCheckBox.selectedProperty().not());

        final GridPane settingsContainer = new GridPane();
        settingsContainer.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);
        settingsContainer.add(filenameLabel, 0, 0);
        settingsContainer.add(filenameField, 1, 0);
        settingsContainer.add(destinationLabel, 0, 1);
        settingsContainer.add(destinationControl, 1, 1);
        settingsContainer.add(exportMaterialsLabel, 0, 2);
        settingsContainer.add(exportMaterialsCheckBox, 1, 2);
        settingsContainer.add(materialsFolderLabel, 0, 3);
        settingsContainer.add(materialsFolderControl, 1, 3);
        settingsContainer.add(overwiteMaterials, 0, 4);
        settingsContainer.add(overwriteMaterialsCheckBox, 1, 4);

        FXUtils.addToPane(settingsContainer, root);

        FXUtils.addClassTo(filenameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(filenameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(destinationLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(exportMaterialsLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(materialsFolderLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(overwiteMaterials, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(settingsContainer, CONTAINER_OFFSET);
    }

    /**
     * Validate.
     */
    private void validate() {

        final Button okButton = getOkButton();
        okButton.setDisable(true);

        final TextField filenameField = getFilenameField();

        if (StringUtils.isEmpty(filenameField.getText())) {
            return;
        }

        final ChooseFolderControl destinationControl = getDestinationControl();

        if (destinationControl.getFolder() == null) {
            return;
        }

        final CheckBox exportMaterialsCheckBox = getExportMaterialsCheckBox();
        final ChooseFolderControl materialsFolderControl = getMaterialsFolderControl();

        if (exportMaterialsCheckBox.isSelected() && materialsFolderControl.getFolder() == null) {
            return;
        }

        okButton.setDisable(false);
    }

    /**
     * @return the export materials check box.
     */
    @NotNull
    private CheckBox getExportMaterialsCheckBox() {
        return requireNonNull(exportMaterialsCheckBox);
    }

    /**
     * @return true if need to export materials.
     */
    public boolean isExportMaterials() {
        return getExportMaterialsCheckBox().isSelected();
    }

    /**
     * @return the overwrite materials check box.
     */
    @NotNull
    private CheckBox getOverwriteMaterialsCheckBox() {
        return requireNonNull(overwriteMaterialsCheckBox);
    }

    /**
     * @return true if we can overwrite materials.
     */
    public boolean isOverwriteMaterials() {
        return getOverwriteMaterialsCheckBox().isSelected();
    }

    /**
     * @return the destination folder control.
     */
    @NotNull
    private ChooseFolderControl getDestinationControl() {
        return requireNonNull(destinationControl);
    }

    /**
     * @return the destination folder.
     */
    @NotNull
    public Path getDestinationFolder() {
        return requireNonNull(getDestinationControl().getFolder());
    }

    /**
     * @return the materials destination folder control.
     */
    @NotNull
    private ChooseFolderControl getMaterialsFolderControl() {
        return requireNonNull(materialsFolderControl);
    }

    /**
     * @return the materials destination folder.
     */
    @NotNull
    public Path getMaterialsFolder() {
        return requireNonNull(getMaterialsFolderControl().getFolder());
    }

    /**
     * @return the filename field.
     */
    @NotNull
    private TextField getFilenameField() {
        return requireNonNull(filenameField);
    }

    /**
     * @return the filename.
     */
    @NotNull
    public String getFilename() {
        return getFilenameField().getText();
    }

    /**
     * @return the callback.
     */
    @NotNull
    private Consumer<ModelConverterDialog> getCallback() {
        return callback;
    }

    @Override
    protected void processOk() {
        super.processOk();
        getCallback().accept(this);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.MODEL_CONVERTER_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.MODEL_CONVERTER_DIALOG_BUTTON_OK;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
