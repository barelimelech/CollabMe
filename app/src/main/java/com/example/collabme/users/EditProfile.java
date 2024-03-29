package com.example.collabme.users;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Color.rgb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.collabme.Activites.LoginActivity;
import com.example.collabme.R;
import com.example.collabme.model.ModelPhotos;
import com.example.collabme.model.ModelUsers;
import com.example.collabme.model.Modelauth;
import com.example.collabme.objects.User;
import com.facebook.login.LoginManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
/**
 *
 * the edit user fragment  -fragment that design to edit an user in the application
 * using dialog for professions and platformes
 * setting field with navigation from the user details and showing on the fragment screen
 * filling several felled including image and calling tha edit user function and upload image
 *
 */

public class EditProfile extends Fragment {
    TextView platform, professions, usernameType, username;
    EditText age, followers, postuploads, email, gender;
    ImageView saveBtn, deleteBtn, logout, profilepicture, galleryBtn;
    ImageButton cameraBtn, cancelBtn;
    String[] platformArr, newProfession, professionArr, chosen, chosen2;
    Boolean influencer1, company1;
    String password, email1, gender1, username1, age1, Followers, posts;
    boolean[] selectedProfessions = new boolean[16];
    boolean[] selectedPlatforms = new boolean[5];
    ArrayList<Integer> langList = new ArrayList<>();
    ArrayList<Integer> langList2 = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PIC = 2;
    Bitmap bitmap;
    ArrayList<String> rejectedOffers;
    ProgressBar progressBar;

