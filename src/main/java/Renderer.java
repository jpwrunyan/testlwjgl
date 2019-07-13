import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import utils.ShaderFileUtil;

import java.nio.FloatBuffer;

public class Renderer {

    private ShaderProgram shaderProgram;
    //private int vaoId;
    //private int vboId;

    public Renderer() {

    }

    public void init() throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(
            ShaderFileUtil.loadResource("/vertex.vs")
        );
        shaderProgram.createFragmentShader(
            ShaderFileUtil.loadResource("/fragment.fs")
        );
        shaderProgram.link();

        /*
        Moved to Mesh class.
        //Raw vertices for a triangle
        float[] vertices = new float[]{
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
        };

        //Create the buffer in off-heap memory so that it's accessible by the OpenGL library.
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        //After we have stored the data (with the put method) we need to reset the position of the buffer to the 0 position with the flip method (that is, we say that we’ve finishing writing to it).
        verticesBuffer.put(vertices).flip();

        //Create Vertex Array Object (VAO) and bind it.
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        //Create the Vertex Buffer Object (VBO) and bind it.
        vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        //Define the structure of the data and store it on one of the attribute lists of the VAO.
        //
        //index: Specifies the location where the shader expects this data.
        //size: Specifies the number of components per vertex attribute (from 1 to 4). In this case, we are passing 3D coordinates, so it should be 3.
        //type: Specifies the type of each component in the array, in this case a float.
        //normalized: Specifies if the values should be normalized or not.
        //stride: Specifies the byte offset between consecutive generic vertex attributes. (We will explain it later).
        //offset: Specifies an offset to the first component in the buffer.
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

        //After we are finished with our VBO we can unbind it and the VAO (bind them to 0).
        //Unbind the VBO.
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        //Unbind the VAO.
        GL30.glBindVertexArray(0);

        //We *must* free the off-heap memory that was allocated by the FloatBuffer.
        if (verticesBuffer != null) {
            MemoryUtil.memFree(verticesBuffer);
        }
        */
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Mesh mesh) {
        clear();

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        /*
        Moved to Mesh class.

        //Bind the VAO.
        GL30.glBindVertexArray(vaoId);
        GL30.glEnableVertexAttribArray(0);

        //Draw the vertices.
        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        */

        //Draw the mesh.
        GL30.glBindVertexArray(mesh.getVaoId());
        //Get vertices.
        GL30.glEnableVertexAttribArray(0);
        //Get colors.
        GL30.glEnableVertexAttribArray(1);
        //This used only the vertices:
        //GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
        //This uses vertices with indices:
        GL30.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        //Restore state.
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        /*
        Moved to Mesh#cleanup()
        GL30.glDisableVertexAttribArray(0);

        //Delete the VBO.
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(vboId);

        //Delete the VAO.
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
        */
    }
}