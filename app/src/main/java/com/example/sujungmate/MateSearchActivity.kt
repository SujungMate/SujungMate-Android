package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sujungmate.adapters.MyAdapter
import com.example.sujungmate.tables.Request
import com.example.sujungmate.tables.SearchFilter
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_mate_search.*
import java.util.*

class MateSearchActivity : AppCompatActivity() {
    var userArrayList : ArrayList<Users> = arrayListOf()    // DB에서 가져와 담을 공간
    val auth : FirebaseAuth = FirebaseAuth.getInstance()    // 사용자 계정(Auth) 참조
    val userDB : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")   // userDB(특징) 참조

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_search)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mateSearch)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // 매칭 관리로
        goMateManage.setOnClickListener {
            val intent = Intent(this, MateManageActivity::class.java)
            startActivity(intent)
        }

        // MateInfo에서 설정한 조건들을 MateSearch로 가져오기
        val filterClass: SearchFilter? = intent.getParcelableExtra("search_Filter")
        Log.d("검색 조건 : ", filterClass.toString())

        // RecyclerView Settings
        sujungMateListRV.layoutManager = LinearLayoutManager(this@MateSearchActivity)
        sujungMateListRV.setHasFixedSize(true)
//        sujungMateListRV.adapter = MyAdapter(userArrayList)

