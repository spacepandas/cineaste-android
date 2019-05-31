package de.cineaste.android.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateAwareGson {

    val gson: Gson

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): Date? {
                return try {
                    df.parse(json.asString)
                } catch (e: ParseException) {
                    null
                }
            }
        })
        gson = gsonBuilder.create()
    }
}

object ExtendedDateAwareGson {
    val gson: Gson

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)

            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): Date? {
                return try {
                    df.parse(json.asString)
                } catch (e: ParseException) {
                    null
                }
            }
        })
        gson = gsonBuilder.create()
    }
}
