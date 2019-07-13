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
 */
class TestGameLogic implements GameLogic {

    private int direction = 0;
    private float color = 0.0f;
    private final Renderer renderer;
    private Mesh mesh;

    public TestGameLogic() {
        renderer = new Renderer();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        /*
        float[] positions = new float[]{
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
        };
        */
        //By the book...
        float[] positions = new float[]{
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
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
        renderer.render(window, mesh);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}