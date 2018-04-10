package de.cineaste.android.entity.movie

import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson

import java.nio.charset.Charset
//todo use data class
class NearbyMessage {

    val userName: String
    private val deviceId: String?
    val movies: List<MovieDto>

    private constructor(): this("", "", emptyList())

    constructor(userName: String, deviceId: String, movies: List<MovieDto>) {
        this.userName = userName
        this.deviceId = deviceId
        this.movies = movies
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as NearbyMessage?

        return !if (deviceId != null) deviceId != that!!.deviceId else that!!.deviceId != null
    }

    override fun hashCode(): Int {

        return deviceId?.hashCode() ?: 0
    }

    companion object {

        private val GSON = Gson()

        fun fromMessage(message: Message): NearbyMessage {
            val nearbyMessageString = String(message.content).trim { it <= ' ' }

            return GSON.fromJson(
                    String(nearbyMessageString.toByteArray(Charset.forName("UTF-8"))),
                    NearbyMessage::class.java)
        }

        fun newNearbyMessage(nearbyMessage: NearbyMessage): Message {
            return Message(
                    GSON.toJson(nearbyMessage).toByteArray(Charset.forName("UTF-8")))
        }
    }
}
