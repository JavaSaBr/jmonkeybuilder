package com.ss.editor.ui.control.model.tree.action;


import com.ss.editor.ui.control.model.node.spatial.GeometryTreeNode;
import com.ss.editor.ui.control.model.node.spatial.NodeTreeNode;

public class DataCopy
{
    private static NodeTreeNode copySpatial = null;

    private static GeometryTreeNode copyGeom = null;


    public static NodeTreeNode getCopySpatial() {
        return copySpatial;
    }

    public static void setCopySpatial(NodeTreeNode copySpatial) {
        DataCopy.copySpatial = copySpatial;
        DataCopy.copyGeom = null;
    }

    public static GeometryTreeNode getCopyGeom() {
        return copyGeom;
    }

    public static void setCopyGeom(GeometryTreeNode copyGeom) {
        DataCopy.copyGeom = copyGeom;
        DataCopy.copySpatial = null;
    }
}
