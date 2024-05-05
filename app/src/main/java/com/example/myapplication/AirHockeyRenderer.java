package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import static javax.microedition.khronos.opengles.GL10.GL_LINES;
import static javax.microedition.khronos.opengles.GL10.GL_LINE_LOOP;
import static javax.microedition.khronos.opengles.GL10.GL_POINTS;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final Context context;

    private FloatBuffer vertexData;
    private int program;
    private int aColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private final float[] temp = new float [16];
    private final float [] modelMatrix = new float [16];
    private final float [] projectionMatrix = new float[16];
    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
               //Order of coordinates: X, Y, R, G, B
                // Triangle fan
                0f,    0f, 0f,    1f,   1f,   1f,
                -0.5f, -0.8f, 0f,   0.7f, 0.7f, 0.7f,
                -0.5f,  0.8f, 0f,   0.7f, 0.7f, 0.7f,
                0.5f,  0.8f, 0f,    0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0f,    0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0f,    0.7f, 0.7f, 0.7f

                // Line 1
                -0.5f, 0f, 0f,  1f, 0f, 0f,
                0.5f, 0f, 0f,  1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f,  0f, 0f, 1f,
                0f,  0.4f, 0f,  1f, 0f, 0f


        };

        vertexData =ByteBuffer.allocateDirect(
                        tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.4f, 0.4f, 0.5f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(
                context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader .readTextFileFromResource(
                context, R.raw.simple_fragment_shader);
        //compiling the shaders from our Renderer Class
        // An OpenGL program is simply one vertex shader and one fragment shader linked together into a single object. Vertex shaders and fragment shaders always go together.
        // 1. Without fragment shader, OpenGL wouldn't know how to draw the fragments that make up each point, line, and triangle;
        // 2. Without a vertex shader, OpenGl wouldn't know where to draw these fragments.
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program =ShaderHelper.linkProgram(vertexShader, fragmentShader);

        glUseProgram(program);
        if (LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }

        aColorLocation = glGetUniformLocation(program, "u_Color");
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        aPositionLocation = glGetAttribLocation(program, "a_Position");

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");

    }
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

       // this creates perspective projection with field of vision of 45 degrees.
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2f);

        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0,  temp.length);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }
    @Override
    public void onDrawFrame(GL10 gl) {
            // Clear the rendering surface.
            glClear(GL_COLOR_BUFFER_BIT);

            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

            glDrawArrays(GL_LINES, 6, 2);

            // Draw the first mallet blue.
            glDrawArrays(GL_POINTS, 8, 1);

            // Draw the second mallet red.
            glDrawArrays(GL_POINTS, 9, 1);



        }
}
