package sungshin.hashtagcafe.authentication;
import sungshin.hashtagcafe.R;
import sungshin.hashtagcafe.cloudStorage.CloudStorageActivity;
import sungshin.hashtagcafe.firestore.FirestoreActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartAuthActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_auth);

        Button firebaseauthbtn = (Button)findViewById(R.id.firebaseauthbtn);
        firebaseauthbtn.setOnClickListener(this);
        Button firebasecloudfirestorebtn = (Button)findViewById(R.id.firebasecloudfirestorebtn);
        firebasecloudfirestorebtn.setOnClickListener(this);
        Button firebasecloudstoragebtn = (Button)findViewById(R.id.firebasecloudstoragebtn);
        firebasecloudstoragebtn.setOnClickListener(this);
        Button goToLoginBtn = (Button)findViewById(R.id.goToLoginBtn);
        goToLoginBtn.setOnClickListener(this);

        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(intent);

        firebaseauthbtn.setVisibility(View.INVISIBLE);
        firebasecloudfirestorebtn.setVisibility(View.INVISIBLE);
        firebasecloudstoragebtn.setVisibility(View.INVISIBLE);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.firebaseauthbtn:
                Intent i = new Intent(this, AuthActivity.class);
                startActivity(i);
                break;
            case R.id.firebasecloudfirestorebtn:
                i = new Intent(this, FirestoreActivity.class);
                startActivity(i);
                break;
            case R.id.firebasecloudstoragebtn:
                i = new Intent(this, CloudStorageActivity.class);
                startActivity(i);
                break;
            case R.id.goToLoginBtn:
                i = new Intent(this, AuthActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}