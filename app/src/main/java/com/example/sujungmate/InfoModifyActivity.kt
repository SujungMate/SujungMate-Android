package com.example.sujungmate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_info_modify.view.*
import kotlinx.android.synthetic.main.activity_sign_up3.*
import java.util.*


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

        // 프로필 사진
            profileImage_infoModify.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        changePW_infoModify.setOnClickListener {
            val intent = Intent(this, PWChangeActivity::class.java)
            startActivity(intent)
        }

        modifyBtn_infoModify.setOnClickListener {
            uploadImagetoFirebaseStorage()
        }
    }

    var selectedPhotoUri: Uri? = null
    var newPhotoPath: String? = "테스트"

    // 프로필 사진 선택
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // proceed and check what the selected image was....
            Log.d("InfoModifyActivity","사진 선택됨")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            // val selectphoto_button_register = findViewById<Button>(R.id.selectphoto_button_register)

            profileImage_infoModify.setBackgroundDrawable(bitmapDrawable)
        }
    }

    // 파이어베이스 저장소에 새 프로필 사진 저장
    private fun uploadImagetoFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("InfoModifyActivity","Successfully uploaded image: ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                Log.d("InfoModifyActivity","File Location: $it")
                // 새로운 사진 경로 저장
                newPhotoPath = it.toString()
                Log.d("profile_img", "img stored in users {$newPhotoPath}\n")
                saveUserToFirebaseDatabase(it.toString())
            }
        }.addOnFailureListener{

        }
    }

    // Firebase에 실제로 저장
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        // Firebase 데이터 수정
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val ref_uid = FirebaseDatabase.getInstance().getReference("/users/$uid/uid")
        Log.d("database", "DB connected")
        
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(Users::class.java)
                Log.d("onDataChange", "Function worked")

                var user : Users = Users()

                // val profileImg = currentUser!!.profile_img
                // Log.d("img", "img stored in profileImg")

                user.stuNum = currentUser!!.stuNum
                user.nickname = nickname_infoModify.text.toString()
                user.major = major_infoModify.selectedItem.toString()
                user.lecture = lecture_infoModify.text.toString()
                user.mbti = mbti_infoModify.selectedItem.toString()
                user.interest = interest1_infoModify.selectedItem.toString()
                user.msg = statusMessage_infoModify.text.toString()
                user.profile_img = profileImageUrl
                Log.d("프로필 재설정: ", "{$newPhotoPath}")

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
        val intent = Intent(this, MyPageActivity::class.java)
        startActivity(intent)
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