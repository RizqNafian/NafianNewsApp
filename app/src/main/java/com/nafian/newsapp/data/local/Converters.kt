package com.nafian.newsapp.data.local

import androidx.room.TypeConverter
import com.nafian.newsapp.data.model.Source

/**
 * method utnuk mengkonfersi data.
 */
class Converters {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}