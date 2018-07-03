package com.ss.editor.ui.control.tree.node.impl.spatial;

import static com.ss.editor.Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL;
import static com.ss.editor.Messages.MODEL_NODE_TREE_ACTION_CREATE;
import static com.ss.editor.part3d.editor.impl.scene.AbstractSceneEditor3dPart.KEY_MODEL_NODE;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.light.Light;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.InvisibleObject;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.impl.AddControlOperation;
import com.ss.editor.model.undo.impl.MoveControlOperation;
import com.ss.editor.model.undo.impl.RenameNodeOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.impl.AddUserDataAction;
import com.ss.editor.ui.control.tree.action.impl.DisableAllControlsAction;
import com.ss.editor.ui.control.tree.action.impl.EnableAllControlsAction;
import com.ss.editor.ui.control.tree.action.impl.RemoveNodeAction;
import com.ss.editor.ui.control.tree.action.impl.control.CreateCustomControlAction;
import com.ss.editor.ui.control.tree.action.impl.control.CreateLightControlAction;
import com.ss.editor.ui.control.tree.action.impl.control.CreateMotionControlAction;
import com.ss.editor.ui.control.tree.action.impl.control.physics.CreateCharacterControlAction;
import com.ss.editor.ui.control.tree.action.impl.control.physics.CreateRigidBodyControlAction;
import com.ss.editor.ui.control.tree.action.impl.control.physics.CreateStaticRigidBodyControlAction;
import com.ss.editor.ui.control.tree.action.impl.control.physics.vehicle.CreateVehicleControlAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.light.LightTreeNode;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The implementation of the {@link TreeNode} to represent a {@link Spatial} in an editor.
 *
 * @param <T> the type of {@link Spatial}.
 * @author JavaSaBr
 */
public class SpatialTreeNode<T extends Spatial> extends TreeNode<T> {

    @FunctionalInterface
    public interface ActionFactory {

        @FxThread
        @Nullable MenuItem create(@NotNull SpatialTreeNode<?> treeNode, @NotNull NodeTree<?> tree);
    }

    /**
     * @see ActionFactory
     */
    public static final String EP_CREATION_ACTION_FACTORIES = "SpatialTreeNode#creationActionFactories";

    /**
     * @see ActionFactory
     */
    public static final String EP_CREATION_CONTROL_ACTION_FACTORIES = "SpatialTreeNode#creationControlActionFactories";

    private static final ExtensionPoint<ActionFactory> CREATION_ACTION_FACTORIES =
            ExtensionPointManager.register(EP_CREATION_ACTION_FACTORIES);

    private static final ExtensionPoint<ActionFactory> CREATION_CONTROL_ACTION_FACTORIES =
            ExtensionPointManager.register(EP_CREATION_CONTROL_ACTION_FACTORIES);

