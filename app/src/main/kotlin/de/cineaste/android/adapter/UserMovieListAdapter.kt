package de.cineaste.android.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.entity.movie.MatchingResultMovie
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.listener.MovieSelectClickListener
import de.cineaste.android.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserMovieListAdapter(
    val listener: ItemClickListener,
    private val dataSet: List<Pair<MatchingResultMovie, Int>>,
    private val movieSelectClickListener: MovieSelectClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_movie_matched, parent, false)

        return UserMovieListViewHolder(view, listener, movieSelectClickListener, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserMovieListViewHolder).assignData(
            dataSet[position].first,
            dataSet[position].second
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class UserMovieListViewHolder(
        itemView: View,
        private val listener: ItemClickListener,
        private val movieSelectClickListener: MovieSelectClickListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(itemView) {

        private val movieReleaseDate: TextView = itemView.findViewById(R.id.movieReleaseDate)
        private val movieRuntime: TextView = itemView.findViewById(R.id.movieRuntime)
        private val movieCounter: TextView = itemView.findViewById(R.id.movie_counter)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val poster: ImageView = itemView.findViewById(R.id.poster_image_view)
        private val watchNow = itemView.findViewById<Button>(R.id.watch_now)
        private val resources: Resources = context.resources


        fun assignData(movie: MatchingResultMovie, counter: Int) {
            itemView.setOnClickListener {
                listener.onItemClickListener(
                    movie.id,
                    arrayOf(itemView, poster)
                )
            }

            watchNow.setOnClickListener {
                movieSelectClickListener.onMovieClickListener(movie.id)
            }
            if (counter < 1) {
                movieCounter.visibility = View.GONE
            } else {
                movieCounter.text =
                    context.resources.getString(R.string.movie_counter, movie.counter, counter)
            }

            title.text = movie.title
            val releaseDate = movie.releaseDate

            if (releaseDate != null) {
                movieReleaseDate.text = convertDate(releaseDate)
                movieReleaseDate.visibility = View.VISIBLE
            } else {
                movieReleaseDate.visibility = View.INVISIBLE
            }

            movieRuntime.text = resources.getString(R.string.runtime, movie.runtime)
            setPoster(movie.posterPath)
        }

        private fun setPoster(posterName: String?) {
            val posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", posterName ?: "/")
                .replace("<API_KEY>", context.getString(R.string.movieKey))
            Picasso.get()
                .load(posterUri)
                .resize(222, 334)
                .error(R.drawable.placeholder_poster)
                .into(poster)
        }

        private fun convertDate(date: Date): String {
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            return simpleDateFormat.format(date)
        }
    }
}