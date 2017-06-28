package com.ss.editor.ui.control.model.tree.action;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.STRING;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyCountOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.factory.ObjectFactoryDialog;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to add a new user data.
 *
 * @author JavaSaBr
 */
public class AddUserDataAction extends AbstractNodeAction<ModelChangeConsumer> {

    private enum DataType {
        /**
         * Float data type.
         */
        FLOAT,
        /**
         * Integer data type.
         */
        INTEGER,
        /**
         * Vector 3 f data type.
         */
        VECTOR3F,
        /**
         * Vector 2 f data type.
         */
        VECTOR2F,
        /**
         * Color data type.
         */
        COLOR,
        /**
         * Boolean data type.
         */
        BOOLEAN,
        /**
         * String data type.
         */
        STRING,
    }


    @NotNull
    private static final String PROPERTY_NAME = "name";

    @NotNull
    private static final String PROPERTY_DATA_TYPE = "dataType";

    /**
     * Instantiates a new Add user data action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public AddUserDataAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_USER_DATA;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.ADD_12;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(STRING, Messages.MODEL_PROPERTY_NAME, PROPERTY_NAME, ""));
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_DATA_TYPE, PROPERTY_DATA_TYPE, DataType.STRING));

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final ObjectFactoryDialog dialog = new ObjectFactoryDialog(definitions, this::addUserData,
                vars -> !StringUtils.isEmpty(vars.getString(PROPERTY_NAME)));

        dialog.setTitle(Messages.ADD_USER_DATA_DIALOG_TITLE);
        dialog.setButtonOkLabel(Messages.ADD_USER_DATA_DIALOG_BUTTON_OK);
        dialog.show(scene.getWindow());
    }

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

        final ModelNode<?> node = getNode();
        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final Spatial element = (Spatial) node.getElement();

        final ModelPropertyCountOperation<Spatial, Object> operation =
                new ModelPropertyCountOperation<>(element, "userData", value, null);

        operation.setApplyHandler((object, val) -> object.setUserData(name, val));

        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(operation);
    }
}
