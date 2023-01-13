import lwjglutils.OGLBuffers;
import lwjglutils.OGLRenderTarget;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import transforms.Mat4Scale;
import transforms.Mat4Transl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class TargetViewer {
    protected final int shaderProgram;
    protected final OGLBuffers buffers;
    protected final int locMat;
    protected final int locLevel;
    protected final OGLRenderTarget renderTarget;

    private static final String[] SHADER_VERT_SRC = {
            "#version 330\n",
            "in vec2 inPosition;",
            "in vec2 inTexCoord;",
            "uniform mat4 matTrans;",
            "out vec2 texCoords;",
            "void main() {",
            "	gl_Position = matTrans * vec4(inPosition , 0.0f, 1.0f);",
            "   texCoords = inTexCoord;",
            "}"
    };

    private static final String[] SHADER_FRAG_SRC = {
            "#version 330\n",
            "in vec2 texCoords;",
            "out vec4 fragColor;",
            "uniform sampler2D colorTexture;",
            "uniform sampler2D depthTexture;",
            "uniform int level;",
            "void main() {",
            " 	fragColor = texture(colorTexture, texCoords);",
            " 	if (level >= 0)",
            " 		fragColor = textureLod(colorTexture, texCoords, level);",
            "  gl_FragDepth = textureLod(depthTexture, texCoords, level).x;",
            "}"
    };

    private OGLBuffers createBuffers() {
        float[] vertexBufferData = {
                0, 0, 0, 0,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 1, 1, 1 };
        int[] indexBufferData = { 0, 1, 2, 3 };

        OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 2),
                new OGLBuffers.Attrib("inTexCoord", 2) };

        return new OGLBuffers(vertexBufferData, attributes, indexBufferData);
    }

    public TargetViewer(OGLRenderTarget renderTarget) {
        this(renderTarget, ShaderUtils.loadProgram(SHADER_VERT_SRC, SHADER_FRAG_SRC, null, null, null, null));
    }

    protected TargetViewer(OGLRenderTarget renderTarget, int shaderProgram) {
        this.renderTarget = renderTarget;
        buffers = createBuffers();
        this.shaderProgram = shaderProgram;
        locMat = glGetUniformLocation(shaderProgram, "matTrans");
        locLevel = glGetUniformLocation(shaderProgram, "level");
    }

    public void view() {
        view(0, -1, -1);
    }

    public void view(int colorTextureIndex) {
        view(colorTextureIndex, -1, -1, 2);
    }

    public void view(int colorTextureIndex, double x, double y) {
        view(colorTextureIndex, x, y, 1.0, 1.0);
    }

    public void view(int colorTextureIndex, double x, double y, double scale) {
        view(colorTextureIndex, x, y, scale, 1.0);
    }
    public void view(int colorTextureIndex, double x, double y, double scale, double aspectXY) {
        view(colorTextureIndex, x, y, scale, aspectXY, -1);
    }

    public void view(int colorTextureIndex, double x, double y, double scale, double aspectXY, int level) {
        if (glIsProgram(shaderProgram)) {
            glPushAttrib(GL_DEPTH_BUFFER_BIT|GL_ENABLE_BIT);
            int[] sp = {'0'};
            glGetIntegerv(GL_CURRENT_PROGRAM, sp);
            glUseProgram(shaderProgram);
            glActiveTexture(GL_TEXTURE0);
            glEnable(GL_TEXTURE_2D);
            glUniformMatrix4fv(locMat, false, ToFloatArray
                    .convert(new Mat4Scale(scale * aspectXY, scale, 1).mul(new Mat4Transl(x, y, 0))));
            glUniform1i(locLevel, level);
            renderTarget.getColorTexture(colorTextureIndex).bind(shaderProgram, "colorTexture", 0);
            renderTarget.getDepthTexture().bind(shaderProgram, "depthTexture", 1);
            buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
            glDisable(GL_TEXTURE_2D);
            glUseProgram(sp[0]);
            glPopAttrib();
        }
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        //if (glIsProgram(shaderProgram))
        //	glDeleteProgram(shaderProgram);
    }


}
