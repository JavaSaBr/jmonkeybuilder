package com.ss.editor.ui.control.model;

import static com.ss.editor.util.NodeUtils.findParent;
import com.jme3.material.Material;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
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

    /**
     * The list of additional 'isNeedUpdate' checkers.
     */
    @NotNull
    private static final Array<BiPredicate<@Nullable Object, @Nullable Object>> IS_NEED_UPDATE_CHECKERS = ArrayFactory.newArray(BiPredicate.class);

    /**
     * The list of additional 'canEdit' checkers.
     */
    @NotNull
    private static final Array<BiPredicate<@Nullable Object, @Nullable Object>> CAN_EDIT_CHECKERS = ArrayFactory.newArray(BiPredicate.class);

    /**
     * Register the additional checker which checks a current object and a checked object and
     * returns true if need to update this in the property editor.
     *
     * @param checker the additional checker.
     */
    @FxThread
    public static void registerIsNeedUpdateChecker(@NotNull final BiPredicate<@Nullable Object, @Nullable Object> checker) {
        IS_NEED_UPDATE_CHECKERS.add(checker);
    }

    /**
     * Register the additional checker which checks a checked object and its parent and
     * returns false if we can't edit this in the property editor.
     *
     * @param checker the additional checker.
     */
    @FxThread
    public static void registerCanEditChecker(@NotNull final BiPredicate<@Nullable Object, @Nullable Object> checker) {
        CAN_EDIT_CHECKERS.add(checker.negate());
    }

    public ModelPropertyEditor(@NotNull final ModelChangeConsumer changeConsumer) {
        super(changeConsumer);
    }

    @Override
    @FxThread
    protected boolean isNeedUpdate(@Nullable final Object object) {
        return IS_NEED_UPDATE_CHECKERS.search(getCurrentObject(), object, BiPredicate::test) != null ||
                super.isNeedUpdate(object);
    }

    @Override
    @FxThread
    protected boolean canEdit(@NotNull final Object object, @Nullable final Object parent) {

        if (object instanceof Control) {
            return true;
        } else if (object instanceof Material) {
            final Material material = (Material) object;
            if (material.getKey() != null) return false;
        } else if (object instanceof Spatial) {
            final Object linkNode = findParent((Spatial) object, AssetLinkNode.class::isInstance);
            return linkNode == null || linkNode == object;
        } else if (parent instanceof Spatial) {
            final Object linkNode = findParent((Spatial) parent, AssetLinkNode.class::isInstance);
            return linkNode == null;
        } else if (CAN_EDIT_CHECKERS.search(object, parent, BiPredicate::test) != null) {
            return false;
        }

        return super.canEdit(object, parent);
    }
}
