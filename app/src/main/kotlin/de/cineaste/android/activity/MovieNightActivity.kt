package de.cineaste.android.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import de.cineaste.android.R
import de.cineaste.android.adapter.NearbyUserAdapter
import de.cineaste.android.database.NearbyMessageHandler
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.database.dbHelper.UserDbHelper
import de.cineaste.android.entity.User
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.movie.MovieDto
import de.cineaste.android.entity.movie.NearbyMessage
import de.cineaste.android.fragment.UserInputFragment
import de.cineaste.android.fragment.UserMovieListFragment
import de.cineaste.android.fragment.WatchState
import de.cineaste.android.listener.UserClickListener
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import kotlin.collections.ArrayList

class MovieNightActivity : AppCompatActivity(), UserInputFragment.UserNameListener,
    UserClickListener {

    private val nearbyMessagesArrayList = ArrayList<NearbyMessage>()

    private lateinit var startBtn: Button
    private lateinit var searchingFriends: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var nearbyUserRv: RecyclerView

    private val mMessageListener: MessageListener = CineasteMessageListener()
    private lateinit var localNearbyMessage: NearbyMessage

    private lateinit var nearbyUserAdapter: NearbyUserAdapter

    private var currentUser: User? = null
    private lateinit var userDbHelper: UserDbHelper
    private lateinit var timeOut: Runnable

    private val myUUid: String
        get() = getUUID(
            getSharedPreferences(
                applicationContext.packageName, Context.MODE_PRIVATE
            )
        )

    override fun onUserClickListener(nearbyMessage: NearbyMessage) {
        val dialog = UserMovieListFragment()
        val args = Bundle()
        args.putString("entry", Gson().toJson(nearbyMessage))
        dialog.arguments = args
        dialog.show(supportFragmentManager, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_night)
        userDbHelper = UserDbHelper.getInstance(this)
        currentUser = userDbHelper.user

        initViews()

        if (currentUser == null) {
            startDialogFragment()
        } else {
            buildLocalMessage()
        }
        initializeTimeout()

        timedOut()
    }

    public override fun onPause() {
        super.onPause()
        nearbyUserRv.removeCallbacks(timeOut)
    }

    private fun timedOut() {
        nearbyUserRv.removeCallbacks(timeOut)
        nearbyUserRv.postDelayed(timeOut, 45000)
    }

    private fun initializeTimeout() {
        timeOut = Runnable {
            val snackBar = Snackbar
                .make(nearbyUserRv, R.string.no_friends_found_try_again, Snackbar.LENGTH_LONG)
            snackBar.show()
        }
    }

    private fun initViews() {
        nearbyUserRv = findViewById(R.id.nearbyUser_rv)
        startBtn = findViewById(R.id.start_btn)
        startBtn.visibility = View.GONE
        searchingFriends = findViewById(R.id.searchingFriends)
        progressBar = findViewById(R.id.progressBar)

        nearbyMessagesArrayList.clear()
        nearbyUserAdapter = NearbyUserAdapter(nearbyMessagesArrayList, this, this)

        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        nearbyUserRv.layoutManager = llm
        nearbyUserRv.itemAnimator = DefaultItemAnimator()
        nearbyUserRv.adapter = nearbyUserAdapter

        val divider = ContextCompat.getDrawable(nearbyUserRv.context, R.drawable.divider)
        val itemDecor = DividerItemDecoration(
            nearbyUserRv.context,
            llm.orientation
        )

        divider?.let {
            itemDecor.setDrawable(it)
        }
        nearbyUserRv.addItemDecoration(itemDecor)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle(R.string.startMovieNight)
    }

    private fun buildLocalMessage() {
        val watchlistDbHelper = MovieDbHelper.getInstance(this)
        val localWatchlistMovies = watchlistDbHelper.readMoviesByWatchStatus(WatchState.WATCH_STATE)
        val localMovies = transFormMovies(localWatchlistMovies)
        currentUser?.userName?.let { userName ->
            localNearbyMessage = NearbyMessage(userName, myUUid, localMovies)
        }
    }

    private fun startDialogFragment() {
        UserInputFragment().show(supportFragmentManager, "")
    }

    override fun onFinishUserDialog(userName: String) {
        if (userName.isNotEmpty()) {
            currentUser = User(userName)
            currentUser?.let { user ->
                userDbHelper.createUser(user)
            }
        }
        buildLocalMessage()
    }

    private fun transFormMovies(movies: List<Movie>): List<MovieDto> {
        val movieDtos = ArrayList<MovieDto>()
        for (movie in movies) {
            movieDtos.add(MovieDto(movie))
        }
        return movieDtos
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    public override fun onStart() {
        super.onStart()

        clearDeviceList()
        Nearby.getMessagesClient(this).publish(localNearbyMessage.toNearbyMessage())
        Nearby.getMessagesClient(this).subscribe(mMessageListener)

        startBtn.setOnClickListener {
            NearbyMessageHandler.clearMessages()
            NearbyMessageHandler.addMessage(localNearbyMessage)
            NearbyMessageHandler.addMessages(nearbyMessagesArrayList)
            val intent = Intent(this@MovieNightActivity, ResultActivity::class.java)
            startActivity(intent)
            finish()
        }
        // todo remove after testing
        addInitalUsers()
    }

    private fun addInitalUsers() {
        val messages = mutableListOf<NearbyMessage>()
        messages.add(
            NearbyMessage(
                "Test1", "1234567890", mutableListOf(
                    MovieDto(
                        120,
                        "/zn5dEU1ygVeCEtFgttvujW3dCUj.jpg",
                        "Der Herr der Ringe - Die Gefährten",
                        Date(),
                        9.0,
                        120
                    ),
                    MovieDto(
                        121,
                        "/cMa7haLxqVe4fWNORPIq6fGdjys.jpg",
                        "Der Herr der Ringe - Die zwei Türme",
                        Date(),
                        9.5,
                        124
                    ),
                    MovieDto(
                        122,
                        "/viKyV73yclmtmpnJmCkfQsni9aa.jpg",
                        "Der Herr der Ringe - Die Rückkehr des Königs",
                        Date(),
                        8.0,
                        125
                    )
                )
            )
        )
        messages.add(
            NearbyMessage(
                "Test2", "12345678901", mutableListOf(
                    MovieDto(
                        253,
                        "/dgabslxiRr0lLSarFASWrf9Ihqv.jpg",
                        "James Bond 007 - Leben und sterben lassen",
                        Date(),
                        7.0,
                        110
                    ),
                    MovieDto(
                        272,
                        "/bDpi3sixe9YwWB5KTPwmjhqZQGk.jpg",
                        "Batman Begins",
                        Date(),
                        4.0,
                        12
                    )
                )
            )
        )

        GlobalScope.launch(Main) {
            for (message in messages) {
                if (!nearbyMessagesArrayList.contains(message)) {
                    nearbyMessagesArrayList.add(message)
                    if (nearbyMessagesArrayList.isNotEmpty()) {
                        startBtn.visibility = View.VISIBLE
                        nearbyUserRv.visibility = View.VISIBLE
                        searchingFriends.visibility = View.GONE
                        progressBar.visibility = View.GONE
                    }
                    nearbyUserAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    public override fun onStop() {
        Nearby.getMessagesClient(this).unpublish(localNearbyMessage.toNearbyMessage())
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener)

        super.onStop()
    }

    private fun clearDeviceList() {
        GlobalScope.launch(Main) {
            nearbyMessagesArrayList.clear()
            nearbyUserAdapter.notifyDataSetChanged()
        }
    }

    private inner class CineasteMessageListener : MessageListener() {
        override fun onFound(message: Message) {
            GlobalScope.launch(Main) {
                val nearbyMessage = NearbyMessage.fromMessage(message)
                if (!nearbyMessagesArrayList.contains(nearbyMessage)) {
                    nearbyMessagesArrayList.add(nearbyMessage)
                    if (nearbyMessagesArrayList.isNotEmpty()) {
                        startBtn.visibility = View.VISIBLE
                        nearbyUserRv.visibility = View.VISIBLE
                        searchingFriends.visibility = View.GONE
                        progressBar.visibility = View.GONE
                    }
                    nearbyUserAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onLost(message: Message) {
            // do not remove messages when connection lost
        }
    }

    companion object {

        private const val KEY_UUID = "key_uuid"

        private fun getUUID(sharedPreferences: SharedPreferences): String {
            var uuid = sharedPreferences.getString(KEY_UUID, "")
            if (uuid.isNullOrEmpty()) {
                uuid = UUID.randomUUID().toString()
                sharedPreferences.edit().putString(KEY_UUID, uuid).apply()
            }
            return uuid
        }
    }
}