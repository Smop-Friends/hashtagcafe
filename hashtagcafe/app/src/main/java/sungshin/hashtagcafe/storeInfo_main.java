package sungshin.hashtagcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sungshin.hashtagcafe.firestore.CafeInfo;
import sungshin.hashtagcafe.firestore.ReviewInfo;


public class storeInfo_main extends AppCompatActivity implements OnMapReadyCallback {

    //private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    FragmentManager fm;
    MapFragment mapFragment;

    int reviewcafeRating;

    public String name; //수정화면에서 받는 카페이름
    String passName;
    public int count=0;
    TextView cafeNameInfo,cafeIntroInfo,cafeLocationInfo,cafeTimeInfo,cafeMenuInfo,cafeCheckInfo;
    ImageButton mapBtn;
    Button modifyBtn;
    LinearLayout back1,back2;
    storeInfo_modify m = (storeInfo_modify) storeInfo_modify.modi;
    boolean open=true;
    final Geocoder geocoder = new Geocoder(this);
    double lat,lon;

    //리뷰리스트
    ArrayList<storeInfo_reviewlist> dataList = new ArrayList<>();
    final storeInfo_reviewlistAdapter adapter = new storeInfo_reviewlistAdapter(this,dataList);
    int cafe = R.drawable.coffeegood;
    String userID;
    Intent intent5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info_main);

        mapBtn= (ImageButton) findViewById(R.id.map);
        modifyBtn= (Button)findViewById(R.id.modify);

        cafeNameInfo=(TextView)findViewById(R.id.cafeName_Info);
        cafeIntroInfo=(TextView)findViewById(R.id.introduce_info);
        cafeLocationInfo=(TextView)findViewById(R.id.location);
        cafeTimeInfo=(TextView)findViewById(R.id.time);
        cafeMenuInfo=(TextView)findViewById(R.id.menu_info);
        cafeCheckInfo=(TextView)findViewById(R.id.check_info);

        // jenny 0525
        // jenny start
        // fromHashtagCafeList();
        // jenny end

        //데이터패씽하고 m.finish();하기 main->modify   (name)보내기
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passName = cafeNameInfo.getText().toString();
                Intent intent = new Intent(getApplicationContext(), storeInfo_modify.class);
                intent.putExtra("toModi", passName);
                startActivity(intent);
                finish();

            }
        });
        //modify->main   (name_mod)받기
        Intent intent = getIntent();
        String data = intent.getStringExtra("toMain");//From modify
        if(data!=null)
        {
            Log.i("카페","데이터 null아님");
            showDocTxt(data);
           // String s= data;
            showImgsInFolder(data);
            selectWhereDoc(data);
            //cafeNameInfo.setText(data);
           // showDocTxt(cafeCheckInfo.getText().toString());
        }
        else if(data==null)
           Log.i("카페","데이터 null임");

        Intent intent2 = getIntent();
        String data2=intent2.getStringExtra("cafename");//From jenny
       //Log.i("카페",data);
        if(data2!=null)
        {
            Log.i("카페","데이터2 null아님 "+data2);
            showDocTxt(data2);
            showImgsInFolder(data2);
            selectWhereDoc(data2);
            //cafeNameInfo.setText(data);
            // showDocTxt(cafeCheckInfo.getText().toString());
        }
        else if(data2==null)
            Log.i("카페","데이터2 null임");

        Intent intent3 = getIntent();
        String data3=intent3.getStringExtra("cafename");//From sujung
        if(data3!=null)
        {
            Log.i("카페","데이터3 null아님 "+data3);
            showDocTxt(data3);
            showImgsInFolder(data3);
            selectWhereDoc(data3);
            //cafeNameInfo.setText(data);
            // showDocTxt(cafeCheckInfo.getText().toString());
        }
        else if(data3==null)
            Log.i("카페","데이터3 null임");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        //민지언니 페이지로 이동+ 카페이름 넘김
        Button addReview=(Button)findViewById(R.id.addReview);
        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent5 = new Intent(getApplicationContext(), review_main.class);
                intent5.putExtra("cafename", cafeNameInfo.getText().toString());
                Log.i("리뷰로 넘김", cafeNameInfo.getText().toString());
                selectWhereDoc2(data2);
            }
        });
        //selectWhereDoc(data2);
        //showDocTxt(name);
        //
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("haex8ay584")
        );
        fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map_s);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_s, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        // ...
        LinearLayout lay=(LinearLayout)findViewById(R.id.lay1);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(open)
              {
                    Log.i("됨","됨");
                    open=false;
                    lay.setVisibility(View.VISIBLE);
                    changeLocation();
                    //Marker test = new Marker();
                    //test.setPosition(new LatLng(37.5823329,126.98130479999999));
                    //test.setMap(naverMap);

                 // CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(
                  //        new LatLng(33.2712, 126.5354),15)
                   ///       .animate(CameraAnimation.Fly, 3000);

                //  naverMap.moveCamera(cameraUpdate);

                    /*
                    Marker marker1 = new Marker();
                    marker1.setPosition(new LatLng(lat,lon));
                    marker1.setMap(naverMap);*/
                }
                else
                {
                    open=true;
                    lay.setVisibility(View.GONE);
                }
            }});
        final Geocoder geocoder = new Geocoder(this);


             /*   //////////////////////////////////////////////////////////////////////////////////////////////////
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //주소지->위도 경도

                // 주소입력후 지도2버튼 클릭시 해당 위도경도값의 지도화면으로 이동
                List<Address> list = null;
                String str = cafeLocationInfo.getText().toString();
                try {
                    list = geocoder.getFromLocationName
                            (str, // 지역 이름
                                    10); // 읽을 개수
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0) {
                        Toast.makeText (getApplicationContext(),"해당되는 주소 정보는 없습니다",Toast.LENGTH_SHORT).show( );
                        //tv.setText("해당되는 주소 정보는 없습니다");
                    } else {
                        // 해당되는 주소로 인텐트 날리기
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();

                        String sss = String.format("geo:%f,%f", lat, lon);

                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(sss));
                        startActivity(intent);
                    }
                }
            }
        });
*/


        //리뷰리스트
        RecyclerView recyclerView = findViewById(R.id.recyclerview_main_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    //매장사진
    public class MyGalleryAdapter2 extends BaseAdapter {

        Context context;

        Integer[] posterID2 = { R.drawable.p1,R.drawable.p2,R.drawable.p3 };//////////////<--------

        public MyGalleryAdapter2(Context c) {
            context = c;
        }

        public int getCount() {
            return posterID2.length;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview = new ImageView(context);
            imageview.setLayoutParams(new Gallery.LayoutParams(200, 300));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview.setPadding(5, 5, 5, 5);
            imageview.setImageResource(posterID2[position]);

            final int pos = position;


            return imageview;
        }
    }
    private void showDocTxt(String n){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("cafe").document(n);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        CafeInfo cafeInfo = document.toObject(CafeInfo.class);

                        cafeNameInfo.setText(cafeInfo.getCafeName());
                        cafeIntroInfo.setText(cafeInfo.getCafeIntroduce());
                        cafeLocationInfo.setText(cafeInfo.getCafeLocation());

                        cafeTimeInfo.setText(cafeInfo.getTime());
                        cafeMenuInfo.setText(cafeInfo.getCafeMenu());
                        String s=cafeInfo.getCheckList();
                        String ss="";
                        String[] arr2 = s.split("/");
                        for(int k=0;k<arr2.length;k++)
                            ss+=arr2[k]+" . ";
                        cafeCheckInfo.setText(ss);

                    }
                    else
                    {
                        Log.d("cafe", "No such document");
                        ///firestoreshowdatatxt.setText("No such document");
                    }
                }
                else
                {
                    Log.d("cafe", "get failed with ", task.getException());
                    //firestoreshowdatatxt.setText("failed");
                }
            }
        });
    }

    private void selectWhereDoc(String n)
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        //String newUserID = "arbffUC3Fbpo6yqHttuY";  ///storage/키에리arbffUC3Fbpo6yqHttuY
        //newUserID = newUserID.trim();
        // System.out.println("storage/"+newFolderName+"/"+newUserID);
        StorageReference listRef = storage.getReference().child("storage/"+n);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("review")
                .whereEqualTo("cafeName", n)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReviewInfo reviewInfo = document.toObject(ReviewInfo.class);
                                reviewcafeRating = reviewInfo.getRating();
                                //Uri uri;
                                //Log.d("review", document.getId() + " => " + document.getData()+i);
                                dataList.add(new storeInfo_reviewlist(cafe,reviewInfo.getHashtags(),reviewcafeRating, reviewInfo.getReviewTxt(),
                                        reviewInfo.getCafeName(), reviewInfo.getUserid(),reviewInfo.getImgCount()));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("review", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    private void selectWhereDoc2(String n)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("storage/"+n);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("review")
                .whereEqualTo("cafeName", n)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReviewInfo reviewInfo = document.toObject(ReviewInfo.class);
                                if(reviewInfo.getUserid().equals(userID)){
                                    Toast.makeText(getApplicationContext(),"리뷰는 한번만 작성할 수 있습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            startActivity(intent5);
                        } else {
                            Log.d("review", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }



    private void showAllDocTxt(){ //데베읽어서 출력하기 함수
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cafe")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("cafe", document.getId() + " => " + document.getData());
                                CafeInfo cafeInfo = document.toObject(CafeInfo.class);
                                //firestoreshowdatatxt.append("name = "+cafeInfo.getCafeName()+"\n"+"geopoint = "+cafeInfo.getCafeGeopoint()+"\n\n");
                            }
                        } else {
                            Log.d("cafe", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    // jenny 0525
    // jenny start
    private void fromHashtagCafeList() {
        Intent intent = getIntent();
        String documentData = intent.getStringExtra(TagSearchList.GOTO_INFOPAGE);

        // TextView testTV = findViewById(R.id.cafeNamePass);
        // testTV.setText(documentData);
    }
    // jenny end
    public void showImgsInFolder(String s) {
        //Toast.makeText(getApplicationContext(), "버튼누름", Toast.LENGTH_SHORT).show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("storage/"+s+"/"+"store");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            LinearLayout layout = (LinearLayout) findViewById(R.id.imgLayout2);
                            //imageview 동적생성
                            ImageView iv = new ImageView(storeInfo_main.this);
                            //iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            iv.setLayoutParams(new ViewGroup.LayoutParams(300,300));
                            iv.setPadding(10,10,10,10);
                            layout.addView(iv);


                            // reference의 item(이미지) url 받아오기
                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        // Glide 이용하여 이미지뷰에 로딩
                                        Glide.with(storeInfo_main.this)
                                                .load(task.getResult())
                                                .into(iv);
                                    } else {
                                        // URL을 가져오지 못하면 토스트 메세지
                                        Toast.makeText(storeInfo_main.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Uh-oh, an error occurred!
                                }
                            });
                        }
                    }
                });
    }

    //지도

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Marker test = new Marker();
        test.setPosition(new LatLng(lat, lon));
        test.setMap(naverMap);
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(
                new LatLng(lat, lon),15)
                .animate(CameraAnimation.Fly, 3000);

        naverMap.moveCamera(cameraUpdate);
        //naverMap.setLocationSource(locationSource);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        //이 아래는 권한확인. onRequestPermissionResult 콜백 메소드 호출
        //ActivityCompat.requestPermissisons(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        Log.v("태그", "onMapReady");
    }

    void changeLocation()
    {
        List<Address> list = null;
        String str =cafeLocationInfo.getText().toString();
        try {
            list = geocoder.getFromLocationName(
                    str, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null) {
            if (list.size() == 0) {
                Toast.makeText (getApplicationContext(),"해당되는 주소 정보는 없습니다",Toast.LENGTH_SHORT).show( );
            } else {

                Log.i("위치","국가명 "+ list.get(0).getCountryName());
                Log.i("위치","위도 "+ list.get(0).getLatitude());
                Log.i("위치","경도 "+ list.get(0).getLongitude());
                lat=list.get(0).getLatitude();
                lon=list.get(0).getLongitude();

            }
        }
    }

}