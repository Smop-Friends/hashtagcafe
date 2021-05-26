package sungshin.hashtagcafe.firestore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import sungshin.hashtagcafe.R;

public class FirestoreActivity extends AppCompatActivity implements View.OnClickListener {
    TextView firestoreshowdatatxt;
    EditText cafeNameTxt;
    EditText cafeGeopointLatTxt;
    EditText cafeGeopointLngTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore);

        Button selectdocbtn = (Button)findViewById(R.id.firestoreseldatabtn);//데이터 추가
        selectdocbtn.setOnClickListener(this);
        Button adddocbtn = (Button)findViewById(R.id.firestoreadddatabtn);//데이터 조회
        adddocbtn.setOnClickListener(this);

        firestoreshowdatatxt = (TextView)findViewById(R.id.firestoreshowdatatxt);//데이터조회 텍스트뷰
        cafeNameTxt=(EditText)findViewById(R.id.cafeNameTxt);//이름
        cafeGeopointLatTxt=(EditText)findViewById(R.id.cafeGeopointLatTxt);//위도
        cafeGeopointLngTxt=(EditText)findViewById(R.id.cafeGeopointLngTxt);//경도
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
            case R.id.firestoreseldatabtn:
                //showDocTxt();
                firestoreshowdatatxt.setText("");//출력 비우기
                showAllDocTxt();//데이터읽어서 출력
                break;
            case R.id.firestoreadddatabtn://추가버튼
                addData();//데이터 추가하기
                break;

        }
    }


    private void showDocTxt(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("cafe").document("tFAwRpxoFdZHqN6lXFwL");
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
                        //Log.d("cafe","name = "+cafeInfo.getCafeName());
                        //Log.d("cafe","geopoint = "+cafeInfo.getCafeGeopoint());
                        firestoreshowdatatxt.setText("name = "+cafeInfo.getCafeName()+"\n"+"geopoint = "+cafeInfo.getCafeGeopoint());
                    }
                    else
                    {
                        //Log.d("cafe", "No such document");
                        firestoreshowdatatxt.setText("No such document");
                    }
                }
                else
                {
                    //Log.d("cafe", "get failed with ", task.getException());
                    firestoreshowdatatxt.setText("failed");
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
                                firestoreshowdatatxt.append("name = "+cafeInfo.getCafeName()+"\n"+"geopoint = "+cafeInfo.getCafeGeopoint()+"\n\n");
                            }
                        } else {
                            Log.d("cafe", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addData()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> newCafe = new HashMap<>();
        newCafe.put("cafeName", cafeNameTxt.getText().toString());

        GeoPoint cafeGeopoint = new GeoPoint(Double.parseDouble(cafeGeopointLatTxt.getText().toString()),Double.parseDouble(cafeGeopointLngTxt.getText().toString()));
        newCafe.put("cafeGeopoint", cafeGeopoint);

        db.collection("cafe")
                .add(newCafe)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Log.d("cafe", "Document ID = " + documentReference.get());
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

}