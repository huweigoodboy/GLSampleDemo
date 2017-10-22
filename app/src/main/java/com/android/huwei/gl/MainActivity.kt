package com.android.huwei.gl

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var mRecyclerView: RecyclerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRecyclerView = RecyclerView(this)
        setContentView(mRecyclerView)

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MyAdapter()
        mRecyclerView.adapter = adapter

        //init Data
        var clickItem: ClickItem = ClickItem()
        clickItem.title = "Triangle"
        clickItem.listener = View.OnClickListener { startActivity(Intent(this, TriangleActivity::class.java)) }
        adapter.data.add(clickItem)

        clickItem = ClickItem()
        clickItem.title = "Rectangle"
        clickItem.listener = View.OnClickListener { startActivity(Intent(this, RectangleActivity::class.java)) }
        adapter.data.add(clickItem)

        clickItem = ClickItem()
        clickItem.title = "Cube"
        clickItem.listener = View.OnClickListener { startActivity(Intent(this, CubeActivity::class.java)) }
        adapter.data.add(clickItem)

        clickItem = ClickItem()
        clickItem.title = "CubeWithChartlet"
        clickItem.listener = View.OnClickListener { startActivity(Intent(this, CubeWithChartletActivity::class.java)) }
        adapter.data.add(clickItem)
    }

    inner class MyAdapter : RecyclerView.Adapter<ViewHolder>() {
        val data: ArrayList<ClickItem> = ArrayList()

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val clickItem: ClickItem = data.get(position)
            holder!!.textView.text = clickItem.title
            holder.textView.setOnClickListener {
                clickItem.listener!!.onClick(holder.textView)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            return ViewHolder(Button(this@MainActivity))
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    class ViewHolder(view: Button) : RecyclerView.ViewHolder(view) {
        val textView: Button = view
    }
}
