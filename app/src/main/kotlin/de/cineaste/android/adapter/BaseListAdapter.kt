package de.cineaste.android.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable

import de.cineaste.android.fragment.WatchState
import de.cineaste.android.listener.ItemClickListener

abstract class BaseListAdapter constructor(
    val context: Context,
    val displayMessage: DisplayMessage,
    val listener: ItemClickListener,
    val state: WatchState
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    protected abstract val internalFilter: Filter
    protected abstract val layout: Int

    interface DisplayMessage {
        fun showMessageIfEmptyList()
    }

    protected abstract fun createViewHolder(v: View): RecyclerView.ViewHolder
    protected abstract fun assignDataToViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    override fun getFilter(): Filter {
        return internalFilter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)
        return createViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        assignDataToViewHolder(holder, position)
    }
}
