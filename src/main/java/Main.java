import display.DisplayObject;
import display.Overlay2d;
import graphics.Mesh;
import graphics.Texture;
import graphics.model.*;
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

    private float lightAngle = -90f;
    private DirectionalLight sun;

    private PointLight pointLight;

    private Overlay2d hud;

    public TestGameLogic() {
        //Empty constructor.
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        Mesh cubeMesh = OBJLoader.loadMesh("/cube.obj");
        Material material = new Material(new Texture("/grassblock.png"), 0.9f);
        cubeMesh.setMaterial(material);

        //ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        ambientLight = new Vector3f(0.003f, 0.003f, 0.003f);

        sun = new DirectionalLight(new Vector3f(0.2f, 0.1f, 0.1f), new Vector3f(-1, 0, 0), 2);

        Vector3f lightColor = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, -7);
        float lightIntensity = 5.0f;
        Attenuation attenuation = new Attenuation();
        attenuation.constant = 0.0f;
        attenuation.linear = 0.1f;
        attenuation.exponent = 0.9f;
        pointLight = new PointLight(lightColor, lightPosition, lightIntensity, attenuation);

        displayObjects = new DisplayObject[]{
            new DisplayObject(
                cubeMesh
            ).setPosition(0, 0, -10),
            new DisplayObject(
                cubeMesh
            ).setPosition(5, 0, -10),
            new DisplayObject(
                cubeMesh
            ).setPosition(10, 10, -20)
        };

        hud = new Hud("DEMO");
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

        //Update directional light direction, intensity, and color.
        lightAngle += 1f;
        double angleRadians = Math.toRadians(lightAngle);
        sun.direction.x = (float) Math.sin(angleRadians);
        sun.direction.y = (float) Math.cos(angleRadians);

        //Update based on mouse.
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotation = mouseInput.getDisplayVector();
            camera.moveRotation(rotation.x * MOUSE_SENSITIVITY, rotation.y * MOUSE_SENSITIVITY, 0);
            
            // Update HUD compass
            ((Hud) hud).rotateCompass(camera.getRotation().y);
        }

        // Update rotation angle
        for (DisplayObject displayObject : displayObjects) {
            float rotation = displayObject.getRotation().x + 0.1f;
            if (rotation > 360) {
                rotation -= 360;
            }
            displayObject.setRotation(rotation, rotation, rotation);
        }
    }

    @Override
    public void render(Window window) {
        window.setClearColor(color, color, color, 0.0f);
        ((Hud) hud).updateSize(window);
        renderer.render(window, camera, displayObjects, ambientLight, sun, pointLight, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (DisplayObject displayObject : displayObjects) {
            displayObject.getMesh().cleanup();
        }
        hud.cleanup();
    }
}

class Hud implements Overlay2d {
    private static final int FONT_COLS = 16;
    private static final int FONT_ROWS = 16;
    private static final String FONT_TEXTURE = "/ExportedFont.png";

    private final DisplayObject[] displayObjects;
    private final TextObject statusText;
    private final DisplayObject compass;

    public Hud(String status) throws Exception {
        statusText = new TextObject(status, FONT_TEXTURE, FONT_COLS, FONT_ROWS);

        //Create compass
        Mesh compassMesh = OBJLoader.loadMesh("/compass.obj");
        Material material = new Material(null, 0.9f);
        compassMesh.setMaterial(material);
        compass = new DisplayObject(compassMesh);
        compass.setScale(40f);
        compass.setRotation(0, 0, 180);
        displayObjects = new DisplayObject[]{statusText, compass};
    }

    @Override
    public DisplayObject[] getDisplayObjects() {
        return displayObjects;
    }

    public void updateSize(Window window) {
        statusText.setPosition(10f, window.getHeight() - 50, 0);
        compass.setPosition(window.getWidth() - 50, 50, 0);
    }

    public void rotateCompass(float angle) {
        compass.setRotation(0, 0, 180 + angle);
    }
}