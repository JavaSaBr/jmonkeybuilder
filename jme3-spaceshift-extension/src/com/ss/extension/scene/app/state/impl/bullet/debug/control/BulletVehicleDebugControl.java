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

import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.ss.extension.scene.app.state.impl.bullet.debug.BulletDebugAppState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author normenhansen, JavaSaBr
 */
public class BulletVehicleDebugControl extends AbstractPhysicsDebugControl {

    /**
     * The physics vehicle.
     */
    @NotNull
    protected final PhysicsVehicle body;

    /**
     * The suspension node.
     */
    @NotNull
    protected final Node suspensionNode;

    public BulletVehicleDebugControl(@NotNull final BulletDebugAppState debugAppState,
                                     @NotNull final PhysicsVehicle body) {
        super(debugAppState);
        this.body = body;
        this.suspensionNode = new Node("Suspension");
    }

    @Override
    public void setSpatial(@Nullable final Spatial spatial) {

        final Spatial currentSpatial = getSpatial();

        if (spatial != null && spatial instanceof Node) {
            final Node node = (Node) spatial;
            node.attachChild(suspensionNode);
        } else if (spatial == null && currentSpatial != null) {
            final Node node = (Node) currentSpatial;
            node.detachChild(suspensionNode);
        }

        super.setSpatial(spatial);
    }

    private void checkAndUpdateVehicle() {

        final int numWheels = body.getNumWheels();

        suspensionNode.detachAllChildren();

        for (int i = 0; i < numWheels; i++) {

            final VehicleWheel physicsVehicleWheel = body.getWheel(i);

            final Vector3f location = new Vector3f(physicsVehicleWheel.getLocation());
            final Vector3f direction = new Vector3f(physicsVehicleWheel.getDirection());
            direction.normalizeLocal();

            final Vector3f axle = new Vector3f(physicsVehicleWheel.getAxle());
            axle.normalizeLocal();
            axle.multLocal(0.3f);

            final float restLength = physicsVehicleWheel.getRestLength();
            final float radius = physicsVehicleWheel.getRadius();

            final Arrow locArrow = new Arrow(location);
            final Arrow axleArrow = new Arrow(axle);
            final Arrow wheelArrow = new Arrow(direction.multLocal(radius));
            final Arrow dirArrow = new Arrow(direction.multLocal(restLength));

            final Geometry locGeom = new Geometry("WheelLocationDebugShape" + i, locArrow);
            final Geometry dirGeom = new Geometry("WheelDirectionDebugShape" + i, dirArrow);
            final Geometry axleGeom = new Geometry("WheelAxleDebugShape" + i, axleArrow);
            final Geometry wheelGeom = new Geometry("WheelRadiusDebugShape" + i, wheelArrow);

            dirGeom.setLocalTranslation(location);
            axleGeom.setLocalTranslation(location.add(direction));
            wheelGeom.setLocalTranslation(location.add(direction));

            locGeom.setMaterial(debugAppState.getDebugMagenta());
            dirGeom.setMaterial(debugAppState.getDebugMagenta());
            axleGeom.setMaterial(debugAppState.getDebugMagenta());
            wheelGeom.setMaterial(debugAppState.getDebugMagenta());

            suspensionNode.attachChild(locGeom);
            suspensionNode.attachChild(dirGeom);
            suspensionNode.attachChild(axleGeom);
            suspensionNode.attachChild(wheelGeom);
        }
    }

    @Override
    protected void controlUpdate(final float tpf) {
        checkAndUpdateVehicle();

        for (int i = 0; i < body.getNumWheels(); i++) {

            final VehicleWheel physicsVehicleWheel = body.getWheel(i);

            final Vector3f location = new Vector3f(physicsVehicleWheel.getLocation());
            final Vector3f direction = new Vector3f(physicsVehicleWheel.getDirection());
            direction.normalizeLocal();

            final Vector3f axle = new Vector3f(physicsVehicleWheel.getAxle());
            axle.normalizeLocal();
            axle.multLocal(0.3f);

            final float restLength = physicsVehicleWheel.getRestLength();
            final float radius = physicsVehicleWheel.getRadius();

            final Geometry locGeom = (Geometry) suspensionNode.getChild("WheelLocationDebugShape" + i);
            final Geometry dirGeom = (Geometry) suspensionNode.getChild("WheelDirectionDebugShape" + i);
            final Geometry axleGeom = (Geometry) suspensionNode.getChild("WheelAxleDebugShape" + i);
            final Geometry wheelGeom = (Geometry) suspensionNode.getChild("WheelRadiusDebugShape" + i);

            final Arrow locArrow = (Arrow) locGeom.getMesh();
            locArrow.setArrowExtent(location);

            final Arrow axleArrow = (Arrow) axleGeom.getMesh();
            axleArrow.setArrowExtent(axle);

            final Arrow wheelArrow = (Arrow) wheelGeom.getMesh();
            wheelArrow.setArrowExtent(direction.multLocal(radius));

            final Arrow dirArrow = (Arrow) dirGeom.getMesh();
            dirArrow.setArrowExtent(direction.multLocal(restLength));

            dirGeom.setLocalTranslation(location);
            axleGeom.setLocalTranslation(location.addLocal(direction));
            wheelGeom.setLocalTranslation(location);
        }

        final Vector3f physicsLocation = body.getPhysicsLocation(physicalLocation);
        final Quaternion physicsRotation = body.getPhysicsRotation(physicalRotation);

        applyPhysicsTransform(physicsLocation, physicsRotation);
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
