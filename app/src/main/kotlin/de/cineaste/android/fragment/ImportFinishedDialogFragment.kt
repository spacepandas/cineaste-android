package de.cineaste.android.fragment

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import de.cineaste.android.R
import de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.Companion.MOVIE_COUNT
import de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.Companion.SERIES_COUNT

class ImportFinishedDialogFragment : DialogFragment() {

    private lateinit var movies: TextView
    private lateinit var series: TextView

    private var movieCount: Int = 0
    private var seriesCount: Int = 0

    interface BundleKeyWords {
        companion object {
            const val MOVIE_COUNT = "movieCount"
            const val SERIES_COUNT = "seriesCount"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            movieCount = bundle.getInt(MOVIE_COUNT, -1)
            seriesCount = bundle.getInt(SERIES_COUNT, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_import_finised_dialog, container, false)

        val button = view.findViewById<Button>(R.id.ok)

        button.setOnClickListener { dialog?.dismiss() }

        movies = view.findViewById(R.id.movie)
        series = view.findViewById(R.id.series)

        fillTextViews()

        dialog?.setCancelable(false)

        return view
    }

    private fun fillTextViews() {
        if (movieCount < 0) {
            movies.setText(R.string.importedMoviesFailed)
        } else {
            movies.text = getString(R.string.importedMovies, movieCount.toString())
        }
        if (seriesCount < 0) {
            series.setText(R.string.importedSeriesFailed)
        } else {
            series.text = getString(R.string.importedSeries, seriesCount.toString())
        }
    }
}
