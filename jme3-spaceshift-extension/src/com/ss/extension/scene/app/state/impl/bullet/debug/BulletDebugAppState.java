/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ss.extension.scene.app.state.impl.bullet.debug;

import static java.util.Objects.requireNonNull;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.extension.scene.app.state.impl.bullet.debug.control.BulletCharacterDebugControl;
import com.ss.extension.scene.app.state.impl.bullet.debug.control.BulletRigidBodyDebugControl;
import org.jetbrains.annotations.NotNull;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author normenhansen, JavaSaBr
 */
public class BulletDebugAppState extends AbstractAppState {

    protected static final Logger LOGGER = LoggerManager.getLogger(BulletDebugAppState.class);

    /**
     * The current registered rigid bodies.
     */
    protected final ObjectDictionary<PhysicsRigidBody, Spatial> bodies;

    /**
     * The previous registered rigid bodies.
     */
    protected final ObjectDictionary<PhysicsRigidBody, Spatial> prevBodies;

    /**
     * The current registered characters.
     */
    protected final ObjectDictionary<PhysicsCharacter, Spatial> characters;

    /**
     * The previous registered characters.
     */
    protected final ObjectDictionary<PhysicsCharacter, Spatial> prevCharacters;

    protected final ObjectDictionary<PhysicsJoint, Spatial> joints;
    protected final ObjectDictionary<PhysicsJoint, Spatial> prevJoints;

    protected final ObjectDictionary<PhysicsGhostObject, Spatial> ghosts;
    protected final ObjectDictionary<PhysicsGhostObject, Spatial> prevGhosts;

    protected final ObjectDictionary<PhysicsVehicle, Spatial> vehicles;
    protected final ObjectDictionary<PhysicsVehicle, Spatial> prevVehicles;

    /**
     * The debug root node.
     */
    @NotNull
    protected final Node debugRootNode;

    /**
     * The physical space.
     */
    @NotNull
    protected final PhysicsSpace physicsSpace;

    /**
     * The node to attach debug nodes.
     */
    @Nullable
    protected Node rootNode;

    /**
     * The blue material.
     */
    @Nullable
    public Material debugBlue;

    /**
     * The red material.
     */
    @Nullable
    public Material debugRed;

    /**
     * The green material.
     */
    @Nullable
    public Material debugGreen;

    /**
     * The yellow material.
     */
    @Nullable
    public Material debugYellow;

    /**
     * The magenta material.
     */
    @Nullable
    public Material debugMagenta;

    /**
     * The pink material.
     */
    @Nullable
    public Material debugPink;

    /**
     * The display filter.
     */
    @Nullable
    protected Predicate<Object> filter;

    /**
     * The application.
     */
    @Nullable
    protected Application application;

    /**
     * The asset manager.
     */
    @Nullable
    protected AssetManager assetManager;

    public BulletDebugAppState(@NotNull final PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
        this.debugRootNode = new Node("Physics Debug Root Node");
        this.debugRootNode.setCullHint(Spatial.CullHint.Never);
        this.bodies = DictionaryFactory.newObjectDictionary();
        this.prevBodies = DictionaryFactory.newObjectDictionary();
        this.joints = DictionaryFactory.newObjectDictionary();
        this.prevJoints = DictionaryFactory.newObjectDictionary();
        this.ghosts = DictionaryFactory.newObjectDictionary();
        this.prevGhosts = DictionaryFactory.newObjectDictionary();
        this.characters = DictionaryFactory.newObjectDictionary();
        this.prevCharacters = DictionaryFactory.newObjectDictionary();
        this.vehicles = DictionaryFactory.newObjectDictionary();
        this.prevVehicles = DictionaryFactory.newObjectDictionary();
    }

    /**
     * @param filter the display filter.
     */
    public void setFilter(@Nullable final Predicate<Object> filter) {
        this.filter = filter;
    }

    /**
     * @return the display filter.
     */
    @Nullable
    public Predicate<Object> getFilter() {
        return filter;
    }

