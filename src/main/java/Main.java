import graphics.Mesh;
import graphics.Texture;
import graphics.model.Attenuation;
import graphics.model.Material;
import graphics.model.PointLight;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import utils.OBJLoader;

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
    private static final float CAMERA_POS_STEP = 0.1f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Camera camera = new Camera();
    private final Vector3f cameraPosChange = new Vector3f(0, 0, 0);

    private final Renderer renderer = new Renderer();
    DisplayObject[] displayObjects;

    private int direction = 0;
    private float color = 0.0f;

    private Vector3f ambientLight;
    private PointLight pointLight;

    public TestGameLogic() {
        //Empty constructor.
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        Mesh cubeMesh = OBJLoader.loadMesh("/cube.obj");
        Material material = new Material(new Texture("/grassblock.png"), 1f);
        cubeMesh.setMaterial(material);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 10.0f;
        Attenuation attenuation = new Attenuation();
        attenuation.constant = 0.0f;
        attenuation.linear = 0.0f;
        attenuation.exponent = 1.0f;

        pointLight = new PointLight(lightColor, lightPosition, lightIntensity, attenuation);
        displayObjects = new DisplayObject[]{
            new DisplayObject(
                cubeMesh
            ).setPosition(0, 0, -10),
            new DisplayObject(
                cubeMesh
            ).setPosition(10, 10, -20)
        };
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            direction = 1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }

        cameraPosChange.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraPosChange.z = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraPosChange.z = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraPosChange.x = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraPosChange.x = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
            cameraPosChange.y = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
            cameraPosChange.y = 1;
        }


        if (window.isKeyPressed(GLFW.GLFW_KEY_I)) {
            pointLight.position.z -= 0.1f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_K)) {
            pointLight.position.z += 0.1f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_J)) {
            pointLight.position.x -= 0.1f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_L)) {
            pointLight.position.x += 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if (color < 0) {
            color = 0.0f;
        }
        //Update camera position.
        camera.movePosition(
            cameraPosChange.x * CAMERA_POS_STEP,
            cameraPosChange.y * CAMERA_POS_STEP,
            cameraPosChange.z * CAMERA_POS_STEP
        );

        //Update based on mouse.
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotation = mouseInput.getDisplayVector();
            camera.moveRotation(rotation.x * MOUSE_SENSITIVITY, rotation.y * MOUSE_SENSITIVITY, 0);
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
        // Update rotation angle
        for (DisplayObject displayObject : displayObjects) {
            float rotation = displayObject.getRotation().x + 1.5f;
            if (rotation > 360) {
                rotation -= 360;
            }
            displayObject.setRotation(rotation, rotation, rotation);
        }
        //render(Window window, Camera camera, DisplayObject[] displayObjects, Vector3f ambientLight, PointLight pointLight) {

        renderer.render(window, camera, displayObjects, ambientLight, pointLight);
        //renderer.render(window, mesh2);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}