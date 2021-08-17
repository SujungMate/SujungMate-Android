package com.example.sujungmate.tables

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubDistinction (
    var subNickname : String,
    var subMajor : String,
    var subStudentID : Long
    ) : Parcelable