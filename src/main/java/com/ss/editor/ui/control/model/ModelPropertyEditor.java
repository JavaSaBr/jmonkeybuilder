package com.ss.editor.ui.control.model;

import static com.ss.editor.util.NodeUtils.findParent;
import com.jme3.material.Material;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.PropertyEditor;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The component to contains property controls in the editor.
 *
 * @author JavaSaBr
 */
public class ModelPropertyEditor extends PropertyEditor<ModelChangeConsumer> {

    @FunctionalInterface
    public interface IsNeedUpdateChecker {

        @FxThread
        boolean isNeedUpdate(@Nullable Object currentObject, @Nullable Object changedObject);
    }

    @FunctionalInterface
    public interface CanEditChecker {

        @FxThread
        boolean canEdit(@Nullable Object object, @Nullable Object parent);
    }

    /**
     * @see IsNeedUpdateChecker
     */
    public static final String EP_NEED_UPDATE_CHECKERS = "ModelPropertyEditor#needUpdateCheckers";

    /**
     * @see CanEditChecker
     */
    public static final String EP_CAN_EDIT_CHECKERS = "ModelPropertyEditor#canEditCheckers";

    private static final ExtensionPoint<IsNeedUpdateChecker> NEED_UPDATE_CHECKERS =
            ExtensionPointManager.register(EP_NEED_UPDATE_CHECKERS);

    private static final ExtensionPoint<CanEditChecker> CAN_EDIT_CHECKERS =
            ExtensionPointManager.register(EP_CAN_EDIT_CHECKERS);

    public ModelPropertyEditor(@NotNull ModelChangeConsumer changeConsumer) {
        super(changeConsumer);
    }

    @Override
    @FxThread
    protected boolean isNeedUpdate(@Nullable Object changedObject) {

        var currentObject = getCurrentObject();

        if (changedObject instanceof EditableProperty) {
            changedObject = ((EditableProperty) changedObject).getObject();
        }

        return NEED_UPDATE_CHECKERS.anyMatch(currentObject, changedObject,
                IsNeedUpdateChecker::isNeedUpdate) || super.isNeedUpdate(changedObject);
    }

    @Override
    @FxThread
    protected boolean canEdit(@NotNull Object object, @Nullable Object parent) {

        if (object instanceof Control) {
            return true;
        } else if (object instanceof Material) {

            var material = (Material) object;

            if (material.getKey() != null) {
                return false;
            }

        } else if (object instanceof Spatial) {
            var linkNode = findParent((Spatial) object, AssetLinkNode.class::isInstance);
            return linkNode == null || linkNode == object;
        } else if (parent instanceof Spatial) {
            var linkNode = findParent((Spatial) parent, AssetLinkNode.class::isInstance);
            return linkNode == null;
        } else if(CAN_EDIT_CHECKERS.anyMatchNot(object, parent, CanEditChecker::canEdit)) {
            return false;
        }

        return super.canEdit(object, parent);
    }
}
