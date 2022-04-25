package com.example.collabme.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.collabme.Activites.ChatActivity;
import com.example.collabme.R;
import com.example.collabme.model.ModelPhotos;
import com.example.collabme.model.ModelUsers;
import com.example.collabme.objects.User;
import com.example.collabme.viewmodel.userViewModel;

//


public class chatUserPage extends Fragment {

    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    OnItemClickListener listener;
    userViewModel viewModel;
    String username;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(userViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatuser_page,container,false);

        swipeRefresh = view.findViewById(R.id.users_swiperefresh);
        swipeRefresh.setOnRefreshListener(ModelUsers.instance3::refreshPostList);

        RecyclerView list = view.findViewById(R.id.users_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);


        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view,int idview) {
                ModelUsers.instance3.getUserConnect(new ModelUsers.getuserconnect() {
                    @Override
                    public void onComplete(User profile) {
                        username = profile.getUsername();
                        tochatActivity();
                    }
                });

            }

        });

        setHasOptionsMenu(true);
        viewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(ModelUsers.instance3.getusersListLoadingState().getValue() == ModelUsers.UserLoadingState.loading);
        ModelUsers.instance3.getusersListLoadingState().observe(getViewLifecycleOwner(), PostsListLoadingState -> {
            if (PostsListLoadingState == ModelUsers.UserLoadingState.loading){
                swipeRefresh.setRefreshing(true);
            }else{
                swipeRefresh.setRefreshing(false);
            }

        });



        //adapter.notifyDataSetChanged();

        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    private void tochatActivity() {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("name",username);


        startActivity(intent);
        getActivity().finish();
    }
    //////////////////////////VIEWHOLDER////////////////////////////////////

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView user_pic;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username=(TextView)itemView.findViewById(R.id.users__listrow_username);
            user_pic =(ImageView)itemView.findViewById(R.id.row_users_profile);


            itemView.setOnClickListener(v -> {
                int viewId = v.getId();
                int pos = getAdapterPosition();
                listener.onItemClick(pos,v,viewId);

            });


        }
        public void bind(User user){
            username.setText(user.getUsername());
            ModelPhotos.instance3.getimages(user.getImage(), new ModelPhotos.getimagesfile() {
                @Override
                public void onComplete(Bitmap responseBody) {
                    user_pic.setImageBitmap(responseBody);
                }
            });
        }
    }


    //////////////////////////MYYYYYYYY APATERRRRRRRR///////////////////////
    interface OnItemClickListener{
        void onItemClick(int position,View view,int idview);
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        public void setListener(OnItemClickListener listener1) {
            listener = listener1;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.user_caht_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            User user = viewModel.getData().getValue().get(position);
            holder.bind(user);

        }

        @Override
        public int getItemCount() {
            if(viewModel.getData().getValue() == null){
                return 0;
            }
            return viewModel.getData().getValue().size();
        }
    }


}