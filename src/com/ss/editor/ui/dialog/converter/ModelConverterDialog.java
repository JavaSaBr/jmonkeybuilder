package com.ss.editor.ui.dialog.converter;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.choose.ChooseFolderControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static final Point DIALOG_SIZE = new Point(570, -1);

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

    /**
     * Instantiates a new Model converter dialog.
     *
     * @param source      the source
     * @param destination the destination
     * @param callback    the callback
     */
    public ModelConverterDialog(@NotNull final Path source, @NotNull final Path destination,
                                @NotNull final Consumer<ModelConverterDialog> callback) {
        this.callback = callback;
        getDestinationControl().setFolder(destination.getParent());
        getFilenameField().setText(destination.getFileName().toString());
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label filenameLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_RESULT_NAME + ":");
        filenameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        filenameField = new TextField();
        filenameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        filenameField.textProperty().addListener((observable, oldValue, newValue) -> validate());

        final Label destinationLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_DESTINATION_FOLDER + ":");
        destinationLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        destinationControl = new ChooseFolderControl();
        destinationControl.maxWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        destinationControl.setChangeHandler(this::validate);

        final Label exportMaterialsLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS + ":");
        exportMaterialsLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        exportMaterialsCheckBox = new CheckBox();
        exportMaterialsCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        exportMaterialsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        final Label materialsFolderLabel = new Label(Messages.MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER + ":");
        materialsFolderLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        materialsFolderControl = new ChooseFolderControl();
        materialsFolderControl.maxWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        materialsFolderControl.disableProperty().bind(exportMaterialsCheckBox.selectedProperty().not());
        materialsFolderControl.setChangeHandler(this::validate);

        final Label overwiteMaterials = new Label(Messages.MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS + ":");
        overwiteMaterials.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        overwriteMaterialsCheckBox = new CheckBox();
        overwriteMaterialsCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));
        overwriteMaterialsCheckBox.disableProperty().bind(exportMaterialsCheckBox.selectedProperty().not());

        root.add(filenameLabel, 0, 0);
        root.add(filenameField, 1, 0);
        root.add(destinationLabel, 0, 1);
        root.add(destinationControl, 1, 1);
        root.add(exportMaterialsLabel, 0, 2);
        root.add(exportMaterialsCheckBox, 1, 2);
        root.add(materialsFolderLabel, 0, 3);
        root.add(materialsFolderControl, 1, 3);
        root.add(overwiteMaterials, 0, 4);
        root.add(overwriteMaterialsCheckBox, 1, 4);

        FXUtils.addClassTo(filenameLabel, destinationLabel, exportMaterialsLabel, materialsFolderLabel,
                overwiteMaterials, CSSClasses.DIALOG_DYNAMIC_LABEL);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
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
        return notNull(exportMaterialsCheckBox);
    }

    /**
     * Is export materials boolean.
     *
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
        return notNull(overwriteMaterialsCheckBox);
    }

    /**
     * Is overwrite materials boolean.
     *
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
        return notNull(destinationControl);
    }

    /**
     * Gets destination folder.
     *
     * @return the destination folder.
     */
    @NotNull
    public Path getDestinationFolder() {
        return notNull(getDestinationControl().getFolder());
    }

    /**
     * @return the materials destination folder control.
     */
    @NotNull
    private ChooseFolderControl getMaterialsFolderControl() {
        return notNull(materialsFolderControl);
    }

    /**
     * Gets materials folder.
     *
     * @return the materials destination folder.
     */
    @NotNull
    public Path getMaterialsFolder() {
        return notNull(getMaterialsFolderControl().getFolder());
    }

    /**
     * @return the filename field.
     */
    @NotNull
    private TextField getFilenameField() {
        return notNull(filenameField);
    }

    /**
     * Gets filename.
     *
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

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