    User user;
    String[] langArray = {"Sport", "Cooking", "Fashion", "Music", "Dance", "Cosmetic", "Travel", "Gaming", "Tech", "Food",
            "Art", "Animals", "Movies", "Photograph", "Lifestyle", "Other"};
    String[] langArray2 = {"youtube", "facebook", "tiktok", "instagram", "twitter"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        newProfession = new String[16];

        usernameType = view.findViewById(R.id.fragment_edituser_usernameType);
        username = view.findViewById(R.id.fragment_edituser_username);
        age = view.findViewById(R.id.fragment_edituser_age);
        professions = view.findViewById(R.id.fragment_edituser_profession);
        followers = view.findViewById(R.id.fragment_edituser_followers);
        postuploads = view.findViewById(R.id.fragment_edituser_postsuploads);
        platform = view.findViewById(R.id.fragment_edituser_platform);
        email = view.findViewById(R.id.fragment_edituser_email);
        gender = view.findViewById(R.id.fragemnt_edituser_gender);
        galleryBtn = view.findViewById(R.id.fragment_edituser_galleryBtn);
        cameraBtn = view.findViewById(R.id.fragment_edituser_cameraBtn);
        deleteBtn = view.findViewById(R.id.fragemnt_edituser_deleteBtn);
        saveBtn = view.findViewById(R.id.fragemnt_edituser_saveBtn);
        cancelBtn = view.findViewById(R.id.fragment_edituser_cancelBtn);
        logout = view.findViewById(R.id.fragment_edituser_logoutBtn);
        profilepicture = view.findViewById(R.id.fragment_edituser_pic);

        username1 = EditProfileArgs.fromBundle(getArguments()).getUsername();
        password = EditProfileArgs.fromBundle(getArguments()).getPassword();
        email1 = EditProfileArgs.fromBundle(getArguments()).getEmail();
        gender1 = EditProfileArgs.fromBundle(getArguments()).getGender();
        age1 = EditProfileArgs.fromBundle(getArguments()).getAge();
        Followers = EditProfileArgs.fromBundle(getArguments()).getFollowers();
        platformArr = EditProfileArgs.fromBundle(getArguments()).getPlatform();
        professionArr = EditProfileArgs.fromBundle(getArguments()).getProfession();
        posts = EditProfileArgs.fromBundle(getArguments()).getPostsuploads();
        influencer1 = EditProfileArgs.fromBundle(getArguments()).getInfluencer();
        company1 = EditProfileArgs.fromBundle(getArguments()).getCompany();
        setRejectOffersArrList(EditProfileArgs.fromBundle(getArguments()).getRejectedOffers());
        progressBar = view.findViewById(R.id.fragment_edituser_progressbar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(132, 80, 160), android.graphics.PorterDuff.Mode.MULTIPLY);

        updateUsernameType(influencer1, company1);
        username.setText(username1);
        username.setEnabled(false);
        age.setText(age1);
        //professions
        followers.setText(Followers);
        postuploads.setText(posts);
        //platform
        email.setText(email1);
        email.setEnabled(false);
        gender.setText(gender1);
        gender.setEnabled(false);
        ModelUsers.instance3.getUserConnect(new ModelUsers.getuserconnect() {
            @Override
            public void onComplete(User profile) {
                if (profile != null) {
                    professionArr = profile.getProfessions();
                    platformArr = profile.getPlatforms();
                    if (profile.getImage() != null) {
                        ModelPhotos.instance3.getimages(profile.getImage(), new ModelPhotos.getimagesfile() {
                            @Override
                            public void onComplete(Bitmap responseBody) {

                                if (responseBody != null) {
                                    profilepicture.setImageBitmap(responseBody);
                                }
                            }
                        });
                    }
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                openGallery();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                openCam();
            }
        });

        //deleteBtn
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Modelauth.instance2.logout(code -> {
                                    if (code == 200) {
                                        LoginManager.getInstance().logOut();
                                        ModelUsers.instance3.deleteUser(username1, new ModelUsers.DeleteUserListener() {
                                            @Override
                                            public void onComplete(int code) {
                                                Toast.makeText(getActivity(), "user deleted", Toast.LENGTH_LONG).show();
                                                toLoginActivity();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        cancelBtn.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        professions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // set title
                builder.setTitle("Select Professions:");

                // set dialog non cancelable
                builder.setCancelable(false);
                int m = 0;

                for (int k = 0; k < langArray.length; k++) {
                    for (int h = 0; h < professionArr.length; h++) {
                        if (professionArr[h] != null) {
                            if (professionArr[h].equals(langArray[k])) {
                                if (!langList.contains(k)) {

                                    langList.add(k);
                                    Collections.sort(langList);
                                    selectedProfessions[k] = true;
                                }
                            }
                        }
                    }
                }

                builder.setMultiChoiceItems(langArray, selectedProfessions, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position in lang list
                            langList.add(i);
                            // Sort array list
                            Collections.sort(langList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chosen = new String[langList.size()];
                        professions.clearComposingText();
                        professionArr = chosen;
                        // use for loop
                        for (int j = 0; j < langList.size(); j++) {
                            // concat array value
                            chosen[j] = (langArray[langList.get(j)]); //to check again
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedProfessions.length; j++) {
                            // remove all selection
                            selectedProfessions[j] = false;
                            // clear language list
                            langList.clear();
                            // clear text view value
                            professions.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }

        });

        platform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());

                // set title
                builder2.setTitle("Select Platforms:");

                // set dialog non cancelable
                builder2.setCancelable(false);
                int m = 0;

                for (int km = 0; km < langArray2.length; km++) {
                    for (int he = 0; he < platformArr.length; he++) {
                        if (platformArr[he] != null) {
                            if (platformArr[he].equals(langArray2[km])) {
                                langList2.add(km);
                                Collections.sort(langList2);
                                selectedPlatforms[km] = true;
                            }
                        }
                    }
                }

                builder2.setMultiChoiceItems(langArray2, selectedPlatforms, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position in lang list
                            langList2.add(i);
                            // Sort array list
                            Collections.sort(langList2);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList2.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chosen2 = new String[langList2.size()];
                        platform.clearComposingText();
                        platformArr = chosen2;
                        for (int j = 0; j < langList2.size(); j++) {
                            // concat array value
                            chosen2[j] = (langArray2[langList2.get(j)]); //to check again

                        }
                    }
                });

                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder2.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedPlatforms.length; j++) {
                            // remove all selection
                            selectedPlatforms[j] = false;
                            // clear language list
                            langList2.clear();
                            // clear text view value
                            platform.setText("");
                        }
                    }
                });
                // show dialog
                builder2.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidDate()) {

                    if (chosen != null) {
                        professionArr = chosen;
                    }

                    if (chosen2 != null) {
                        platformArr = chosen2;
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    user = new User(gender1, password, email1, username.getText().toString(),
                            age.getText().toString(), followers.getText().toString(), postuploads.getText().toString(),
                            company1, influencer1, professionArr, platformArr, rejectedOffers);
                    if (bitmap != null) {
                        ModelPhotos.instance3.uploadImage(bitmap, getActivity(), new ModelPhotos.PostProfilePhoto() {
                            @Override
                            public void onComplete(String uri) {
                                user.setImage(uri);
                                ModelUsers.instance3.EditUser(user, new ModelUsers.EditUserListener() {
                                    @Override
                                    public void onComplete(int code) {
                                        if (code == 200) {
                                            Toast.makeText(getActivity(), "user changes saved", Toast.LENGTH_LONG).show();
                                            Navigation.findNavController(v).navigateUp();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(getActivity(), "user changes not saved", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                    } else {
                        ModelUsers.instance3.EditUser(user, new ModelUsers.EditUserListener() {
                            @Override
                            public void onComplete(int code) {
                                if (code == 200) {
                                    Toast.makeText(getActivity(), "user changes saved", Toast.LENGTH_LONG).show();
                                    Navigation.findNavController(v).navigateUp();
                                } else {
                                    Toast.makeText(getActivity(), "user changes not saved", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Modelauth.instance2.logout(new Modelauth.logout() {
                    @Override
                    public void onComplete(int code) {
                        if (code == 200) {
                            ModelUsers.instance3.setUserConnected(null);
                            LoginManager.getInstance().logOut();
                            toLoginActivity();
                        }
                    }
                });
            }
        });

        return view;
        // Inflate the layout for this fragment
    }

    public void openGallery() {
        progressBar.setVisibility(View.GONE);
        Intent photoPicerIntent = new Intent(Intent.ACTION_PICK);
        photoPicerIntent.setType("image/jpeg");
        startActivityForResult(photoPicerIntent, REQUEST_IMAGE_PIC);
    }

    public void openCam() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public boolean checkValidDate() {
        if (age.getText().toString().isEmpty() || age.length() > 2 || age.length() < 1) {
            age.setError("Age is required");
            return false;
        } else if (followers.getText().toString().isEmpty() || !(followers.getText().toString().matches("^[1-9]{1}(?:[0-9])*?$"))) {
            followers.setError("Followers is required");
            return false;
        } else if (postuploads.getText().toString().isEmpty() || !(postuploads.getText().toString().matches("^[1-9]{1}(?:[0-9])*?$"))) {
            postuploads.setError("Post/Uploads is required");
            return false;
        } else if (platformArr.length == 0) {
            Toast.makeText(getActivity(), "Platform is required", Toast.LENGTH_LONG).show();
            return false;
        } else if (professionArr.length == 0) {
            Toast.makeText(getActivity(), "Professions is required", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                profilepicture.setImageBitmap(bitmap);
            }
        } else if (requestCode == REQUEST_IMAGE_PIC) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    profilepicture.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void toLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void updateUsernameType(Boolean influencer, Boolean company) {
        if (influencer && company) {
            usernameType.setText("Influencer & Company profile");
            return;
        } else if (influencer) {
            usernameType.setText("Influencer profile");
            return;
        } else if (company) {
            usernameType.setText("Company profile");
            return;
        }
    }

    public void setRejectOffersArrList(String[] arr) {
        rejectedOffers = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            rejectedOffers.add(arr[i]);
        }
    }
}