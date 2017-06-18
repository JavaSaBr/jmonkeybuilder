package com.ss.editor.ui.control.model.tree.dialog;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyCountOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The implementation of a dialog to add a new user data.
 *
 * @author JavaSaBr
 */
public class AddUserDataDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 157);

    private enum DataType {
        FLOAT,
        INTEGER,
        VECTOR3F,
        VECTOR2F,
        COLOR,
        BOOLEAN,
        STRING,
    }

    @NotNull
    private static final ObservableList<DataType> DATA_TYPES = observableArrayList(DataType.values());

    /**
     * The change consumer.
     */
    @NotNull
    private ModelChangeConsumer changeConsumer;

    /**
     * The edited model.
     */
    @NotNull
    private Spatial spatial;

    /**
     * The text field.
     */
    @Nullable
    private TextField nameField;

    /**
     * The combo box with data type.
     */
    @Nullable
    private ComboBox<DataType> dataTypeComboBox;

    public AddUserDataDialog(final @NotNull ModelChangeConsumer changeConsumer, final @NotNull Spatial spatial) {
        super();
        this.changeConsumer = changeConsumer;
        this.spatial = spatial;
        getOkButton().disableProperty().bind(getDataTypeComboBox()
                .getSelectionModel()
                .selectedItemProperty().isNull()
                .or(getNameField().textProperty().isEmpty()));
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label nameLabel = new Label(Messages.ADD_USER_DATA_DIALOG_NAME + ":");
        nameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        nameField = new TextField();
        nameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        nameField.prefWidthProperty().bind(root.widthProperty());

        final Label dataTypeLabel = new Label(Messages.ADD_USER_DATA_DIALOG_DATA_TYPE + ":");
        dataTypeLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        dataTypeLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        dataTypeComboBox = new ComboBox<>(DATA_TYPES);
        dataTypeComboBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        dataTypeComboBox.prefWidthProperty().bind(root.widthProperty());

        root.add(nameLabel, 0, 0);
        root.add(nameField, 1, 0);
        root.add(dataTypeLabel, 0, 1);
        root.add(dataTypeComboBox, 1, 1);

        FXUtils.addClassTo(nameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(nameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(dataTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(dataTypeComboBox, CSSClasses.SPECIAL_FONT_14);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * @return the combo box with data type.
     */
    @NotNull
    private ComboBox<DataType> getDataTypeComboBox() {
        return requireNonNull(dataTypeComboBox);
    }

    /**
     * @return the text field.
     */
    @NotNull
    private TextField getNameField() {
        return requireNonNull(nameField);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.ADD_USER_DATA_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.ADD_USER_DATA_DIALOG_BUTTON_OK;
    }

    @Override
    protected void processOk() {
        super.processOk();

        final ComboBox<DataType> dataTypeComboBox = getDataTypeComboBox();
        final TextField nameField = getNameField();

        final String key = nameField.getText();

        final SingleSelectionModel<DataType> selectionModel = dataTypeComboBox.getSelectionModel();
        final DataType selectedItem = selectionModel.getSelectedItem();

        Object value = null;

        switch (selectedItem) {
            case BOOLEAN:
                value = Boolean.FALSE;
                break;
            case FLOAT:
                value = 0F;
                break;
            case INTEGER:
                value = 0;
                break;
            case VECTOR3F:
                value = new Vector3f();
                break;
            case VECTOR2F:
                value = new Vector2f();
                break;
            case COLOR:
                value = new ColorRGBA();
                break;
            case STRING:
                value = "empty string";
                break;
        }

        final ModelPropertyCountOperation<Spatial, Object> operation =
                new ModelPropertyCountOperation<>(spatial, "userData", value, null);

        operation.setApplyHandler((object, val) -> object.setUserData(key, val));

        changeConsumer.execute(operation);
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
