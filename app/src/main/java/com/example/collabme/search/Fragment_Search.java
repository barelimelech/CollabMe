package com.example.collabme.search;

import static android.graphics.Color.rgb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.collabme.Activites.LoginActivity;
import com.example.collabme.R;
import com.example.collabme.model.ModelSearch;
import com.example.collabme.model.ModelUsers;
import com.example.collabme.model.Modelauth;
import com.example.collabme.objects.Offer;
import com.facebook.login.LoginManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
/**
 * the search  fragment - included :
 * filter according to felled in the offer object
 * has ranges for different searches for price and date
 * can filter for couple of fields together
 * has a free search for looking information on all the fields of the offer
 */

public class Fragment_Search extends Fragment {

    Offer[] offersFromSearch;
    EditText proposer, headline, todates, toprice, fromdates, fromprice, freeSearch, description;
    TextView professions;
    Button search;
    ImageView freesearchbutton, logout;
    String proposer1, headline1, todates1, fromdates1, toprice1, freeSearch1, fromprice1, description1;
    boolean[] selectedProfessions = new boolean[16];
    ArrayList<Integer> langList = new ArrayList<>();
    String[] langArray = {"Sport", "Cooking", "Fashion", "Music", "Dance", "Cosmetic", "Travel", "Gaming", "Tech", "Food",
            "Art", "Animals", "Movies", "Photograph", "Lifestyle", "Other"};
    String[] professionArr, chosen;
    boolean goodsign = true;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        proposer = view.findViewById(R.id.fragment_Search_proposer);
        headline = view.findViewById(R.id.fragment_Search_headline);
        fromdates = view.findViewById(R.id.fragment_Search_fromdates);
        fromprice = view.findViewById(R.id.fragment_Search_from_price_et);
        todates = view.findViewById(R.id.fragment_Search_todates_et);
        toprice = view.findViewById(R.id.fragment_Search_toprice_et);
        search = view.findViewById(R.id.fragment_Search_button_search);
        professions = view.findViewById(R.id.fragment_Search_profession);
        freeSearch = view.findViewById(R.id.fragment_Search_freesearch_et);
        freesearchbutton = view.findViewById(R.id.fragment_search_freesearc_btn);
        logout = view.findViewById(R.id.fragment_search_logoutBtn);

