package de.cineaste.android.viewholder.series

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import de.cineaste.android.R
import de.cineaste.android.entity.series.Season
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.util.Constants

class SeasonViewHolder(
    private val view: View,
    private val itemClickListener: ItemClickListener?,
    private val context: Context
) : RecyclerView.ViewHolder(view) {

    private val poster: ImageView = view.findViewById(R.id.poster_image_view)
    private val seasonNumber: TextView = view.findViewById(R.id.season)
    private val numberOfEpisodes: TextView = view.findViewById(R.id.episodes)
    private val releaseDate: TextView = view.findViewById(R.id.releaseDate)
    private val resources: Resources = context.resources

    fun assignData(season: Season) {
        seasonNumber.text =
            resources.getString(R.string.currentSeason, season.seasonNumber.toString())
        numberOfEpisodes.text =
            resources.getString(R.string.episodes, season.episodeCount.toString())
        if (season.releaseDate == null) {
            releaseDate.visibility = View.GONE
        } else {
            releaseDate.text = convertDate(season.releaseDate)
        }

        setMoviePoster(season)

        view.setOnClickListener { view ->
            itemClickListener?.onItemClickListener(season.id, arrayOf(view, poster))
        }
    }

    private fun setMoviePoster(season: Season) {
        val posterName = season.posterPath
        val posterUri = Constants.POSTER_URI_SMALL
            .replace("<posterName>", posterName ?: "/")
            .replace("<API_KEY>", context.getString(R.string.movieKey))
        Picasso.get()
            .load(posterUri)
            .resize(342, 513)
            .error(R.drawable.placeholder_poster)
            .into(poster)
    }

    private fun convertDate(date: Date?): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(date)
    }
}
