package com.android.huwei.gl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import com.android.huwei.gl.render.CubeRender

/**
 * Created by huwei on 17-9-27.
 */
class CubeActivity : AppCompatActivity() {
    private val TOUCH_SCALE_FACTOR = 180.0f / 320
    private var mPreviousX: Float = 0.toFloat()
    private var mPreviousY: Float = 0.toFloat()
    lateinit var mRenderer : CubeRender
    lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)

        mRenderer = CubeRender(this)

        glSurfaceView.setEGLContextClientVersion(3)
        glSurfaceView.setRenderer(mRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        glSurfaceView.setOnTouchListener(View.OnTouchListener { v, e ->
            val x = e.getX()
            val y = e.getY()
            when (e.getAction()) {
                MotionEvent.ACTION_MOVE -> {
                    val dx = x - mPreviousX
                    val dy = y - mPreviousY
                    mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR
                    mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR
                    mRenderer.handleTouch()
                    glSurfaceView.requestRender()
                }
            }
            mPreviousX = x
            mPreviousY = y
            true
        })
    }
}