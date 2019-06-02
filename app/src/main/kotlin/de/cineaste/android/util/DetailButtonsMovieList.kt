package de.cineaste.android.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import de.cineaste.android.R

class DetailButtonsMovieList : ConstraintLayout {

    private lateinit var toHistoryBtn: Button
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

    fun setClickListener(historyButton: OnClickListener, deleteButton: OnClickListener) {
        toHistoryBtn.setOnClickListener(historyButton)
        deleteBtn.setOnClickListener(deleteButton)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.addView(inflater.inflate(R.layout.buttons_movie_list, this, false))

        toHistoryBtn = findViewById(R.id.history_button)
        deleteBtn = findViewById(R.id.delete_button)
    }
}