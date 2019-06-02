package de.cineaste.android.entity.movie

import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson
import de.cineaste.android.util.MultiList

data class NearbyMessage(
    val userName: String = "",
    val deviceId: String = "",
    val movies: MultiList = MultiList()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NearbyMessage

        if (userName != other.userName) return false
        if (deviceId != other.deviceId) return false
        if (movies != other.movies) return false

        return true
    }

    override fun hashCode(): Int {
        return deviceId.hashCode()
    }

    fun toNearbyMessage(): Message {
        return Message(gson.toJson(this).toByteArray(Charsets.UTF_8))
    }

    companion object {

        private val gson = Gson()

        fun fromMessage(message: Message): NearbyMessage {
            val nearbyMessageString = String(message.content).trim { it <= ' ' }

            return gson.fromJson(
                String(nearbyMessageString.toByteArray(Charsets.UTF_8)),
                NearbyMessage::class.java
            )
        }
    }
}
