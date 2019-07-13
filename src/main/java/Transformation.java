import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A world transformation? Is this global? What to call it?
 */
public class Transformation {
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f worldMatrix = new Matrix4f();

    public Transformation() {
        //empty constructor
    }

    public final Matrix4f createProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, width / height, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f createWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
        worldMatrix.identity().translate(offset)
            .rotateX((float) Math.toRadians(rotation.x))
            .rotateY((float) Math.toRadians(rotation.y))
            .rotateZ((float) Math.toRadians(rotation.z))
            .scale(scale);
        return worldMatrix;
    }
}
