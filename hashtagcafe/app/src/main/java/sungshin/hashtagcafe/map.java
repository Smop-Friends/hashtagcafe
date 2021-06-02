package sungshin.hashtagcafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import sungshin.hashtagcafe.firestore.CafeInfo;



public class map extends AppCompatActivity implements OnMapReadyCallback, Overlay.OnClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    Vector<String> nameVector;
    Vector<Marker> mark;
    HashMap<Marker, String> cafeSet;
    FragmentManager fm;
    MapFragment mapFragment;
    DrawerLayout drawerLayout;
    View drawerView;
    ImageButton draw;
    TextView menu1;
    TextView menu3;
    EditText srchtext;
    ImageButton srchbtn;
//    TextView menu2;
//    String[] listItems;
//    boolean[] checkedItems;
//    ArrayList<Integer> mUserItems = new ArrayList<>();

    //임시버튼
    Button yejinBtn,minjiBtn,ddbb;

    // jenny start
    // pass hashtag selected item to next activity from alert dialog
    public static final String ITEM_SELECTED = "sungshin.hashtagcafe.ITEM_SELECTED";

    // 변수 선언
    // Button mExplore;
    TextView mExplore;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();

    // jenny end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //nameArray = new ArrayList<Object>();
        mark = new Vector<Marker>();
        nameVector = new Vector<String>();
        cafeSet = new HashMap<Marker, String>();

        //Marker test = new Marker();
        //test.setPosition(new LatLng(33.2712, 126.5354));
        //mark.add(test);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawerView);
        draw = (ImageButton) findViewById(R.id.drawerButton);

        draw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(drawerView);
            }
        });

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("haex8ay584")
        );
        fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        // ...
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

       /* menu1 = (TextView) findViewById(R.id.writeReview);
        menu1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), review_main.class);
                startActivity(intent);
            }
        }); */

        menu3 = (TextView) findViewById(R.id.addstore);
        menu3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                startActivity(intent);
            }
        });
//        menu2 = (TextView) findViewById(R.id.searchHash);
//        listItems = getResources().getStringArray(R.array.hashtag_list);
//        checkedItems = new boolean[listItems.length];
//        Log.v("태그","no problem");
//        // 해시태그 선택 후, 선택결과에 따라 카페 목록 보여줌
//        ExploreHashtag();

        getDB();

        srchtext = (EditText) findViewById(R.id.srchtext);
        srchbtn = (ImageButton) findViewById(R.id.srchbtn);

        srchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = srchtext.getText().toString();

                // 다음 페이지 넘어가기 카페 목록 서치 확인
                Intent intent = new Intent(getApplicationContext(), cafesrch.class);
                intent.putExtra("srchcafename", s);
                startActivity(intent);
            };
        });

        //임시버튼 페이지 연결입니다-예진
        yejinBtn= (Button)findViewById(R.id.ha);
        yejinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                startActivity(intent);
            }
        });

        minjiBtn= (Button)findViewById(R.id.kang);
        minjiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), review_main.class);
                startActivity(intent);
            }
        });

        ddbb = (Button)findViewById(R.id.dbdbdb);
        ddbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), displayPhotoActivity.class);
                startActivity(intent);
            }
        });
        // jenny start
        // 위젯 대입
        // mExplore = (Button) findViewById(R.id.BtnExplore);
        mExplore = (TextView) findViewById(R.id.BtnExplore);

        listItems = getResources().getStringArray(R.array.hashtag_list);
        checkedItems = new boolean[listItems.length];

        // 해시태그 선택 후, 선택결과에 따라 카페 목록 보여줌
        ExploreHashtag();
        // jenny end

    }

    public void getDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cafe")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.v("태그","db in for");
                                //Log.d("cafe", document.getId() + " => " + document.getData());
                                CafeInfo cafeInfo = document.toObject(CafeInfo.class);
                                GeoPoint gp = cafeInfo.getCafeGeopoint();
                                String cn = cafeInfo.getCafeName();

                                Marker m = new Marker();
                                m.setPosition(new LatLng(gp.getLatitude(), gp.getLongitude()));
                                Log.v("태그","db after setmap");
                                Log.v("태그",""+ gp.getLatitude()+ ", " + gp.getLongitude());
                                mark.add(m);
                                nameVector.add(cn);
                                cafeSet.put(m,cn);
                            }
                            for(int i = 0; i < mark.size(); i++){
                                Log.v("태그","marking");
                                //mark.get(i).setOnClickListener(this);
                                mark.get(i).setMap(naverMap);
                                mark.get(i).setOnClickListener(new Overlay.OnClickListener() {
                                    @Override
                                    public boolean onClick(@NonNull Overlay overlay) {
                                        String name = cafeSet.get(overlay);

                                        Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                                        intent.putExtra("cafename", name);
                                        startActivity(intent);
                                        Log.v("태그", name);
                                        return false;
                                    }
                                });
                                Log.v("태그","marking set map");
                            }
                        } else {
                            Log.d("cafe", "Error getting documents: ", task.getException());
                        }
                    }
                });

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.v("태그","db before for");
//                for(DataSnapshot mSnapshot: snapshot.getChildren()){
//                    Log.v("태그","db in for");
//                    String values = mSnapshot.child("cafeName").getValue().toString();
//                    GeoPoint gp = (GeoPoint)mSnapshot.child("cafeGeopoint").getValue();
//                    Marker m = new Marker();
//                    m.setPosition(new LatLng(gp.getLatitude(), gp.getLongitude()));
//                    //m.setMap(naverMap);
//                    Log.v("태그","db after setmap");
//                    mark.add(m);
//                    nameArray.add(values);
//                }
//                //String cafeName = snapshot.getValue(String.class);
//                //이름 setText
//            }
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else{
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            Log.v("태그","isHere");
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.v("태그", "TheLast");
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE );
        //이 아래는 권한확인. onRequestPermissionResult 콜백 메소드 호출
        //ActivityCompat.requestPermissisons(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        Log.v("태그","onMapReady");

        /*
        this.naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener(){
            @Override
            public void onCameraChange(int reason, boolean animated){
                freeActiveMarkers();
                LatLng currentPosition = getCurrentPosition(naverMap);
                for(LatLng markerPosition: markersPosition){
                    if(!withinSightMarker(currentPosition, markerPosition))
                        continue;
                    Marker marker = new Marker();
                    marker.setPosition(markerPosition);
                    marker.setMap(naverMap);
                    activeMarkers.add(marker);
                }
            }
        });
        */

        //Marker marker = new Marker();
        //마커 클릭이벤트 추가 //onClick()은 아래에
        //Log.d("태그", ""+mark.size()); // -> 0
