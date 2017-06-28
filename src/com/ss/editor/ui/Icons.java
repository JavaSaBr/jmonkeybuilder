package com.ss.editor.ui;

import com.ss.editor.manager.FileIconManager;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * The interface with all icons of this application.
 *
 * @author JavaSaBr
 */
public interface Icons {

    /**
     * The constant ICON_MANAGER.
     */
    @NotNull
    FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The constant REMOVE_12.
     */
    Image REMOVE_12 = ICON_MANAGER.getImage("/ui/icons/actions/svg/horizontal-line-remove-button.svg", 12, false);
    /**
     * The constant ADD_12.
     */
    Image ADD_12 = ICON_MANAGER.getImage("/ui/icons/actions/svg/add-plus-button.svg", 12, false);

    /**
     * The constant SAVE_16.
     */
    Image SAVE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/save.png", 16);
    /**
     * The constant SCALE_16.
     */
    Image SCALE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/scale.png", 16);
    /**
     * The constant ROTATION_16.
     */
    Image ROTATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/rotation.png", 16);
    /**
     * The constant CUBE_16.
     */
    Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/hollow-cube.svg");
    /**
     * The constant MOVE_16.
     */
    Image MOVE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/move.png", 16);
    /**
     * The constant LIGHT_16.
     */
    Image LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/idea.svg");
    /**
     * The constant INFLUENCER_16.
     */
    Image INFLUENCER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/influencer.png");
    /**
     * The constant SPHERE_16.
     */
    Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/planet-sphere.svg");
    /**
     * The constant PLANE_16.
     */
    Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/plane.png");
    /**
     * The constant NODE_16.
     */
    Image NODE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/node.png");
    /**
     * The constant PARTICLES_16.
     */
    Image PARTICLES_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particles.png");
    /**
     * The constant PARTICLE_GEOMETRY_16.
     */
    Image PARTICLE_GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particle-geometry.png");
    /**
     * The constant GEOMETRY_16.
     */
    Image GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/cube-divisions.svg");
    /**
     * The constant MESH_16.
     */
    Image MESH_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/grid.svg");
    /**
     * The constant EDIT_16.
     */
    Image EDIT_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/pencil-edit-button.svg");
    /**
     * The constant AMBIENT_16.
     */
    Image AMBIENT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/ambient.png");
    /**
     * The constant LAMP_16.
     */
    Image LAMP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/lamp.png");
    /**
     * The constant POINT_LIGHT_16.
     */
    Image POINT_LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/point.png");
    /**
     * The constant SUN_16.
     */
    Image SUN_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sun.png");
    /**
     * The constant PLAY_16.
     */
    Image PLAY_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/play-button.svg");
    /**
     * The constant STOP_16.
     */
    Image STOP_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/stop.svg");
    /**
     * The constant PAUSE_16.
     */
    Image PAUSE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/pause.svg");
    /**
     * The constant ANIMATION_16.
     */
    Image ANIMATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/animation.png");
    /**
     * The constant ANI_CHANNEL_16.
     */
    Image ANI_CHANNEL_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/anim-channel.png");
    /**
     * The constant GEAR_16.
     */
    Image GEAR_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/gear.png");
    /**
     * The constant BONE_16.
     */
    Image BONE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/bone.png");
    /**
     * The constant AUDIO_16.
     */
    Image AUDIO_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/audio-volume.svg");
    /**
     * The constant SETTINGS_16.
     */
    Image SETTINGS_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/settings.png");
    /**
     * The constant PASTE_16.
     */
    Image PASTE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/paste.png");
    /**
     * The constant VIEW_16.
     */
    Image VIEW_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/view.png");
    /**
     * The constant NEW_FILE_16.
     */
    Image NEW_FILE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/new_file.png");
    /**
     * The constant CUT_16.
     */
    Image CUT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cut.png");
    /**
     * The constant COPY_16.
     */
    Image COPY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/copy.png");
    /**
     * The constant TRANSFORMATION_16.
     */
    Image TRANSFORMATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/transformation.png");
    /**
     * The constant EXTRACT_16.
     */
    Image EXTRACT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/extract.png");
    /**
     * The constant GENERATE_16.
     */
    Image GENERATE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/generate_16.png");
    /**
     * The constant SCENE_16.
     */
    Image SCENE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/scene.png");
    /**
     * The constant LAYERS_16.
     */
    Image LAYERS_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/layers.png");
    /**
     * The constant OPEN_FILE_16.
     */
    Image OPEN_FILE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/open-folder-with-document.svg");
    /**
     * The constant BACKGROUND_16.
     */
    Image BACKGROUND_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/background.png");
    /**
     * The constant EMITTER_16.
     */
    Image EMITTER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/emitter.png");
    /**
     * The constant SKY_16.
     */
    Image SKY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sky.png");
    /**
     * The constant INVISIBLE_16.
     */
    Image INVISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/invisible.png");
    /**
     * The constant VISIBLE_16.
     */
    Image VISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/visible.png");
    /**
     * The constant STATIC_RIGID_BODY_16.
     */
    Image STATIC_RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/static-rigid-body.png");
    /**
     * The constant RIGID_BODY_16.
     */
    Image RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/rigid-body.png");
    /**
     * The constant REPLAY_16.
     */
    Image REPLAY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/reply.png");
    /**
     * The constant CHARACTER_16.
     */
    Image CHARACTER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/character.png");
    /**
     * The constant SKELETON_16.
     */
    Image SKELETON_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/skeleton.png");
    /**
     * The constant VEHICLE_16.
     */
    Image VEHICLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/vehicle.png");
    /**
     * The constant ATOM_16.
     */
    Image ATOM_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/atom.png");
    /**
     * The constant PHYSICS_16.
     */
    Image PHYSICS_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/physics.png");
    /**
     * The constant DOLL_16.
     */
    Image DOLL_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/doll.png");
    /**
     * The constant CAPSULE_16.
     */
    Image CAPSULE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/capsule.png");
    /**
     * The constant CONE_16.
     */
    Image CONE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cone.png");
    /**
     * The constant CYLINDER_16.
     */
    Image CYLINDER_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/cylinder.svg");
    /**
     * The constant TERRAIN_16.
     */
    Image TERRAIN_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/terrain.svg");
    /**
     * The constant WHEEL_16.
     */
    Image WHEEL_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/wheel.svg");
    /**
     * The constant TRIANGLE_16.
     */
    Image TRIANGLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/triangle.svg");
    /**
     * The constant DOME_16.
     */
    Image DOME_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/reichstag-dome.svg");
    /**
     * The constant QUAD_16.
     */
    Image QUAD_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/basic-square.svg");
    /**
     * The constant RHOMB_16.
     */
    Image RHOMB_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/rhombus.svg");
    /**
     * The constant TORUS_16.
     */
    Image TORUS_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/circle.svg");
    /**
     * The constant POINTS_16.
     */
    Image POINTS_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/because-mathematical-symbol.svg");
    /**
     * The constant IMPOSTOR_16.
     */
    Image IMPOSTOR_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/plus.svg");
    /**
     * The constant REMOVE_16.
     */
    Image REMOVE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/horizontal-line-remove-button.svg", 16, false);
    /**
     * The constant ADD_16.
     */
    Image ADD_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/add-plus-button.svg", 16, false);
    /**
     * The constant MOTION_16.
     */
    Image MOTION_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/horse-in-running-motion-silhouette.svg", 16, false);
    /**
     * The constant PATH_16.
     */
    Image PATH_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/map-location.svg", 16, false);
    /**
     * The constant WAY_POINT_16.
     */
    Image WAY_POINT_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/placeholder.svg", 16, false);
    /**
     * The constant VERTEX_16.
     */
    Image VERTEX_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/graphene.svg", 16, false);
    /**
     * The constant DATA_16.
     */
    Image DATA_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/database.svg", 16, false);
    /**
     * The constant LINKNODE_16.
     */
    Image LINKNODE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/linknode.png");
    /**
     * The constant LINK_FILE_16.
     */
    Image LINK_FILE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/link-folder-with-document.svg");
    /**
     * The constant STATISTICS_16.
     */
    Image STATISTICS_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/bar-chart.svg");
    /**
     * The constant STATISTICS_16.
     */
    Image DOR_IN_CIRCLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/dot-and-circle.svg");

