package com.ss.editor.ui.control.model;

import static com.ss.editor.util.NodeUtils.findParent;
import com.jme3.material.Material;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.PropertyEditor;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

/**
 * The component to contains property controls in the editor.
 *
 * @author JavaSaBr
 */
public class ModelPropertyEditor extends PropertyEditor<ModelChangeConsumer> {

    @FunctionalInterface
    public interface IsNeedUpdateChecker {

        boolean isNeedUpdate(@Nullable Object currentObject, @Nullable Object changedObject);
    }

    @FunctionalInterface
    public interface CanEditChecker {

        boolean canEdit(@Nullable Object object, @Nullable Object parent);

        default @NotNull CanEditChecker negate() {
            return (object, parent) -> !canEdit(object, parent);
        }
    }

    /**
     * The list of additional 'isNeedUpdate' checkers.
     */
    private static final Array<IsNeedUpdateChecker> IS_NEED_UPDATE_CHECKERS =
            ArrayFactory.newCopyOnModifyArray(IsNeedUpdateChecker.class);

    /**
     * The list of additional 'canEdit' checkers.
     */
    private static final Array<CanEditChecker> CAN_EDIT_CHECKERS =
            ArrayFactory.newCopyOnModifyArray(BiPredicate.class);

    /**
     * Register the additional checker which checks a current object and a changed object and
     * returns true if need to update this in the property editor.
     *
     * @param checker the additional checker.
     */
    @FromAnyThread
    public static void registerIsNeedUpdateChecker(@NotNull IsNeedUpdateChecker checker) {
        IS_NEED_UPDATE_CHECKERS.add(checker);
    }

    /**
     * Register the additional checker which checks a checked object and its parent and
     * returns false if we can't edit this in the property editor.
     *
     * @param checker the additional checker.
     */
    @FromAnyThread
    public static void registerCanEditChecker(@NotNull CanEditChecker checker) {
        CAN_EDIT_CHECKERS.add(checker.negate());
    }

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

        return IS_NEED_UPDATE_CHECKERS.search(currentObject, changedObject, IsNeedUpdateChecker::isNeedUpdate) != null ||
                super.isNeedUpdate(changedObject);
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
        } else if (CAN_EDIT_CHECKERS.search(object, parent, CanEditChecker::canEdit) != null) {
            return false;
        }

        return super.canEdit(object, parent);
    }
}
