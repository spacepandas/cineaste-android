package de.cineaste.android.viewholder

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class BaseViewHolder protected constructor(
    protected val view: View,
    protected val listener: ItemClickListener?,
    private val context: Context
) : RecyclerView.ViewHolder(view) {

    protected val title: TextView = view.findViewById(R.id.title)
    protected val resources: Resources = context.resources
    protected val poster: ImageView = view.findViewById(R.id.poster_image_view)

    protected fun setPoster(posterName: String?) {
        val posterUri = Constants.POSTER_URI_SMALL
            .replace("<posterName>", posterName ?: "/")
            .replace("<API_KEY>", context.getString(R.string.movieKey))
        Picasso.get()
            .load(posterUri)
            .resize(222, 334)
            .error(R.drawable.placeholder_poster)
            .into(poster)
    }

    protected fun convertDate(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(date)
    }
}
