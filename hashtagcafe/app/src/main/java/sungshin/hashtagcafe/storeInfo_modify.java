package sungshin.hashtagcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sungshin.hashtagcafe.cloudStorage.UploadInfo;
import sungshin.hashtagcafe.firestore.CafeInfo;


public class storeInfo_modify extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback{


    //private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    FragmentManager fm;
    MapFragment mapFragment;

    int PICK_IMAGE_MULTIPLE = 1;
    public static Activity modi;
    String passName2,newFolderName;
    boolean open=true;
    double lat,lon;
    final Geocoder geocoder = new Geocoder(this);
    HashMap<String,String> hashMenu = new HashMap<String,String>();
    TextView checkCovid,checkInterior,checkCafe,checkMenu;
    Boolean textCovidClick=true,textInteriorClick=true,textCafeClick=true,textMenuClick=true;
    //카페시간은 텍스트뷰로바꾸기
    EditText editCafeName,editCafeMenu,introduce,editMenuPrice,deleteMenuName,deleteMenuprice,editlocation;
    TextView cafeLocation,cafeTimetxt;
    TextView menuTxt1,menuTxt2;
    ImageButton addMenu,delectMenu,mapBtn;
    Button modify2,add01,add02;
    TimePicker cafeOpen,cafeClose;
    String openMin ,  closeMin,setResult="";
    String name_mod;
    String result = "";

    CheckBox [] checkBoxCovids=new CheckBox[10];
    CheckBox [] checkBoxInteriors=new CheckBox[10];
    CheckBox [] checkBoxCafes=new CheckBox[10];
    CheckBox [] checkBoxMenus=new CheckBox[10];

    public boolean [] checkResultCovid=new boolean[15];
    public boolean [] checkResultInterior=new boolean[15];
    public boolean [] checkResultMenu=new boolean[15];
    public boolean [] checkResultCafe=new boolean[15];

    Integer[] checkBoxCovidId={R.id.check_covid19_1,R.id.check_covid19_2,R.id.check_covid19_3,
            R.id.check_covid19_4,R.id.check_covid19_5};
    Integer[] checkBoxInteriorId={R.id.check_interior_1,R.id.check_interior_2,R.id.check_interior_3,R.id.check_interior_4,
            R.id.check_interior_5,R.id.check_interior_6,R.id.check_interior_7,R.id.check_interior_8,R.id.check_interior_9};
    Integer[] checkBoxCafeId={R.id.check_cafe_1,R.id.check_cafe_2,R.id.check_cafe_3,R.id.check_cafe_4,R.id.check_cafe_5,
            R.id.check_cafe_6,R.id.check_cafe_7,R.id.check_cafe_8};
    Integer[] checkBoxMenuId={R.id.check_menu_1,R.id.check_menu_2,R.id.check_menu_3,R.id.check_menu_4,R.id.check_menu_5,
            R.id.check_menu_6,R.id.check_menu_7,R.id.check_menu_8};

