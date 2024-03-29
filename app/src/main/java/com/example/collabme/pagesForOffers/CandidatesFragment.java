package com.example.collabme.pagesForOffers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.collabme.Activites.LoginActivity;
import com.example.collabme.R;
import com.example.collabme.model.ModelCandidates;
import com.example.collabme.model.ModelOffers;
import com.example.collabme.model.ModelPhotos;
import com.example.collabme.model.ModelUsers;
import com.example.collabme.model.Modelauth;
import com.example.collabme.objects.Offer;
import com.example.collabme.objects.User;
import com.example.collabme.viewmodel.CandidatesViewmodel;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.util.ArrayUtils;


/**
 * the candidates fragmenrt - inclused :
 * viewholder for the recycleview in the candidates page
 * viewmodel for the users which are a candidates
 * refreshpost call from the model for refreshing the users that are candidates
 * Adapter for the recycleview items -users candidates items
 * search is includs - to search the candidates you want to pick from
 * you can choose a candidate for your offer with the checkbox to
 *      each candidate and the choose button
 * swipe refresh for the refreshing of the candidates
 */

public class CandidatesFragment extends Fragment {

    ImageView logout;
    MyAdapter adapter;
    CandidatesViewmodel viewModel;
    String offerId;
    OnItemClickListener listener;
    SwipeRefreshLayout swipeRefresh;
    Button choosen;
    CheckBox choosenCandidate;
    TextView username;
    EditText candidatesearch;
    ImageView searchimg;
    boolean contains = false, clicked = false;
    User user1;
    ImageButton backBtn;
    int positionOut;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(CandidatesViewmodel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidates, container, false);

        offerId = CandidatesFragmentArgs.fromBundle(getArguments()).getOfferId();
        ModelCandidates.instance2.refreshPostList(offerId);

        candidatesearch = view.findViewById(R.id.fragment_candidates_freesearch);
        searchimg = view.findViewById(R.id.fragment_candidates_searchbtn);

        swipeRefresh = view.findViewById(R.id.candidates_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> ModelCandidates.instance2.refreshPostList(offerId));

        //////////////////////////////

        searchimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                String n = candidatesearch.getText().toString();
                ModelCandidates.instance2.getCandidateFromSearch(n, new ModelCandidates.getCandidateFromSearchListener() {
                    @Override
                    public void onComplete(User user) {
                        user1 = user;
                        ModelOffers.instance.getOfferById(offerId, new ModelOffers.GetOfferListener() {
                            @Override
                            public void onComplete(Offer offer) {
                                if (user1 != null) {
                                    if (!user1.getUsername().equals("")) {
                                        contains = ArrayUtils.contains(offer.getUsers(), user1.getUsername());
                                    }
                                } else {
                                    contains = false;
                                }

                                RecyclerView list = view.findViewById(R.id.candidates_rv);
                                list.setHasFixedSize(true);
                                adapter = new MyAdapter();
                                list.setAdapter(adapter);
                                setHasOptionsMenu(true);
                            }
                        });
                    }
                });
            }

        });

        ////////////////////////////

        RecyclerView list = view.findViewById(R.id.candidates_rv);
        list.setHasFixedSize(true);
        choosen = view.findViewById(R.id.fragemnt_candidates_choosen);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        setHasOptionsMenu(true);
        viewModel.getCandidates(offerId).observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(ModelCandidates.instance2.getcandidateslistloding().getValue() == ModelCandidates.candidatelistloding.loading);
        ModelCandidates.instance2.getcandidateslistloding().observe(getViewLifecycleOwner(), PostsListLoadingState -> {
            if (PostsListLoadingState == ModelCandidates.candidatelistloding.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        logout = view.findViewById(R.id.fragment_candidates_logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Modelauth.instance2.logout(code -> {
                    if (code == 200) {
                        ModelUsers.instance3.setUserConnected(null);
                        LoginManager.getInstance().logOut();
                        toLoginActivity();
                    }
                });
            }
        });

        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int idview) {
                if (view.findViewById(R.id.candidates_listrow_checkBox).getId() != idview) {
                    User user = viewModel.getCandidates(offerId).getValue().get(position);
                    Navigation.findNavController(view).navigate(CandidatesFragmentDirections.actionCandidatesFragmentToUserProfile(
                            user.getUsername(), user.getPassword(), user.getCompany(), user.getInfluencer(), user.getAge(), user.getEmail(), user.getSex(),
                            user.getPlatforms(), user.getProfessions(), user.getFollowers(), user.getNumOfPosts()));
                }
            }
        });

        backBtn = view.findViewById(R.id.fragment_candidates_backBtn);
        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());


        choosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosenCandidate == null) {
                    Toast.makeText(getActivity(), "you didnt choose candidate", Toast.LENGTH_LONG).show();
                } else if (positionOut ==0 || choosenCandidate.isChecked()) {
                    ModelOffers.instance.getOfferById(offerId, new ModelOffers.GetOfferListener() {
                        @Override
                        public void onComplete(Offer offer) {
                            offer.setStatus("InProgress");
                            String[] user = new String[1];
                            user[0] = username.getText().toString();
                            offer.setUsers(user);
                            ModelOffers.instance.editOffer(offer, new ModelOffers.EditOfferListener() {
                                @Override
                                public void onComplete(int code) {
                                    if (code == 200) {
                                        Navigation.findNavController(v).navigate(CandidatesFragmentDirections.actionCandidatesFragmentToInprogressfragment(offerId));
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "you didnt choose candidate", Toast.LENGTH_LONG).show();
                }
            }
        });
        refresh();

        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }
    //////////////////////////VIEWHOLDER////////////////////////////////////

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profilepic;
        public MyViewHolder(@NonNull View itemView) {


            super(itemView);

            username = itemView.findViewById(R.id.candidates_listrow_username);
            choosenCandidate = itemView.findViewById(R.id.candidates_listrow_checkBox);
            profilepic=itemView.findViewById(R.id.row_candidates_profile);
            choosenCandidate.setChecked(false);
            itemView.setOnClickListener(v -> {
                int viewId = v.getId();
                int pos = getAdapterPosition();
                listener.onItemClick(pos, v, viewId);
            });
            choosenCandidate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int viewid = v.getId();
                    int position = getAdapterPosition();
                    positionOut = position;
                    listener.onItemClick(position, itemView, viewid);
                }
            });
        }

        public void bind(User user) {
            if (contains && clicked) {
                username.setText(candidatesearch.getText().toString());
            }
            if (!clicked) {
                username.setText(user.getUsername());
            }

            if (user != null) {
                if (user.getImage() != null) {
                    ModelPhotos.instance3.getimages(user.getImage(), new ModelPhotos.getimagesfile() {
                        @Override
                        public void onComplete(Bitmap responseBody) {
                            if (responseBody != null) {
                                profilepic.setImageBitmap(responseBody);

                            }
                        }
                    });
                }
                else {
                    profilepic.setImageResource(R.drawable.profile);
                }
            }
            choosenCandidate.setChecked(false);
        }
    }

    private void toLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    //////////////////////////MYYYYYYYY APATERRRRRRRR///////////////////////
    interface OnItemClickListener {
        void onItemClick(int position, View view, int idview);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        public void setListener(OnItemClickListener listener1) {
            listener = listener1;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.candidtaes_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (contains && clicked) {
                User user = user1;
                holder.bind(user);
            }
            if (!clicked) {
                User post = viewModel.getCandidates(offerId).getValue().get(position);
                holder.bind(post);
            }
        }

        @Override
        public int getItemCount() {
            if (!clicked) {
                if (viewModel.getCandidates(offerId).getValue() == null) {
                    return 0;
                }
                return viewModel.getCandidates(offerId).getValue().size();
            } else {
                if (contains) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}