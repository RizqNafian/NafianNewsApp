package com.nafian.newsapp.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * pengaturan tanggal dan waktu local (tanggal dan waktu dari device)
 */
class DateUtil{

    companion object{
        fun changeDateFormat(strDate: String?): String {
            if(strDate.isNullOrEmpty()){
               return ""
            }
            return try{
                val sourceSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val requiredSdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                requiredSdf.format(sourceSdf.parse(strDate))
            }catch (ex: Exception){
                ""
            }
        }
    }
}

