package sungshin.hashtagcafe.authentication;
import sungshin.hashtagcafe.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUIActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        Button firebaseuiauthbtn = (Button)findViewById(R.id.firebaseuiauthbtn);
        firebaseuiauthbtn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK)
            {
                // Successfully signed in
                Intent i = new Intent(this, SignedInActivity.class);
                i.putExtras(data);
                startActivity(i);

            }
            else
            {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.firebaseuiauthbtn:
                signin();
                break;
            default:
                break;
        }
    }

    /**
     * ?????? ??????
     */
    private void signin()
    {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())                 // Theme ??????
                        .setLogo(getSelectedLogo())                  // ?????? ??????
                        .setAvailableProviders(getSelectedProviders())// Providers ??????
                        .setTosAndPrivacyPolicyUrls("https://naver.com",
                                "https://google.com")
                        .setIsSmartLockEnabled(true)              //SmartLock ??????
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * FirebaseUI??? ????????? ?????? ??????
     * @return ?????? ??????
     */
    private int getSelectedTheme()
    {
        return AuthUI.getDefaultTheme();
    }

    /**
     * Firebase UI??? ????????? ?????? ?????????
     * @return ?????? ?????????
     */
    private int getSelectedLogo()
    {
        //return AuthUI.NO_LOGO;
        //return R.mipmap.ic_launcher;
        return R.mipmap.ic_hstg;
    }

    /**
     * FirebaseUI??? ?????? ?????? ?????? ?????? ????????? ??????
     * @return ?????? ?????????
     */
    private List<AuthUI.IdpConfig> getSelectedProviders()
    {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        CheckBox googlechk = (CheckBox)findViewById(R.id.google_provider);
        CheckBox emailchk = (CheckBox)findViewById(R.id.email_provider);

        if(googlechk.isChecked())
        {
            selectedProviders.add(new AuthUI.IdpConfig.GoogleBuilder().build());
        }


        if(emailchk.isChecked())
        {
            selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder().build());
        }

        return selectedProviders;
    }
}