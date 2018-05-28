package com.ss.editor.ui.control.tree.action.impl.animation;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.legacyanim.AnimationControlTreeNode;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to open play settings of an animation.
 *
 * @author JavaSaBr
 */
public class PlaySettingsAction extends AbstractNodeAction<ModelChangeConsumer> {

    @NotNull
    private static final String PROPERTY_LOOP_MODE = "loopMode";

    @NotNull
    private static final String PROPERTY_SPEED = "speed";

    public PlaySettingsAction(@NotNull final NodeTree<?> nodeTree, @NotNull final AnimationControlTreeNode node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.SETTINGS_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final AnimationControlTreeNode node = (AnimationControlTreeNode) getNode();
        final LoopMode loopMode = node.getLoopMode();
        final float speed = node.getSpeed();

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_LOOP_MODE, PROPERTY_LOOP_MODE, loopMode));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_SPEED, PROPERTY_SPEED, speed));

        final GenericFactoryDialog dialog = new GenericFactoryDialog(definitions, vars ->
                node.updateSettings(vars.get(PROPERTY_LOOP_MODE), vars.get(PROPERTY_SPEED)));

        dialog.setTitle(Messages.PLAY_ANIMATION_SETTINGS_DIALOG_TITLE);
        dialog.setButtonOkText(Messages.SIMPLE_DIALOG_BUTTON_OK);
        dialog.show();
    }
}
