package com.liflymark.normalschedule.ui.about

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.model.GitProject

class GitListAdapter(
    private val activity: GitListActivity,
    private val gitList: List<GitProject>
): RecyclerView.Adapter<GitListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var gitNameTextView : TextView = view.findViewById(R.id.gitListItemName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_git_list_item, parent, false)

        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            Log.e("GitlistAdapter", position.toString())
            val gitProject = gitList[position]
            activity.openBrowser(gitProject.url)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.gitNameTextView.text = gitList[position].name
    }

    override fun getItemCount(): Int {
        return gitList.size
    }


}