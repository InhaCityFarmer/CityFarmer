package com.CapstoneDesign.cityfarmer.`object`

data class Post(
    var writer: String = "",//글쓴이
    var writerUID : String = "",//글쓴이 UID
    var title : String = "", //제목
    var body : String = "" //본문
)
