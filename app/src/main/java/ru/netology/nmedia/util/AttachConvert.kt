package ru.netology.nmedia.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.netology.nmedia.dto.Attachment

class AttachmentConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAttachment(attachment: Attachment): String {
        return gson.toJson(attachment)
    }

    @TypeConverter
    fun toAttachment(json: String): Attachment {
        return gson.fromJson(json, Attachment::class.java)
    }
}