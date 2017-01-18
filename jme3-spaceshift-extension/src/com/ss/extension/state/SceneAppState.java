package com.ss.extension.state;

import com.jme3.app.state.AppState;
import com.jme3.export.Savable;
import com.jme3.util.clone.JmeCloneable;

/**
 * The interface to implement a scene app state.
 *
 * @author JavaSaBr
 */
public interface SceneAppState extends AppState, Savable, Cloneable, JmeCloneable {
}

