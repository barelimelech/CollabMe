package com.example.collabme.users;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.collabme.Activites.LoginActivity;
import com.example.collabme.R;
import com.example.collabme.model.ModelPhotos;
import com.example.collabme.model.ModelUsers;
import com.example.collabme.model.Modelauth;
import com.example.collabme.objects.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class UserProfile extends Fragment {

    TextView usernameType, username, age, followers, postuploads, email, gender;
    Spinner professions, platform;
    ArrayList<String> platformArr;
    ImageButton editBtn;
    ArrayList<String> professionsArr;
    String[] plat;
    String[] pref;
    String password;
    Boolean influencer, company;
    ImageView logout;
    Bitmap bitmap;
    ImageView profilepicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        usernameType = view.findViewById(R.id.fragment_userprofile_usernameType);
        username = view.findViewById(R.id.fragment_userprofile_username);
        age = view.findViewById(R.id.fragment_userprofile_age);
        email = view.findViewById(R.id.fragment_userprofile_email);
        gender = view.findViewById(R.id.fragemnt_userprofile_gender);
        platform = view.findViewById(R.id.fragemnt_signup_platform);
        professions = view.findViewById(R.id.fragemnt_signup_proffesions);
        followers = view.findViewById(R.id.fragment_userprofile_followers);
        postuploads = view.findViewById(R.id.fragment_userprofile_postsuploads);
        editBtn = view.findViewById(R.id.fragemnt_userprofile_editBtn);
        logout = view.findViewById(R.id.fragment_userprofile_logoutBtn);
        profilepicture = view.findViewById(R.id.fragment_userprofile_pic);

        ModelUsers.instance3.getUserConnect(new ModelUsers.getuserconnect() {
            @Override
            public void onComplete(User profile) {
                if (profile != null) {
                    ModelPhotos.instance3.getimages(profile.getImage(), new ModelPhotos.getimagesfile() {
                        @Override
                        public void onComplete(String responseBody) {
                            checkUsernameType(profile);
                            username.setText(profile.getUsername());
                            age.setText(profile.getAge());
                            followers.setText(profile.getFollowers());
                            postuploads.setText(profile.getNumOfPosts());
                            plat = profile.getPlatforms();
                            pref = profile.getProfessions();
                            gender.setText(profile.getSex());
                            email.setText(profile.getEmail());
                            platformArr = ChangeToArray(profile.getPlatforms());
                            professionsArr = ChangeToArray(profile.getProfessions());
                            password = profile.getPassword();
                            influencer = profile.getInfluencer();
                            company = profile.getInfluencer();
                            initSpinnerFooter(platformArr.size(), platformArr, platform);
                            initSpinnerFooter(professionsArr.size(), professionsArr, professions);
                            //bitmap = StringToBitMap(responseBody);
                            //profilepicture.setImageBitmap(bitmap);
                           // Uri uri = getImageUri(bitmap);
                            profilepicture.setImageURI(Uri.parse(responseBody));
                            Picasso.get().load(responseBody).into(profilepicture);
                        }
                    });
                }
            }
        });





        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(UserProfileDirections.actionUserProfileToEditProfile2(username.getText().toString(), password, company, influencer, age.getText().toString(), email.getText().toString(), gender.getText().toString(), plat, pref, followers.getText().toString(), postuploads.getText().toString()));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Modelauth.instance2.logout(new Modelauth.logout() {
                    @Override
                    public void onComplete(int code) {
                        if (code == 200) {
                            toLoginActivity();
                        } else {

                        }
                    }
                });
            }
        });

        return view;
    }



    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void checkUsernameType(User profile) {
        if (profile.getInfluencer() && profile.getCompany()) {
            usernameType.setText("Influencer & Company profile");
            return;
        }
        if (profile.getInfluencer()) {
            usernameType.setText("Influencer profile");
            return;
        }
        if (profile.getCompany()) {
            usernameType.setText("Company profile");
            return;
        }
    }

    private void toLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void initSpinnerFooter(int size, ArrayList<String> array, Spinner spinner) {
        int tmp = 0;
        for (int j = 0; j < size; j++) {
            if (array.get(j) != null) {
                tmp++;
            }
        }
        String[] items = new String[tmp];

        for (int i = 0; i < tmp; i++) {
            items[i] = array.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public ArrayList<String> ChangeToArray(String[] array) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            arrayList.add(array[i]);
        }

        return arrayList;
    }
}