package de.cineaste.android.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import de.cineaste.android.R

class DetailButtonsHistory : ConstraintLayout {

    private lateinit var toMovieListBtn: Button
    private lateinit var deleteBtn: Button

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

    fun setClickListener(toMovieListButton: OnClickListener, deleteButton: OnClickListener) {
        toMovieListBtn.setOnClickListener(toMovieListButton)
        deleteBtn.setOnClickListener(deleteButton)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.addView(inflater.inflate(R.layout.buttons_history_list, this, false))

        toMovieListBtn = findViewById(R.id.to_watchlist_button)
        deleteBtn = findViewById(R.id.delete_button)
    }
}