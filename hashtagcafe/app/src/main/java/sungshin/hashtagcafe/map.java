package sungshin.hashtagcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;

public class map extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    FragmentManager fm;
    MapFragment mapFragment;

    //임시버튼
    Button yejinBtn,minjiBtn;

    // jenny start
    // pass hashtag selected item to next activity from alert dialog
    public static final String ITEM_SELECTED = "sungshin.hashtagcafe.ITEM_SELECTED";

    // 변수 선언
    Button mExplore;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    // jenny end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
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

        // jenny start
        // 위젯 대입
        mExplore = (Button) findViewById(R.id.BtnExplore);

        listItems = getResources().getStringArray(R.array.hashtag_list);
        checkedItems = new boolean[listItems.length];

        // 해시태그 선택 후, 선택결과에 따라 카페 목록 보여줌
        ExploreHashtag();
        // jenny end

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);//Follow 설정
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
        Log.v("태그","Through");
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
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE );
        //이 아래는 권한확인. onRequestPermissionResult 콜백 메소드 호출
        //ActivityCompat.requestPermissisons(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        Log.v("태그","onMapReady");

        Marker marker = new Marker();
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
                                item = item + ", ";
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
    // jenny end

}