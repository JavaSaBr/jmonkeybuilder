package com.ss.editor.ui;

import com.ss.editor.manager.FileIconManager;

import javafx.scene.image.Image;

/**
 * The interface with all icons of this application.
 *
 * @author JavaSaBr
 */
public interface Icons {

    FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cube.png");
    Image INFLUENCER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/influencer.png");
    Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sphere.png");
    Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/plane.png");
    Image NODE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/node.png");
    Image PARTICLES_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particles.png");
    Image PARTICLE_GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particle_geometry.png");
    Image GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/geometry.png");
    Image MESH_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/mesh.png");
    Image EDIT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/edit.png");
    Image AMBIENT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/ambient.png");
    Image LAMP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/lamp.png");
    Image POINT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/point.png");
    Image SUN_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sun.png");
    Image PLAY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/play.png");
    Image STOP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/stop.png");
    Image ANIMATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/animation.png");
    Image ANI_CHANNEL_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/anim_channel.png");
    Image GEAR_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/gear.png");

    Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/refresh.png", 18);
    Image REMOVE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/remove.png", 18);
    Image ADD_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/add.png", 18);
    Image CLOSE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/close.png", 18);

    Image SAVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/save.png", 24);
    Image ADD_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add.png", 24);
    Image LIGHT_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/light.png", 24);
    Image ROTATION_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/rotation.png", 24);
    Image MOVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/move.png", 24);
    Image SCALE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/scale.png", 24);
    Image CUBE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/cube.png", 24);
    Image SPHERE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/sphere.png", 24);
    Image PLANE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/plane.png", 24);
    Image ADD_CIRCLE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add_circle.png", 24);
    Image IMAGE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/image.png", 24);
    Image WARNING_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/warning.png", 24);
    Image EDIT_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/edit.png", 24);

    Image IMAGE_512 = ICON_MANAGER.getImage("/ui/icons/512/image.png", 512);
}
