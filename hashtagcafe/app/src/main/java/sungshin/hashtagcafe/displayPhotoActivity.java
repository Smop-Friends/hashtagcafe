package sungshin.hashtagcafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import sungshin.hashtagcafe.cloudStorage.UploadActivity;
import sungshin.hashtagcafe.cloudStorage.UploadInfo;
import sungshin.hashtagcafe.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class displayPhotoActivity extends AppCompatActivity implements View.OnClickListener{
    private final int REQUEST_CODE = 100;
    private final int REQ_CODE_SELECT_IMAGE = 1000;

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;

    Button imguploadbtn;
    EditText edtFolderName;
    String newFolderName;
    Button getimgbtn;
    Button gallerybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);

                Toast.makeText(this, "안드로이드 6.0부터 마시멜로부터 일부 권한에 대해 사용자에게 동의 필요!", Toast.LENGTH_LONG).show();
            }
        }

        imguploadbtn = (Button)findViewById(R.id.imguploadbtn);
        imguploadbtn.setOnClickListener(this);

        edtFolderName=(EditText)findViewById(R.id.edtFolderName);

        getimgbtn = (Button)findViewById(R.id.getimgbtn);
        getimgbtn.setOnClickListener(this);

        gallerybtn = (Button)findViewById(R.id.gallerybtn);
        gallerybtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.imguploadbtn:
                newFolderName = edtFolderName.getText().toString();
                uploadFile(mImgPath);
                break;
                ///////////////////////////////////////////////////////////////////////////////////
            case R.id.getimgbtn:
                newFolderName = edtFolderName.getText().toString();
                showImgsInFolder();
                break;
                //////////////////////////////////////////////////////////////////////////////////
            case R.id.gallerybtn:
                getGallery();
                break;
            default:
                break;
        }
    }

    // db에 있는애 보여주는
    public void showImgsInFolder() {
        Toast.makeText(getApplicationContext(), "버튼누름", Toast.LENGTH_SHORT).show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("storage/"+newFolderName);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.

                            LinearLayout layout = (LinearLayout) findViewById(R.id.imgLayout);
                            //imageview 동적생성
                            ImageView iv = new ImageView(displayPhotoActivity.this);
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
                                        Glide.with(displayPhotoActivity.this)
                                                .load(task.getResult())
                                                .into(iv);
                                    } else {
                                        // URL을 가져오지 못하면 토스트 메세지
                                        Toast.makeText(displayPhotoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    imguploadbtn.setEnabled(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 선택된 사진을 받아 서버에 업로드합니다.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                getImageNameToUri(uri);

                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ImageView img = (ImageView) findViewById(R.id.showimg);
                    img.setImageBitmap(bm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 사진 선택을 위해 갤러리를 호출합니다.
     */
    private void getGallery()
    {
        Intent intent = null;

        // 안드로이드 KitKat(level 19)부터는 ACTION_PICK 이용
        if(Build.VERSION.SDK_INT >= 19)
        {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }


    /**
     * URI 정보를 이용하여 사진 정보 가져옴
     */
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
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_orientation = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);

        mImgPath = cursor.getString(column_data);
        mImgTitle = cursor.getString(column_title);
        mImgOrient = cursor.getString(column_orientation);

        Log.d("cafe", "mImgPath = " + mImgPath);
        Log.d("cafe", "mImgTitle = " + mImgTitle);
        Log.d("cafe", "mImgOrient = " + mImgOrient);
    }

    /**
     *
     * Firebase Cloud Storage 파일 업로드
     *
     */
    private void uploadFile(String aFilePath)
    {
        Uri file = Uri.fromFile(new File(aFilePath));
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        // 참조 만들기 및 파일 업로드
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        //UploadTask uploadTask = storageRef.child("storage/"+file.getLastPathSegment()).putFile(file, metadata);
        UploadTask uploadTask = storageRef.child("storage/"+newFolderName+"/"+file.getLastPathSegment()).putFile(file, metadata);
        //Log.d("cafe", newFolderName);


        // 업로드 상태 받기
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
            {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(displayPhotoActivity.this, "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(displayPhotoActivity.this, "업로드가 완료되었습니다.!!", Toast.LENGTH_SHORT).show();

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