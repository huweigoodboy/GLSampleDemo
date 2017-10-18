package com.android.huwei.gl.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.opengl.Matrix.*
import com.android.huwei.gl.R
import com.android.huwei.gl.data.VertexArray
import com.android.huwei.gl.util.*
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by huwei on 17-6-1.
 */
class CubeRender : GLSurfaceView.Renderer {
    private val U_COLOR = "u_Color"
    private val A_POSITION = "a_Position"
    private val U_MATRIX = "u_Matrix"
    private val POSITION_COMPONENT_COUNT = 3
    private var program: Int = 0
    private var uColorLocation: Int = 0
    private var uMatrixLocation: Int = 0
    private var aPositionLocation: Int = 0
    private var context: Context

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    val indexArray: ByteBuffer

    val points: FloatArray = floatArrayOf(
            // Triangle 1
            -1f, 1f, 1f, //top-left near
            1f, 1f, 1f, //top-right near
            -1f, -1f, 1f, //bottom-left near
            1f, -1f, 1f, //bottom-right near
            -1f, 1f, -1f, //top-left far
            1f, 1f, -1f, //top-right far
            -1f, -1f, -1f, //bottom-left far
            1f, -1f, -1f //bottom-right near
    )

    val faceColors: FloatArray = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            1f, 0f, 1f, 0f,
            1f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f
    )

    constructor(context: Context) {
        this.context = context
        points.forEachIndexed { index, value ->
            points.set(index, value * 0.2f)
        }

        indexArray = ByteBuffer.allocate(6 * 6).put(byteArrayOf(
                // Front
                1, 3, 0,
                0, 3, 2,

                // Back
                4, 6, 5,
                5, 6, 7,

                // Left
                0, 2, 4,
                4, 2, 6,

                // Right
                5, 7, 1,
                1, 7, 3,

                // Top
                5, 1, 4,
                4, 1, 0,

                // Bottom
                6, 2, 7,
                7, 2, 3
        ))
        indexArray.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        // Assign the matrix
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)

        glUniform4fv(uColorLocation, 4, faceColors, 0)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        Matrix.perspectiveM(projectionMatrix, 0, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

        /*
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2f);
        */

        setIdentityM(modelMatrix, 0)

        translateM(modelMatrix, 0, 0f, 0f, -2f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        val vertexShaderSource: String = readTextFileFromResource(context, R.raw.cube_vertex_shader)
        val fragmentShaderSource: String = readTextFileFromResource(context, R.raw.cube_fragment_shader)

        //编译
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)

        //链接
        program = linkProgram(vertexShader, fragmentShader)

        //校验
        validateProgram(program)

        //装载
        GLES20.glUseProgram(program)

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR)
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX)

        var verticeArray = VertexArray(points)
        verticeArray.setVertexAttribPointer(0, aPositionLocation, POSITION_COMPONENT_COUNT, 0)
    }
}