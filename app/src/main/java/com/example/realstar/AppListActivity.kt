package com.example.realstar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_app_list.*
import kotlinx.android.synthetic.main.app_list_item_layout.view.*

class AppListActivity : AppCompatActivity() {

    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        sa = (application as SkyApp).sa

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
        showbut.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
//                showFloat()
            } else {
                getPermis()
            }
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

        assign.setImageResource(
            if (action.line.isEmpty()) android.R.drawable.ic_input_add
            else android.R.drawable.ic_delete
        )
    }
}

class AppListAdapter : RecyclerView.Adapter<ViewH>() {
    var type = EndAction.Type.APP

    private var lists = HashMap<EndAction.Type, List<EndAction>>().apply {
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
