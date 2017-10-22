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
class CubeWithChartletRender : GLSurfaceView.Renderer {
    private val A_POSITION = "a_Position"
    private val U_MATRIX = "u_Matrix"
    private val U_TEXTURE_UNIT = "u_TextureUnit"
    private val POSITION_COMPONENT_COUNT = 3
    private var program: Int = 0
    private var uMatrixLocation: Int = 0
    private var aPositionLocation: Int = 0
    private var uTextureUnitLocation : Int = 0
    private var context: Context

    private var cubeTexture: Int = 0

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    var mAngleX : Float = 0.toFloat()
    var mAngleY : Float = 0.toFloat()

    var ratio : Float = 0.toFloat()


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

    val cubeChartletArray : IntArray = intArrayOf(
            R.mipmap.cube1,
            R.mipmap.cube2,
            R.mipmap.cube3,
            R.mipmap.cube4,
            R.mipmap.cube5,
            R.mipmap.cube6
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 1f, 10f)
        setIdentityM(modelMatrix, 0)

        translateM(modelMatrix, 0, 0f, 0f, -2f)
        rotateM(modelMatrix, 0, mAngleX, 0f, 1f, 0f)
        rotateM(modelMatrix, 0, mAngleY, 1f, 0f, 0f)


        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)

        aPositionLocation = glGetAttribLocation(program, A_POSITION)
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX)

        var verticeArray = VertexArray(points)
        verticeArray.setVertexAttribPointer(0, aPositionLocation, POSITION_COMPONENT_COUNT, 0)

        // Assign the matrix
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeTexture)
        glUniform1i(uTextureUnitLocation, 0)

        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        ratio = width.toFloat() / height.toFloat()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glEnable(GLES20.GL_CULL_FACE) //开启剔除功能，默认剔除背面
        glFrontFace(GL_CW)  //设置顺时针是正面

        val vertexShaderSource: String = readTextFileFromResource(context, R.raw.cubechartlet_vertex_shader)
        val fragmentShaderSource: String = readTextFileFromResource(context, R.raw.cubechartlet_fragment_shader)

        //编译
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)

        //链接
        program = linkProgram(vertexShader, fragmentShader)

        //校验
        validateProgram(program)

        //装载
        GLES20.glUseProgram(program)

        cubeTexture = loadCubeMap(context, cubeChartletArray)
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT)
    }

    fun handleTouch() {
    }
}