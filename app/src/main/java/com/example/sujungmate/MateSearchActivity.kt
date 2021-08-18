package com.example.sujungmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.tables.Request
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MateSearchActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Users>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_search)

        // 사용자 정보 참조
        auth = FirebaseAuth.getInstance()

        //mateInfo에서 검색한 요소들을 MateSearch로 가져오기
        val filterClass: SearchFilter? = intent.getParcelableExtra("search_Filter")
        Log.d("필터 : ", filterClass.toString())

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mateSearch)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // RecyclerView
        userArrayList = arrayListOf<Users>()

        userRecyclerView = findViewById(R.id.sujungMateListRV) //리사이클러 뷰 가져오기
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        if(auth.currentUser != null) {
            if (filterClass != null) // 검색 조건이 있을 경우에
                getUserDataWithFilter(filterClass, auth.currentUser!!)
            else    // 검색 조건이 없을 경우에(null을 받아올 경우)
                getUserData(auth.currentUser!!)
        }
    }

    //파이어베이스로부터 데이터 가져오기 (가져와서 ArrayList에 삽입) - 세부 설정 후 매칭(필터 적용)
    private fun getUserDataWithFilter(filterClass: SearchFilter, currentUser: FirebaseUser) {
        // 전체 User DB 참조
        dbref = FirebaseDatabase.getInstance().getReference("users")
        var myAccountRef : Users = Users()

        dbref.orderByChild("stuNum").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // 현재 로그인 한 계정 제외
                        val user = userSnapshot.getValue(Users::class.java) //가져온 오브젝트 여기에 저장
                        if(user!!.uid != currentUser.uid)
                            userArrayList.add(user!!)   // 전체 유저 들어있음
                        else if(user.uid == currentUser.uid) {
                            myAccountRef = user
                            Log.d("현재 계정 정보 : ", myAccountRef.toString())
                        }
                    }

                    // 검색 결과 없음
                    if (userArrayList.size == 0) {
                        findViewById<ImageView>(R.id.errorImg).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.noResult).visibility = View.VISIBLE
                    }

                    //1. 학번 필터(필수 항목)
                    when (filterClass.searchRelationship) {
                        "후배" -> userArrayList =
                            userArrayList.filter { myAccountRef.compareTo(it) < 0 } as ArrayList<Users>
                        "동기" -> userArrayList =
                            userArrayList.filter { myAccountRef.compareTo(it) == 0 } as ArrayList<Users>
                        "선배" -> userArrayList =
                            userArrayList.filter { myAccountRef.compareTo(it) > 0 } as ArrayList<Users>
                        else -> Toast.makeText(applicationContext, "학번 입력 오류", Toast.LENGTH_SHORT)
                            .show()
                    }

                    //2. 관심 학과
                    if (filterClass.searchMajor != "관심 학과 선택")
                        userArrayList = userArrayList.filter { filterClass.searchMajor == it.major } as ArrayList<Users>

                    //3. 수강 과목
                    if (filterClass.searchLecture != "")
                        userArrayList = userArrayList.filter {
                            filterClass.searchLecture?.lowercase()
                                ?.trim() == it.lecture?.lowercase()?.trim()
                        } as ArrayList<Users>

                    //4. MBTI
                    if (filterClass.searchMbti != "MBTI 선택")
                        userArrayList = userArrayList.filter {
                            filterClass.searchMbti?.lowercase() == it.mbti?.lowercase()
                        } as ArrayList<Users>

                    //5-1. 관심사(대분류)
                    if (filterClass.searchInterest != "")
                        userArrayList = userArrayList.filter {
                            filterClass.searchInterest?.lowercase() == it.interest?.lowercase()
                        } as ArrayList<Users>

                }

                var adapter = MyAdapter(userArrayList)
                userRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        Toast.makeText(applicationContext, "요청이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        // 요청 데이터 생성

                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })

    }

    //파이어베이스로부터 데이터 가져오기 (가져와서 ArrayList에 삽입) - 랜덤매칭
    private fun getUserData(currentUser: FirebaseUser) {
        // users DB 참조
        dbref = FirebaseDatabase.getInstance().getReference("users")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // 현재 로그인 한 계정 제외
                        val user = userSnapshot.getValue(Users::class.java) //가져온 오브젝트 여기에 저장
                        if(user!!.uid != currentUser.uid)
                            userArrayList.add(user!!)
                    }

                    // 검색 결과 없음
                    if (userArrayList.size == 0) {
                        findViewById<ImageView>(R.id.errorImg).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.noResult).visibility = View.VISIBLE
                    }

                    var adapter = MyAdapter(userArrayList)
                    userRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            Toast.makeText(applicationContext, "요청이 완료되었습니다", Toast.LENGTH_SHORT).show()
                            // 요청 데이터 생성

                        }
                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })

    }

    //이전 화면으로 되돌리기, 새로고침
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.refresh -> {
                Toast.makeText(applicationContext, "refresh", Toast.LENGTH_SHORT).show()

                //리스트 새로고침(적용되는지는 모르겠음)
                userRecyclerView.adapter?.notifyDataSetChanged()
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