package com.CapstoneDesign.cityfarmer.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.adapter.InventoryRecyclerViewAdapter
import com.CapstoneDesign.cityfarmer.adapter.MapRecyclerViewAdapter
import com.CapstoneDesign.cityfarmer.databinding.ActivityInventoryBinding
import com.CapstoneDesign.cityfarmer.databinding.ActivityMapBinding
import com.CapstoneDesign.cityfarmer.`object`.Farm
import com.CapstoneDesign.cityfarmer.`object`.Item
import com.CapstoneDesign.cityfarmer.`object`.Post
import com.CapstoneDesign.cityfarmer.`object`.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource

class MapActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private lateinit var arrayListMarker: ArrayList<Marker>
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private lateinit var binding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var adapter : MapRecyclerViewAdapter
    private lateinit var mypost : ArrayList<Post>

    // onCreate에서 권한을 확인하며 위치 권한이 없을 경우 사용자에게 권한을 요청한다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_map)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }
        //데이터 바인딩 하기 위한 설정
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //mypost 배열을 사용하기 전 초기화 해주기
        mypost = ArrayList<Post>()
        //파이어베이스 권한 생성 및 받아오기
        auth = Firebase.auth
        //파이어스토어 DB 접근 객체 얻어오기
        db = FirebaseFirestore.getInstance()
        // 어댑터 객체 할당
        //this@MapActivity.adapter = MapRecyclerViewAdapter(mypost)

        //글쓰기 버튼
        binding.btnPost.setOnClickListener {
            // 누르면 PostActivity로 intent함
            val intent = Intent(this,PostActivity::class.java)
            startActivity(intent)
        }

        //검색 버튼
        binding.btnSearch.setOnClickListener {
            //유저가 입력한 검색어 가져옴
            val textSearch = binding.editTextSearchField.text
            mypost = ArrayList<Post>()
            db.collection("Farm").get().addOnSuccessListener {documents ->
                if(documents.isEmpty) {}
                else {
                    for(document in documents) {//DB에 존재하는 모든 농장 불러옴
                        var farm = document.toObject(Farm::class.java)//Farm 객체 타입으로 임시 저장
                        var tempPostLists = farm.post
                        for( tempPostList in tempPostLists)
                        {
                            //유저가 입력한 검색어를 포함하는 게시글 제목이 있으면 해당 게시글을
                            //mypost에 추가해서 리사이클러뷰에 띄워준다.
                            if(tempPostList.title.contains(textSearch))
                            {
                                mypost.add(tempPostList)
                            }
                        }
                    }
                    //유저가 입력한 검색어로 바텀 시트 타이틀 변경 및 검색어를 포함하는 게시글 띄움
                    setRecyclerView(textSearch.toString())
                }
            }

            //바텀시트 펼치기 위해 bottomBehavior 할당
            val bottomBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
            //바텀시트 펼침
            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }




    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    // hasPermission()에서는 위치 권한이 있을 경우 true를, 없을 경우 false를 반환한다.
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // 현재 위치
        naverMap.locationSource = locationSource
        // 현재 위치 버튼 기능
        naverMap.uiSettings.isLocationButtonEnabled = true
        // 위치를 추적하면서 카메라도 따라 움직인다.
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        arrayListMarker = ArrayList<Marker>()
        db.collection("Farm").get().addOnSuccessListener {documents ->
            if(documents.isEmpty) {}
            else {
                for(document in documents) {//DB에 존재하는 모든 농장 불러옴
                    var farm = document.toObject(Farm::class.java)//Farm 객체 타입으로 임시 저장
                    var marker = Marker()//임시 마커 생성
                    marker.position = LatLng(farm.lat,farm.lon) //가져온 농장의 좌표로 마커 좌표 설정
                    marker.map = naverMap //마커 맵을 naver map으로 지정
                    marker.captionText = farm.name //마커의 이름을 농장 이름으로 설정
                    marker.onClickListener = this@MapActivity //마커 클릭시 실행 할 온클릭 리스너 설정
                    arrayListMarker.add(marker)
                }
            }
        }
    }

    //마커 클릭시 실행되는 마커클릭리스너를 오버라이드 하여 클릭한 농장의 게시글이 바텀시트에 뜨게 한다.
    private val markerClickListner = object : Overlay.OnClickListener{
        override fun onClick(overlay: Overlay): Boolean {
            if(overlay is Marker){
                    //내 인벤토리의 아이템 리스트 전부 가져오기 시작
                    db.collection("Farm").document(overlay.captionText).get()
                        .addOnSuccessListener {document ->
                            //만약 document가 null이 아니라면 해당 경로에 데이터가 존재하는 것이므로 가져온다.
                            if( document != null)
                            {
                                //mypost 초기화
                                mypost = ArrayList<Post>()
                                //클릭한 농장 객체 가져오기
                                val myFarm = document.toObject<Farm>(Farm::class.java)
                                //내 인벤토리 가져오기
                                val tempList : ArrayList<Post> = myFarm!!.post
                                for( i in tempList )
                                {
                                    mypost.add(i)
                                }
                            }
                            setRecyclerView(overlay.captionText)
                        }
            }
            return false
        }
    }

    override fun onClick(overlay: Overlay): Boolean {
        return markerClickListner.onClick(overlay)
    }

    private fun setRecyclerView(farm: String){
        // 리사이클러뷰 설정
        runOnUiThread{
            this@MapActivity.adapter = MapRecyclerViewAdapter(mypost) // 어댑터 객체 할당
            binding.bottomSheet.bottomSheetTitleTextView.text = farm
            binding.bottomSheet.recyclerViewMap.adapter = this@MapActivity.adapter // 리사이클러뷰 어댑터로 위에 만든 어댑터 올리기
            binding.bottomSheet.recyclerViewMap.layoutManager = LinearLayoutManager(this@MapActivity) // 레이아웃 매니저 설정
            adapter.notifyDataSetChanged()
            adapter.setItemClickListener(object : MapRecyclerViewAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    // 클릭 시 이벤트 작성
//                    Toast.makeText(v.context,
//                        "${mypost[position].title}\n${mypost[position].writer}",
//                        Toast.LENGTH_SHORT).show()

                    val intent = Intent(baseContext, ViewPostActivity::class.java)
                    intent.putExtra("title", mypost[position].title)
                    intent.putExtra("body", mypost[position].body)
                    intent.putExtra("writer", mypost[position].writer)
                    intent.putExtra("writerUID", mypost[position].writerUID)
                    startActivity(intent)
                }
            })
        }
    }
}