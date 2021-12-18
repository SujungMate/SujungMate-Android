var express = require('express');
var app = express();
var template = require('./lib/template.js');
var qs = require('querystring');
//const { request } = require('http');
var mysql = require('mysql');
const { request, response } = require('express');
const path = require('path');
// var sanitizeHtml = require('sanitize-html')
// 태그를 막고 싶은데 적용이 안되네..


var port = 3000

var connection = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'godwikimiki25!',
    database:'diary'
});

connection.connect();

connection.query('select * from diary_tb', function(err, results){
    if(err){
       console.log(err);
    } 
});

// 테스트용
app.get('/', (req, res) => {
  res.send('Hello World!')
});

app.get('/diary_register', (req, res) => {
    // diary_id 증가 시키기
    var title = "일기 쓰기";
    var body = `
        <div id="wrapper">
        <h2 id="title">일기 작성</h2>
        <form action="/diary_register_process" method="post">
            <input type="hidden" name="writer_id" value="default">
            <input type="hidden" name="group_id" value="default">
            <div id="top_div">
                <div class="top_div_left">
                    <h4>제목</h4>
                    <input type="text" name="title" placeholder="일기 제목을 입력해주세요.">
                </div>
                <div class="top_div_right">
                    <h4>오늘의 날씨</h4>
                    <div class="weather">
                        <input type="hidden" value="" id="weather" name="weather">
                        <input type="radio" name="weather" value="sunny" > 맑음
                        <input type="radio" name="weather" value="cloudy" > 흐림
                        <input type="radio" name="weather" value="rainy" > 비
                        <input type="radio" name="weather" value="thunder" > 번개
                        <input type="radio" name="weather" value="snowy" > 눈
                    </div>
                </div>
            </div>

            <div id="middle_div">
                <h4>내용</h4>
                <textarea name="content" placeholder="내용을 입력해주세요."></textarea>
            </div>

            <div id="bottom_div">
                <input type="submit" value="완료">
            </div>

        </form>
    </div>
    `

    var diary_register = template.REGISTER(title, body);

    return res.send(diary_register);
});

app.post('/diary_register_process',(req,res)=>{
    var body = '';
    req.on('data',function(data){
        body = body +data;
    });
    req.on('end',function(){
        var post = qs.parse(body);
        var diary_id = parseInt(post.diary_id);
        var writer_id = post.writer_id;
        var group_id = post.group_id;
        var title = post.title;
        var content = post.content;
        var weather = parseInt(post.weather);

        
        // Q. weather이 문제
        connection.query(`INSERT INTO diary_tb (writer_id, group_id, title, content, weather, writer_day, update_day) VALUES(2, 2, ?, ?, 'suuny', NOW(), NOW())`, 
            [title, content, weather], function(error, topics){
            if(error){
                throw error;
            }
     
            res.writeHead(302, {Location: '/mygroup'});
            res.end();
        });
        
    })
});

app.get(('/mygroup'),(req,res)=>{
    // 나중에 writer_id 출력 대신 writer 이름 출력으로 수정 (join 해서)
    // group 테이블하고도 join해서 해당 그룹에 맞는 일기 보여주기
    // 지금은 내 그룹으로 전체 내 그룹 합쳐서 보여주고 있음
    connection.query('SELECT diary_id, writer_id, group_id, title, content, weather, date_format(writer_day, "%Y/%m/%d") writer_day, date_format(update_day,"%Y/%m/%d") update_day FROM diary_tb', function(error,diarys) {
        if(error){
            throw error;
        }

        console.log(diarys);
        //console.log(diarys.length);

        var title = "그룹 일기";
        // 그룹 이름도 테이블 join해서 넣기
        var groupName = "그룹1"

        var i = 0;
        var diary_list = '';
        while(i<diarys.length){
            diary_list = diary_list + `<div id="diary_div">
            <div id="diary_top_div">
                <div class="diary_top_div_left">
                    <img>
                </div>
                <div class="diary_top_div_middle">
                    <h4>${diarys[i].writer_id}</h4>
                    <h5>${diarys[i].update_day}${diarys[i].weather}</h5>
    
                </div>` +
                // 글쓴이 확인하고 수정 삭제
                `<div class="diary_top_div_right">
                    
                    <div class="div_btn_float_right">
                    <form method="post" action="/delete_process">
                        <input type="hidden" name="diary_id" value="${diarys[i].diary_id}">
                        <input type="hidden" name="writer_id" value="${diarys[i].writer_id}">
                        <input type="hidden" name="group_id" value="${diarys[i].group_id}">
                        <input type = "submit" class="diary_btn" value= "삭제">
                    </form></div>
                    <div class="div_btn_float_right"><input type = "submit" class="diary_btn" value= "수정" onclick = "location.href = '/diary_modify/${diarys[i].diary_id}'"></div>
                    
                </div>` +
            ` </div>
            <div id="diary_middle_div">
                <h4>${diarys[i].title}</h4>
                <h5>${diarys[i].content}</h5>
            </div>
        </div>`
        i = i+1;
        }

        var MYGROUP = template.MYGROUP(title,groupName,diary_list);

    
        res.writeHead(200);
        res.end(MYGROUP);
    });
})

