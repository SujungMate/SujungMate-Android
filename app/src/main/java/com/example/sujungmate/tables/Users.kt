package com.example.sujungmate.tables

data class Users (
    //    val uid : String = "", // 사용자 uid
    val stuNum: Int = 0,    // 학번 (userid.email에서 앞 두자리 따오면 될 듯)
    var nickname: String? = null, // 닉네임
    var major: String = "", // 주전공
    var lecture : String? = null, // 수강 과목
    var mbti : String? = null, // MBTI
    var interest : String? = null, // 관심사
    var msg: String? = null,  // 상태메시지
    var profile_img : Int = 1
)