# jMonkeyEngine 3 SpaceShift Editor 0.9.3 #
## License: Apache Version 2.0 ##

* Download: https://yadi.sk/d/UuKcJBNgqbV3a

## [Video about this editor.](https://www.youtube.com/watch?v=I9ads0-I_LI&feature=youtu.be) ##

## ver. 0.9.3 ##
* [Video](https://www.youtube.com/watch?v=FdGnkPj_qX0&feature=youtu.be)
* -Added a bullet app state to scene editor.
* -Added supporting debug physics to a bullet app state.
* -Added supporting editing rigid/vehicle/character controls.
* -Added actions to make collision shapes.
* -Added supporting creating/editing vehicle wheels.
* -Updated working with layers in scene editor.
* -Some fixes and improvements.

## ver. 0.9.2 ##
* [Video](https://www.youtube.com/watch?v=CPNaI9jDoOk&feature=youtu.be)
* -Implemented scene filters editing and provided API to support custom filters.
* -Added some build in filters.
* -Fixed problems with custom classpath.
* -Implemented D&D models from asset tree to model/scene editor.
* -Implemented renaming lights in model/scene editor.
* -Re-implemented working with scene layers.
* -Implemented more user-friendly light editing in scene/model editor.
* -Updated API for custom app states.
* -Updated file selector dialogs, added some actions to a context menu.
* -Updated a grid in scene/model editor.
* -Added auto move editor's camera to selected node.
* -Implemented D&D materials from asset tree to model/scene editor.
* -Implemented D&D textures from asset tree to material editor.
* -Added hotkeys(R/G/S) to switch a manipulator mode in scene/model editor.
* -Added hotkey(delete) to delete a selected node from scene/model editor.
* -Implemented D&D audio files from asset tree to audio data property in model/scene editor.
* -Implemented control editing and provided API to support custom controls.

## ver. 0.9.1 ##
* [Video](https://www.youtube.com/watch?v=UkG3blab-xg&feature=youtu.be)
* -Add some settings to setting dialog.
* -Added shift+ctrl value scrolling.
* -Added environments folder to settings dialog.
* -Started implementing a scene editor.
* -Updated jME libraries.
* -Implemented layers in a scene editor.
* -Implemented scene states editing and provided API to support custom states.
* -Integrated lighting and sky states from SimFX library.
* -Implemented user data editing.
* -Added some performance optimizations with jME render inside JavaFX.

## ver. 0.9.0 ##
* [Video](https://www.youtube.com/watch?v=LNrDesrg7zc&feature=youtu.be)
* -Implemented soft particles emitter.
* -Fixed gamma correction and updated fast envs in the ModelEditor.
* -Fixed some problems with standard shaders.
* -Fixed the some bugs with editing influencers.
* -Implemented auto-refreshing files which was edited from external editors.
* -Implemented memory optimizations of the toneg0d emitters.
* -Added lifetime of an emitter node.
* -Added control to edit geometry list of a physics influencer.

## ver. 0.8.9 ##
* [Video](https://www.youtube.com/watch?v=xCSUIia-jh4&feature=youtu.be)
* -Added the Apache License.
* -Added an action to create a single color texture.
* -Added an AudioViewer and implement supporting AudioNode in the ModelEditor.
* -Added an action to convert a .xbuf model to .j3o.
* -Updated the list of embedded environment textures.
* -Implemented a LoD Generator.
* -Added a frame rate setting to settings dialog.
* -Updated a style of a ColorPicker control.
* -Updated an ImageViewer.
* -Added an action to extract sub-animations.

## ver. 0.8.8 ##
* -Implemented drawing jME application inside JavaFX Canvas.
* -Migrated from the ReflectionAllocator to the JEmallocAllocator.
* -Added actions to convert .scene/.mesh.xml/.fbx/.obj to .j3o.
* -Updated context menus in the AssetTree and ModelNodeTree.
* -Added showing animation tracks in the ModelNodeTree.
* -Added some actions to animation nodes in the ModelEditor.
* -Bugfixes.

## ver. 0.8.7 ##
* -Implemented saving/loading particle emitter nodes.
* -Added a LogView panel.

## ver. 0.8.6 ##
* -Fixed opening a renamed file in a new editor.
* -Added google analytics, you can disable this in a settings dialog.

## ver. 0.8.5 ##
* -Finished implementing editing particle influencers.
* -Fixed material definition duplicates in the material editor.
* -Changed fonts.
* -Fixed some errors with standard shaders.

## ver. 0.7.8 ##
* -Implemented saving states of window, resource tree and split panels.
* -Updated LWJGL to 3.1.0
* -Updated UI.
* -Fixed some bugs.

## ver. 0.7.7 ##
* -Updated JME platform to 3.2 master branch.
* -Updated JFX and switched integration JavaFX with JME to using ImageView like a viewport of JME.
* -Updated UI.
* -Fixed some bugs.

## ver. 0.7.5 ##
* -Fixed auto synchronize of sub dirrectories in a workspace.

## ver. 0.7.4 ##
* -Updated JME from 3.1 to 3.2.
* -Updated LWJGL from 3.0.0b to 3.0.0 release.

## ver. 0.7.3 ##
* -Updated icons.
* -Updated the vector and the rotation controls for model editor.
* -Implemented controling of camera using keys num1, num2, num3, num4, num6, num7, num8, num9 like the Blender.

## ver. 0.7.2 ##
* -Updated the editor camera for working with large objects.

## ver. 0.7.1 ##
* -Implemented additional classpath folder in Settings -> Other settings.
* -Implemented autosynchronize asset folder with filesystem.

## ver. 0.7.0 ##
* -Implemented loading classes from every *.jars in opened asset folder.

## ver. 0.6.9 ##
* -Updated JME3 libraries.
* -Bug fixes.

## ver. 0.6.7 ##
* -Fixed the preview of TGA textures.
* -Changed the cache of preview of texture.
* -Added the player of animation in the ModelEditor.

## ver. 0.6.6 ##
* -Changed the size of batch of light pass to 5.
* -Updated the JME to 3.1.alpha4.
* -Applyed the last fix for PBR.

## ver. 0.6.5 ##
* -Fixed the problems with sticking cursor while rotating camera of editor.
* -Fixed the crash when loading model with bad material. 
* -Fixed the using custom cursors on LWJGL3.
* -Added beta support of resizing window.
* -Added the functionality for works with a light in Model Editor. 

## ver. 0.6.2 ##
* -Fixed the problems with mouse events. 

## ver. 0.6.1 ##
* -Fixed the missing of tangents in models in Material Editor.
* -Added the option of gamma correction and option of ToneMapFilter to Graphics Settings.
* -Fixed the problems with sticking cursor while rotating camera of editor. 

## ver. 0.6.0 ##
* -Implemented undo-redo(ctrl+z/ctrl+y) in Material Editor and Model Editor.

## ver. 0.5.2 ##
* -Implemented the autocomplete for type of material in material creator.
* -Fixed the problems with focus in dialogs of editor.
* -Added the animation of loading for editors.

## ver. 0.5.0 ##
* -Fixed the exception with LwjglWindow on Windows;
* -Implemented the camera like camera of Blender.
* -Fixed the text input.
* -Implemented the hotkey 'delete' to delete files in tree.
* -Implemented the manipulators in Model Editor.
* -Updated the action for generating tangents.
* -Changed rotation property of model to Euler in Model Editor.

## ver. 0.4.5 ##
* -Added the text editor for GLSL with highlighting.
* -Added the reloading materials when change shaders.
* -Updated the logic for change of material type.
* -Fixed problems with menu bar.
* -Added the action for closing editor to menu bar.
* -Added tooltip with fullpath for root folder in asset tree.
* -Added the action for reopening last asset folders.
* -Implemented the saving of opened files.
* -Updated the jME to alpha 3 with PBR.
* -Migrated the editor from LWJGL 2.9 to LWJGL 3.0
* -Added the combobox with Queue Bucket for model of material preview to material editor.
* -Implemented the save on ctrl+S hotkey.

## ver. 0.4.0 ##
* -Added the action for creating a folder.
* -Added the action for creating an empty file.
* -Implemented image viewer.
* -Added actions for creating node, box, sphere and quad in ModelEditor.
* -Added action for creating SkyBox.
* -Added action for loading other model to model in Model Editor.
* -Fixed severals bugs.

## ver. 0.3.7 ##
* -Implemented the fullscreen mode.
* -Implemented the preview of channels of image.
* -Implemented the cache of preview of image.
* -Changed the sensitivity of zoom.

## ver. 0.3.5 ##
* -Implemeted open file in external editor.
* -Finished implementing the operations of moving/renaming/cut-copy-paste for files in Asset Tree.
* -Added dialog for handling exception.

## ver. 0.3.0 ##
* -Implemented Simple Model Editor.
* -Added action for converting .blend to .j3o.
* -Updated UI.

## ver. 0.2.0 ##
* -Implemented dialogs for creating new material and post filter view file.
* -Implemented dialog of graphic settings.
* -Updated the sky in material editor.
* -Upgraded jME3 libraries to 3.1 alpha 2 with PBR.

## ver. 0.1.1 ##
* -Implemented preview of images in resource selector.
* -Migrated editor to jME 3.1 with PBR.
* -Added support PBR to Material Editor.
* -Finished implementation PostFilterViewer.
## ver. 0.1 ##
* -Implemented the base actions in AssetTree.
* -Implemented MaterialEditor.
* -Added English localization.