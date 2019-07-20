package graphics.model;

import org.joml.Vector3f;

/**
 * This class is bugged. See:
 * https://gamedev.stackexchange.com/questions/173916/i-need-help-debugging-this-spotlight-implementation
 */
@Deprecated
public class SpotLight {

    public PointLight pointLight;
    public Vector3f direction;
    public float cutoffAngleRadians;

    public SpotLight(PointLight pointLight, Vector3f direction, float cutoffAngleRadians) {
        this.pointLight = pointLight;
        this.direction = direction;
        this.cutoffAngleRadians = 0.9999254f;
        setCutoffAngle(this.cutoffAngleRadians);
    }

    /**
     * Clone constructor
     * @param spotLight
     */
    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.pointLight), new Vector3f(spotLight.direction), spotLight.cutoffAngleRadians);
        System.out.println(cutoffAngleRadians);
    }

    public final void setCutoffAngle(float cutoffAngleDegrees) {
        cutoffAngleRadians = (float) Math.cos(Math.toRadians(cutoffAngleDegrees));
    }
}