app.get(('/new'),(req,res)=>{
    // 나중에 writer_id 출력 대신 writer 이름 출력으로 수정 (join 해서)
    connection.query('SELECT diary_id, writer_id, group_id, title, content, weather, date_format(writer_day, "%Y/%m/%d") writer_day, date_format(update_day,"%Y/%m/%d") update_day FROM diary_tb WHERE date_format(writer_day, "%Y-%m-%d") = CURDATE() ', function(error,diarys) {
        if(error){
            throw error;
        }

        console.log(diarys);
        //console.log(diarys.length);

        // 그룹 이름도 테이블 join해서 넣기
        var groupName = "그룹1"
        var title = "NEW!";
        var i = 0;
        var diary_list = '';
        while(i<diarys.length){
            diary_list = diary_list + 
            `
            <div id="diary_div">
                <div id="diary_top_div">
                    <div class="diary_top_div_left">
                        <img>
                    </div>
                    <div class="diary_top_div_middle">
                        <h4>${diarys[i].writer_id} / ${groupName}</h4>
                        <h5>${diarys[i].writer_day}</h5>
                    </div>
                    
                </div>
                <div id="diary_middle_div">
                    <h4>${diarys[i].title}</h4>
                    <h5>${diarys[i].content}</h5>
                </div>
            </div>
            `;
        i = i+1;
        }

        var NEW_DIARY = template.NEW_DIARY(title, diary_list);

    
        res.writeHead(200);
        res.end(NEW_DIARY);
    });
})

app.get('/diary_modify/:diary_id', (req, res) => {
    var title = "일기 수정";
    //var _url = request.url;
    //var queryData = url.parse(_url, true).query;
    var diary_id = path.parse(req.params.diary_id).base;

    // '만약 현재 로그인한 유저가 작성자이면' 추가
    connection.query('SELECT * FROM diary_tb',function(err,diarys){
        if(err){
            console.log(err);
            throw err;
        }
        connection.query('SELECT * FROM diary_tb WHERE diary_id=?',[diary_id],function(err2,diary){
            if(err2){
                console.log(err2);
                throw err2;
            }

            var update_diary = template.DIARY_UPATE(title,`
            <form action="/diary_modify/update_process" method="post">
            <div id="top_div">
                <div class="top_div_left">
                    <h4>제목</h4>
                    <input type="hidden" name="diary_id" value="${diary_id}">
                    <input type="hidden" name="writer_id" value="${diary[0].writer_id}">
                    <input type="hidden" name="group_id" value="${diary[0].group_id}">
                    <input type="text" name="title" placeholder="수정할 일기 제목을 입력해주세요." value="${diary[0].title}">
                </div>
                <div class="top_div_right">
                    <h4>오늘의 날씨</h4>
                    <div class="weather">
                        <input type="hidden" value="" id="weather" name="weather">
                        <input type="button" value="☀️" id="sunny" onclick="weatherChange('sunny')">
                        <input type="button" value="🌥" id="cloudy" onclick="weatherChange('cloudy');">
                        <input type="button" value="🌧" id="rainy" onclick="weatherChange('rainy');">
                        <input type="button" value="⛈" id="thunder" onclick="weatherChange('thunder');">
                        <input type="button" value="🌨" id="snowy" onclick="weatherChange('snowy');">
                    </div>
                </div>
            </div>

            <div id="middle_div">
                <h4>내용</h4>
                <textarea name="content" placeholder="수정할 내용을 입력해주세요.">${diary[0].content}</textarea>
            </div>

            <div id="bottom_div">
                <input type="submit" value="완료">
            </div>

        </form>`);   
        res.writeHead(200);
        res.end(update_diary);
    });
    });

});

app.post('/diary_modify/update_process', function(req,res){
    var body = '';
    req.on('data', function(data){
        body = body + data;
    })
    req.on('end',function(){
        var post = qs.parse(body);
        var diary_id = parseInt(post.diary_id);
        var writer_id = parseInt(post.user_id);
        var group_id = parseInt(post.group_id);
        var title = post.title;
        var content = post.content;
        var weather = "cloudy";
        //var weather = parseInt(post.weather);

        connection.query("UPDATE diary_tb SET title=?, content=?, weather=?, update_day=curdate() WHERE diary_id=?",
        [title,content,weather,diary_id],function(err){
            if(err){
                console.log(err);
                throw err;
            }
            res.writeHead(302,{Location: '/mygroup'});
            res.end()
        });
    });
});

app.post('/delete_process',function(req,res){
    var body = '';
    req.on('data',function(data){
        body = body + data;
    });
    req.on('end',function(){
        var post = qs.parse(body);
        var writer_id = post.writer_id;
        var diary_id = post.diary_id;

        // 로그인한 유저의 아이디가 글 쓴 아이디와 동일하면 delete하게 하기
        // '만약 현재 로그인한 유저가 작성자이면' 추가
        connection.query(`DELETE FROM diary_tb WHERE diary_id=?`,[diary_id], function(error, diary){
            if(error){
                console.log(error);
                throw error;
            }
            res.writeHead(302, {Location: '/mygroup'});
            res.end();
        });
        
    })
})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})