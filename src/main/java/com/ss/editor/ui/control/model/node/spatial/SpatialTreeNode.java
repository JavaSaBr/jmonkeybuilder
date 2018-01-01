package com.ss.editor.ui.control.model.node.spatial;

import static com.ss.editor.Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL;
import static com.ss.editor.Messages.MODEL_NODE_TREE_ACTION_CREATE;
import static com.ss.editor.util.NodeUtils.findParent;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.control.transform.EditorTransformSupport;
import com.ss.editor.extension.scene.InvisibleObject;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.ControlTreeNode;
import com.ss.editor.ui.control.model.node.light.LightTreeNode;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.*;
import com.ss.editor.ui.control.model.tree.action.control.CreateCustomControlAction;
import com.ss.editor.ui.control.model.tree.action.control.CreateLightControlAction;
import com.ss.editor.ui.control.model.tree.action.control.CreateMotionControlAction;
import com.ss.editor.ui.control.model.tree.action.control.physics.CreateCharacterControlAction;
import com.ss.editor.ui.control.model.tree.action.control.physics.CreateRigidBodyControlAction;
import com.ss.editor.ui.control.model.tree.action.control.physics.CreateStaticRigidBodyControlAction;
import com.ss.editor.ui.control.model.tree.action.control.physics.vehicle.CreateVehicleControlAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddControlOperation;
import com.ss.editor.ui.control.model.tree.action.operation.MoveControlOperation;
import com.ss.editor.ui.control.model.tree.action.operation.RenameNodeOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} to represent a {@link Spatial} in an editor.
 *
 * @param <T> the type of {@link Spatial}.
 * @author JavaSaBr
 */
public class SpatialTreeNode<T extends Spatial> extends TreeNode<T> {

    protected SpatialTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {
        if (!(nodeTree instanceof ModelNodeTree)) return;

        final T element = getElement();
        final AssetLinkNode linkNode = findParent(element, AssetLinkNode.class::isInstance);

        if (linkNode == null) {
            final Menu createMenu = createCreationMenu(nodeTree);
            if (createMenu != null) items.add(createMenu);
            final Menu toolMenu = createToolMenu(nodeTree);
            if (toolMenu != null) items.add(toolMenu);
        }

        if (linkNode == null || element == linkNode) {
            items.add(new AddUserDataAction(nodeTree, this));
        }

        if (canRemove()) {
            items.add(new RemoveNodeAction(nodeTree, this));
        }

        if(canCopy()){
            items.add(new CopyNodeAction(nodeTree,this));
        }

        if(DataCopy.copySpatial != null){
            items.add(new PasteNodeAction(nodeTree,this));
        }


        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FXThread
    public boolean canMove() {
        return true;
    }

    @Override
    @FXThread
    public boolean canCopy() {
        return true;
    }

    @Override
    @FXThread
    public boolean canAccept(@NotNull final TreeNode<?> child, final boolean isCopy) {
        final Object element = child.getElement();
        return element instanceof AbstractControl || super.canAccept(child, isCopy);
    }

    @Override
    @FXThread
    public void accept(@NotNull final ChangeConsumer changeConsumer, @NotNull final Object object,
                       final boolean isCopy) {

        final T spatial = getElement();

        if (object instanceof AbstractControl) {

            final AbstractControl control = (AbstractControl) object;
            final Spatial prevParent = control.getSpatial();

            if (isCopy) {
                final AbstractControl clone = (AbstractControl) control.jmeClone();
                clone.setSpatial(null);
                changeConsumer.execute(new AddControlOperation(clone, spatial));
            } else {
                changeConsumer.execute(new MoveControlOperation(control, prevParent, spatial));
            }
        }

        super.accept(changeConsumer, object, isCopy);
    }

    @Override
    @FXThread
    public boolean canRemove() {
        final Node parent = getElement().getParent();
        return parent != null && parent.getUserData(EditorTransformSupport.class.getName()) != Boolean.TRUE;
    }

    /**
     * Create creation menu menu.
     *
     * @param nodeTree the node tree
     * @return the menu
     */
    @FXThread
    protected @Nullable Menu createCreationMenu(@NotNull final NodeTree<?> nodeTree) {

        final T element = getElement();

        final Menu menu = new Menu(MODEL_NODE_TREE_ACTION_CREATE, new ImageView(Icons.ADD_12));
        final Menu createControlsMenu = new Menu(MODEL_NODE_TREE_ACTION_ADD_CONTROL, new ImageView(Icons.ADD_12));

        final ObservableList<MenuItem> items = createControlsMenu.getItems();
        items.add(new CreateCustomControlAction(nodeTree, this));

        if (element.getControl(RigidBodyControl.class) == null) {
            items.add(new CreateStaticRigidBodyControlAction(nodeTree, this));
            items.add(new CreateRigidBodyControlAction(nodeTree, this));
        }

        if (element.getControl(VehicleControl.class) == null) {
            items.add(new CreateVehicleControlAction(nodeTree, this));
        }

        if (element.getControl(CharacterControl.class) == null) {
            items.add(new CreateCharacterControlAction(nodeTree, this));
        }

        if (element.getControl(MotionEvent.class) == null) {
            items.add(new CreateMotionControlAction(nodeTree, this));
        }

        items.add(new CreateLightControlAction(nodeTree, this));

        //final SkeletonControl skeletonControl = element.getControl(SkeletonControl.class);
        //if (skeletonControl != null) {
            //FIXME items.add(new CreateKinematicRagdollControlAction(nodeTree, this));
        //}

        menu.getItems().add(createControlsMenu);

        return menu;
    }


    /**
     * Create tool menu menu.
     *
     * @param nodeTree the node tree
     * @return the menu
     */
    @FXThread
    protected @Nullable Menu createToolMenu(final @NotNull NodeTree<?> nodeTree) {
        return null;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final String name = getElement().getName();
        return name == null ? "name is null" : name;
    }

    @Override
    @FXThread
    public boolean canEditName() {
        return true;
    }

    @Override
    @FXThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @Override
    @FXThread
    public void changeName(@NotNull final NodeTree<?> nodeTree, @NotNull final String newName) {
        if (StringUtils.equals(getName(), newName)) return;

        super.changeName(nodeTree, newName);

        final Spatial spatial = getElement();
        final ChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
        consumer.execute(new RenameNodeOperation(spatial.getName(), newName, spatial));
    }

    @Override
    @FXThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        final Spatial element = getElement();

        final LightList lightList = element.getLocalLightList();
        lightList.forEach(light -> {
            if (!(light instanceof InvisibleObject)) {
                result.add(FACTORY_REGISTRY.createFor(light));
            }
        });

        final int numControls = element.getNumControls();

        for (int i = 0; i < numControls; i++) {
            final Control control = element.getControl(i);
            result.add(FACTORY_REGISTRY.createFor(control));
        }

        return result;
    }

    @Override
    @FXThread
    public void add(@NotNull final TreeNode<?> child) {
        super.add(child);

        final T element = getElement();

        if (child instanceof LightTreeNode) {
            final Light light = (Light) child.getElement();
            element.addLight(light);
        } else if (child instanceof ControlTreeNode) {
            final Control control = (Control) child.getElement();
            element.addControl(control);
        }
    }

    @Override
    @FXThread
    public void remove(@NotNull final TreeNode<?> child) {
        super.remove(child);

        final T element = getElement();

        if (child instanceof LightTreeNode) {
            final Light light = (Light) child.getElement();
            element.removeLight(light);
        } else if (child instanceof ControlTreeNode) {
            final Control control = (Control) child.getElement();
            element.removeControl(control);
        }
    }
}
