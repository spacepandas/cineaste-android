package de.cineaste.android.activity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonParser
import de.cineaste.android.R
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.network.NetworkCallback
import de.cineaste.android.network.NetworkResponse
import de.cineaste.android.util.DateAwareGson
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type

abstract class AbstractSearchActivity : AppCompatActivity(), ItemClickListener {

    internal val gson = DateAwareGson.gson
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private var searchText: String? = null
    protected abstract val layout: Int
    protected abstract val listAdapter: RecyclerView.Adapter<*>
    protected abstract val listType: Type

    val networkCallback: NetworkCallback
        get() = object : NetworkCallback {
            override fun onFailure() {
                GlobalScope.launch(Main) { showNetworkError() }
            }

            override fun onSuccess(response: NetworkResponse) {
                val responseObject = JsonParser.parseReader(response.responseReader).asJsonObject
                val json = responseObject.get("results").toString()
                val listType = listType

                GlobalScope.launch(Main) { getRunnable(json, listType).run() }
            }
        }

    protected abstract fun getIntentForDetailActivity(itemId: Long): Intent
    protected abstract fun initAdapter()
    protected abstract fun getSuggestions()
    protected abstract fun searchRequest(searchQuery: String)
    protected abstract fun getRunnable(json: String, listType: Type): Runnable

    override fun onItemClickListener(itemId: Long, views: Array<View>) {
        val intent = getIntentForDetailActivity(itemId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                Pair.create(views[0], "card"),
                Pair.create(views[1], "poster")
            )
            this.startActivity(intent, options.toBundle())
        } else {
            this.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        initToolbar()

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("query", "").replace("+", " ")
        }

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.search_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        val divider = ContextCompat.getDrawable(recyclerView.context, R.drawable.divider)
        val itemDecor = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        divider?.let {
            itemDecor.setDrawable(it)
        }
        recyclerView.addItemDecoration(itemDecor)
        initAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = listAdapter

        progressBar.visibility = View.VISIBLE
        getSuggestions()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        if (!TextUtils.isEmpty(searchText)) {
            outState.putString("query", searchText)
        }
        super.onSaveInstanceState(outState)
    }

    public override fun onPause() {
        super.onPause()
        val outState = Bundle()
        outState.putString("query", searchText)
        onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
            searchView.isFocusable = true
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    var myQuery = query
                    if (myQuery.isNotEmpty()) {
                        myQuery = myQuery.replace(" ", "+")
                        progressBar.visibility = View.VISIBLE

                        scheduleSearchRequest(myQuery)

                        searchText = myQuery
                    } else {
                        getSuggestions()
                    }
                    return false
                }
            })
            if (!TextUtils.isEmpty(searchText))
                searchView.setQuery(searchText, false)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun scheduleSearchRequest(query: String) {
        searchView.removeCallbacks(getSearchRunnable(query))
        searchView.postDelayed(getSearchRunnable(query), 500)
    }

    private fun getSearchRunnable(searchQuery: String): Runnable {
        return Runnable { searchRequest(searchQuery) }
    }

    private fun showNetworkError() {
        val snackBar = Snackbar
            .make(recyclerView, R.string.noInternet, Snackbar.LENGTH_LONG)
        snackBar.show()
    }

    override fun onStop() {
        super.onStop()
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromInputMethod(view.windowToken, 0)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
