package de.cineaste.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.cineaste.android.R
import de.cineaste.android.entity.movie.NearbyMessage
import de.cineaste.android.listener.UserClickListener

class NearbyUserAdapter(
    private val nearbyMessages: List<NearbyMessage> = listOf(),
    private val context: Context,
    private val listener: UserClickListener
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
        holder.assignData(nearbyMessage)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val userName: TextView = itemView.findViewById(R.id.userName_tv)
        private val movieCounter: TextView = itemView.findViewById(R.id.movie_counter_tv)

        fun assignData(nearbyMessage: NearbyMessage) {
            userName.text = nearbyMessage.userName
            val resources = context.resources
            val count = nearbyMessage.movies.size
            movieCounter.text = resources.getQuantityString(R.plurals.movieCounter, count, count)
            itemView.setOnClickListener {
                listener.onUserClickListener(nearbyMessage)
            }
        }

        override fun onClick(v: View?) {
            listener.onUserClickListener(nearbyMessages[adapterPosition])
        }
    }
}
