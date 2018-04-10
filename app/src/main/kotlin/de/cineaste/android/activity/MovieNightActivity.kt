package de.cineaste.android.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
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
import de.cineaste.android.fragment.WatchState
import java.util.*

class MovieNightActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, UserInputFragment.UserNameListener {

    private val nearbyMessagesArrayList = ArrayList<NearbyMessage>()

    private var mGoogleApiClient: GoogleApiClient? = null

    private var startBtn: Button? = null
    private var searchingFriends: TextView? = null
    private var progressBar: ProgressBar? = null
    private var nearbyUserRv: RecyclerView? = null

    private var mMessageListener: MessageListener? = null
    private var localNearbyMessage: NearbyMessage? = null

    private var nearbyUserAdapter: NearbyUserAdapter? = null

    private var currentUser: User? = null
    private var userDbHelper: UserDbHelper? = null
    private var timeOut: Runnable? = null

    private val myUUid: String
        get() = getUUID(getSharedPreferences(
                applicationContext.packageName, Context.MODE_PRIVATE))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_night)
        userDbHelper = UserDbHelper.getInstance(this)
        currentUser = userDbHelper!!.user

        initViews()

        if (currentUser == null) {
            startDialogFragment()
        } else {
            buildLocalMessage()
        }
        mMessageListener = MyMessageListener()
        initializeTimeout()
        buildGoogleApiClient()

        timedOut()
    }

    public override fun onPause() {
        super.onPause()
        nearbyUserRv!!.removeCallbacks(timeOut)
    }

    private fun timedOut() {
        nearbyUserRv!!.removeCallbacks(timeOut)
        nearbyUserRv!!.postDelayed(timeOut, 45000)
    }

    private fun initializeTimeout() {
        timeOut = Runnable {
            val snackbar = Snackbar
                    .make(nearbyUserRv!!, R.string.no_friends_found_try_again, Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }

    private fun initViews() {
        nearbyUserRv = findViewById(R.id.nearbyUser_rv)
        startBtn = findViewById(R.id.start_btn)
        startBtn!!.visibility = View.GONE
        searchingFriends = findViewById(R.id.searchingFriends)
        progressBar = findViewById(R.id.progressBar)

        nearbyUserAdapter = NearbyUserAdapter(nearbyMessagesArrayList, this)

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        nearbyUserRv!!.layoutManager = llm
        nearbyUserRv!!.itemAnimator = DefaultItemAnimator()
        nearbyUserRv!!.adapter = nearbyUserAdapter
        initToolbar()

    }

    private fun buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return
        }
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build()
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
        localNearbyMessage = NearbyMessage(currentUser!!.userName!!, myUUid, localMovies)
    }

    private fun startDialogFragment() {
        UserInputFragment().show(supportFragmentManager, "")
    }


    override fun onFinishUserDialog(userName: String) {
        if (!userName.isEmpty()) {
            currentUser = User(userName)
            userDbHelper!!.createUser(currentUser!!)
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
        mGoogleApiClient!!.connect()

        startBtn!!.setOnClickListener {
            NearbyMessageHandler.clearMessages()
            NearbyMessageHandler.addMessage(localNearbyMessage!!)
            NearbyMessageHandler.addMessages(nearbyMessagesArrayList)
            val intent = Intent(this@MovieNightActivity, ResultActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public override fun onStop() {
        if (mGoogleApiClient!!.isConnected && !isChangingConfigurations) {
            unsubscribe()
            unpublish()

            mGoogleApiClient!!.disconnect()
        }
        super.onStop()
    }

    private fun unsubscribe() {
        if (!mGoogleApiClient!!.isConnected) {
            if (!mGoogleApiClient!!.isConnecting) {
                mGoogleApiClient!!.connect()
            }
        } else {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback { status ->
                        if (!status.isSuccess) {
                            logAndShowSnackbar("Could not unsubscribe, status = $status")
                        }
                    }
        }
    }

    private fun unpublish() {
        if (!mGoogleApiClient!!.isConnected) {
            if (!mGoogleApiClient!!.isConnecting) {
                mGoogleApiClient!!.connect()
            }
        } else {
            Nearby.Messages.unpublish(mGoogleApiClient, localNearbyMessage!!.toNearbyMessage())
                    .setResultCallback { status ->
                        if (!status.isSuccess) {
                            logAndShowSnackbar("Could not unpublish, status = $status")
                        }
                    }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        subscribe()
        publish()
    }

    private fun subscribe() {
        //trying to subscribe
        if (!mGoogleApiClient!!.isConnected) {
            if (!mGoogleApiClient!!.isConnecting) {
                mGoogleApiClient!!.connect()
            }
        } else {
            clearDeviceList()
            val options = SubscribeOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(object : SubscribeCallback() {

                    }).build()

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                    .setResultCallback { status ->
                        if (!status.isSuccess) {
                            logAndShowSnackbar("Could not subscribe, status = $status")
                        }
                    }
        }
    }

    private fun clearDeviceList() {
        runOnUiThread {
            nearbyMessagesArrayList.clear()
            nearbyUserAdapter!!.notifyDataSetChanged()
        }
    }

    private fun publish() {
        if (!mGoogleApiClient!!.isConnected) {
            if (!mGoogleApiClient!!.isConnecting) {
                mGoogleApiClient!!.connect()
            }
        } else {
            val options = PublishOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(object : PublishCallback() {

                    }).build()
            Nearby.Messages.publish(mGoogleApiClient, localNearbyMessage!!.toNearbyMessage(), options)
                    .setResultCallback { status ->
                        if (!status.isSuccess) {
                            logAndShowSnackbar("Could not publish, status = $status")
                        }
                    }
        }
    }

    override fun onConnectionSuspended(cause: Int) {
        // GoogleApiClient connection suspended
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //connection to GoogleApiClient failed
        logAndShowSnackbar("Exception while connecting to Google Play services: " + connectionResult.errorMessage!!)
    }

    private fun logAndShowSnackbar(text: String) {
        Log.w(MovieNightActivity::class.java.simpleName, text)
        val container = findViewById<View>(R.id.recycler_view)
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show()
        }
    }

    private inner class MyMessageListener : MessageListener() {
        override fun onFound(message: Message?) {
            runOnUiThread {
                if (!nearbyMessagesArrayList.contains(NearbyMessage.fromMessage(message!!))) {
                    nearbyMessagesArrayList.add(NearbyMessage.fromMessage(message))
                    if (nearbyMessagesArrayList.size > 0) {
                        startBtn!!.visibility = View.VISIBLE
                        nearbyUserRv!!.visibility = View.VISIBLE
                        searchingFriends!!.visibility = View.GONE
                        progressBar!!.visibility = View.GONE
                    }
                    nearbyUserAdapter!!.notifyDataSetChanged()
                }
            }
        }

        override fun onLost(message: Message?) {
            //do not remove messages when connection lost
        }
    }

    companion object {

        private const val TTL_IN_SECONDS = 3 * 60
        private const val KEY_UUID = "key_uuid"

        private val PUB_SUB_STRATEGY = Strategy.Builder()
                .setTtlSeconds(TTL_IN_SECONDS).build()

        private fun getUUID(sharedPreferences: SharedPreferences): String {
            var uuid = sharedPreferences.getString(KEY_UUID, "")
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString()
                sharedPreferences.edit().putString(KEY_UUID, uuid).apply()
            }
            return uuid
        }
    }
}
