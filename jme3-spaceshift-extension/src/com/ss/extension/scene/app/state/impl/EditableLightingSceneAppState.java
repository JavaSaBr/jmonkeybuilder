package com.ss.extension.scene.app.state.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.util.clone.Cloner;
import com.simsilica.fx.LightingState;
import com.simsilica.lemur.core.VersionedHolder;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.property.EditableProperty;
import com.ss.extension.scene.app.state.property.EditablePropertyType;
import com.ss.extension.scene.app.state.property.SimpleProperty;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable version of lighting state.
 *
 * @author JavaSaBr
 */
public class EditableLightingSceneAppState extends LightingState implements EditableSceneAppState {

    public EditableLightingSceneAppState() {
    }

    @NotNull
    @Override
    public String getName() {
        return "Lighting State";
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

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = ArrayFactory.newArray(EditableProperty.class);

        result.add(new SimpleProperty<>(EditablePropertyType.COLOR, "Ambient color", this,
                LightingState::getAmbient, LightingState::setAmbient));
        result.add(new SimpleProperty<>(EditablePropertyType.COLOR, "Sun color", this,
                LightingState::getSunColor, LightingState::setSunColor));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Time of day", this,
                LightingState::getTimeOfDay, LightingState::setTimeOfDay));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Orientation", this,
                LightingState::getOrientation, LightingState::setOrientation));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        lightDir = new VersionedHolder<>();
        rootNode = cloner.clone(rootNode);
        ambient = cloner.clone(ambient);
        sun = cloner.clone(sun);
        resetLightDir();
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(isEnabled(), "enabled", false);
        capsule.write(getAmbient(), "ambient", ColorRGBA.White);
        capsule.write(getSunColor(), "sun", ColorRGBA.White);
        capsule.write(getTimeOfDay(), "timeOfDay", FastMath.atan2(1, 0.3f) / FastMath.PI);
        capsule.write(getOrientation(), "orientation", 0);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        setEnabled(capsule.readBoolean("enabled", false));
        setAmbient((ColorRGBA) capsule.readSavable("ambient", ColorRGBA.White));
        setSunColor((ColorRGBA) capsule.readSavable("sun", ColorRGBA.White));
        setTimeOfDay(capsule.readFloat("timeOfDay", FastMath.atan2(1, 0.3f) / FastMath.PI));
        setOrientation(capsule.readFloat("orientation", 0F));
    }
}