    /**
     * The constant REFRESH_18.
     */
    Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/refresh.png", 18);
    /**
     * The constant CLOSE_18.
     */
    Image CLOSE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/close.png", 18);

    /**
     * The constant SAVE_24.
     */
    Image SAVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/save.png", 24);
    /**
     * The constant ADD_24.
     */
    Image ADD_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add.png", 24);
    /**
     * The constant LIGHT_24.
     */
    Image LIGHT_24 = ICON_MANAGER.getImage("/ui/icons/actions/svg/idea.svg", 24);
    /**
     * The constant ROTATION_24.
     */
    Image ROTATION_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/rotation.png", 24);
    /**
     * The constant MOVE_24.
     */
    Image MOVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/move.png", 24);
    /**
     * The constant SCALE_24.
     */
    Image SCALE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/scale.png", 24);
    /**
     * The constant CUBE_24.
     */
    Image CUBE_24 = ICON_MANAGER.getImage("/ui/icons/actions/svg/hollow-cube.svg", 24);
    /**
     * The constant SPHERE_24.
     */
    Image SPHERE_24 = ICON_MANAGER.getImage("/ui/icons/actions/svg/planet-sphere.svg", 24);
    /**
     * The constant PLANE_24.
     */
    Image PLANE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/plane.png", 24);
    /**
     * The constant ADD_CIRCLE_24.
     */
    Image ADD_CIRCLE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add_circle.png", 24);
    /**
     * The constant IMAGE_24.
     */
    Image IMAGE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/image.png", 24);
    /**
     * The constant WARNING_24.
     */
    Image WARNING_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/warning.png", 24);
    /**
     * The constant FROM_FULLSCREEN_24.
     */
    Image FROM_FULLSCREEN_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/from_fullscreen.png", 24);
    /**
     * The constant TO_FULLSCREEN_24.
     */
    Image TO_FULLSCREEN_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/to_fullscreen.png", 24);

