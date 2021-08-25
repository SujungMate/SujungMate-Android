package com.example.sujungmate.tables

class SujungMateSend(val id:String, val request:Boolean, val fromId: String, val toId:String, val timestamp:Long){
    constructor() : this("",false, "", "", -1)
}