package com.example.sujungmate

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.tables.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_info_modify.*
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_sign_up4.*
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_info_modify.view.*
import kotlinx.android.synthetic.main.activity_sign_up3.*


class InfoModifyActivity : AppCompatActivity() {
    companion object {
        var currentUser: Users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_modify)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_infoModify)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_infoModify) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

//        // 프로필 사진
//        profileImage_infoModify.setOnClickListener{
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent,0)
//        }

        changePW_infoModify.setOnClickListener {
            val intent = Intent(this, PWChangeActivity::class.java)
            startActivity(intent)
        }

        modifyBtn_infoModify.setOnClickListener {
            // Firebase 데이터 수정
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            val ref_uid = FirebaseDatabase.getInstance().getReference("/users/$uid/uid")
            Log.d("database", "DB connected")

            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("onDataChange", "Function worked")

                    var user : Users = Users()

                    // val STUNUM = currentUser!!.stuNum
                    // val profileImg = currentUser!!.profile_img
                    // Log.d("img", "img stored in profileImg")

                    Log.d("img", "img stored in users")
                    user.nickname = nickname_infoModify.text.toString()
                    user.major = major_infoModify.selectedItem.toString()
                    user.lecture = lecture_infoModify.text.toString()
                    user.mbti = mbti_infoModify.selectedItem.toString()
                    user.interest = interest1_infoModify.selectedItem.toString()
                    user.msg = statusMessage_infoModify.text.toString()
                    Log.d("data", "data stored")

                    // 이미지는 추후 반영
                    // user.profile_img = currentUser!!.profile_img
                    // Log.d("profile", "Image stored")

                    ref.setValue(user)
                    Log.d("set user", "set user's value")
                    ref_uid.setValue(uid)
                    Log.d("set uid", "set uid")
                    // ref_stuNum.setValue(currentUser!!.stuNum)
                    // Log.d("set stuNum", "set stuNum")
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    // String -> Editable 타입 변환
    // fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    // 이전 화면으로 되돌리기 구현
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.nickname_infoModify)
        val editText2 = findViewById<EditText>(R.id.lecture_infoModify)
        val editText3 = findViewById<EditText>(R.id.statusMessage_infoModify)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
        imm.hideSoftInputFromWindow(editText2.windowToken, 0)
        imm.hideSoftInputFromWindow(editText3.windowToken, 0)
    }
}