    /**
     * The constant TERRAIN_LEVEL_32.
     */
    Image TERRAIN_LEVEL_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/level_terrain.svg", 32);
    /**
     * The constant TERRAIN_PAINT_32.
     */
    Image TERRAIN_PAINT_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/paint_terrain.svg", 32);
    /**
     * The constant TERRAIN_ROUGH_32.
     */
    Image TERRAIN_ROUGH_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/rough_terrain.svg", 32);
    /**
     * The constant TERRAIN_SLOPE_32.
     */
    Image TERRAIN_SLOPE_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/slope_terrain.svg", 32);
    /**
     * The constant TERRAIN_SMOOTH_32.
     */
    Image TERRAIN_SMOOTH_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/smooth_terrain.svg", 32);
    /**
     * The constant TERRAIN_UP_32.
     */
    Image TERRAIN_UP_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/raise_terrain.svg", 32);

    /**
     * The constant PLAY_128.
     */
    Image PLAY_128 = ICON_MANAGER.getImage("/ui/icons/actions/128/play.png", 128);
    /**
     * The constant PAUSE_128.
     */
    Image PAUSE_128 = ICON_MANAGER.getImage("/ui/icons/actions/128/pause.png", 128);
    /**
     * The constant STOP_128.
     */
    Image STOP_128 = ICON_MANAGER.getImage("/ui/icons/actions/128/stop.png", 128);

    /**
     * The constant IMAGE_512.
     */
    Image IMAGE_512 = ICON_MANAGER.getImage("/ui/icons/512/image.png", 512);
}
