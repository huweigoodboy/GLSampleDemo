package com.android.huwei.gl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import android.util.Log


/**
 * Created by huwei on 17-6-1.
 */

const val TAG : String = "ShaderHelper"

/**
 * 编译顶点着色器脚本
 */
fun compileVertexShader(shaderCode : String) : Int {
    return compileShader(GL_VERTEX_SHADER, shaderCode)
}

/**
 * 编译片源着色器脚本
 */
fun compileFragmentShader(shaderCode: String) : Int{
    return compileShader(GL_FRAGMENT_SHADER, shaderCode)
}

/**
 * 编译gl脚本
 */
fun compileShader(type : Int, shaderCode: String) : Int {
    //创建shader对象
    var shaderObjId = glCreateShader(type)
    if (shaderObjId == 0) {
        Log.w(TAG, "Could not create new shader.")
        return 0
    }

    //绑定shader
    glShaderSource(shaderObjId, shaderCode)

    //编译shader
    glCompileShader(shaderObjId)

    //获取编译状态
    var compileStatus = IntArray(1)
    glGetShaderiv(shaderObjId, GL_COMPILE_STATUS, compileStatus, 0)

    Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
            + glGetShaderInfoLog(shaderObjId));

    //检测编译状态
    if (compileStatus[0] == 0) {
        //编译失败 清理shader
        glDeleteShader(shaderObjId);

        Log.w(TAG, "Compilation of shader failed.");
        return 0;
    }

    return shaderObjId
}

/**
 * 链接程序
 */
fun linkProgram(vertexShaderId : Int, fragmentShaderId : Int) : Int {
    //创建program对象
    var programObjId = glCreateProgram()

    if (programObjId == 0) {
        Log.w(TAG, "Could not create new program");

        return 0;
    }

    //attach顶点着色器
    glAttachShader(programObjId, vertexShaderId)
    //attach片元着色器
    glAttachShader(programObjId, fragmentShaderId)

    glLinkProgram(programObjId)

    val linkStatus = getProgramiv(programObjId, GL_LINK_STATUS)
    Log.v(TAG, "Results of linking program:\n"
            + glGetProgramInfoLog(programObjId));

    //验证编译状态
    if (linkStatus == 0) {
        //清理program对象
        glDeleteProgram(programObjId);

        Log.w(TAG, "Linking of program failed.");
        return 0;
    }

    return programObjId
}

/**
 * 验证程序
 */
fun validateProgram(programObjId : Int) : Boolean {
    glValidateProgram(programObjId)

    val validateStatus = getProgramiv(programObjId, GL_VALIDATE_STATUS)
    Log.v(TAG, "Results of validating program: " + validateStatus
            + "\nLog:" + glGetProgramInfoLog(programObjId))
    return validateStatus != 0
}

fun getProgramiv(program : Int, pname : Int) : Int {
    val status = IntArray(1)
    glGetProgramiv(program, pname, status, 0)
    return status[0]
}

/**
 * Loads a cubemap texture from the provided resources and returns the
 * texture ID. Returns 0 if the load failed.

 * @param context
 * *
 * @param cubeResources
 * *            An array of resources corresponding to the cube map. Should be
 * *            provided in this order: left, right, bottom, top, front, back.
 * *
 * @return
 */
fun loadCubeMap(context: Context, cubeResources: IntArray): Int {
    val textureObjectIds = IntArray(1)
    glGenTextures(1, textureObjectIds, 0)  //生成纹理

    if (textureObjectIds[0] == 0) {
        if (LoggerConfig.ON) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.")
        }
        return 0
    }
    val options = BitmapFactory.Options()
    options.inScaled = false
    val cubeBitmaps = arrayOfNulls<Bitmap>(6)
    for (i in 0..5) {
        cubeBitmaps[i] = BitmapFactory.decodeResource(context.resources,
                cubeResources[i], options)

        if (cubeBitmaps[i] == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + cubeResources[i]
                        + " could not be decoded.")
            }
            glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }
    }
    // Linear filtering for minification and magnification
    glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0])

    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0)
    texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0)

    texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0)
    texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0)

    texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0)
    texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0)
    glBindTexture(GL_TEXTURE_2D, 0)

    for (bitmap in cubeBitmaps) {
        bitmap!!.recycle()
    }

    return textureObjectIds[0]
}
