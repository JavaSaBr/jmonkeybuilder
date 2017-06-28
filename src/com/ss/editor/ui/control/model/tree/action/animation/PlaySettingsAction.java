package com.ss.editor.ui.control.model.tree.action.animation;

import static com.ss.editor.extension.property.EditablePropertyType.ENUM;
import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.dialog.factory.ObjectFactoryDialog;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
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

    /**
     * Instantiates a new Play settings action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public PlaySettingsAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final AnimationControlModelNode node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SETTINGS_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final AnimationControlModelNode node = (AnimationControlModelNode) getNode();
        final LoopMode loopMode = node.getLoopMode();
        final float speed = node.getSpeed();

        final Array<PropertyDefinition> definitions = ArrayFactory.newArray(PropertyDefinition.class);
        definitions.add(new PropertyDefinition(ENUM, Messages.MODEL_PROPERTY_LOOP_MODE, PROPERTY_LOOP_MODE, loopMode));
        definitions.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_SPEED, PROPERTY_SPEED, speed));

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final ObjectFactoryDialog dialog = new ObjectFactoryDialog(definitions, vars ->
                node.updateSettings(vars.get(PROPERTY_LOOP_MODE), vars.get(PROPERTY_SPEED)));

        dialog.setTitle(Messages.PLAY_ANIMATION_SETTINGS_DIALOG_TITLE);
        dialog.setButtonOkLabel(Messages.PLAY_ANIMATION_SETTINGS_DIALOG_BUTTON_OK);
        dialog.show(scene.getWindow());
    }
}
