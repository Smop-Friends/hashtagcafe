package sungshin.hashtagcafe.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sungshin.hashtagcafe.R;
import sungshin.hashtagcafe.cloudStorage.CloudStorageActivity;
import sungshin.hashtagcafe.map;

import com.firebase.ui.auth.AuthUI;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Button firebaseuibtn = (Button)findViewById(R.id.firebaseui);
        firebaseuibtn.setOnClickListener(this);

        Button firebasesignout = (Button)findViewById(R.id.firebasesignout);
        firebasesignout.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // 인증이 되어 있는 상태
            //firebaseuibtn.setEnabled(false);
            firebasesignout.setEnabled(true);
        }
        else
        {
            // 인증이 되어 있지 않은 상태
            firebaseuibtn.setEnabled(true);
            firebasesignout.setEnabled(false);
        }

        //임시버튼 페이지 연결입니다-첫화면이동
        Button toMapBtn= (Button)findViewById(R.id.toMapBtn);
        toMapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                    builder.setTitle("경고");
                    builder.setMessage("사용자 인증이 되지 않았습니다. 로그인 후 사용하세요.");
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

                Intent intent = new Intent(getApplicationContext(), map.class);
                startActivity(intent);
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.firebaseui:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    // 인증이 되어 있는 상태
                    Toast.makeText (getApplicationContext(),"로그인 상태입니다. \n [시작하기]를 눌러주세요!",Toast.LENGTH_LONG).show( );
                }
                else {
                    Intent i = new Intent(this, FirebaseUIActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.firebasesignout:
                signOut();
                break;

            default:
                break;
        }
    }

    private void signOut()
    {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            finish();
                        }
                        else
                        {
                        }
                    }
                });
    }
}