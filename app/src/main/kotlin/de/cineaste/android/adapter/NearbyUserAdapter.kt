package de.cineaste.android.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.cineaste.android.R
import de.cineaste.android.entity.movie.NearbyMessage

class NearbyUserAdapter(
    private val nearbyMessages: List<NearbyMessage> = listOf(),
    private val context: Context
) : RecyclerView.Adapter<NearbyUserAdapter.ViewHolder>() {
    private val rowLayout: Int = R.layout.card_nearby_user

    override fun getItemCount(): Int {
        return nearbyMessages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nearbyMessage = nearbyMessages[position]
        holder.userName.text = nearbyMessage.userName
        val resources = context.resources
        val count = nearbyMessage.movies.size
        holder.movieCounter.text = resources.getQuantityString(R.plurals.movieCounter, count, count)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName_tv)
        val movieCounter: TextView = itemView.findViewById(R.id.movie_counter_tv)
    }
}
