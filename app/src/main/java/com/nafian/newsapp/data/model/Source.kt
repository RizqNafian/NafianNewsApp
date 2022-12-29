package com.nafian.newsapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * data class Source mempreprentasikan entity dari Source/sumber.
 */
@Parcelize
data class Source(
    val id: String,
    val name: String
):Parcelable