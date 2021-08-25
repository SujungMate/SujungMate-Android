package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_mate_type.*

class MateTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_type)

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mateType)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // 랜덤 매칭
        randomMatchButton.setOnClickListener {
            val intent = Intent(this, MateSearchActivity::class.java)
            startActivity(intent)
        }

        // 세부 설정 후 매칭
        detailsMatchButton.setOnClickListener {
            val intent = Intent(this, MateInfoActivity::class.java)
            startActivity(intent)
        }
    }

    //이전 화면으로 되돌리기 구현
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
}