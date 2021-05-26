package sungshin.hashtagcafe.cloudStorage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import sungshin.hashtagcafe.R;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener
{
    private File localFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        Button firebaseuibtn = (Button)findViewById(R.id.imgfirebaseuidnbtn);
        firebaseuibtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.imgfirebaseuidnbtn:
                showFirebaseUiDownloadImageView();
                break;
            default:
                break;
        }
    }


    private void showFirebaseUiDownloadImageView()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("storage/다운로드파일-7.jpg");

        ImageView imageView = (ImageView)findViewById(R.id.fcstorageimg);

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(imageView);
    }
}