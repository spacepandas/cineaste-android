package de.cineaste.android.activity

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.util.Constants
import de.markusfisch.android.scalingimageview.widget.ScalingImageView

class PosterActivity : AppCompatActivity() {

    private lateinit var poster: ScalingImageView
    private var posterPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        posterPath = intent.getStringExtra(POSTER_PATH)

        poster = ScalingImageView(this)
        setTransitionNameIfNecessary()
        poster.setImageResource(R.drawable.placeholder_poster)
        setContentView(poster)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        displayPoster()
    }

    @TargetApi(21)
    private fun setTransitionNameIfNecessary() {
        poster.transitionName = "poster"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    private fun displayPoster() {
        Picasso.get()
            .load(getPosterUrl(Constants.POSTER_URI_SMALL))
            .error(R.drawable.placeholder_poster)
            .into(poster, object : Callback {
                override fun onSuccess() {
                    val placeHolder = poster.drawable
                    setBackgroundColor((placeHolder as BitmapDrawable).bitmap)
                    Picasso.get()
                        .load(getPosterUrl(Constants.POSTER_URI_ORIGINAL))
                        .placeholder(placeHolder)
                        .into(poster, object : Callback {
                            override fun onSuccess() {
                                Snackbar.make(
                                    poster,
                                    R.string.poster_reloaded,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            override fun onError(e: Exception) {
                                poster.setImageDrawable(placeHolder)
                            }
                        })
                }

                override fun onError(e: Exception) {
                    displayPoster()
                }
            })
    }

    private fun setBackgroundColor(moviePoster: Bitmap) {
        val paletteAsyncListener = Palette.PaletteAsyncListener { palette ->
            val swatch = palette?.dominantSwatch ?: return@PaletteAsyncListener

            window.decorView.setBackgroundColor(swatch.rgb)
        }

        Palette.from(moviePoster).generate(paletteAsyncListener)
    }

    private fun getPosterUrl(postUri: String): String {
        return postUri
            .replace("<posterName>", posterPath ?: "/")
            .replace("<API_KEY>", getString(R.string.movieKey))
    }

    companion object {
        const val POSTER_PATH = "posterPath"
    }
}
