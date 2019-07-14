import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPos = new Vector2d(-1, -1);
    private final Vector2d currentPos = new Vector2d(0, 0);
    private final Vector2f displayVector = new Vector2f();

    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public MouseInput() {
        //Empty constructor
    }

    public void init(Window window) {
        GLFW.glfwSetCursorPosCallback(
            window.getWindowHandle(),
            (windowHandle, xPos, yPos) -> {
                currentPos.x = xPos;
                currentPos.y = yPos;
                //System.out.println("currentPos in callback: " + currentPos);
            }
        );
        GLFW.glfwSetCursorEnterCallback(
            window.getWindowHandle(),
            (windowHandle, entered) -> inWindow = entered
        );
        GLFW.glfwSetMouseButtonCallback(
            window.getWindowHandle(),
            (windowHandle, button, action, mode) -> {
                leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
                rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
            }
        );
    }

    public Vector2f getDisplayVector() {
        return displayVector;
    }

    public void input(Window window) {
        displayVector.x = 0;
        displayVector.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double dx = currentPos.x - previousPos.x;
            double dy = currentPos.y - previousPos.y;
            boolean rotateX = dx != 0;
            boolean rotateY = dy != 0;
            if (rotateX) {
                displayVector.y = (float) dx;
            }
            if (rotateY) {
                displayVector.x = (float) dy;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
