package com.CapstoneDesign.cityfarmer.`object`

data class User(
    var UID : String = "", //UID
    var name : String = "", //이름
    var firstLogin : Boolean = false, // 첫 로그인 판별
    var farmName : String = "", // 농장 이름
    var address : String = "", //농장 주소
    var sector : String = "", //농장 농지 번호
    var crop : ArrayList<HashMap<String,Array<Double>>> = ArrayList<HashMap<String,Array<Double>>>(), //유저가 키우는 작물 센서값 Array<Crop>과 동일함
    var inventory : ArrayList<Item> = ArrayList<Item>(),// 인벤토리
    var prev_chat : HashMap<String,String> = HashMap<String,String>()// 채팅 목록
)
