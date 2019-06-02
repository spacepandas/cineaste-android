package de.cineaste.android.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import de.cineaste.android.R

class DetailButtonsSearch : ConstraintLayout {

    private lateinit var toMovieListBtn: Button
    private lateinit var toHistoryButton: Button

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
            super(context, attrs, defStyle) {
        init(context)
    }

    fun setClickListener(toMovieListButton: OnClickListener, toHistoryButton: OnClickListener) {
        toMovieListBtn.setOnClickListener(toMovieListButton)
        this.toHistoryButton.setOnClickListener(toHistoryButton)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.addView(inflater.inflate(R.layout.buttons_movie_search, this, false))

        toMovieListBtn = findViewById(R.id.to_watchlist_button)
        toHistoryButton = findViewById(R.id.history_button)
    }
}