package sungshin.hashtagcafe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import sungshin.hashtagcafe.firestore.ReviewInfo;

public class reviewprofile_main extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    int i = 0;

    //private String[] images = new String[4];
    private ArrayList<String> images = new ArrayList<>();
    String myUserID , myCafeName;
    int myImgCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewprofile_main);

        Intent intent = getIntent();
        myUserID = intent.getStringExtra("userID");
        TextView profileCafeName = (TextView)findViewById(R.id.profileCafeName);
        myCafeName = intent.getStringExtra("cafename");
        profileCafeName.setText(myCafeName);
        myImgCount = intent.getIntExtra("imgCount",0);

        //?????? ??????
        final ImageButton button1 = (ImageButton) findViewById(R.id.reviewDeclaration);
        button1.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                AlertDialog.Builder dlg = new AlertDialog.Builder(reviewprofile_main.this);
                dlg.setTitle("????????????"); //??????
                final String[] versionArray = new String[] {"??????/??????","??????/?????????","??????????????????","???????????????","??????"};
                final boolean[] checkArray = new boolean[]{false,false,false,false,false};

                dlg.setMultiChoiceItems(versionArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });
//                ?????? ????????? ??????
                dlg.setPositiveButton("??????",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.show();
            }
        });

        showImgsInFolder();
        //????????? ????????????
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);

        sliderViewPager.setOffscreenPageLimit(1); //?????? ?????? ?????????????????? ???????????? ?????? ???????????? ????????? ??????
        sliderViewPager.setAdapter(new reviewprofile_ImageSliderAdapter(this, images)); //???????????? set?????? ???????????? ?????? ??????

        //???????????? ??????????????? ??? ????????? ?????? Indicator??? ????????? ??? ?????? ???.
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position); //??????
                setCurrentIndicator(position); // ?????? ?????? ?????? Indicator??? ??????
            }
        });

        setupIndicators(myImgCount);; //???????????? ????????? ?????? ImageView??? ????????????, LinearLayout??? addView()??? ?????? ???????????????.
        selectWhereDoc();

    }

    private void selectWhereDoc()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("review")
                .whereEqualTo("userid", myUserID)
                .whereEqualTo("cafeName",myCafeName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReviewInfo reviewInfo = document.toObject(ReviewInfo.class);

                                RatingBar ratingBar = (RatingBar)findViewById(R.id.profileRatingBar);
                                TextView hashtag = (TextView)findViewById(R.id.profileHashtag);
                                TextView Review = (TextView)findViewById(R.id.profileReview);
                                ratingBar.setRating(reviewInfo.getRating());
                                hashtag.setText(reviewInfo.getHashtags());
                                Review.setText(reviewInfo.getReviewTxt());

                                //Log.d("review", document.getId() + " => " + document.getData()+i);
                            }

                        } else {
                            Log.d("review", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    public void showImgsInFolder() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String newFolderName = myCafeName;
        String newUserID = myUserID;  ///storage/?????????arbffUC3Fbpo6yqHttuY
        newUserID = newUserID.trim();

        System.out.println("storage/"+newFolderName+"/"+newUserID);

        StorageReference listRef = storage.getReference().child("storage/"+newFolderName+"/review/"+newUserID);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        images.add(task.getResult().toString());
                                        sliderViewPager.getAdapter().notifyDataSetChanged();
                                       // System.out.println(i + images[i]);
                                        i++;
                                    } else {
                                        // URL??? ???????????? ????????? ????????? ?????????
                                        Toast.makeText(reviewprofile_main.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    // oncreate ???


    private String getURLForResource(int resId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resId).toString();
    }


    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //???????????? ??????

        params.setMargins(16, 8, 16, 8); //???????????? ??????

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }
}




