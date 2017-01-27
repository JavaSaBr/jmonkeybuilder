package com.ss.editor.ui.control.model.tree.dialog;

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

import org.jetbrains.annotations.NotNull;

import java.awt.Point;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a dialog to add a new user data.
 *
 * @author JavaSaBr
 */
public class AddUserDataDialog extends AbstractSimpleEditorDialog {

    private static final Insets NAME_OFFSET = new Insets(20, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets DATA_TYPE_OFFSET = new Insets(4, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    private static final Point DIALOG_SIZE = new Point(400, 168);

    private enum DataType {
        FLOAT,
        INTEGER,
        VECTOR3F,
        VECTOR2F,
        COLOR,
        BOOLEAN,
        STRING,
    }

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
    private TextField nameField;

    /**
     * The combo box with data type.
     */
    private ComboBox<DataType> dataTypeComboBox;

    public AddUserDataDialog(final @NotNull ModelChangeConsumer changeConsumer, final @NotNull Spatial spatial) {
        super();
        this.changeConsumer = changeConsumer;
        this.spatial = spatial;
        getOkButton().disableProperty().bind(dataTypeComboBox.getSelectionModel()
                .selectedItemProperty().isNull().or(nameField.textProperty().isEmpty()));
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final HBox nameContainer = new HBox();
        nameContainer.setAlignment(Pos.CENTER_LEFT);

        final Label nameLabel = new Label(Messages.ADD_USER_DATA_DIALOG_NAME + ":");
        nameLabel.setId(CSSIds.ADD_USER_DATA_DIALOG_LABEL);

        nameField = new TextField();
        nameField.setId(CSSIds.ADD_USER_DATA_FIELD);
        nameField.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(nameLabel, nameContainer);
        FXUtils.addToPane(nameField, nameContainer);
        FXUtils.addToPane(nameContainer, root);

        final HBox dataTypeContainer = new HBox();
        dataTypeContainer.setAlignment(Pos.CENTER_LEFT);

        final Label dataTypeLabel = new Label(Messages.ADD_USER_DATA_DIALOG_DATA_TYPE + ":");
        dataTypeLabel.setId(CSSIds.ADD_USER_DATA_DIALOG_LABEL);

        dataTypeComboBox = new ComboBox<>(DATA_TYPES);
        dataTypeComboBox.setId(CSSIds.ADD_USER_DATA_FIELD);
        dataTypeComboBox.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(dataTypeLabel, dataTypeContainer);
        FXUtils.addToPane(dataTypeComboBox, dataTypeContainer);
        FXUtils.addToPane(dataTypeContainer, root);

        FXUtils.addClassTo(nameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(nameField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(dataTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(dataTypeComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(nameContainer, NAME_OFFSET);
        VBox.setMargin(dataTypeContainer, DATA_TYPE_OFFSET);
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

        final ModelPropertyCountOperation<Spatial, Object> operation = new ModelPropertyCountOperation<>(spatial, "userData", value, null);
        operation.setApplyHandler((object, val) -> object.setUserData(key, val));

        changeConsumer.execute(operation);
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
