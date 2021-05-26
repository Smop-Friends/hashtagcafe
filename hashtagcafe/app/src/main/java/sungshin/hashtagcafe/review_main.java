package sungshin.hashtagcafe;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sungshin.hashtagcafe.cloudStorage.UploadInfo;

public class review_main extends AppCompatActivity {
    int PICK_IMAGE_MULTIPLE = 1;
    ImageButton addbtn;

    private ArrayList<review_photo> mArrayList;
    private review_photoAdapter mAdapter;
    private int count = 0;

    //추가 시작
    EditText edtReview;
    RatingBar ratingBar;
    Button btnOk;
    MultiAutoCompleteTextView searchText;

    ArrayList<String> mImgPathList = new ArrayList<>();
    private String mImgPath = null;
    String newFolderName;
    String userID;
    String data3;

    String cafeName2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        Intent intent3 = getIntent();
        data3=intent3.getStringExtra("cafename");//From yejin

        String[] items = {"감성인테리어","감성포토존","감성소품",
                "사진포인트","셀카맛집","디저트맛집","커피맛집","데이트코스","가족과함께","개방형","아늑한","조용한",
                "루프탑","넓은","깨끗한","반려동물동반"};

        MultiAutoCompleteTextView multi = (MultiAutoCompleteTextView)findViewById(R.id.searchText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,items);
        MultiAutoCompleteTextView.CommaTokenizer token = new MultiAutoCompleteTextView.CommaTokenizer();
        multi.setTokenizer(token);
        multi.setAdapter(adapter);

        addbtn = findViewById(R.id.picAddBtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        mAdapter = new review_photoAdapter(findViewById(R.id.picCount),this,mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        //추가 시작
        edtReview = (EditText)findViewById(R.id.edtReview);
        ratingBar = (RatingBar)findViewById(R.id.profileRatingBar);
        btnOk = (Button)findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                for(int i = 0; i<mImgPathList.size();i++)
                    uploadFile(mImgPathList.get(i));
                finish();
            }
        });

        searchText = (MultiAutoCompleteTextView)findViewById(R.id.searchText);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView pictureCount = (TextView)findViewById(R.id.picCount);
        if (requestCode == PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK&& data.getData() != null) {
                ClipData mClipData = data.getClipData();
                if ( mClipData == null ) {
                    if (review_photo.picCount == 4) return;
                    Uri imageurl = data.getData();
                    review_photo data2 = new review_photo(imageurl);
                    mArrayList.add(data2);
                    mAdapter.notifyDataSetChanged();
                    review_photo.picCount++;
                    pictureCount.setText(review_photo.picCount+"/4");

                    getImageNameToUri(imageurl);
                }
                else {
                    int cout = mClipData.getItemCount();
                    for (int i = 0; i < cout; i++) {
                        if (review_photo.picCount == 4) return;
                        Uri imageurl = mClipData.getItemAt(i).getUri();
                        review_photo data2 = new review_photo(imageurl);
                        mArrayList.add(data2); // RecyclerView의 마지막 줄에 삽입
                        mAdapter.notifyDataSetChanged();
                        review_photo.picCount++;
                        pictureCount.setText(review_photo.picCount+"/4");

                        getImageNameToUri(imageurl);
                    }
                }
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


    //추가 시작
    private void addData()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newReview = new HashMap<>();
        newReview.put("cafeName", data3);
        newReview.put("userid", userID);
        newReview.put("reviewTxt", edtReview.getText().toString());
        newReview.put("rating", ratingBar.getRating());
        newReview.put("hashtags", searchText.getText().toString());

        db.collection("review")
                .add(newReview)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Log.d("review", "Document ID = " + documentReference.get());
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.d("review", "Document Error!!");
                    }
                });
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
        newFolderName = data3;
        Uri file = Uri.fromFile(new File(aFilePath));
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        // 참조 만들기 및 파일 업로드
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        //UploadTask uploadTask = storageRef.child("storage/"+file.getLastPathSegment()).putFile(file, metadata);
        UploadTask uploadTask = storageRef.child("storage/"+newFolderName+"/"+"review/"+file.getLastPathSegment()).putFile(file, metadata);
        //Log.d("cafe", newFolderName);


        // 업로드 상태 받기
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
            {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(review_main.this, "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(review_main.this, "업로드가 완료되었습니다.!!", Toast.LENGTH_SHORT).show();

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
}