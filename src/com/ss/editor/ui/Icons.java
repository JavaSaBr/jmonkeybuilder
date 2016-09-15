package com.ss.editor.ui;

import com.ss.editor.manager.FileIconManager;

import javafx.scene.image.Image;

/**
 * Интерфейс для перечисление иконок.
 *
 * @author Ronn
 */
public interface Icons {

    public static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    public static final Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cube.png");
    public static final Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sphere.png");
    public static final Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/plane.png");
    public static final Image NODE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/node.png");
    public static final Image PARTICLES_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particles.png");
    public static final Image GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/geometry.png");
    public static final Image MESH_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/mesh.png");
    public static final Image EDIT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/edit.png");
    public static final Image AMBIENT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/ambient.png");
    public static final Image LAMP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/lamp.png");
    public static final Image POINT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/point.png");
    public static final Image SUN_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sun.png");
    public static final Image PLAY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/play.png");
    public static final Image STOP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/stop.png");
    public static final Image ANIMATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/animation.png");
    public static final Image ANI_CHANNEL_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/anim_channel.png");
    public static final Image GEAR_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/gear.png");

    public static final Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/refresh.png", 18);
    public static final Image REMOVE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/remove.png", 18);
    public static final Image ADD_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/add.png", 18);
    public static final Image CLOSE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/close.png", 18);

    public static final Image SAVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/save.png", 24);
    public static final Image ADD_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add.png", 24);
    public static final Image LIGHT_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/light.png", 24);
    public static final Image ROTATION_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/rotation.png", 24);
    public static final Image MOVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/move.png", 24);
    public static final Image SCALE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/scale.png", 24);
    public static final Image CUBE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/cube.png", 24);
    public static final Image SPHERE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/sphere.png", 24);
    public static final Image PLANE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/plane.png", 24);
    public static final Image ADD_CIRCLE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add_circle.png", 24);
    public static final Image IMAGE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/image.png", 24);
    public static final Image EDIT_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/edit.png", 24);

    public static final Image IMAGE_512 = ICON_MANAGER.getImage("/ui/icons/512/image.png", 512);
}
