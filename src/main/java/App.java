import org.joml.Matrix4f;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.Date;

import static org.lwjgl.glfw.GLFW.*;

public class App implements Runnable {
    public static GLFWKeyCallbackI keyCallback;

    public static boolean[] keys = new boolean[65536];


    private int width = 1280;
    private int height = 720;

    private Thread thread;

    private boolean running = false;

    private long window;

    public static void main(String[] args) {
        System.out.println("it works!");
        new App().start();

    }

    public void start() {
        running = true;
        //TODO: make one thread for the game and one for renderering.
        thread = new Thread(this, "GameThread");
        thread.start();
    }

    public void run() {
        init(); //init open GL must be on the same thread. Can't do in constructor.

        double secsPerUpdate = 1.0d / 30.0d;
        double previous = new Date().getTime();
        double steps = 0.0;
        while (running) {
            double loopStartTime = new Date().getTime();
            double elapsed = loopStartTime - previous;
            previous = loopStartTime;
            steps += elapsed;

            handleInput();

            if (glfwWindowShouldClose(window) == true) {
                running = false;
            }

            while (steps >= secsPerUpdate) {
                updateGameState();
                steps -= secsPerUpdate;
            }

            render();
            sync(loopStartTime);
        }

        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        //glfwSetErrorCallback(null).free();
    }

    private void init() {
        if (glfwInit() != true) {
            System.out.println("Terrible error, glfw did not init");
            return;
        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(width, height, "flappy", MemoryUtil.NULL, MemoryUtil.NULL);
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                keys[key] = action == GLFW_RELEASE ? false : true;
            }
        });

        GL.createCapabilities();
        GL11.glClearColor(1.0f, 1.0f,1.0f,1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        System.out.println("OpenGL: " + GL11.glGetString(GL11.GL_VERSION));
        Matrix4f m = new Matrix4f();

    }
    private void update() {
        glfwPollEvents();

        if (keys[GLFW_KEY_SPACE]) {
            System.out.println("flap");
        } else if (keys[GLFW_KEY_ESCAPE]) {
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        }
    }

    private void handleInput() {
        glfwPollEvents();

        if (keys[GLFW_KEY_SPACE]) {
            System.out.println("flap");
        } else if (keys[GLFW_KEY_ESCAPE]) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private void updateGameState() {

    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(window);
    }

    private void sync(double loopStartTime) {
        float loopSlot = 1f / 50;
        double endTime = loopStartTime + loopSlot;
        while(new Date().getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {}
        }
    }
}

