package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_info_modify.*
import kotlinx.android.synthetic.main.activity_sign_up4.*

class InfoModifyActivity : AppCompatActivity() {
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

        changePW_infoModify.setOnClickListener {
            val intent = Intent(this, PWChangeActivity::class.java)
            startActivity(intent)
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
        val editText1 = findViewById<EditText>(R.id.nickname_infoModify)
        val editText2 = findViewById<EditText>(R.id.lecture_infoModify)
        val editText3 = findViewById<EditText>(R.id.statusMessage_infoModify)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
        imm.hideSoftInputFromWindow(editText2.windowToken, 0)
        imm.hideSoftInputFromWindow(editText3.windowToken, 0)
    }
}