package com.example.sujungmate.tables

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Request (
        val uid : String = "", // 사용자 uid
        var profile_img : String = "", // 프로필 사진
        val nickname : String? = null, // 닉네임
        var major : String = "", // 주전공
        val stuNum : String = "", // 학번 (String으로 변경)
        var msg : String? = null, // 상태메시지
) : Parcelable