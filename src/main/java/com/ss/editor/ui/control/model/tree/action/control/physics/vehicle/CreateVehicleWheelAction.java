package com.ss.editor.ui.control.model.tree.action.control.physics.vehicle;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddVehicleWheelOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link VehicleWheel}.
 *
 * @author JavaSaBr
 */
public class CreateVehicleWheelAction extends AbstractNodeAction<ModelChangeConsumer> {

    @NotNull
    private static final String PROPERTY_LOCATION = "location";

    @NotNull
    private static final String PROPERTY_DIRECTION = "direction";

    @NotNull
    private static final String PROPERTY_AXLE = "axle";

    @NotNull
    private static final String PROPERTY_RADIUS = "radius";

    @NotNull
    private static final String PROPERTY_REST_LENGTH = "restLength";

    @NotNull
    private static final String PROPERTY_IS_FRONT = "isFront";

    @NotNull
    private static final Array<PropertyDefinition> DEFINITIONS = ArrayFactory.newArray(PropertyDefinition.class);

    static {
        DEFINITIONS.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_LOCATION, PROPERTY_LOCATION, new Vector3f(1, 1, 1)));
        DEFINITIONS.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_DIRECTION, PROPERTY_DIRECTION, new Vector3f(0, -1, 0)));
        DEFINITIONS.add(new PropertyDefinition(VECTOR_3F, Messages.MODEL_PROPERTY_AXLE, PROPERTY_AXLE, new Vector3f(-1, 0, -1)));
        DEFINITIONS.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_RADIUS, PROPERTY_RADIUS, 1F));
        DEFINITIONS.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_REST_LENGTH, PROPERTY_REST_LENGTH, 0.2F));
        DEFINITIONS.add(new PropertyDefinition(BOOLEAN, Messages.MODEL_PROPERTY_IS_FRONT, PROPERTY_IS_FRONT, false));
    }

    /**
     * Instantiates a new Create vehicle wheel action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateVehicleWheelAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @FXThread
    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_WHEEL;
    }

    @FXThread
    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.WHEEL_16;
    }

    @FXThread
    @Override
    protected void process() {
        final GenericFactoryDialog dialog = new GenericFactoryDialog(DEFINITIONS, this::handleResult);
        dialog.setTitle(Messages.ADD_VEHICLE_WHEEL_DIALOG_TITLE);
        dialog.show();
    }

    private void handleResult(@NotNull final VarTable vars) {

        final TreeNode<?> node = getNode();
        final VehicleControl control = (VehicleControl) node.getElement();

        final Vector3f location = vars.get(PROPERTY_LOCATION);
        final Vector3f direction = vars.get(PROPERTY_DIRECTION);
        final Vector3f axle = vars.get(PROPERTY_AXLE);

        final float restLength = vars.getFloat(PROPERTY_REST_LENGTH);
        final float radius = vars.getFloat(PROPERTY_RADIUS);

        final boolean isFront = vars.getBoolean(PROPERTY_IS_FRONT);

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddVehicleWheelOperation(control, location, direction, axle, restLength, radius, isFront));
    }
}
