import display.DisplayObject;
import display.Overlay2d;
import graphics.model.DirectionalLight;
import graphics.model.PointLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import utils.ShaderFileUtil;

public class Renderer {
    private static final float FOV = (float) Math.toRadians(60f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000f;
    //private Matrix4f projectionMatrix;
    private final Transformation transformation = new Transformation();
    private final float specularPower = 10f;

    private ShaderProgram shaderProgram;
    private ShaderProgram hudShaderProgram;
    //private int vaoId;
    //private int vboId;

    public Renderer() {

    }

    private static Matrix4f createProjectionMatrix(int width, int height) {
        float aspectRatio = width / height;
        return new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public void init(Window window) throws Exception {


        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(
            ShaderFileUtil.loadResource("/vertex.vs")
        );
        shaderProgram.createFragmentShader(
            ShaderFileUtil.loadResource("/fragment.fs")
        );
        shaderProgram.link();


        //Initializes uniforms for projection and world matrices to be accessed by shader programs' native code.
        shaderProgram.createUniform("projectionMatrix");
        //shaderProgram.createUniform("worldMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        //What is texture_sampler being used for anymore?
        shaderProgram.createUniform("texture_sampler");

        //Create uniform for material.
        shaderProgram.createMaterialUniform("material");

        //Create lighting related uniforms.
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createUniform("specularPower");

        shaderProgram.createDirectionalLightUniform("directionalLight");

        shaderProgram.createPointLightUniform("pointLight");

        //shaderProgram.createUniform("color");
        //shaderProgram.createUniform("useColorFlag");
        window.setClearColor(0, 0, 0, 0);


        setupHudShader();
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(
            ShaderFileUtil.loadResource("/ui.vs")
        );
        hudShaderProgram.createFragmentShader(
            ShaderFileUtil.loadResource("/ui.fs")
        );
        hudShaderProgram.link();

        // Create uniforms for Orthographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("color");
        hudShaderProgram.createUniform("hasTexture");
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void render(
        Window window,
        Camera camera,
        DisplayObject[] displayObjects,
        Vector3f ambientLight,
        DirectionalLight directionalLight,
        PointLight pointLight,
        Overlay2d hud
    ) {
        clear();

        //Just a test that matrix transform works.
        //camera.movePosition(0.001f, 0.001f, 0.01f);

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        renderScene(window, camera, displayObjects, ambientLight, directionalLight, pointLight);
        renderHud(window, hud);
    }

    private void renderHud(Window window, Overlay2d hud) {
        hudShaderProgram.bind();
        //orthogonal projection
        Matrix4f orthographicMatrix = transformation.getOrthographicMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (DisplayObject displayObject : hud.getDisplayObjects()) {
            //Matrix4f modelViewMatrix = transformation.createModelViewMatrix(displayObject, viewMatrix);

            // Set orthographic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrthogonalProjModelMatrix(displayObject, orthographicMatrix);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("color", displayObject.getMesh().getMaterial().getAmbientColor());
            hudShaderProgram.setUniform("hasTexture", displayObject.getMesh().getMaterial().hasTexture());
            // Render the mesh for this HUD item
            displayObject.getMesh().render();
        }
        hudShaderProgram.unbind();
    }

    private void renderScene(
        Window window,
        Camera camera,
        DisplayObject[] displayObjects,
        Vector3f ambientLight,
        DirectionalLight directionalLight,
        PointLight pointLight
    ) {

        shaderProgram.bind();

        //Update projection matrix:
        Matrix4f projectionMatrix = transformation.createProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Update view matrix:
        Matrix4f viewMatrix = transformation.createViewMatrix(camera);

        //Update light uniforms:
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        //Get a copy of the directional light and transform its position to view coordinates.
        DirectionalLight currentDirectionalLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currentDirectionalLight.direction, 0);
        dir.mul(viewMatrix);
        currentDirectionalLight.direction = new Vector3f(dir.x, dir.y, dir.z);
        shaderProgram.setUniform("directionalLight", currentDirectionalLight);

        //Get a copy of the point light object and transform its position to view coordinates.
        PointLight currentPointLight = new PointLight(pointLight); //Clone constructor
        Vector3f lightPos = currentPointLight.position;
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        //This is updating the value in currentPointLight.position.
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currentPointLight);

        //Set a global texture for now.
        //Is this still used???
        shaderProgram.setUniform("texture_sampler", 0);

        //Render each display.DisplayObject:
        for (DisplayObject displayObject : displayObjects) {
            /*
            //Set world matrix for this item:
            //This name doesn't feel right.
            Matrix4f worldMatrix = transformation.createWorldMatrix(
                displayObject.getPosition(),
                displayObject.getRotation(),
                displayObject.getScale()
            );
            shaderProgram.setUniform("worldMatrix", worldMatrix);
            */
            //Set the model view matrix for this display.DisplayObject.
            Matrix4f modelViewMatrix = transformation.createModelViewMatrix(displayObject, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            //shaderProgram.setUniform("color", displayObject.getMesh().getColor());
            //shaderProgram.setUniform("useColorFlag", displayObject.getMesh().isTextured() ? 0 : 1);
            shaderProgram.setUniform("material", displayObject.getMesh().getMaterial());
            //Render the mesh for this display.DisplayObject.
            displayObject.getMesh().render();
        }

        /*
        Moved to graphics.Mesh class.

        //Bind the VAO.
        GL30.glBindVertexArray(vaoId);
        GL30.glEnableVertexAttribArray(0);

        //Draw the vertices.
        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        */

        /*
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
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        */
        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        /*
        Moved to graphics.Mesh#cleanup()
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