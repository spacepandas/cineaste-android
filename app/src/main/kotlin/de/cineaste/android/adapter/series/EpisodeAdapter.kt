package de.cineaste.android.adapter.series

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.cineaste.android.R
import de.cineaste.android.entity.series.Episode
import de.cineaste.android.viewholder.series.EpisodeViewHolder
import java.util.*

class EpisodeAdapter(episodes: List<Episode>, private val onEpisodeWatchStateChangeListener: EpisodeViewHolder.OnEpisodeWatchStateChangeListener, private val onDescriptionShowToggleListener: EpisodeViewHolder.OnDescriptionShowToggleListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val episodes = ArrayList<Episode>()

    init {
        this.episodes.clear()
        this.episodes.addAll(episodes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.card_episode, parent, false)

        return EpisodeViewHolder(view, onEpisodeWatchStateChangeListener, onDescriptionShowToggleListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as EpisodeViewHolder).assignData(episodes[position])
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    fun update(episodes: List<Episode>) {
        this.episodes.clear()
        this.episodes.addAll(episodes)
    }
}
