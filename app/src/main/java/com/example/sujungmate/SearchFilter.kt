package com.example.sujungmate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// 사용자가 최종적으로 입력한 검색 조건
@Parcelize
data class SearchFilter (
//    var state : Boolean,
    var searchRelationship : String,
    var searchMajor : String?,
    var searchLecture : String?,
    var searchMbti : String?,
    var searchInterest : String?
): Parcelable
