package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;

import android.opengl.GLSurfaceView;
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
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import static javax.microedition.khronos.opengles.GL10.GL_LINES;
import static javax.microedition.khronos.opengles.GL10.GL_POINTS;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT =2;

    private final Context context;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData ;

    private int uColorLocation;
    private int aPositionLocation;
    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                //Triangle 1
                0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
                //Triangle2
                0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
                //Line 1
                -0.5f, 0f, 0.5f, 0f,
                //Mallets
                0f, -0.25f, 0f, 0.25f

        };
        vertexData =ByteBuffer.allocateDirect(
                        tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.7f, 0.2f, 0.0f);
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
        int program =ShaderHelper.linkProgram(vertexShader, fragmentShader);

        glUseProgram(program);
        if (LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }

        uColorLocation = glGetUniformLocation(program, "u_Color");
        aPositionLocation = glGetAttribLocation(program, "a_Position");

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        // Drawing the dividing line (red)
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6,2); //Make sure this draws the whole line
        //Draw the first mallet blue
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
        //Draw the second mallet red
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9,1);
    }
}
