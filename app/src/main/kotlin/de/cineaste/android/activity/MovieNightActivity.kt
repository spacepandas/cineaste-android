package de.cineaste.android.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.database.dbHelper.UserDbHelper
import de.cineaste.android.entity.User
import de.cineaste.android.entity.movie.MatchingResult
import de.cineaste.android.entity.movie.MovieDto
import de.cineaste.android.entity.movie.NearbyMessage
import de.cineaste.android.fragment.UserInputFragment
import de.cineaste.android.fragment.UserMovieListFragment
import de.cineaste.android.fragment.WatchState
import de.cineaste.android.listener.UserClickListener
import de.cineaste.android.util.MultiList
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class MovieNightActivity : AppCompatActivity(), UserInputFragment.UserNameListener,
    UserClickListener {

    private val nearbyMessagesArrayList = ArrayList<NearbyMessage>()

    private lateinit var searchingFriends: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var nearbyUserRv: RecyclerView

    private val mMessageListener: MessageListener = CineasteMessageListener()
    private lateinit var localNearbyMessage: NearbyMessage
    private val localMovies = mutableListOf<MovieDto>()
    private val globalList = MultiList()

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
        var participatingUser = 0
        if (nearbyMessage.deviceId == localNearbyMessage.deviceId) {
            participatingUser = nearbyUserAdapter.itemCount
        }
        val matchingResult = MatchingResult(participatingUser, nearbyMessage)
        args.putString("entry", Gson().toJson(matchingResult))
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
        localMovies.addAll(localWatchlistMovies.map { MovieDto(it) }.toMutableList())

        localNearbyMessage =
            NearbyMessage(this.getString(R.string.all_movies), myUUid, MultiList(localMovies))

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    public override fun onStart() {
        super.onStart()

        clearDeviceList()
        Nearby.getMessagesClient(this).publish(localNearbyMessage.toNearbyMessage())
        Nearby.getMessagesClient(this).subscribe(mMessageListener)
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

    private fun addUser(nearbyMessage: NearbyMessage) {
        GlobalScope.launch(Main) {
            if (nearbyMessagesArrayList.isNotEmpty()) {
                nearbyMessagesArrayList.remove(nearbyMessagesArrayList.first())
            }
            updateGlobalList()
            nearbyMessagesArrayList.add(
                0,
                NearbyMessage(
                    applicationContext.getString(R.string.all_movies),
                    myUUid,
                    globalList
                )
            )

            if (!nearbyMessagesArrayList.contains(nearbyMessage)) {
                nearbyMessagesArrayList.add(nearbyMessage)
                if (nearbyMessagesArrayList.isNotEmpty()) {
                    nearbyUserRv.visibility = View.VISIBLE
                    searchingFriends.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
                nearbyUserAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateGlobalList() {
        globalList.clear()
        globalList.addAll(localMovies)
        for (nearbyMessage in nearbyMessagesArrayList) {
            globalList.addAll(nearbyMessage.movies.sortedList().map { it.movieDto })
        }
    }

    private inner class CineasteMessageListener : MessageListener() {
        override fun onFound(message: Message) {
            addUser(NearbyMessage.fromMessage(message))
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