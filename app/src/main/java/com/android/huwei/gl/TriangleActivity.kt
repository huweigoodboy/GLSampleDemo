package com.android.huwei.gl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.huwei.gl.render.TriangleRender

/**
 * Created by huwei on 17-9-18.
 */
class TriangleActivity : AppCompatActivity() {
    lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(TriangleRender(this))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

    }
}