//        for(int i = 0; i < mark.size(); i++){
//            Log.v("태그","in for");
//            mark.get(i).setOnClickListener(this);
//            mark.get(i).setMap(naverMap);
//            Log.v("태그","set map");
//        }
    }

    // jenny start
    // 해시태그 선택 후, 선택결과에 따라 카페 목록 보여줌
    private void ExploreHashtag(){
        // multiple choice list dialog
        mExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(map.this);
                mBuilder.setTitle(R.string.dialog_title);

                // User Onclick for the dialog
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        // if the item is checked
//                      if (isChecked) {
                        // and not part of the list
//                          if (!mUserItems.contains(position)) {
                        // add the item
//                              mUserItems.add(position);
//                          }
//                      } else if (mUserItems.contains(position)) {
//                          // else remove it
//                          mUserItems.remove(position);
//                      }

                        if (isChecked){
                            mUserItems.add(position);
                        } else{
                            mUserItems.remove((Integer.valueOf(position)));
                        }

                    }
                });

                mBuilder.setCancelable(false);

                // OK button for positive
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++){
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + "/";  // changed to "/" from ", "
                            }
                        }
                        // not using
                        // mItemSelected.setText(item);

                        // 다음 페이지 넘어가기 카페 목록 서치 확인
                        Intent intent = new Intent(map.this, TagSearchList.class);
                        // pass hashtag selected item to next activity from alert dialog
                        intent.putExtra(ITEM_SELECTED, item);
                        startActivity(intent);

                    }
                });

                // Dismiss button for negative
                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // Clear All button for neutral
                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            // clear the items
                            mUserItems.clear();
                            // not using
                            // start from the empty text
                            // mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                // Show the dialog
                mDialog.show();

            }
        });
    }

    //마커 클릭 이벤트
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if(overlay instanceof Marker) {
//            //각 페이지에 이어지도록...
//            String name = cafeSet.get(overlay);
//
//            Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
//            intent.putExtra("cafename", name);
//            startActivity(intent);
//            Log.v("태그", name);
            return true;
        }
        return false;
    }

}