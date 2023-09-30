package ru.netology.nmedia.db

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.util.AttachmentConverter


@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(AttachmentConverter::class)
abstract class AppDb: RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context)
                .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .allowMainThreadQueries()
            .build()
    }
}
