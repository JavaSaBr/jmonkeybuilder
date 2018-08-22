package com.ss.builder.ui.control.tree.action.impl;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.STRING;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.plugin.api.dialog.GenericFactoryDialog;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.control.property.operation.PropertyCountOperation;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.operation.PropertyCountOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to add new user data.
 *
 * @author JavaSaBr
 */
public class AddUserDataAction extends AbstractNodeAction<ModelChangeConsumer> {

    private enum DataType {
        FLOAT,
        INTEGER,
        VECTOR3F,
        VECTOR2F,
        COLOR,
        BOOLEAN,
        STRING
    }

    @NotNull
    private static final String PROPERTY_NAME = "name";

    @NotNull
    private static final String PROPERTY_DATA_TYPE = "dataType";

    public AddUserDataAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @FxThread
    @Override
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_USER_DATA;
    }

    @FxThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.ADD_12;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(STRING, Messages.MODEL_PROPERTY_NAME, PROPERTY_NAME, ""));
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_DATA_TYPE, PROPERTY_DATA_TYPE, DataType.STRING));

        final GenericFactoryDialog dialog = new GenericFactoryDialog(definitions, this::addUserData, this::validate);

        dialog.setTitle(Messages.ADD_USER_DATA_DIALOG_TITLE);
        dialog.setButtonOkText(Messages.SIMPLE_DIALOG_BUTTON_ADD);
        dialog.show();
    }

    /**
     * Validate the input paramaters.
     *
     * @param vars the input paramaters.
     * @return true if all is ok.
     */
    @FxThread
    private boolean validate(@NotNull final VarTable vars) {
        if(!vars.has(PROPERTY_NAME)) return false;

        final String name = vars.getString(PROPERTY_NAME);

        final TreeNode<?> node = getNode();
        final Spatial element = (Spatial) node.getElement();

        return !StringUtils.isEmpty(name) && element.getUserData(name) == null;
    }

    /**
     * Add new user date.
     *
     * @param vars the input paramaters.
     */
    @FxThread
    private void addUserData(@NotNull final VarTable vars) {

        final String name = vars.get(PROPERTY_NAME);
        final DataType dataType = vars.get(PROPERTY_DATA_TYPE);

        Object value = null;

        switch (dataType) {
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

        final TreeNode<?> node = getNode();
        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final Spatial element = (Spatial) node.getElement();

        final PropertyCountOperation<ChangeConsumer, Spatial, Object> operation =
                new PropertyCountOperation<>(element, "userData", value, null);

        operation.setApplyHandler((object, val) -> object.setUserData(name, val));

        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(operation);
    }
}
