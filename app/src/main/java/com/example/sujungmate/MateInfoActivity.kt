package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout

class MateInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_info)

        Toast.makeText(application, "MateInfoActivity입니다. 매칭 세부설정 Activity입니다.", Toast.LENGTH_LONG)
            .show()

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // 관심 학과 드롭다운(Spinner)
        val majors = resources.getStringArray(R.array.interesting_major) //전공 배열 가져오기
        val spinner : Spinner = findViewById(R.id.major) // 스피너 가져오기
        ArrayAdapter.createFromResource(
            this,
            R.array.interesting_major,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(    // 동작할 코드, 각 항목의 위치는 position으로 전달됨
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position != 0)
                    Toast.makeText(application, majors[position], Toast.LENGTH_LONG).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        // editText(수강 과목 입력) - 외부 터치시 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_mateInfo) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        // 검색 결과 화면으로 이동하는 버튼(매칭하기)
        findViewById<View>(R.id.goMateSearch).setOnClickListener {
            val intent = Intent(this, MateSearchActivity::class.java)
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

    //키보드 숨기기
    fun hideKeyboard() {
        val editText1 = findViewById<EditText>(R.id.lecture)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }
}