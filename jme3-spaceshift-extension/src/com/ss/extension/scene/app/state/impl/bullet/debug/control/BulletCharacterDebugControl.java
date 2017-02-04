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
package com.ss.extension.scene.app.state.impl.bullet.debug.control;

import static com.jme3.bullet.util.DebugShapeFactory.getDebugShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.extension.scene.app.state.impl.bullet.debug.BulletDebugAppState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author normenhansen, JavaSaBr
 */
public class BulletCharacterDebugControl extends AbstractPhysicsDebugControl {

    /**
     * The physics character.
     */
    @NotNull
    protected final PhysicsCharacter body;

    /**
     * The current shape.
     */
    @Nullable
    protected CollisionShape currentShape;

    /**
     * The geometry.
     */
    @Nullable
    protected Spatial geom;

    public BulletCharacterDebugControl(@NotNull final BulletDebugAppState debugAppState,
                                       @NotNull final PhysicsCharacter body) {
        super(debugAppState);
        this.body = body;
        this.currentShape = body.getCollisionShape();
        this.geom = DebugShapeFactory.getDebugShape(body.getCollisionShape());
        this.geom.setMaterial(debugAppState.getDebugPink());
    }

    @Override
    public void setSpatial(@Nullable final Spatial spatial) {

        final Spatial currentSpatial = getSpatial();

        if (spatial != null && spatial instanceof Node) {
            final Node node = (Node) spatial;
            node.attachChild(geom);
        } else if (spatial == null && currentSpatial != null) {
            final Node node = (Node) currentSpatial;
            node.detachChild(geom);
        }

        super.setSpatial(spatial);
    }

    /**
     * @return the physics character.
     */
    @NotNull
    public PhysicsCharacter getBody() {
        return body;
    }

    @Override
    protected void controlUpdate(final float tpf) {

        final PhysicsCharacter body = getBody();
        final CollisionShape shape = body.getCollisionShape();

        if (currentShape != shape) {
            final Node node = (Node) getSpatial();
            node.detachChild(geom);
            geom = getDebugShape(shape);
            geom.setMaterial(debugAppState.getDebugPink());
            node.attachChild(geom);
            currentShape = shape;
        }

        final Vector3f physicsLocation = body.getPhysicsLocation(physicalLocation);

        applyPhysicsTransform(physicsLocation, Quaternion.IDENTITY);

        geom.setLocalScale(shape.getScale());
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
