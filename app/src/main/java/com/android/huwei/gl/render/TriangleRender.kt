package com.android.huwei.gl.render

import android.content.Context
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20.*
import com.android.huwei.gl.R
import com.android.huwei.gl.util.*

/**
 * Created by huwei on 17-6-1.
 */
class TriangleRender : GLSurfaceView.Renderer {
    private val U_COLOR = "u_Color"
    private val A_POSITION = "a_Position"
    private val POSITION_COMPONENT_COUNT = 2
    private val BYTES_PER_FLOAT = 4
    private var program: Int = 0
    private var uColorLocation: Int = 0
    private var aPositionLocation: Int = 0
    private var context : Context
    var vertexData : FloatBuffer

    constructor(context: Context) {
        this.context = context

        var tableVerticesWithTriangles : FloatArray = floatArrayOf(
                // Triangle 1
                -0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f
        )

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClearColor(0f, 0f, 0f, 0f)

        glUniform4f(uColorLocation, 1f, 0f, 1f, 1f)
        glDrawArrays(GL_TRIANGLES, 0, 6)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)

        val vertexShaderSource : String = readTextFileFromResource(context, R.raw.triangle_vertex_shader)
        val fragmentShaderSource : String = readTextFileFromResource(context, R.raw.triangle_fragment_shader)

        //编译
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)

        //链接
        program = linkProgram(vertexShader, fragmentShader)

        //校验
        validateProgram(program)

        //装载
        glUseProgram(program)

        aPositionLocation = glGetAttribLocation(program, A_POSITION)
        uColorLocation = glGetUniformLocation(program, U_COLOR)

        vertexData.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData)

        glEnableVertexAttribArray(aPositionLocation)
    }
}