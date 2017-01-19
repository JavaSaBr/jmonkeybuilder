package com.ss.extension.scene.app.state.impl;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.util.clone.Cloner;
import com.simsilica.fx.LightingState;
import com.simsilica.fx.sky.SkyState;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.SceneAppState;
import com.ss.extension.scene.app.state.property.EditableProperty;
import com.ss.extension.scene.app.state.property.EditablePropertyType;
import com.ss.extension.scene.app.state.property.SimpleProperty;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable version of sky state.
 *
 * @author JavaSaBr
 */
public class EditableSkySceneAppState extends SkyState implements EditableSceneAppState {

    public EditableSkySceneAppState() {
    }

    @NotNull
    @Override
    public String getName() {
        return "Sky State";
    }

    @Override
    public boolean canCreate(@NotNull final Array<SceneAppState> exists) {
        return exists.search(appState -> appState instanceof LightingState) != null;
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = ArrayFactory.newArray(EditableProperty.class);

        result.add(new SimpleProperty<>(EditablePropertyType.BOOLEAN, "Flat shaded", this,
                SkyState::isFlatShaded, SkyState::setFlatShaded));

        result.add(new SimpleProperty<>(EditablePropertyType.BOOLEAN, "Show ground", this,
                SkyState::isShowGroundDisc, SkyState::setShowGroundDisc));

        return result;
    }

    @NotNull
    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(final Cloner cloner, final Object original) {

    }

    @Override
    public void write(final JmeExporter ex) throws IOException {

    }

    @Override
    public void read(final JmeImporter im) throws IOException {

    }
}
