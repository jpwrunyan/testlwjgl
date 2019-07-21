import display.DisplayObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A world transformation? Is this global? What to call it?
 */
public class Transformation {
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f modelViewMatrix = new Matrix4f();
    private final Matrix4f orthographicMatrix = new Matrix4f();


    public Transformation() {
        //empty constructor
    }

    public final Matrix4f createProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, width / height, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f createModelViewMatrix(DisplayObject displayObject, Matrix4f viewMatrix) {
        Vector3f rotation = displayObject.getRotation();
        modelViewMatrix.set(viewMatrix).translate(displayObject.getPosition())
            .rotateX((float) Math.toRadians(-rotation.x))
            .rotateY((float) Math.toRadians(-rotation.y))
            .rotateZ((float) Math.toRadians(-rotation.z))
            .scale(displayObject.getScale());
        return modelViewMatrix;
    }

    public Matrix4f createViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();

        viewMatrix.identity();
        //First rotate the camera over its position:
        viewMatrix.rotate(
            (float) Math.toRadians(cameraRot.x), new Vector3f(1, 0, 0)
        ).rotate(
            (float) Math.toRadians(cameraRot.y), new Vector3f(0, 1, 0)
        ); //no z rotation apparently.

        //Then do the translation:
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public final Matrix4f getOrthographicMatrix(float left, float right, float bottom, float top) {
        orthographicMatrix.identity();
        orthographicMatrix.setOrtho2D(left, right, bottom, top);
        return orthographicMatrix;
    }

    public Matrix4f getOrthogonalProjModelMatrix(DisplayObject displayObject, Matrix4f orthographicMatrix) {
        Vector3f rotation = displayObject.getRotation(); //should never be rotated
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity().translate(displayObject.getPosition())
            .rotateX((float) Math.toRadians(-rotation.x))
            .rotateY((float) Math.toRadians(-rotation.y))
            .rotateZ((float) Math.toRadians(-rotation.z))
            .scale(displayObject.getScale());
        Matrix4f currentOrthographicMatrix = new Matrix4f(orthographicMatrix);
        currentOrthographicMatrix.mul(modelMatrix);
        return currentOrthographicMatrix;
    }
}
