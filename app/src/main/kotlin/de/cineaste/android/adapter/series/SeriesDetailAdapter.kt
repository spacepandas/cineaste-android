package de.cineaste.android.adapter.series

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

import de.cineaste.android.R
import de.cineaste.android.entity.series.Season
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.util.Constants

class SeriesDetailAdapter(
    private var series: Series,
    private val clickListener: ItemClickListener,
    private val state: Int,
    private val listener: SeriesStateManipulationClickListener,
    private val posterClickListener: View.OnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface SeriesStateManipulationClickListener {
        fun onDeleteClicked()
        fun onAddToHistoryClicked()
        fun onAddToWatchClicked()
    }

    init {
        val seasons = mutableListOf<Season>()
        for (season in series.seasons) {
            if (season.seasonNumber > 0) {
                seasons.add(season)
            }
        }
        this.series.seasons = seasons
    }

    fun updateSeries(series: Series) {
        this.series = series
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.series_detail_triangle, parent, false)
                return TriangleViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.series_detail_base, parent, false)
                return BaseViewHolder(view, parent.context, posterClickListener)
            }
            2 -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.series_detail_buttons, parent, false)
                return ButtonsViewHolder(view, state, listener)
            }
            3 -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.series_detail_description, parent, false)
                return DescriptionViewHolder(view, parent.context)
            }
            4 -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.series_detail_seasons, parent, false)
                return SeasonsListViewHolder(view, parent.context, clickListener)
            }
            else -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.series_detail_base, parent, false)
                return BaseViewHolder(view, parent.context, posterClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as TriangleViewHolder).assignData(series)
            1 -> (holder as BaseViewHolder).assignData(series)
            2 -> (holder as ButtonsViewHolder).assignData()
            3 -> (holder as DescriptionViewHolder).assignData(series)
            4 -> (holder as SeasonsListViewHolder).assignData(series)
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private inner class TriangleViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val rating: TextView = itemView.findViewById(R.id.rating)

        internal fun assignData(series: Series) {
            rating.text = series.voteAverage.toString()
        }
    }

    private inner class BaseViewHolder
    internal constructor(
        itemView: View,
        val context: Context,
        val clickListener: View.OnClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.poster)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val seasons: TextView = itemView.findViewById(R.id.seasons)
        private val episodes: TextView = itemView.findViewById(R.id.episodes)
        private val currentStatus: TextView = itemView.findViewById(R.id.currentStatus)
        private val releaseDate: TextView = itemView.findViewById(R.id.releaseDate)
        private val toBeContinued: TextView = itemView.findViewById(R.id.toBeContinued)
        private val resources: Resources = context.resources

        internal fun assignData(series: Series) {
            title.text = series.name
            if (series.releaseDate != null) {
                releaseDate.text = convertDate(series.releaseDate)
                releaseDate.visibility = View.VISIBLE
            } else {
                releaseDate.visibility = View.GONE
            }

            episodes.text =
                resources.getString(R.string.episodes, series.numberOfEpisodes.toString())
            seasons.text = resources.getString(R.string.seasons, series.numberOfSeasons.toString())
            currentStatus.text = resources.getString(
                R.string.currentStatus,
                series.currentNumberOfSeason.toString(),
                series.currentNumberOfEpisode.toString()
            )
            if (series.isInProduction) {
                toBeContinued.visibility = View.VISIBLE
            } else {
                toBeContinued.visibility = View.GONE
            }

            setPoster(series)
            poster.setOnClickListener(clickListener)
        }

        private fun setPoster(series: Series) {
            val posterName = series.posterPath
            val posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", posterName ?: "/")
                .replace("<API_KEY>", context.getString(R.string.movieKey))
            Picasso.get()
                .load(posterUri)
                .resize(273, 410)
                .error(R.drawable.placeholder_poster)
                .into(poster)
        }

        private fun convertDate(date: Date?): String {
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            return simpleDateFormat.format(date)
        }
    }

    private inner class ButtonsViewHolder
    internal constructor(
        itemView: View,
        state: Int,
        private val listener: SeriesStateManipulationClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val deleteBtn: Button = itemView.findViewById(R.id.delete_button)
        private val historyBtn: Button = itemView.findViewById(R.id.history_button)
        private val watchListBtn: Button = itemView.findViewById(R.id.to_watchlist_button)

        init {

            when (state) {
                R.string.searchState -> {
                    deleteBtn.visibility = View.GONE
                    historyBtn.visibility = View.VISIBLE
                    watchListBtn.visibility = View.VISIBLE
                }
                R.string.historyState -> {
                    deleteBtn.visibility = View.VISIBLE
                    historyBtn.visibility = View.GONE
                    watchListBtn.visibility = View.VISIBLE
                }
                R.string.watchlistState -> {
                    deleteBtn.visibility = View.VISIBLE
                    historyBtn.visibility = View.VISIBLE
                    watchListBtn.visibility = View.GONE
                }
            }
        }

        fun assignData() {
            deleteBtn.setOnClickListener { listener.onDeleteClicked() }

            historyBtn.setOnClickListener { listener.onAddToHistoryClicked() }

            watchListBtn.setOnClickListener { listener.onAddToWatchClicked() }
        }
    }

    private inner class DescriptionViewHolder
    internal constructor(
        itemView: View,
        context: Context
    ) : RecyclerView.ViewHolder(itemView) {
        private val description: TextView = itemView.findViewById(R.id.description)
        private val more: TextView = itemView.findViewById(R.id.more)
        private val resources: Resources = context.resources

        internal fun assignData(series: Series) {
            more.setOnClickListener { showCompleteText(series) }
            description.text = getTrimmedDescription(series)
        }

        private fun setDescription(series: Series) {
            val original = series.description
            description.text = if (original == null || original.isEmpty())
                resources.getString(R.string.noDescription)
            else
                original
        }

        private fun showCompleteText(series: Series) {
            setDescription(series)
            more.visibility = View.GONE
        }

        private fun getTrimmedDescription(series: Series): String {
            val original = series.description

            if (original.isNullOrEmpty()) {
                return resources.getString(R.string.noDescription)
            }

            if (original.length <= 200) {
                more.visibility = View.GONE
                return original
            }
            more.visibility = View.VISIBLE
            return original.substring(0, 200) + "..."
        }
    }

    private inner class SeasonsListViewHolder
    internal constructor(
        itemView: View,
        private val context: Context,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.seasonPoster)

        internal fun assignData(series: Series?) {
            recyclerView.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.isNestedScrollingEnabled = false

            val adapter = SeasonAdapter(context, itemClickListener, series)
            recyclerView.adapter = adapter
        }
    }
}
