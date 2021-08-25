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
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_info_modify.view.*
import kotlinx.android.synthetic.main.activity_sign_up3.*
import java.util.*


class InfoModifyActivity : AppCompatActivity() {
    companion object {
        var currentUser: Users? = null
    }

    var interest = ""

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

        SpinnerSettings(findViewById(R.id.interest1_infoModify), R.array.large_category)

        // 관심사1 - 대분류 스피너 설정
        val mainCategorySpinner = findViewById<Spinner>(R.id.interest1_infoModify)
        val mainSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.large_category,
            android.R.layout.simple_spinner_item
        )
        mainCategorySpinner.adapter = mainSpinnerAdapter // 어댑터 연결
        mainSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 관심사2 - 소분류 스피너 설정
        val subCategorySpinner = findViewById<Spinner>(R.id.interest2_infoModify)
        var subSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sub_category_title,
            android.R.layout.simple_spinner_item
        )
        subCategorySpinner.adapter = subSpinnerAdapter
        subSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너 동작 감지(다중 스피너)
        mainCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) { // 소분류
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@InfoModifyActivity,
                        R.array.sub_category_title,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 1) {    //엔터테인먼트·예술
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@InfoModifyActivity,
                        R.array.sub_category_entertainment,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 2) { //생활·노하우·쇼핑
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@InfoModifyActivity,
                        R.array.sub_category_dailyLife,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 3) {  //취미·여가·여행
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@InfoModifyActivity,
                        R.array.sub_category_hobby,
                        android.R.layout.simple_spinner_item
                    )
                } else {    //지식·동향(4)
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@InfoModifyActivity,
                        R.array.sub_category_knowledge,
                        android.R.layout.simple_spinner_item
                    )
                }

                // 공통 기능
                subCategorySpinner.adapter = subSpinnerAdapter
                subSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                subCategorySpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
//                            Toast.makeText(applicationContext, "2번째 스피너 완료", Toast.LENGTH_SHORT)
//                                .show()

                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            Toast.makeText(applicationContext, "Nothing Selected", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Nothing Selected", Toast.LENGTH_SHORT).show()
            }
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
            interest = subCategorySpinner.selectedItem.toString()
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

                user.stuNum = currentUser!!.stuNum
                user.nickname = nickname_infoModify.text.toString()
                user.major = major_infoModify.selectedItem.toString()
                user.lecture = lecture_infoModify.text.toString()
                user.mbti = mbti_infoModify.selectedItem.toString()
                user.interest = interest
                user.msg = statusMessage_infoModify.text.toString()
                user.profile_img = profileImageUrl
                Log.d("프로필 재설정: ", "{$newPhotoPath}")


                ref.setValue(user)
                Log.d("set user", "set user's value")
                ref_uid.setValue(uid)
                Log.d("set uid", "set uid")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        val intent = Intent(this, MyPageActivity::class.java)
        startActivity(intent)
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
        val editText1 = findViewById<EditText>(R.id.nickname_infoModify)
        val editText2 = findViewById<EditText>(R.id.lecture_infoModify)
        val editText3 = findViewById<EditText>(R.id.statusMessage_infoModify)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
        imm.hideSoftInputFromWindow(editText2.windowToken, 0)
        imm.hideSoftInputFromWindow(editText3.windowToken, 0)
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