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
import com.example.sujungmate.tables.SearchFilter

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

        // 관심사1 - 대분류 스피너 설정
        val mainCategorySpinner = findViewById<Spinner>(R.id.interest1_mateInfo)
        val mainSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.large_category,
            android.R.layout.simple_spinner_item
        )
        mainCategorySpinner.adapter = mainSpinnerAdapter // 어댑터 연결
        mainSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 관심사2 - 소분류 스피너 설정
        val subCategorySpinner = findViewById<Spinner>(R.id.interest2_mateInfo)
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
                        this@MateInfoActivity,
                        R.array.sub_category_title,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 1) {    //엔터테인먼트·예술
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@MateInfoActivity,
                        R.array.sub_category_entertainment,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 2) { //생활·노하우·쇼핑
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@MateInfoActivity,
                        R.array.sub_category_dailyLife,
                        android.R.layout.simple_spinner_item
                    )
                } else if (position == 3) {  //취미·여가·여행
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@MateInfoActivity,
                        R.array.sub_category_hobby,
                        android.R.layout.simple_spinner_item
                    )
                } else {    //지식·동향(4)
                    subSpinnerAdapter = ArrayAdapter.createFromResource(
                        this@MateInfoActivity,
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

        // 검색 결과 화면으로 이동(매칭하기)
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
//            Log.d("선택 아이템 : ", MBTI)

            // 5. 관심사 (소분류 가져오기)
            var INTEREST : String? = subCategorySpinner.selectedItem.toString()

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

        // 스피너 동작 감지
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(    // 동작할 코드, 각 항목의 위치는 position으로 전달됨
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if(position != 0)
//                    Toast.makeText(application, spinner.selectedItem.toString(), Toast.LENGTH_LONG).show()

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