    /**
     * @param rootNode the node to attach debug nodes.
     */
    public void setRootNode(@Nullable final Node rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * @return the node to attach debug nodes.
     */
    @Nullable
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        this.application = application;
        this.assetManager = application.getAssetManager();

        if (getRootNode() == null) {
            setRootNode(((SimpleApplication) application).getRootNode());
        }

        loadMaterials(application);

        final Node rootNode = requireNonNull(getRootNode());
        rootNode.attachChild(debugRootNode);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node rootNode = requireNonNull(getRootNode());
        rootNode.detachChild(debugRootNode);

        application = null;
        assetManager = null;
        setRootNode(null);
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);
        // update all object links
        updateRigidBodies();
        updateCharacters();
        // update our debug root node
        debugRootNode.updateLogicalState(tpf);
        debugRootNode.updateGeometricState();
    }

    private void loadMaterials(@NotNull final Application application) {

        final AssetManager assetManager = application.getAssetManager();

        if (debugBlue == null) {
            debugBlue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            debugBlue.getAdditionalRenderState().setWireframe(true);
            debugBlue.setColor("Color", ColorRGBA.Blue);
        }

        if (debugGreen == null) {
            debugGreen = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            debugGreen.getAdditionalRenderState().setWireframe(true);
            debugGreen.setColor("Color", ColorRGBA.Green);
        }

        if (debugRed == null) {
            debugRed = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            debugRed.getAdditionalRenderState().setWireframe(true);
            debugRed.setColor("Color", ColorRGBA.Red);
        }

        if (debugYellow == null) {
            debugYellow = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            debugYellow.getAdditionalRenderState().setWireframe(true);
            debugYellow.setColor("Color", ColorRGBA.Yellow);
        }

        if (debugMagenta == null) {
            debugMagenta = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            debugMagenta.getAdditionalRenderState().setWireframe(true);
            debugMagenta.setColor("Color", ColorRGBA.Magenta);
        }

        if (debugPink == null) {
            debugPink = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            debugPink.getAdditionalRenderState().setWireframe(true);
            debugPink.setColor("Color", ColorRGBA.Pink);
        }
    }

    /**
     * @return the blue material.
     */
    @Nullable
    public Material getDebugBlue() {
        return debugBlue;
    }

    /**
     * @return the magenta material.
     */
    @Nullable
    public Material getDebugMagenta() {
        return debugMagenta;
    }

    /**
     * @return the pink material.
     */
    @Nullable
    public Material getDebugPink() {
        return debugPink;
    }

    /**
     * @return the previous registered rigid bodies.
     */
    @NotNull
    protected ObjectDictionary<PhysicsRigidBody, Spatial> getPrevBodies() {
        return prevBodies;
    }

    /**
     * @return the current registered rigid bodies.
     */
    @NotNull
    protected ObjectDictionary<PhysicsRigidBody, Spatial> getBodies() {
        return bodies;
    }

    /**
     * @return the previous registered characters.
     */
    @NotNull
    protected ObjectDictionary<PhysicsCharacter, Spatial> getPrevCharacters() {
        return prevCharacters;
    }

    /**
     * @return the current registered characters.
     */
    @NotNull
    protected ObjectDictionary<PhysicsCharacter, Spatial> getCharacters() {
        return characters;
    }

    private void updateRigidBodies() {

        final Predicate<Object> filter = getFilter();

        final ObjectDictionary<PhysicsRigidBody, Spatial> prevBodies = getPrevBodies();
        final ObjectDictionary<PhysicsRigidBody, Spatial> bodies = getBodies();

        prevBodies.put(bodies);
        bodies.clear();

        final Collection<PhysicsRigidBody> current = physicsSpace.getRigidBodyList();

        for (final PhysicsRigidBody object : current) {

            // copy existing spatials
            if (prevBodies.containsKey(object)) {
                bodies.put(object, prevBodies.get(object));
            } else {
                if (filter == null || filter.test(object)) {
                    //create new spatial
                    final Node node = new Node(object.toString());
                    node.addControl(new BulletRigidBodyDebugControl(this, object));
                    bodies.put(object, node);
                    debugRootNode.attachChild(node);
                }
            }
        }

        prevBodies.forEach(bodies, (actual, key, value) -> {
            if (!actual.containsKey(key)) value.removeFromParent();
        });

        prevBodies.clear();
    }

    private void updateCharacters() {

        final Predicate<Object> filter = getFilter();

        final ObjectDictionary<PhysicsCharacter, Spatial> prevCharacters = getPrevCharacters();
        final ObjectDictionary<PhysicsCharacter, Spatial> characters = getCharacters();

        prevCharacters.put(characters);
        characters.clear();

        final Collection<PhysicsCharacter> current = physicsSpace.getCharacterList();

        for (final PhysicsCharacter object : current) {

            // copy existing spatials
            if (prevCharacters.containsKey(object)) {
                characters.put(object, prevCharacters.get(object));
            } else {
                if (filter == null || filter.test(object)) {
                    //create new spatial
                    final Node node = new Node(object.toString());
                    node.addControl(new BulletCharacterDebugControl(this, object));
                    characters.put(object, node);
                    debugRootNode.attachChild(node);
                }
            }
        }

        prevCharacters.forEach(characters, (actual, key, value) -> {
            if (!actual.containsKey(key)) value.removeFromParent();
        });

        prevCharacters.clear();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
    }
}
