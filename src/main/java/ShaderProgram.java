import graphics.model.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 1. Create an OpenGL program.
 * 2. Load the vertex and fragment shader code files.
 * 3. For each shader, create a new shader program and specify its type (vertex, fragment).
 * 4. Compile the shader.
 * 5. Attach the shader to the program.
 * 6. Link the program.
 */
public class ShaderProgram {

    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> uniforms = new HashMap<>();
    /**
     * Creates the OpenGL program.
     * @throws Exception
     */
    public ShaderProgram() throws Exception {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create shader program!");
        }
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL30.glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".attenuation.constant");
        createUniform(uniformName + ".attenuation.linear");
        createUniform(uniformName + ".attenuation.exponent");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void setUniform(String uniformName, Matrix4f value) {
        //Put the matrix into a float buffer.
        //Use MemoryStack because the size of the data is small and not used beyond this method.
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(16);
            value.get(floatBuffer);
            GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, floatBuffer);
        }
    }

    public void setUniform(String uniformName, int value) {
        GL30.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        GL30.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f color) {
        GL30.glUniform3f(uniforms.get(uniformName), color.x, color.y, color.z);
    }

    public void setUniform(String uniformName, Vector4f color) {
        GL30.glUniform4f(uniforms.get(uniformName), color.x, color.y, color.z, color.w);
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        setUniform(uniformName + ".color", directionalLight.color);
        setUniform(uniformName + ".direction", directionalLight.direction);
        setUniform(uniformName + ".intensity", directionalLight.intensity);
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".color", pointLight.color);
        setUniform(uniformName + ".position", pointLight.position);
        setUniform(uniformName + ".intensity", pointLight.intensity);
        Attenuation attenuation = pointLight.attenuation;
        setUniform(uniformName + ".attenuation.constant", attenuation.constant);
        setUniform(uniformName + ".attenuation.linear", attenuation.linear);
        setUniform(uniformName + ".attenuation.exponent", attenuation.exponent);
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.ambient);
        setUniform(uniformName + ".diffuse", material.diffuse);
        setUniform(uniformName + ".specular", material.specular);
        setUniform(uniformName + ".hasTexture", material.hasTexture());
        setUniform(uniformName + ".reflectance", material.reflectance);
    }

    /**
     * Load a vertex shader.
     * @param shaderCode
     * @throws Exception
     */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    /**
     * Load a fragment shader.
     * @param shaderCode
     * @throws Exception
     */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    /**
     * Create a new shader program (by type), compile it, and attach it to the program.
     * @param shaderCode The shader code to compile
     * @param shaderType The type of shader program (vertex or fragment)
     * @return The id of the shader program created
     * @throws Exception
     */
    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * Link the program.
     * This is called after all shaders have been compiled and attached to the program.
     *
     * @throws Exception
     */
    public void link() throws Exception {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
        //Once the shader program has been linked, the compiled vertex and fragment shaders can be freed up.
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
        }
        //Validate the program from debugging. Remove later.
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            throw new Exception("Warning validating shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }

    /**
     * Activate the program for rendering.
     */
    public void bind() {
        GL20.glUseProgram(programId);
    }

    /**
     * Deactivate the program from rendering.
     */
    public void unbind() {
        GL20.glUseProgram(0);
    }

    /**
     * Free all resources once no longer needed.
     */
    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }
}
