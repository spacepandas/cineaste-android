package de.cineaste.android.viewholder.series

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

import de.cineaste.android.R
import de.cineaste.android.entity.series.Episode

class EpisodeViewHolder(itemView: View, private val onEpisodeWatchStateChangeListener: OnEpisodeWatchStateChangeListener, private val onDescriptionShowToggleListener: OnDescriptionShowToggleListener) : RecyclerView.ViewHolder(itemView) {

    private val episodeNumber: TextView = itemView.findViewById(R.id.episodeNumber)
    private val episodeTitle: TextView = itemView.findViewById(R.id.episodeTitle)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val showDescription: ImageButton = itemView.findViewById(R.id.show_more)
    private val hideDescription: ImageButton = itemView.findViewById(R.id.show_less)
    private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    interface OnEpisodeWatchStateChangeListener {
        fun watchStateChanged(episode: Episode)
    }

    interface OnDescriptionShowToggleListener {
        fun showDescription(showDescription: ImageButton, hideDescription: ImageButton, description: TextView)
        fun hideDescription(showDescription: ImageButton, hideDescription: ImageButton, description: TextView)
    }

    fun assignData(episode: Episode) {
        episodeNumber.text = episode.episodeNumber.toString()
        episodeTitle.text = episode.name
        description.text = episode.description
        checkBox.isChecked = episode.isWatched

        showDescription.setOnClickListener { onDescriptionShowToggleListener.showDescription(showDescription, hideDescription, description) }

        hideDescription.setOnClickListener {
            android.util.Log.d("mgr", "hide")
            onDescriptionShowToggleListener.hideDescription(showDescription, hideDescription, description)
        }

        checkBox.setOnClickListener { onEpisodeWatchStateChangeListener.watchStateChanged(episode) }
    }
}
