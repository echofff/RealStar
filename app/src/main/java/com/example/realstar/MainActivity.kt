package com.example.realstar

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_list_item_layout.view.*

lateinit var sa: SkyAttr

class MainActivity : AppCompatActivity() {

    lateinit var sky: Sky
    lateinit var adapter: AppListAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sa = (application as SkyApp).sa
//        sky = Sky(this, windowManager)

        showbut.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
//                showFloat()
            } else {
                getPermis()
            }
        }

        startService(Intent(baseContext, SkyBack::class.java))

        sa.actions = ActionManager(this)

        adapter = AppListAdapter()
        lists.layoutManager = LinearLayoutManager(this)
        lists.adapter = adapter

        show_launcher.setOnClickListener {
            adapter.type = EndAction.Type.APP
            adapter.notifyDataSetChanged()
        }
        show_all.setOnClickListener {
            adapter.type = EndAction.Type.ACT
            adapter.notifyDataSetChanged()
        }
    }


    private fun getPermis() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 1)
    }
}

class ViewH(root: ConstraintLayout) : RecyclerView.ViewHolder(root) {
    private var icon = root.item_icon
    private var title = root.item_title
    private var line = root.item_gesture
    private lateinit var action: EndAction
    private var assign = root.item_assign
    private var info = root.item_info

    init {
        root.item_assign.setOnClickListener {
            if (action.line.isEmpty()) {
                sa.actions.readToAssign = action
                Log.d("asdfasdf", "on click")
            } else {
                sa.actions.delete(action)
            }
        }
        icon.setOnClickListener {
            sa.actions.launchApp(action)
        }
    }

    fun reset(action: EndAction) {
        this.action = action
        icon.setImageDrawable(action.drawable)
        title.text = action.title
        line.text = action.line
        info.text = action.name.removePrefix(action.pack)
        if (action.line.isEmpty())
            assign.setImageResource(android.R.drawable.ic_input_add)
        else
            assign.setImageResource(android.R.drawable.ic_delete)
    }
}

class AppListAdapter() : RecyclerView.Adapter<ViewH>() {
    var type = EndAction.Type.APP

    var lists = HashMap<EndAction.Type, List<EndAction>>().apply {
        EndAction.Type.values()
            .forEach { type -> put(type, sa.actions.actions.filter { it.type == type }) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewH =
        ViewH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.app_list_item_layout, parent, false) as ConstraintLayout
        )

    override fun getItemCount(): Int = lists[type]!!.size

    override fun onBindViewHolder(holder: ViewH, position: Int) =
        holder.reset(lists[type]!![position])
}