//        // TODO : 스와이프해서 새로고침하는 방법
//        val swipeRefreshLayout = findViewById<swipeRefreshLayout>(R.id.swipeRefreshView)
        // 현재 로그인 한 계정이 없는 경우
        if(auth.currentUser != null) {
            if (filterClass != null) // 검색 조건이 있을 경우에
                startMatching(auth.currentUser!!).getUserDataWithFilter(filterClass)
            else    // 검색 조건이 없을 경우에(null을 받아올 경우)
                startMatching(auth.currentUser!!).getUserDataWithNoFilter()
        }
    }

    /* 매칭 클래스 */
    inner class startMatching(currentUser : FirebaseUser) {
        val fromId = currentUser.uid    // 나의 uid (보내는 쪽 uid)
        lateinit var myUserData : Users

        init {
            userArrayList.clear()
        }

        // 1. 랜덤 매칭 (필터 미적용)
        fun getUserDataWithNoFilter() {
            userDB.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // 현재 로그인 한 계정 제외
                            val user = userSnapshot.getValue(Users::class.java) // DB로부터 가져온 오브젝트 여기에 저장
                            if (user!!.uid != fromId)
                                userArrayList.add(user!!)
                            else
                                myUserData = user   // 내 사용자 정보 담기
                        }
                        Log.d("전체 유저 (나 제외) : ", userArrayList.toString())

                        // 가져온 user 정보 리사이클러 뷰 어댑터 연결
                        // 순서 랜덤으로 섞기
                        userArrayList.shuffle()
                        val adapter = MyAdapter(userArrayList)
                        sujungMateListRV.adapter = adapter

                        // 버튼 클릭시 이벤트 처리
                        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener {
                            override fun onItemClick(position: Int) {
                                val toId = userArrayList[position].uid // 상대방의 uid
                                val fromRef = FirebaseDatabase.getInstance().getReference("/request/send/$fromId/$toId")
                                val toRef = FirebaseDatabase.getInstance().getReference("/request/receive/$toId/$fromId")

                                // (position : 클릭한 카드의 위치)
                                // 보낸 요청 데이터 생성
                                val sendRequest = createRequestData(userArrayList[position])    // 요청 데이터 생성
                                fromRef.setValue(sendRequest).addOnSuccessListener {
                                    Toast.makeText(applicationContext, "요청이 완료되었습니다", Toast.LENGTH_SHORT).show()
//                                    userArrayList.removeAt(position)
                                    Log.d("제거 후 남은 리스트 : ", userArrayList.toString())
                                }

                                // 받은 요청 데이터 생성
                                val receiveRequest = createRequestData(myUserData)
                                toRef.setValue(receiveRequest).addOnSuccessListener {
                                    Toast.makeText(applicationContext, "받은 요청 데이터 저장 완료", Toast.LENGTH_SHORT).show()

                                    Log.d("toRef : ", "받은 요청 데이터 : ${receiveRequest.toString()}")

                                }
                            }

                        })

                    } else {
                        // 검색 결과 없음(users 밑에 아무 것도 없음)
                        errorImg.visibility = View.VISIBLE
                        noResult.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }

            })
        }

        // 2. 세부 설정 후 매칭 (필터 적용)
        fun getUserDataWithFilter(filterClass : SearchFilter) {
            userDB.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // 현재 로그인 한 계정 제외
                            val user = userSnapshot.getValue(Users::class.java) // DB로부터 가져온 오브젝트 여기에 저장
                            if (user!!.uid != fromId)
                                userArrayList.add(user!!)
                            else
                                myUserData = user   // 내 사용자 정보 담기
                        }
                        Log.d("전체 유저 (나 제외) : ", userArrayList.toString())

                        //1. 학번 필터(필수 항목)
                        when (filterClass.searchRelationship) {
                            "후배" -> userArrayList = userArrayList.filter { myUserData.compareTo(it) < 0 } as ArrayList<Users>
                            "동기" -> userArrayList = userArrayList.filter { myUserData.compareTo(it) == 0 } as ArrayList<Users>
                            "선배" -> userArrayList = userArrayList.filter { myUserData.compareTo(it) > 0 } as ArrayList<Users>
                            else -> Toast.makeText(applicationContext, "학번 입력 오류", Toast.LENGTH_SHORT).show()
                        }
                        Log.d("학번 적용 user : ", userArrayList.toString())

                        //2. 관심 학과
                        if (filterClass.searchMajor != "학과 선택")
                            userArrayList = userArrayList.filter { filterClass.searchMajor == it.major } as ArrayList<Users>
                        Log.d("관심학과 적용 user : ", userArrayList.toString())

                        //3. 수강 과목
                        if (filterClass.searchLecture != "")
                            userArrayList = userArrayList.filter {
                                filterClass.searchLecture?.lowercase()?.trim() == it.lecture?.lowercase()?.trim()
                            } as ArrayList<Users>
                        Log.d("수강과목 적용 user : ", userArrayList.toString())

                        //4. MBTI
                        if (filterClass.searchMbti != "MBTI 선택")
                            userArrayList = userArrayList.filter {
                                filterClass.searchMbti?.lowercase() == it.mbti?.lowercase()
                            } as ArrayList<Users>
                        Log.d("MBTI 적용 user : ", userArrayList.toString())

                        //5. 관심사(소분류)
                        if (filterClass.searchInterest != "소분류")
                            userArrayList = userArrayList.filter {
                                filterClass.searchInterest == it.interest
                            } as ArrayList<Users>
                        Log.d("관심사 적용 user : ", userArrayList.toString())

                        Log.d("최종 user : ", userArrayList.toString())

                        // 검색 결과 없음
                        if(userArrayList.size == 0) {
                            errorImg.visibility = View.VISIBLE
                            noResult.visibility = View.VISIBLE
                        }

                        // 순서 랜덤으로 섞기
                        userArrayList.shuffle()
                        // 최종적으로 저장된 user 정보 리사이클러 뷰 어댑터 연결
                        val adapter = MyAdapter(userArrayList)
                        sujungMateListRV.adapter = adapter

                        // 버튼 클릭시 이벤트 처리
                        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener {
                            override fun onItemClick(position: Int) {
                                val toId = userArrayList[position].uid // 상대방의 uid
                                val fromRef = FirebaseDatabase.getInstance().getReference("/request/send/$fromId/$toId")
                                val toRef = FirebaseDatabase.getInstance().getReference("/request/receive/$toId/$fromId")

                                // (position : 클릭한 카드의 위치)
                                // 보낸 요청 데이터 생성
                                val sendRequest = createRequestData(userArrayList[position])    // 요청 데이터 생성
                                fromRef.setValue(sendRequest).addOnSuccessListener {
                                    Toast.makeText(applicationContext, "요청이 완료되었습니다", Toast.LENGTH_SHORT).show()
//                                    userArrayList.removeAt(position)
                                    Log.d("제거 후 남은 리스트 : ", userArrayList.toString())
                                }

                                // 받은 요청 데이터 생성
                                val receiveRequest = createRequestData(myUserData)
                                toRef.setValue(receiveRequest).addOnSuccessListener {
                                    Toast.makeText(applicationContext, "받은 요청 데이터 저장 완료", Toast.LENGTH_SHORT).show()

                                    Log.d("toRef : ", "받은 요청 데이터 : ${receiveRequest.toString()}")

                                }
                            }

                        })

                    } else {
                        // 검색 결과 없음(users 밑에 아무 것도 없음)
                        errorImg.visibility = View.VISIBLE
                        noResult.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
        }

        // 요청 데이터 생성
        private fun createRequestData(target: Users): Request {
            return Request(
                target.uid,
                target.profile_img,
                target.nickname,
                target.major,
                target.stuNum,
                target.msg
            )

        }
    }

    //이전 화면으로 되돌리기, 새로고침
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.refresh -> {
                // 새로 고침
                Toast.makeText(applicationContext, "refresh", Toast.LENGTH_SHORT).show()
                sujungMateListRV.adapter!!.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 메뉴 생성 역할
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}