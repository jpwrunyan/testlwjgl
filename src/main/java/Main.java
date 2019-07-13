import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import utils.ShaderFileUtil;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            GameLogic gameLogic = new TestGameLogic();
            GameEngine gameEngine = new GameEngine(
                "GAME",
                600,
                480,
                vSync,
                gameLogic
            );
            gameEngine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}

/**
 * DummyGame class
 * Honestly, this should really be the main thread that the game engine runs on.
 * Being a separate class feels silly.
 */
class TestGameLogic implements GameLogic {

    private int direction = 0;
    private float color = 0.0f;
    private final Renderer renderer;
    private Mesh mesh;
    private Mesh mesh2;

    public TestGameLogic() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        //By the book...
        float[] positions = new float[]{
            -0.5f,  0.5f, -1.05f,
            -0.5f, -0.5f, -1.05f,
            0.5f, -0.5f, -1.05f,
            0.5f,  0.5f, -1.05f,
        };
        int[] indices = new int[]{0, 1, 3, 3, 1, 2,};

        /*
        //I can also do it this way if I want...
        float[] positions = new float[]{
                -0.5f,  0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,

        };
        int[] indices = new int[]{0, 1, 2, 2, 1, 3};
        */
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        mesh = new Mesh(positions, indices, colors);

        //I can also do it this way if I want...
        positions = new float[]{
                -1f,  0f, -1.05f,
                0f,  0f, -1.05f,
                -1f, -1f, -1.05f,
                0f, -1f, -1.05f,

        };
        indices = new int[]{0, 1, 2, 2, 1, 3};
        colors = new float[]{
                .71f, 0.0f, 0.0f,
                .71f, 0.25f, 0.0f,
                .71f, 0.0f, 0.25f,
                .7f, 0.25f, 0.25f,
        };
        mesh2 = new Mesh(positions, indices, colors);
    }

    @Override
    public void input(Window window) {
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            direction = 1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update(float interval) {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if (color < 0) {
            color = 0.0f;
        }
    }

    @Override
    public void render(Window window) {
        /*
        if (window.isResized()) {
            System.out.println("resized");
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        window.setClearColor(color, color, color, 0.0f);
        renderer.clear();
        */

        window.setClearColor(color, color, color, 0.0f);
        //renderer.clear();

        DisplayObject[] displayObjects = new DisplayObject[]{
            new DisplayObject(mesh).setRotation(0, 0, 10),
            new DisplayObject(mesh2).setRotation(20, 0, 0)
        };

        renderer.render(window, displayObjects);
        //renderer.render(window, mesh2);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}