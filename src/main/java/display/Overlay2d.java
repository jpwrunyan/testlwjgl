package display;

public interface Overlay2d {
    DisplayObject[] getDisplayObjects();

    default void cleanup() {
        DisplayObject[] displayObjects = getDisplayObjects();
        for (DisplayObject displayObject : displayObjects) {
            displayObject.getMesh().cleanup();
        }
    }
}
