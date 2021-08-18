package com.example.sujungmate.tables

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    val uid : String = "", // 사용자 uid
    var stuNum: String = "",    // 학번 (userid.email에서 앞 두자리 따오면 될 듯) //String으로 바꿈
    var nickname: String? = null, // 닉네임
    var major: String = "", // 주전공
    var lecture : String? = null, // 수강 과목
    var mbti : String? = null, // MBTI
    var interest : String? = "", // 관심사
    var msg: String? = null,  // 상태메시지
    var profile_img : String = ""
) : Parcelable, Comparable<Users> {
    // 학번 비교 함수
    override fun compareTo(other: Users): Int {
        try {
            val thisID : Int = this.stuNum.toInt() / 10000  // 학번 4자리 (나)
            val otherID : Int = this.stuNum.toInt() / 10000 // 학번 4자리 (상대방)

            when {
                thisID > otherID -> return 1 // other : 선배
                thisID < otherID -> return -1 // other : 후배
                else -> return 0 // other : 동기
            }
        } catch (e : NumberFormatException) {
            e.printStackTrace()
            return -100
        }
    }
}