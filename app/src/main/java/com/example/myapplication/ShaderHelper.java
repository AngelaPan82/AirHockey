package com.example.myapplication;

import static android.opengl.GLES20.*;
import static java.util.regex.Pattern.compile;

import android.util.Log;


public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderOjbectId = glCreateShader(type);
        if (shaderOjbectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }
        //Pass in the shader source
        glShaderSource(shaderOjbectId, shaderCode);
        // Compile the shader
        glCompileShader(shaderOjbectId);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderOjbectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object
            glDeleteShader(shaderOjbectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed");
            }
            return 0;
        }
        return shaderOjbectId;

    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programOjbectId = glCreateProgram();
        if (programOjbectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program");
            }
            return 0;
        }
        //Attaching the vertex shader to the program
        glAttachShader(programOjbectId, vertexShaderId);
        //Attaching the fragment shader to the program
        glAttachShader(programOjbectId, fragmentShaderId);
        //Link the two shaders together into a program
        glLinkProgram(programOjbectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programOjbectId, GL_LINK_STATUS, linkStatus, 0);
        if (LoggerConfig.ON) {
            //Print the program info log to the Android log output.
            Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programOjbectId));
        }
        //verify the link status
        if (linkStatus[0] == 0) {
            //if it failed, delete the program object.
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed");
            }
            return 0;
        }
        return programOjbectId;
    }

    public static boolean validateProgram(int programOjbectId) {
        glValidateProgram(programOjbectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programOjbectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" +
                glGetProgramInfoLog(programOjbectId));

        return validateStatus[0] != 0;

    }
}


