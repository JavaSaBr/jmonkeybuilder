package com.ss.editor.test;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SafeArrayList;
import com.jme3.util.clone.Cloner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinkedAssetTest extends SimpleApplication {

    public static void main(String[] args) {
        LinkedAssetTest app = new LinkedAssetTest();
        app.setShowSettings(false);
        app.start();
    }

    /**
     * If using the assetManager (true), it messes up the binary importer, if not (false), it doesn't share meshes
     */
    public static boolean READ_FROM_ASSETMANAGER = true;

    /**
     * If the linked model is preloaded (true), it works fine then when reading the model containing the linked (it is on cache)
     */
    public static boolean PRELOAD_LINKED_MODEL = false;

    public static Material SAVE_MATERIAL;

    @Override
    public void simpleInitApp() {

        SAVE_MATERIAL = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        SAVE_MATERIAL.setColor("Color", ColorRGBA.Brown);

        String homePath = System.getProperty("user.home");

        String sceneFileName = "toDelete.j3o";
        String linkedFileName = "toDelete2.j3o";

        File sceneFile = new File(homePath + File.separator + sceneFileName);
        File linkedFile = new File(homePath + File.separator + linkedFileName);

        if (sceneFile.exists() || linkedFile.exists()) {
            throw new RuntimeException();
        }

        BinaryExporter exporter = BinaryExporter.getInstance();

        try {

            // Creating the linked model and saving it to a file.
            Geometry linked = new Geometry("linked", new Box(1, 1, 1));

            exporter.save(linked, linkedFile);

            // Creating the scene, linked the model, and saving it to a file.
            AssetLinkNodePatch scene = new AssetLinkNodePatch("scene", new ModelKey(linkedFileName));
            exporter.save(scene, sceneFile);

            // Loading and attaching it to the scene.
            assetManager.registerLocator(homePath, FileLocator.class);

            if (PRELOAD_LINKED_MODEL) {
                assetManager.loadModel(linkedFileName);
            }

            rootNode.attachChild(assetManager.loadModel(sceneFileName));

        } catch (IOException ex) {
            Logger.getLogger(LinkedAssetTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Deleting the files.
                Files.delete(Paths.get(sceneFile.toURI()));
                Files.delete(Paths.get(linkedFile.toURI()));
            } catch (IOException ex) {
                Logger.getLogger(LinkedAssetTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public static class AssetLinkNodePatch extends Node {

        protected ArrayList<ModelKey> assetLoaderKeys = new ArrayList<ModelKey>();
        protected Map<ModelKey, Spatial> assetChildren = new HashMap<ModelKey, Spatial>();

        public AssetLinkNodePatch() {
        }

        public AssetLinkNodePatch(ModelKey key) {
            this(key.getName(), key);
        }

        public AssetLinkNodePatch(String name, ModelKey key) {
            super(name);
            assetLoaderKeys.add(key);
        }

        /**
         * Called internally by com.jme3.util.clone.Cloner.  Do not call directly.
         */
        @Override
        public void cloneFields(Cloner cloner, Object original) {
            super.cloneFields(cloner, original);

            // This is a change in behavior because the old version did not clone
            // this list... changes to one clone would be reflected in all.
            // I think that's probably undesirable. -pspeed
            this.assetLoaderKeys = cloner.clone(assetLoaderKeys);
            this.assetChildren = new HashMap<ModelKey, Spatial>();
        }

        /**
         * Add a "linked" child. These are loaded from the assetManager when the
         * AssetLinkNode is loaded from a binary file.
         *
         * @param key
         */
        public void addLinkedChild(ModelKey key) {
            if (assetLoaderKeys.contains(key)) {
                return;
            }
            assetLoaderKeys.add(key);
        }

        public void removeLinkedChild(ModelKey key) {
            assetLoaderKeys.remove(key);
        }

        public ArrayList<ModelKey> getAssetLoaderKeys() {
            return assetLoaderKeys;
        }

        public void attachLinkedChild(AssetManager manager, ModelKey key) {
            addLinkedChild(key);
            Spatial child = manager.loadAsset(key);
            assetChildren.put(key, child);
            attachChild(child);
        }

        public void attachLinkedChild(Spatial spat, ModelKey key) {
            addLinkedChild(key);
            assetChildren.put(key, spat);
            attachChild(spat);
        }

        public void detachLinkedChild(ModelKey key) {
            Spatial spatial = assetChildren.get(key);
            if (spatial != null) {
                detachChild(spatial);
            }
            removeLinkedChild(key);
            assetChildren.remove(key);
        }

        public void detachLinkedChild(Spatial child, ModelKey key) {
            removeLinkedChild(key);
            assetChildren.remove(key);
            detachChild(child);
        }

        /**
         * Loads the linked children AssetKeys from the AssetManager and attaches them to the Node<br>
         * If they are already attached, they will be reloaded.
         *
         * @param manager
         */
        public void attachLinkedChildren(AssetManager manager) {
            detachLinkedChildren();
            for (Iterator<ModelKey> it = assetLoaderKeys.iterator(); it.hasNext(); ) {
                ModelKey assetKey = it.next();
                Spatial curChild = assetChildren.get(assetKey);
                if (curChild != null) {
                    curChild.removeFromParent();
                }
                Spatial child = manager.loadAsset(assetKey);
                attachChild(child);
                assetChildren.put(assetKey, child);
            }
        }

        public void detachLinkedChildren() {
            Set<Map.Entry<ModelKey, Spatial>> set = assetChildren.entrySet();
            for (Iterator<Map.Entry<ModelKey, Spatial>> it = set.iterator(); it.hasNext(); ) {
                Map.Entry<ModelKey, Spatial> entry = it.next();
                entry.getValue().removeFromParent();
                it.remove();
            }
        }

        @Override
        public void read(JmeImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            BinaryImporter importer = BinaryImporter.getInstance();
            AssetManager loaderManager = e.getAssetManager();

            assetLoaderKeys = (ArrayList<ModelKey>) capsule.readSavableArrayList("assetLoaderKeyList", new ArrayList<ModelKey>());
            for (Iterator<ModelKey> it = assetLoaderKeys.iterator(); it.hasNext(); ) {
                ModelKey modelKey = it.next();
                AssetInfo info = loaderManager.locateAsset(modelKey);
                Spatial child = null;
                if (info != null) {

                    // --- PROBLEM - CRASH ---

                    if (!READ_FROM_ASSETMANAGER) {
                        // With this line, it loads the spatial, but meshes aren't shared
                        child = (Spatial) importer.load(info);
                    } else {
                        // With this line, however, it messes up the importer
                        child = loaderManager.loadAsset(modelKey);
                    }

                    // --- --------------- ---


                }
                if (child != null) {
                    // I can't access to the child parent directly from here.
                    //                    child.parent = this;
                    //                    children.add(child);
                    attachChild(child);
                    assetChildren.put(modelKey, child);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot locate {0} for asset link node {1}", new Object[]{modelKey, key});
                }
            }

            // Reading extra parameter -- Crashes if: READ_FROM_ASSETMANAGER == true && PRELOAD_LINKED_MODEL == false
            Material material = (Material) capsule.readSavable("material", null);
            for (Spatial child : assetChildren.values()) {
                child.setMaterial(material);
            }
            ///////////////////


        }

        @Override
        public void write(JmeExporter e) throws IOException {
            SafeArrayList<Spatial> childs = children;
            children = new SafeArrayList<Spatial>(Spatial.class);
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.writeSavableArrayList(assetLoaderKeys, "assetLoaderKeyList", null);
            children = childs;

            // Writing extra parameter
            capsule.write(SAVE_MATERIAL, "material", null);
            ////////////////////////
        }

    }
}