        description = view.findViewById(R.id.fragment_Search_description);
        progressBar = view.findViewById(R.id.search_progressbar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(rgb(132, 80, 160), android.graphics.PorterDuff.Mode.MULTIPLY);

        professions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // set title
                builder.setTitle("Select Professions:");

                // set dialog non cancelable
                builder.setCancelable(false);

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
                        StringBuilder stringBuilder;
                        // Initialize string builder
                        stringBuilder = new StringBuilder();
                        chosen = new String[langList.size()];

                        // use for loop
                        for (int j = 0; j < langList.size(); j++) {
                            // concat array value

                            stringBuilder.append(langArray[langList.get(j)]);
                            chosen[j] = (langArray[langList.get(j)]); //to check again

                            // check condition
                            if (j != langList.size() - 1) {
                                // When j value not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        professions.setText(stringBuilder.toString());
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

        freesearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAcordingtoParamters();
                if (!freeSearch1.equals("")) {
                    ModelSearch.instance.getOfferFromFreeSearch(freeSearch1, new ModelSearch.getOfferFromFreeSearchListener() {
                        @Override
                        public void onComplete(List<Offer> offers) {
                            offersFromSearch = offers.toArray(new Offer[0]);
                            Navigation.findNavController(view).navigate(
                                    Fragment_SearchDirections.actionFragmentSearchToFragmentSearchResults(offersFromSearch));
                        }
                    });
                } else {
                    Navigation.findNavController(view).navigate(
                            Fragment_SearchDirections.actionFragmentSearchToFragmentSearchResults(null));
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                searchAcordingtoParamters();
                checks();
                if (goodsign) {
                    String to, from;
                    String[] todates1strings = todates1.split("/" /*<- Regex */);
                    if (!todates1strings[0].equals("null")) {
                        to = todates1strings[0] + todates1strings[1] + todates1strings[2];
                    } else {
                        to = "null";
                    }
                    String[] fromdates1strings = fromdates1.split("/" /*<- Regex */);
                    if (!fromdates1strings[0].equals("null")) {
                        from = fromdates1strings[0] + fromdates1strings[1] + fromdates1strings[2];
                    } else {
                        from = "null";
                    }

                    ModelSearch.instance.getOfferFromSpecificSearch(description1, headline1, from, to, fromprice1, toprice1, proposer1,
                            chosen, offers -> {
                                offersFromSearch = offers.toArray(new Offer[0]);

                                Navigation.findNavController(view).navigate(Fragment_SearchDirections.actionFragmentSearchToFragmentSearchResults(
                                        offersFromSearch));

                            });
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
    }

    public void searchAcordingtoParamters() {
        proposer1 = proposer.getText().toString();
        if (proposer1.equals("")) {
            proposer1 = "null";
        }
        headline1 = headline.getText().toString();
        if (headline1.equals("")) {
            headline1 = "null";
        }
        todates1 = todates.getText().toString();
        if (todates1.equals("")) {
            todates1 = "null";
        }
        fromdates1 = fromdates.getText().toString();
        if (fromdates1.equals("")) {
            fromdates1 = "null";
        }
        toprice1 = toprice.getText().toString();
        if (toprice1.equals("")) {
            toprice1 = "null";
        }
        fromprice1 = fromprice.getText().toString();
        if (fromprice1.equals("")) {
            fromprice1 = "null";
        }
        description1 = description.getText().toString();
        if (description1.equals("")) {
            description1 = "null";
        }
        freeSearch1 = freeSearch.getText().toString();
    }

    private void toLoginActivity() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public void checks() {
        goodsign = true;
        if (!isValidFormat("dd/MM/yyyy", fromdates1) && (!fromdates1.equals("null"))) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "from date is not a date format", Toast.LENGTH_SHORT).show();
            goodsign = false;
            return;
        } else {
            goodsign = true;
        }

        if (!isValidFormat("dd/MM/yyyy", todates1) && (!todates1.equals("null"))) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "to date is not a date format", Toast.LENGTH_SHORT).show();
            goodsign = false;
            return;
        } else {
            goodsign = true;
        }

        if ((!fromdates1.equals("null") && todates1.equals("null")) || (!todates1.equals("null") && fromdates1.equals("null"))) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "you have to fill both from and to date", Toast.LENGTH_SHORT).show();
            goodsign = false;
            return;
        } else {
            goodsign = true;
        }

        if (!fromdates1.equals("null") && !todates1.equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
            try {
                Date fromDate = sdf.parse(fromdates1);
                Date toDate = sdf.parse(todates1);
                if (!fromDate.before(toDate)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Date must be before to date ", Toast.LENGTH_SHORT).show();
                    goodsign = false;
                    return;
                }

            } catch (ParseException ex) {
                ex.printStackTrace();

            }
        }
        if (!fromprice1.equals("null") && !toprice1.equals("null")) {
            if (!(Integer.parseInt(fromprice1) <= Integer.parseInt(toprice1))) {
                progressBar.setVisibility(View.GONE);
                fromprice.setError("Must be small from to price ");
                goodsign = false;
                return;
            }
        }

        if ((!fromprice1.equals("null") && toprice1.equals("null")) || (!toprice1.equals("null") && fromprice1.equals("null"))) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "you have to fill both from and to price", Toast.LENGTH_SHORT).show();
            goodsign = false;
            return;
        }


        if (!isInteger(fromprice1) && (!fromprice1.equals("null")) && !(fromprice1.matches("^[1-9]{1}(?:[0-9])*?$"))) {

            progressBar.setVisibility(View.GONE);
            fromprice.setError("Must numbers");
            goodsign = false;
            return;
        }


        if (!isInteger(toprice1) && (!toprice1.equals("null")) && !(toprice1.matches("^[1-9]{1}(?:[0-9])*?$"))) {
            progressBar.setVisibility(View.GONE);
            toprice.setError("Must numbers");

            goodsign = false;
            return;
        } else {
            goodsign = true;
        }
    }

    public static boolean equalsArr(Object[] a, Object[] b) {
        if (a.length != b.length) return false;
        outer:
        for (Object aObject : a) {
            for (Object bObject : b) {
                if (a.equals(b)) continue outer;
            }
            return false;
        }
        return true;
    }

}