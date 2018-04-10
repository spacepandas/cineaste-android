package de.cineaste.android.database

import java.util.ArrayList

import de.cineaste.android.entity.movie.NearbyMessage

object NearbyMessageHandler {

    private val messages: MutableList<NearbyMessage>

    val size: Int
        get() = messages.size

    init {
        messages = ArrayList()
    }

    fun addMessage(message: NearbyMessage) {
        messages.add(message)
    }

    fun addMessages(message: List<NearbyMessage>) {
        messages.addAll(message)
    }

    fun getMessages(): List<NearbyMessage> {
        return messages
    }

    fun clearMessages() {
        messages.clear()
    }
}
