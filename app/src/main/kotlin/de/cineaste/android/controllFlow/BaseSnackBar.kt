package de.cineaste.android.controllFlow

import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View

abstract class BaseSnackBar protected constructor(
    protected val linearLayoutManager: LinearLayoutManager,
    protected val view: View
) {

    abstract fun getSnackBarLeftSwipe(position: Int)
    abstract fun getSnackBarRightSwipe(position: Int, message: Int)
}
