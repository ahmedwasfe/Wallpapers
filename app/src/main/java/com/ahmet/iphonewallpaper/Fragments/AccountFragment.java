package com.ahmet.iphonewallpaper.Fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    private FirebaseAuth mAuth;


    private Switch mNightMode;


    private View convertView;

    private SaveSettings saveSettings;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

      //  loadUserInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        saveSettings = new SaveSettings(getContext());
        if (saveSettings.getNightModeState() == true){
            getContext().setTheme(R.style.DarkTheme);
        }else {
            getContext().setTheme(R.style.AppTheme);
        }

        // Inflate the layout for this fragment

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            convertView = inflater.inflate(R.layout.fragment_account, container, false);
            convertView.findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                            Common.SIGN_IN_REQUEST_CODE);
                }
            });
        }else {
            convertView = inflater.inflate(R.layout.fragment_login, container, false);

            saveSettings = new SaveSettings(getContext());
            if (saveSettings.getNightModeState() == true){
                getContext().setTheme(R.style.DarkTheme);
            }else {
                getContext().setTheme(R.style.AppTheme);
            }

            TextView textEmail = convertView.findViewById(R.id.text_user_email);
            TextView textName = convertView.findViewById(R.id.text_user_name);
            CircleImageView imageUser = convertView.findViewById(R.id.image_user_login);
            mNightMode = convertView.findViewById(R.id.switch_nihgt_mode);



            if (saveSettings.getNightModeState() == true){
                mNightMode.setChecked(true);
            }

            mNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        saveSettings.setNightModeState(true);
                        restartApp();
                        Toast.makeText(getActivity(), "Dark Style", Toast.LENGTH_SHORT).show();
                    }else{
                        saveSettings.setNightModeState(false);
                        restartApp();
                        Toast.makeText(getActivity(), "White Style", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (mAuth.getCurrentUser() != null) {
                textEmail.setText(mAuth.getCurrentUser().getEmail());
                textName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                Picasso.with(getActivity())
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .placeholder(R.drawable.icon_oops_mismatch_pw_2x)
                        .into(imageUser);
            }



            convertView.findViewById(R.id.text_log_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });

        }

        return convertView;
    }

    private void restartApp() {
        startActivity(new Intent(getActivity(), getContext().getClass()));
        getActivity().finish();
    }


    void signOut(){
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
    }


    private void loadUserInformation(){

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            TextView textEmail = convertView.findViewById(R.id.text_user_email);
            TextView textName = convertView.findViewById(R.id.text_user_name);
            textEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            textName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


        }

    }


//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        GoogleSignInOptions gso =
//                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null){
//
//            CircleImageView imageUser = view.findViewById(R.id.image_user);
//            TextView username = view.findViewById(R.id.text_user_name);
//            TextView useremail = view.findViewById(R.id.text_user_email);
//
//            Picasso.with(getActivity())
//                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
//                    .placeholder(R.drawable.icon_oops_mismatch_pw_2x)
//                    .into(imageUser);
//            username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//            useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//
//            view.findViewById(R.id.text_log_out).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FirebaseAuth.getInstance().signOut();
//                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            getActivity().getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.content_area, new ProfileFragment())
//                                    .commit();
//                        }
//                    });
//                }
//            });
//
//        }else{
//            view.findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                    startActivityForResult(signInIntent, Common.GOOGLE_SIGN_IN_CODE);
//                }
//            });
//        }
//    }

//    @Override

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == Common.GOOGLE_SIGN_IN_CODE){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                e.printStackTrace();
//                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //updateUI(currentUser);
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//
//        Log.w("User ID", "firebaseAuthWithGoogle" + account.getId());
//
//        //FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//
//        mAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(),
//                new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            getActivity().getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.content_area, new ProfileFragment())
//                                    .commit();
//                        }else{
//                            Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });

//    }
}
