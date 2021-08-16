package com.example.sujungmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        // 뒤로가기 버튼
        val toolbar = findViewById<Toolbar>(R.id.toolbar_mateInfo)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        // editText(수강 과목 입력) - 외부 터치시 키보드 내리기
        val outer_layout = findViewById<ConstraintLayout>(R.id.layout_mateInfo) // 레이아웃 가져오기
        outer_layout.setOnClickListener {
            hideKeyboard()
        }

        //관심학과, MBTI, 관심사에 대한 spinner 세팅
        SpinnerSettings(findViewById(R.id.major_mateInfo), R.array.interesting_major)
        SpinnerSettings(findViewById(R.id.MBTI_mateInfo), R.array.MBTI_type)
        SpinnerSettings(findViewById(R.id.interest1_mateInfo), R.array.large_category)

        // 검색 결과 화면으로 이동하는 버튼(매칭하기)
        findViewById<View>(R.id.goMateSearch).setOnClickListener {
            // 최종 검색어 가져오기
            var RELATIONSHIP_STATE : ArrayList<ToggleButton> = arrayListOf() // 토글버튼 상태 리스트(후배, 동기, 선배)
            RELATIONSHIP_STATE.add(findViewById(R.id.junior_mateInfo))
            RELATIONSHIP_STATE.add(findViewById(R.id.classmate_mateInfo))
            RELATIONSHIP_STATE.add(findViewById(R.id.senior_mateInfo))

            //1. 관계
            var RELATIONSHIP : String = ""
            for(state in RELATIONSHIP_STATE) {
                if(state.isChecked)
                    RELATIONSHIP = state.text.toString()
            }

            // 2. 관심학과
            var MAJOR : String? = findViewById<Spinner>(R.id.major_mateInfo).selectedItem.toString()


            // 3. 수강과목(띄어쓰기 없애기)
            var LECTURE : String? = findViewById<EditText>(R.id.lecture_mateInfo).text.toString().trim()

            // 4. MBTI
            var MBTI : String = findViewById<Spinner>(R.id.MBTI_mateInfo).selectedItem.toString()
            Log.d("선택 아이템 : ", MBTI)

            // 5. 관심사
            var INTEREST : String? = findViewById<Spinner>(R.id.interest1_mateInfo).selectedItem.toString()

            if(RELATIONSHIP != "") {
                // 검색필터 클래스 생성(SearchFilter - 한꺼번에..)
                val intent = Intent(this, MateSearchActivity::class.java)
                intent.putExtra("search_Filter", SearchFilter(RELATIONSHIP, MAJOR, LECTURE, MBTI, INTEREST))
                startActivity(intent)
            }
            else
                Toast.makeText(application, "짝수정 관계는 필수 입력 항목입니다.", Toast.LENGTH_LONG).show()


        }
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

        // 드롭다운 선택 시
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(    // 동작할 코드, 각 항목의 위치는 position으로 전달됨
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position != 0)
                    Toast.makeText(application, spinner.selectedItem.toString(), Toast.LENGTH_LONG).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

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
        val editText1 = findViewById<EditText>(R.id.lecture_mateInfo)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText1.windowToken, 0)
    }
}