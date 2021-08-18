package com.example.sujungmate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sujungmate.R
import com.example.sujungmate.tables.SubDistinction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.example.sujungmate.tables.Users
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up2.*
import kotlinx.android.synthetic.main.activity_sign_up3.*
import java.util.*

class SignUpActivity3 : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_signup3)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // EditText 입력 중 외부 터치 시 키보드 내리기 (닉네임)
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_signup3) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        // 프로필 사진
        selectphoto_button_signup3.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)

        }


        next_button_signup3.setOnClickListener{
            val nickname = nickname_edittext_signup3.text.toString()
            val major = major_spinner_signup3.selectedItem.toString()
            uploadImagetoFirebaseStorage(nickname, major)
        }

        // 채연이 코드
        // 주전공에 대한 spinner 세팅
        SpinnerSettings(findViewById(R.id.major_spinner_signup3), R.array.interesting_major)

        /*
        val nextBtn = findViewById<Button>(R.id.next_button_signup3)
        nextBtn.setOnClickListener {

            // 1. 닉네임 (띄어쓰기 제거) ??? 중복 & 글자수 확인 ???
            var NICKNAME : String? = findViewById<EditText>(R.id.nickname_edittext_signup3).text.toString().trim()

            // 2. 주전공 ??? not null 확인 ???
            var MAJOR : String = findViewById<Spinner>(R.id.major_spinner_signup3).selectedItem.toString()

            // 이전 액티비티에서 학번 가져오기
            val subStudentID: Long = intent.getLongExtra("subStudentID", 0)
            Log.d("subStudentID : ", subStudentID.toString())

            // SignUpActivity4에게 특징 값 전달
            val intent = Intent(this, SignUpActivity4::class.java)
            intent.putExtra("sub_distinction", SubDistinction(NICKNAME.toString(), MAJOR, subStudentID))
            startActivity(intent)
        }
    }

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.nickname_edittext_signup3)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }

    //스피너
    private fun SpinnerSettings(spinner : Spinner, arrayId : Int) {
        ArrayAdapter.createFromResource(
            this,
            arrayId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }
     */
    }

    var selectedPhotoUri: Uri? = null

    // profile photo 반영
    // 추가수정: image 동그랗게 가져오는건 코틀린 메니저 영상 3번째 31분부터 보기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // proceed and check what the selected image was....
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            // val selectphoto_button_register = findViewById<Button>(R.id.selectphoto_button_register)

            selectphoto_button_signup3.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun uploadImagetoFirebaseStorage(nickname:String, major:String) {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("SignUpActivity3","Successfully uploaded image: ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                Log.d("SignUpActivity3","File Location: $it")

                val stuNum = intent.getStringExtra("stuNum")

                // saveUserToFirebaseDatabase(it.toString())
                val intent = Intent(this, SignUpActivity4::class.java)
                intent.putExtra("selectedPhotoUri", it.toString())
                intent.putExtra("major",major)
                intent.putExtra("nickname",nickname)
                intent.putExtra("stuNum",stuNum)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }.addOnFailureListener{

        }
    }


    // Firebase에 실제로 저장 (이미지, 닉네임, 주전공)
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid ?: "" // default 체크
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        var major = major_spinner_signup3.selectedItem.toString()

        val user = Users(uid, schoolemail_edittext_signup2.text.toString(), nickname_edittext_signup3.text.toString(),
            major,"","","", "", profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity3","we saved the user's image,name,major to Firebase Database")

                /*
                val intent = Intent(this, SignUpActivity4::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                 */
            }
    }

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
        val editText1 = findViewById<EditText>(R.id.nickname_edittext_signup3)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }

    //스피너
    private fun SpinnerSettings(spinner : Spinner, arrayId : Int) {
        ArrayAdapter.createFromResource(
            this,
            arrayId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }
}

