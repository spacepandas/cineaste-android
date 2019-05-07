package de.cineaste.android.viewholder.series

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

import de.cineaste.android.R
import de.cineaste.android.entity.series.Episode

class EpisodeViewHolder(
    itemView: View,
    private val onEpisodeWatchStateChangeListener: OnEpisodeWatchStateChangeListener,
    private val onDescriptionShowToggleListener: OnDescriptionShowToggleListener,
    private val context: Context
) : RecyclerView.ViewHolder(itemView) {

    private val episodeTitle: TextView = itemView.findViewById(R.id.episodeTitle)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val showDescription: ImageButton = itemView.findViewById(R.id.show_more)
    private val hideDescription: ImageButton = itemView.findViewById(R.id.show_less)
    private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    interface OnEpisodeWatchStateChangeListener {
        fun watchStateChanged(episode: Episode)
    }

    interface OnDescriptionShowToggleListener {
        fun showDescription(
            showDescription: ImageButton,
            hideDescription: ImageButton,
            description: TextView
        )

        fun hideDescription(
            showDescription: ImageButton,
            hideDescription: ImageButton,
            description: TextView
        )
    }

    fun assignData(episode: Episode) {
        episodeTitle.text = episode.name
        checkBox.isChecked = episode.isWatched

        if (episode.description.isNullOrBlank()) {
            description.text = context.getString(R.string.noDescription)
        } else {
            description.text = episode.description
        }

        showDescription.setOnClickListener {
            onDescriptionShowToggleListener.showDescription(
                showDescription,
                hideDescription,
                description
            )
        }

        hideDescription.setOnClickListener {
            onDescriptionShowToggleListener.hideDescription(
                showDescription,
                hideDescription,
                description
            )
        }

        checkBox.setOnClickListener { onEpisodeWatchStateChangeListener.watchStateChanged(episode) }
    }
}
