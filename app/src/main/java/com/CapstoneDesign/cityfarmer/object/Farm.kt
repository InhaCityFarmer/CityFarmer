package com.CapstoneDesign.cityfarmer.`object`

data class Farm(
    var name : String = "", //농장 이름
    var address : String = "", //농장 주소
    var users : HashMap<String,String> = hashMapOf(), //농장 구성원   HashMap<UID,sector>
    var lat : Double = 0.0, //농장 latitude 좌표
    var lon : Double = 0.0, //농장 longitude 좌표
    var post : ArrayList<Post> = ArrayList<Post>() //농장 게시글 Arraylist
)
