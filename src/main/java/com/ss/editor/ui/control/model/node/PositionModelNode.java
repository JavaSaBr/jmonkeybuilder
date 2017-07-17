package com.ss.editor.ui.control.model.node;

import com.jme3.math.Vector3f;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show vectors in the tree.
 *
 * @author JavaSaBr
 */
public class PositionModelNode extends ModelNode<Vector3f> {

    /**
     * The name.
     */
    @Nullable
    private String name;

    /**
     * Instantiates a new Position model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public PositionModelNode(@NotNull final Vector3f element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.WAY_POINT_16;
    }

    @NotNull
    @Override
    public String getName() {
        return name == null ? "Point 3D" : name;
    }

    @Override
    public boolean isNeedToSaveName() {
        return true;
    }

    @Override
    public void setName(@Nullable final String name) {
        this.name = name;
    }
}
