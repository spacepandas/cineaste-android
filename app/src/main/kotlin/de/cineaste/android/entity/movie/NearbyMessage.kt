package de.cineaste.android.entity.movie

import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson

data class NearbyMessage(
    val userName: String = "",
    private val deviceId: String = "",
    val movies: List<MovieDto> = emptyList()
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
        return Message(GSON.toJson(this).toByteArray(Charsets.UTF_8))
    }

    companion object {

        private val GSON = Gson()

        fun fromMessage(message: Message): NearbyMessage {
            val nearbyMessageString = String(message.content).trim { it <= ' ' }

            return GSON.fromJson(
                String(nearbyMessageString.toByteArray(Charsets.UTF_8)),
                NearbyMessage::class.java
            )
        }
    }
}
