package com.example.sujungmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sujungmate.tables.Users
import com.google.firebase.database.*

class MateSearchActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Users>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_search)

        //mateInfo에서 검색한 요소들을 MateSearch로 가져오기
        val filterClass: SearchFilter? = intent.getParcelableExtra("search_Filter")
        Log.d("필터 : ", filterClass.toString() + "${filterClass?.searchRelationship?.javaClass}")

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mateSearch)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // RecyclerView
        userRecyclerView = findViewById(R.id.sujungMateListRV) //리사이클러 뷰 가져오기
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<Users>()

        if (filterClass != null) // 검색 조건이 있을 경우에
            getUserDataWithFilter(filterClass)
        else    // 검색 조건이 없을 경우에(null을 받아올 경우)
            getUserData()

    }

    //파이어베이스로부터 데이터 가져오기 (가져와서 ArrayList에 삽입) - 세부 설정 후 매칭(필터 적용)
    // currentUser : 19학번, 컴퓨터공학과, 컴퓨터개론, MBTI, 게임 이라고 가정
    private fun getUserDataWithFilter(filterClass: SearchFilter) {
        dbref = FirebaseDatabase.getInstance().getReference("users")

        dbref.orderByChild("stuNum").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(Users::class.java) //가져온 오브젝트 여기에 저장

//                        //필터 적용
//                        // 1. 학번
//                        if(filterClass.searchRelationship.equals("후배") && 2019 < user!!.stuNum / 10000) {
//                            g
//
//                        }
                        Log.d("가져온 유저 : ", user.toString())

                        userArrayList.add(user!!)
                    }

                    Log.d("전체 유저 : ", userArrayList.toString())

                    userRecyclerView.adapter = MyAdapter(userArrayList)

                    Toast.makeText(applicationContext, "유저 업로드 완료2(필터)", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })
    }

    //파이어베이스로부터 데이터 가져오기 (가져와서 ArrayList에 삽입) - 랜덤매칭
    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("users")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(Users::class.java) //가져온 오브젝트 여기에 저장
                        Log.d("user 목록 : ", user.toString())
                        userArrayList.add(user!!)
                    }
                    userRecyclerView.adapter = MyAdapter(userArrayList)

                    Toast.makeText(applicationContext, "유저 업로드 완료", Toast.LENGTH_SHORT).show()
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