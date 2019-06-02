package de.cineaste.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import de.cineaste.android.R
import de.cineaste.android.adapter.UserMovieListAdapter
import de.cineaste.android.entity.movie.MatchingResult
import de.cineaste.android.entity.movie.Movie

class UserMovieListFragment : DialogFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var movieList: RecyclerView
    private lateinit var userMovieListAdapter: UserMovieListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var result: MatchingResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.Dialog
        )
        val test = arguments?.getString("entry")
        result = Gson().fromJson(test, MatchingResult::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_movie_list, container, false)

        toolbar = view.findViewById(R.id.toolbar)

        userMovieListAdapter = UserMovieListAdapter(result.movies.map {
            Movie(
                it.id,
                it.posterPath,
                it.title,
                it.runtime,
                it.voteAverage,
                0,
                "",
                false,
                null,
                it.releaseDate
            )
        })

        movieList = view.findViewById(R.id.recycler_view)
        layoutManager = LinearLayoutManager(context)
        movieList.layoutManager = layoutManager
        movieList.adapter = userMovieListAdapter
        val divider = ContextCompat.getDrawable(movieList.context, R.drawable.divider)
        val itemDecor = DividerItemDecoration(
            movieList.context,
            layoutManager.orientation
        )
        divider?.let {
            itemDecor.setDrawable(it)
        }
        movieList.addItemDecoration(itemDecor)
        movieList.setHasFixedSize(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = result.listTitle
        toolbar.setOnMenuItemClickListener {
            dismiss()
            true
        }
    }
}
