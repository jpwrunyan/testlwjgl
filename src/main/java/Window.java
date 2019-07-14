import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Window {

    private final String title;
    private int width;
    private int height;
    private boolean vSync;
    private long windowHandle;
    private boolean resized;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        //Initialize as resized.
        this.resized = true;
    }

    public void init() {
        //Sets up an error callback.
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW.
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initalize GLFW");
        }

        //This is optional, current window hints are already default.
        GLFW.glfwDefaultWindowHints();
        //The window will stay hidden after creation.
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        //The window will be resizable.
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        //This will make the program use the highest OpenGL version possible between 3.2 and 4.1. If those lines are not included, a Legacy version of OpenGL is used.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        //
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window.");
        }
        // Setup resize callback
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        //Set key callback.
        GLFW.glfwSetKeyCallback(windowHandle, (window, key, scanCode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        //Get resolution of the primary monitor.
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        //Center the window.
        GLFW.glfwSetWindowPos(
            windowHandle,
            (vidMode.width() - width) / 2,
            (vidMode.height() - height) / 2
        );

        //Make the OpenGL context current.
        GLFW.glfwMakeContextCurrent(windowHandle);

        if (isVSync()) {
            //Enable v-sync.
            GLFW.glfwSwapInterval(1);
        }

        //Make the window visible.
        GLFW.glfwShowWindow(windowHandle);

        //Must be used to set the current context for GL11.
        GL.createCapabilities();

        //Set the clear color.
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }

    public boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
    }

    public boolean winodowShouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void update() {
        GLFW.glfwSwapBuffers(windowHandle);
        GLFW.glfwPollEvents();
    }
}
