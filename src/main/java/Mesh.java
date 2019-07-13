import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private final int vaoId;
    private final int posVboId;
    private final int indVboId;
    private final int colorsVboId;
    private final int vertexCount;

    public Mesh(float[] positions, int[] indices, float[] colors) {
        vertexCount = indices.length;
        //vertexCount = positions.length / 3;

        //Create the VBA.
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        //Create the position VBO.
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        verticesBuffer.put(positions).flip();
        posVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, posVboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, verticesBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        //Unbind the VBO.
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(verticesBuffer);

        //Create the indices VBO.
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        indVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indVboId);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL30.GL_STATIC_DRAW);
        //We don't have to attribute the pointer?
        //Unbind the VBO...?
        //GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(indicesBuffer);

        //Create the colors VBO.
        FloatBuffer colorsBuffer = MemoryUtil.memAllocFloat(colors.length);
        colorsBuffer.put(colors).flip();
        colorsVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, colorsVboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 0, 0);
        //We don't unbind the VBO here either?
        MemoryUtil.memFree(colorsBuffer);

        //Unbind the VAO
        GL30.glBindVertexArray(0);

    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanup() {
        GL30.glDisableVertexAttribArray(0);

        //Delete VBO.
        //Unbinding is probably redundant.
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(posVboId);
        //We don't unbind the indices???
        GL30.glDeleteBuffers(indVboId);

        GL30.glDeleteBuffers(colorsVboId);

        //Delete the VAO.
        //Unbinding is probably redundant.
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }
}
