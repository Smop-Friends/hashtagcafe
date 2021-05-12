package sungshin.hashtagcafe.authentication;
import sungshin.hashtagcafe.R;

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
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.firebaseauthbtn:
                Intent i = new Intent(this, AuthActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}