package de.cineaste.android.adapter.series

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.cineaste.android.R
import de.cineaste.android.entity.series.Season
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.series.SeasonViewHolder
import java.util.*

internal class SeasonAdapter(private val context: Context, private val listener: ItemClickListener, series: Series?) : RecyclerView.Adapter<SeasonViewHolder>() {

    private val seasons = ArrayList<Season>()

    init {
        this.seasons.clear()
        if (series != null)
            this.seasons.addAll(series.seasons!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.card_season, parent, false)

        return SeasonViewHolder(view, listener, context)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        holder.assignData(seasons[position])
    }

    override fun getItemCount(): Int {
        return seasons.size
    }
}