    //firestore 관련
    private ArrayList<review_photo> mArrayList2;
    private storeInfo_photoAdapter mAdapter2;
    private int count = 0;
    private ArrayList<String> mImgPathList = new ArrayList<>();
    //new
    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info_modify);

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE );

        checkCovid=(TextView)findViewById(R.id.checkCovid);
        checkInterior=(TextView)findViewById(R.id.checkInterior);
        checkCafe=(TextView)findViewById(R.id.checkCafe);
        checkMenu=(TextView)findViewById(R.id.checkMenu);

        editCafeName=(EditText)findViewById(R.id.editCafeName);
        editCafeMenu=(EditText)findViewById(R.id.editMenuName);
        editMenuPrice=(EditText)findViewById(R.id.editMenuPrice);
        introduce=(EditText)findViewById(R.id.introduce);
        deleteMenuName=(EditText)findViewById(R.id.deleteMenuName);
        deleteMenuprice=(EditText)findViewById(R.id.deleteMenuPrice);
        editlocation=(EditText)findViewById(R.id.editlocation);

        cafeLocation=(TextView)findViewById(R.id.location);//수정필요
        //cafeClose=(TextView)findViewById(R.id.addTime);
        menuTxt1=(TextView) findViewById(R.id.menuTxt1);
        //menuTxt2=(EditText) findViewById(R.id.menuTxt2);
        //시간
        cafeTimetxt=(TextView)findViewById(R.id.addTime);
        cafeOpen=(TimePicker)findViewById(R.id.timepickerOpen);
        cafeClose=(TimePicker)findViewById(R.id.timepickerClose);

        modify2=(Button)findViewById(R.id.modify2);
        addMenu=(ImageButton)findViewById(R.id.addMenu);
        delectMenu=(ImageButton)findViewById(R.id.delectMenu);
        Button time=(Button)findViewById(R.id.time);
        TextView t1=(TextView)findViewById(R.id.t1);
        TextView t2=(TextView)findViewById(R.id.t2);
        modify2.setOnClickListener(this);

        Calendar c=Calendar.getInstance();
        mapBtn=(ImageButton)findViewById(R.id.mapPlus);

        modi = storeInfo_modify.this;

        //modi->main (name_mod) 보내기
        modify2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mustInput())
                {
                    checkResult();
                    addData();//데이터 추가하기
                    for(int i = 0; i<mImgPathList.size();i++)
                        uploadFile(mImgPathList.get(i));
                }

                passName2 = editCafeName.getText().toString();
                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                intent.putExtra("toMain", passName2);
                startActivity(intent);
                finish();

            }
        });
        //modify->main   (name_mod)받기
        Intent intent = getIntent();
        String data2 = intent.getStringExtra("toModi");
        name_mod=data2;
        if(!(name_mod.equals(""))||name_mod!=null)
            showDocTxt_Modify(name_mod);
        //지도

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("haex8ay584")
        );
        fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map_m);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_m, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        LinearLayout lay=(LinearLayout)findViewById(R.id.lay2);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(open)
                {
                    Log.i("됨","됨");
                    open=false;
                    lay.setVisibility(View.VISIBLE);
                    changeLocation();

                }
                else
                {
                    open=true;
                    lay.setVisibility(View.GONE);
                }
            }});


        //addMenu.setOnClickListener(this);
        // 체크박스 연결
        for (int i = 0; i < checkBoxCovidId.length; i++) {
            checkBoxCovids[i] = (CheckBox) findViewById(checkBoxCovidId[i]);
        }
        for (int i = 0; i < checkBoxInteriorId.length; i++) {
            checkBoxInteriors[i] = (CheckBox) findViewById(checkBoxInteriorId[i]);
        }
        for (int i = 0; i < checkBoxCafeId.length; i++) {
            checkBoxCafes[i] = (CheckBox) findViewById(checkBoxCafeId[i]);
        }
        for (int i = 0; i < checkBoxMenuId.length; i++) {
            checkBoxMenus[i] = (CheckBox) findViewById(checkBoxMenuId[i]);
        }

        //체크박스 펼치기
        checkCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textCovidClick)
                {
                    textCovidClick=!textCovidClick;
                    for(int i=0;i<checkBoxCovidId.length;i++)
                    {
                        checkBoxCovids[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textCovidClick=!textCovidClick;
                    for(int i=0;i<checkBoxCovidId.length;i++)
                    {
                        checkBoxCovids[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        checkInterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textInteriorClick)
                {
                    textInteriorClick=!textInteriorClick;
                    for(int i=0;i<checkBoxInteriorId.length;i++)
                    {
                        checkBoxInteriors[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textInteriorClick=!textInteriorClick;
                    for(int i=0;i<checkBoxInteriorId.length;i++)
                    {
                        checkBoxInteriors[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        checkCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textCafeClick)
                {
                    textCafeClick=!textCafeClick;
                    for(int i=0;i<checkBoxCafeId.length;i++)
                    {
                        checkBoxCafes[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textCafeClick=!textCafeClick;
                    for(int i=0;i<checkBoxCafeId.length;i++)
                    {
                        checkBoxCafes[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        checkMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textMenuClick)
                {
                    textMenuClick=!textMenuClick;
                    for(int i=0;i<checkBoxMenuId.length;i++)
                    {
                        checkBoxMenus[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textMenuClick=!textMenuClick;
                    for(int i=0;i<checkBoxMenuId.length;i++)
                    {
                        checkBoxMenus[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuTxt1.setText("");

                //벡터에 저장
                hashMenu.put(editCafeMenu.getText().toString(),editMenuPrice.getText().toString());
                //벡터에서 꺼내서 출력
                Iterator<String> itKey=hashMenu.keySet().iterator();
                while (itKey.hasNext())
                {
                    String key=itKey.next();
                    menuTxt1.append(key+"     "+hashMenu.get(key)+"\n");
                }
                editCafeMenu.setText("");
                editMenuPrice.setText("");

            }
        });
        delectMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuTxt1.setText("");
                hashMenu.remove(deleteMenuName.getText().toString());

                //벡터에서 꺼내서 출력
                Iterator<String> itKey=hashMenu.keySet().iterator();
                while (itKey.hasNext())
                {
                    String key=itKey.next();
                    menuTxt1.append(key+"     "+hashMenu.get(key)+"\n");
                }

                deleteMenuName.setText("");
                deleteMenuprice.setText("");

            }
        });

        // 타임피커 다이얼로그
        cafeTimetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafeOpen.setVisibility(View.VISIBLE);
                cafeClose.setVisibility(View.VISIBLE);
                t1.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.toString(cafeOpen.getCurrentMinute())=="0")
                    openMin="00";
                else
                    openMin=Integer.toString(cafeOpen.getCurrentMinute());

                if(Integer.toString(cafeClose.getCurrentMinute())=="0")
                    closeMin="00";
                else
                    closeMin=Integer.toString(cafeClose.getCurrentMinute());

                cafeTimetxt.setText("open - "+Integer.toString(cafeOpen.getCurrentHour())+" : "+openMin+
                        "\n"+"close - "+Integer.toString(cafeClose.getCurrentHour())+" : "+closeMin);
                cafeOpen.setVisibility(View.GONE);
                cafeClose.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
            }
        });
        // name_mod="카페 오월";




        //사진 관련

        //매장사진
        add02 = findViewById(R.id.add02);
        add02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        //리사이클러뷰

        RecyclerView mRecyclerView2 = (RecyclerView) findViewById(R.id.list02);
        LinearLayoutManager mLinearLayoutManager2 = new LinearLayoutManager(this);
        mLinearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView2.setLayoutManager(mLinearLayoutManager2);

        mArrayList2 = new ArrayList<>();

        mAdapter2 = new storeInfo_photoAdapter(this,mArrayList2);
        mRecyclerView2.setAdapter(mAdapter2);


        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(mRecyclerView2.getContext(),
                mLinearLayoutManager2.getOrientation());
        mRecyclerView2.addItemDecoration(dividerItemDecoration2);

    }

    //마커하고 위도경도로 전환하는 함수 실행하기
    void changeLocation()
    {
        List<Address> list = null;


        String str =editlocation.getText().toString();
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
                //Toast.makeText (getApplicationContext(),list.get(0).toString(),Toast.LENGTH_SHORT).show( );
                //ch_location=list.get(0).toString();
                Log.i("위치","국가명 "+ list.get(0).getCountryName());
                Log.i("위치","위도 "+ list.get(0).getLatitude());
                Log.i("위치","경도 "+ list.get(0).getLongitude());
                lat=list.get(0).getLatitude();
                lon=list.get(0).getLongitude();
                //tv.setText(list.get(0).toString());
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
            }
        }
    }
    boolean mustInput()
    {
        if(editCafeName.getText().toString().equals("")||editlocation.getText().toString().equals(""))
        {   Toast.makeText (getApplicationContext(),"필수입력란을 채워주세요",Toast.LENGTH_SHORT).show( );
            return false;
        }
        else
            return true;


    }
    @Override
    public void onClick(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("사용자 인증이 되지 않았습니다. Firebase 인증에서 로그인 후 사용하세요.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    ;
                }
            });
            builder.show();
            return;
        }


        switch (view.getId())
        {

        }
    }

    public String checkResult()
    {
        result = "";  // 결과를 출력할 문자열  ,  항상 스트링은 빈문자열로 초기화 하는 습관을 가지자

        for(int i=0;i<checkBoxCovidId.length;i++)
        {
            if(checkBoxCovids[i].isChecked() == true)
            {
                result += checkBoxCovids[i].getText().toString()+"/";
                checkResultCovid[i]=true;
            }
        }
        for(int i=0;i<checkBoxInteriorId.length;i++)
        {
            if(checkBoxInteriors[i].isChecked() == true)
            {
                result += checkBoxInteriors[i].getText().toString()+"/";
                checkResultInterior[i]=true;
            }

        }
        for(int i=0;i<checkBoxCafeId.length;i++)
        {
            if(checkBoxCafes[i].isChecked() == true)
            {
                result += checkBoxCafes[i].getText().toString()+"/";
                checkResultCafe[i]=true;
            }

        }
        for(int i=0;i<checkBoxMenuId.length;i++)
        {
            if(checkBoxMenus[i].isChecked() == true)
            {
                result += checkBoxMenus[i].getText().toString()+"/";
                checkResultMenu[i]=true;
            }

        }
        return result;
    }

    public void setcheckResult(String str)
    {
        String[] arr = str.split("/");

        for(int i=0;i<checkBoxCovidId.length;i++)
        {
            for(int j=0;j<arr.length;j++)
            {
                if(arr[j].equals(checkBoxCovids[i].getText()))
                    checkBoxCovids[i].setChecked(true) ;
            }
        }

        for(int i=0;i<checkBoxInteriorId.length;i++)
        {
            for(int j=0;j<arr.length;j++)
            {
                if(arr[j].equals(checkBoxInteriors[i].getText()))
                    checkBoxInteriors[i].setChecked(true) ;
            }
        }
        for(int i=0;i<checkBoxCafeId.length;i++)
        {
            for(int j=0;j<arr.length;j++)
            {
                if(arr[j].equals(checkBoxCafes[i].getText()))
                    checkBoxCafes[i].setChecked(true) ;
            }
        }
        for(int i=0;i<checkBoxMenuId.length;i++)
        {
            for(int j=0;j<arr.length;j++)
            {
                if(arr[j].equals(checkBoxMenus[i].getText()))
                    checkBoxMenus[i].setChecked(true) ;
            }
        }

    }

    private void addData()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newCafe = new HashMap<>();

        GeoPoint cafeGeopoint = new GeoPoint( lat, lon);
        // boolean result[]={true,true,true};

        //newCafe.put("cafeGeopoint", cafeGeopoint);

        newCafe.put("cafeName", editCafeName.getText().toString());//카페이름
        newCafe.put("time", cafeTimetxt.getText().toString());//영업시간
        newCafe.put("cafeMenu", menuTxt1.getText().toString());//메뉴추가하기
        newCafe.put("cafeGeopoint", cafeGeopoint);
        newCafe.put("cafeLocation", editlocation.getText().toString());
        //newCafe.put("cafeMenu", menuTxt1.getText().toString());//메뉴추가하기
        newCafe.put("checkList",  checkResult());//체크리스트 목록
        newCafe.put("cafeIntroduce", introduce.getText().toString());//
        //newCafe.put("checkTF", Arrays.result(1,2,3));


        //위도경도

        db.collection("cafe").document(editCafeName.getText().toString())
                .set(newCafe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("cafe", "Document ID = " + documentReference.get());
                        Log.d("cafe", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.d("cafe", "Document Error!!");
                    }
                });
    }

    private void showDocTxt_Modify(String n){
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

                        editCafeName.setText(cafeInfo.getCafeName());
                        introduce.setText(cafeInfo.getCafeIntroduce());
                        editlocation.setText(cafeInfo.getCafeLocation());
                        cafeTimetxt.setText(cafeInfo.getTime());
                        menuTxt1.setText(cafeInfo.getCafeMenu());

                        setResult = cafeInfo.getCheckList();
                        setcheckResult(setResult);
                        // cafeCheckInfo.setText(cafeInfo.getCheckList());

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
    //firestore관련

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK&& data.getData() != null) {
                ClipData mClipData = data.getClipData();
                if ( mClipData == null ) {
                    Uri imageurl = data.getData();
                    review_photo data2 = new review_photo(imageurl);
                    mArrayList2.add(data2);
                    mAdapter2.notifyDataSetChanged();

                    getImageNameToUri(imageurl);
                }
                else {
                    int cout = mClipData.getItemCount();
                    for (int i = 0; i < cout; i++) {

                        Uri imageurl = mClipData.getItemAt(i).getUri();
                        review_photo data2 = new review_photo(imageurl);
                        mArrayList2.add(data2); // RecyclerView의 마지막 줄에 삽입
                        mAdapter2.notifyDataSetChanged();


                        getImageNameToUri(imageurl);
                    }
                }
                //  pictureCount.setText(review_photo.picCount+"/5");
                //   count++;
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void getImageNameToUri(Uri data)
    {
        String[] proj =
                {
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.ORIENTATION
                };

        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mImgPath = cursor.getString(column_data);
        Log.d("cafe", "mImgPath = " + mImgPath);
        mImgPathList.add(mImgPath);
    }

    private void uploadFile(String aFilePath)
    {
        newFolderName = editCafeName.getText().toString();
        Uri file = Uri.fromFile(new File(aFilePath));
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        // 참조 만들기 및 파일 업로드
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        //newFolderName=editCafeName.getText().toString();
        //UploadTask uploadTask = storageRef.child("storage/"+file.getLastPathSegment()).putFile(file, metadata);
        UploadTask uploadTask = storageRef.child("storage/"+newFolderName+"/"+"store/"+file.getLastPathSegment()).putFile(file, metadata);
        //Log.d("cafe", newFolderName);


        // 업로드 상태 받기
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
            {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(storeInfo_modify.this, "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot)
            {
                Log.d("cafe", "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads
                Log.d("cafe", "Upload Exception!!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                // Handle successful uploads on complete
                Toast.makeText(storeInfo_modify.this, "업로드가 완료되었습니다.!!", Toast.LENGTH_SHORT).show();

                String name = storageRef.getName();
                String path = storageRef.getPath();

                Log.d("cafe", "name = " + name);
                Log.d("cafe", "path = " + path);

                // 실시간 데이터베이스 업데이트 합니다.
                writeNewImageInfoToDB(name, path);
            }
        });
    }

    private void writeNewImageInfoToDB(String name, String path)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("images");

        UploadInfo info = new UploadInfo();
        info.setName(name);
        info.setPath(path);

        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(info);
    }
//map
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

}
