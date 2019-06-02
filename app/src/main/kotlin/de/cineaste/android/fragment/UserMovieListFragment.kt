package de.cineaste.android.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
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
import de.cineaste.android.activity.MovieDetailActivity
import de.cineaste.android.adapter.UserMovieListAdapter
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.entity.movie.MatchingResult
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.listener.MovieSelectClickListener
import de.cineaste.android.network.MovieCallback
import de.cineaste.android.network.MovieLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class UserMovieListFragment : DialogFragment(), ItemClickListener, MovieSelectClickListener {

    private lateinit var movieDbHelper: MovieDbHelper
    private lateinit var toolbar: Toolbar
    private lateinit var movieList: RecyclerView
    private lateinit var userMovieListAdapter: UserMovieListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var result: MatchingResult

    override fun onMovieClickListener(movieId: Long) {
        val selectedMovie = movieDbHelper.readMovie(movieId)

        if (selectedMovie == null) {
            val context = this@UserMovieListFragment.context
            context?.let {
                MovieLoader(it).loadLocalizedMovie(
                    movieId,
                    Locale.getDefault(),
                    (object : MovieCallback {
                        override fun onFailure() {
                        }

                        override fun onSuccess(movie: Movie) {
                            GlobalScope.launch(Dispatchers.Main) { updateMovie(movie) }
                        }
                    })
                )
            }

        } else {
            updateMovie(selectedMovie)
        }
        activity?.onBackPressed()
    }

    private fun updateMovie(movie: Movie) {
        movie.isWatched = true
        movieDbHelper.createOrUpdate(movie)
    }

    override fun onItemClickListener(itemId: Long, views: Array<View>) {
        val activity = activity ?: return

        val intent = Intent(activity, MovieDetailActivity::class.java)
        intent.putExtra(BaseDao.MovieEntry.ID, itemId)
        intent.putExtra(getString(R.string.state), R.string.matchState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                android.util.Pair.create(views[0], "card"),
                android.util.Pair.create(views[1], "poster")
            )
            activity.startActivity(intent, options.toBundle())
        } else {
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.Dialog
        )
        val context = this@UserMovieListFragment.context
        context?.let {
            movieDbHelper = MovieDbHelper.getInstance(it)
        }

        val resultJson = arguments?.getString("entry")
        result = Gson().fromJson(resultJson, MatchingResult::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_movie_list, container, false)

        toolbar = view.findViewById(R.id.toolbar)

        userMovieListAdapter =
            UserMovieListAdapter(
                this,
                result.movies.map { Pair(it, result.participatingUser) },
                this
            )

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
