package de.cineaste.android.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import java.util.Locale

import de.cineaste.android.util.Constants
import de.cineaste.android.R
import de.cineaste.android.database.NearbyMessageHandler
import de.cineaste.android.entity.movie.MatchingResult

class ResultAdapter(
        private val results: MutableList<MatchingResult> = mutableListOf(),
        private val listener: OnMovieSelectListener?) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val rowLayout: Int = R.layout.card_result

    interface OnMovieSelectListener {
        fun onMovieSelectListener(position: Int)
    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(rowLayout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.assignData(results[position], NearbyMessageHandler.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val moviePoster: ImageView = itemView.findViewById(R.id.poster_image_view)
        private val watchedButton: Button = itemView.findViewById(R.id.history_button)
        internal val title: TextView = itemView.findViewById(R.id.title)
        internal val counter: TextView = itemView.findViewById(R.id.movie_counter_tv)

        fun assignData(matchingResult: MatchingResult, resultCounter: Int) {
            val posterPath = matchingResult.posterPath
            val posterUri = Constants.POSTER_URI_SMALL
                    .replace("<posterName>", posterPath ?: "/")
                    .replace("<API_KEY>", context.getString(R.string.movieKey))
            Picasso.with(context)
                    .load(Uri.parse(posterUri))
                    .resize(222, 334)
                    .error(R.drawable.placeholder_poster)
                    .into(moviePoster)
            watchedButton.setOnClickListener(this)
            title.text = matchingResult.title
            counter.text = String.format(Locale.getDefault(), "%d/%d", matchingResult.counter, resultCounter)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            listener?.onMovieSelectListener(position)
            results.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
