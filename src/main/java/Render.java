import org.lwjgl.opengl.GL11;
import utils.ShaderFileUtil;

public class Render {

    private ShaderProgram shaderProgram;

    public void init() throws Exception {
        shaderProgram = new ShaderProgram();
        /*
        shaderProgram.createVertexShader(
            ShaderFileUtil.loadResource("/vertex.vs")
        );

         */
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
}
