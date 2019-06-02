package de.cineaste.android.listener

import de.cineaste.android.entity.movie.NearbyMessage

interface UserClickListener {
    fun onUserClickListener(nearbyMessage: NearbyMessage)
}