    protected SpatialTreeNode(@NotNull T element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {

        if (!(nodeTree instanceof ModelNodeTree)) {
            return;
        }

        var element = getElement();
        var linkNode = NodeUtils.<AssetLinkNode>findParent(element,
                AssetLinkNode.class::isInstance);

        if (linkNode == null) {

            createCreationMenu(nodeTree).stream()
                    .peek(this::sort)
                    .forEach(items::add);

            createToolMenu(nodeTree).stream()
                    .peek(this::sort)
                    .forEach(items::add);
        }

        if (linkNode == null || element == linkNode) {
            items.add(new AddUserDataAction(nodeTree, this));
        }

        if (canRemove()) {
            items.add(new RemoveNodeAction(nodeTree, this));
        }

        NodeUtils.children(element)
            .flatMap(ControlUtils::controls)
            .filter(ControlUtils::isNotEnabled)
            .findAny()
            .ifPresent(control -> items.add(new EnableAllControlsAction(nodeTree, this)));

        NodeUtils.children(element)
                .flatMap(ControlUtils::controls)
                .filter(ControlUtils::isEnabled)
                .findAny()
                .ifPresent(control -> items.add(new DisableAllControlsAction(nodeTree, this)));

        super.fillContextMenu(nodeTree, items);
    }

    /**
     * Sort items of the menu.
     *
     * @param menu the menu.
     */
    @FxThread
    protected void sort(@NotNull Menu menu) {
        menu.getItems().sort(ACTION_COMPARATOR);
    }

    @Override
    @FxThread
    public boolean canMove() {
        return true;
    }

    @Override
    @FxThread
    public boolean canCopy() {
        return true;
    }

    @Override
    @FxThread
    public boolean canAccept(@NotNull TreeNode<?> treeNode, boolean isCopy) {
        var element = treeNode.getElement();
        return element instanceof AbstractControl || super.canAccept(treeNode, isCopy);
    }

    @Override
    @FxThread
    public void accept(@NotNull ChangeConsumer changeConsumer, @NotNull Object object, boolean isCopy) {

        var spatial = getElement();

        if (object instanceof AbstractControl) {

            var control = (AbstractControl) object;
            var prevParent = control.getSpatial();

            if (isCopy) {
                var clone = (AbstractControl) control.jmeClone();
                clone.setSpatial(null);
                changeConsumer.execute(new AddControlOperation(clone, spatial));
            } else {
                changeConsumer.execute(new MoveControlOperation(control, prevParent, spatial));
            }
        }

        super.accept(changeConsumer, object, isCopy);
    }

    @Override
    @FxThread
    public boolean canRemove() {
        var parent = getElement().getParent();
        return parent != null && parent.getUserData(KEY_MODEL_NODE) != Boolean.TRUE;
    }

    /**
     * Create creation menu menu.
     *
     * @param nodeTree the node tree
     * @return the menu
     */
    @FxThread
    protected @NotNull Optional<Menu> createCreationMenu(@NotNull NodeTree<?> nodeTree) {

        var element = getElement();

        var menu = new Menu(MODEL_NODE_TREE_ACTION_CREATE, new ImageView(Icons.ADD_12));
        var createControlMenu = new Menu(MODEL_NODE_TREE_ACTION_ADD_CONTROL, new ImageView(Icons.ADD_12));

        var createControlItems = createControlMenu.getItems();
        createControlItems.add(new CreateCustomControlAction(nodeTree, this));

        if (element.getControl(RigidBodyControl.class) == null) {
            createControlItems.add(new CreateStaticRigidBodyControlAction(nodeTree, this));
            createControlItems.add(new CreateRigidBodyControlAction(nodeTree, this));
        }

        if (element.getControl(VehicleControl.class) == null) {
            createControlItems.add(new CreateVehicleControlAction(nodeTree, this));
        }

        if (element.getControl(CharacterControl.class) == null) {
            createControlItems.add(new CreateCharacterControlAction(nodeTree, this));
        }

        if (element.getControl(MotionEvent.class) == null) {
            createControlItems.add(new CreateMotionControlAction(nodeTree, this));
        }

        createControlItems.add(new CreateLightControlAction(nodeTree, this));

        for (var factory : CREATION_CONTROL_ACTION_FACTORIES) {
            var menuItem = factory.create(this, nodeTree);
            if (menuItem != null) {
                createControlItems.add(menuItem);
            }
        }

        createControlItems.sort(ACTION_COMPARATOR);

        //final SkeletonControl skeletonControl = element.getControl(SkeletonControl.class);
        //if (skeletonControl != null) {
            //FIXME resultItems.add(new CreateKinematicRagdollControlAction(nodeTree, this));
        //}

        var resultItems = menu.getItems();
        resultItems.add(createControlMenu);

        for (var factory : CREATION_ACTION_FACTORIES) {
            var menuItem = factory.create(this, nodeTree);
            if (menuItem != null) {
                resultItems.add(menuItem);
            }
        }

        return Optional.of(menu);
    }

    /**
     * Create tool menu menu.
     *
     * @param nodeTree the node tree
     * @return the menu
     */
    @FxThread
    protected @NotNull Optional<Menu> createToolMenu(@NotNull NodeTree<?> nodeTree) {
        return Optional.empty();
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final String name = getElement().getName();
        return name == null ? "name is null" : name;
    }

    @Override
    @FxThread
    public boolean canEditName() {
        return true;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull NodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @Override
    @FxThread
    public void changeName(@NotNull NodeTree<?> nodeTree, @NotNull String newName) {

        if (StringUtils.equals(getName(), newName)){
            return;
        }

        super.changeName(nodeTree, newName);

        var spatial = getElement();

        notNull(nodeTree.getChangeConsumer())
                .execute(new RenameNodeOperation(spatial.getName(), newName, spatial));
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {

        var result = ArrayFactory.<TreeNode<?>>newArray(TreeNode.class);
        var element = getElement();

        var lightList = element.getLocalLightList();
        lightList.forEach(light -> {
            if (!(light instanceof InvisibleObject)) {
                result.add(FACTORY_REGISTRY.createFor(light));
            }
        });

        var numControls = element.getNumControls();

        for (int i = 0; i < numControls; i++) {
            result.add(FACTORY_REGISTRY.createFor(element.getControl(i)));
        }

        return result;
    }

    @Override
    @FxThread
    public void add(@NotNull TreeNode<?> child) {
        super.add(child);

        var element = getElement();

        if (child instanceof LightTreeNode) {
            element.addLight((Light) child.getElement());
        } else if (child instanceof ControlTreeNode) {
            element.addControl((Control) child.getElement());
        }
    }

    @Override
    @FxThread
    public void remove(@NotNull TreeNode<?> child) {
        super.remove(child);

        var element = getElement();

        if (child instanceof LightTreeNode) {
            element.removeLight((Light) child.getElement());
        } else if (child instanceof ControlTreeNode) {
            element.removeControl((Control) child.getElement());
        }
    }
}
