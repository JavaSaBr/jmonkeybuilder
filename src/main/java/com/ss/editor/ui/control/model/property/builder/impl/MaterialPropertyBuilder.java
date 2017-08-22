package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link com.jme3.material.Material} objects.
 *
 * @author JavaSaBr
 */
public class MaterialPropertyBuilder extends AbstractPropertyBuilder<ChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new MaterialPropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private MaterialPropertyBuilder() {
        super(ChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ChangeConsumer changeConsumer) {

        if (!(object instanceof Material)) return;

        final Material material = (Material) object;
        final MaterialDef definition = material.getMaterialDef();

        final Collection<MatParam> materialParams = definition.getMaterialParams();
    }
}
