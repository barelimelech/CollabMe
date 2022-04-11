package com.example.collabme.Activites;

import static com.example.collabme.objects.MyApplication.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.collabme.R;
import com.example.collabme.model.ModelMediaContent;
import com.example.collabme.model.ModelOffers;
import com.example.collabme.model.ModelUsers;
import com.example.collabme.objects.Offer;
import com.example.collabme.objects.User;
import com.example.collabme.search.fragment_search_results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaContentActivity extends AppCompatActivity {

    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    OnItemClickListener listener;

    Intent intent;
    String action;
    String type;
    String[] MediaContent;
    String[] offersToSelect;
    boolean[] checkedItems;
    ArrayList<Integer> langList = new ArrayList<>();
    String[] chosenOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_media_content);

        ViewGroup view = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        ModelUsers.instance3.getUserConnect(new ModelUsers.getuserconnect() {
            @Override
            public void onComplete(User profile) {
                ModelOffers.instance.getoffersfromuserinCandidates(profile.getUsername(), new ModelOffers.getoffersfromuserinCandidates() {
                    @Override
                    public void onComplete(List<Offer> offer) {
                      //  offersToSelect1 = offer.toArray(new String[0]);
                        offersToSelect = new String[offer.size()];
                        for (int i=0;i<offersToSelect.length; i++)
                        {
                            offersToSelect[i] = offer.get(i).getIdOffer();
                        }
                        // should be the offer size
                        checkedItems = new boolean[offersToSelect.length];

                        // Initialize alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(MediaContentActivity.this);

                        // set title
                        builder.setTitle("Select an offer:");

                        // set dialog non cancelable
                        builder.setCancelable(false);

                        builder.setMultiChoiceItems(offersToSelect, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                            int count =0; // counter to limit number of selection (only one can be chosen)

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

                        builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Initialize string builder
                                StringBuilder stringBuilder = new StringBuilder();
                                chosenOffers = new String[langList.size()];

                            }
                        });

                        // show dialog
                        builder.show();
                    }
                });
            }
        });

///////////////////Dialog Alert Pop up////////////////////////////






        //////////////////////////////////////////

        // Get intent, action and MIME type
         intent = getIntent();
         action = intent.getAction();
         type = intent.getType();

         // TODO: !!!!!!!!!!  The offerID in the below function should be changed from "100" to general offer !!!!!!!!!!!!!!!
        ModelOffers.instance.getOfferById("100", new ModelOffers.GetOfferListener() {
            @Override
            public void onComplete(Offer offer) {
                MediaContent = offer.getMediaContent();
                increaseSize();
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if ("text/plain".equals(type)) {
                            String sharedText1 = intent.getStringExtra(Intent.EXTRA_TEXT);
                            if (sharedText1 != null) {
                                MediaContent[MediaContent.length-1]=(sharedText1);
                                ModelMediaContent.instance.addMediaContent("100", MediaContent, new ModelMediaContent.addMediaContentListener() {
                                    @Override
                                    public void onComplete(int code) {
                                        if (code==200) {
                                            System.out.println("add media content done successfully");
                                        }
                                    }
                                });                            }

                    }
                }
            }
        });

        RecyclerView list = view.findViewById(R.id.mediacontent_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        //...
    }

    public void increaseSize() {
        String[] temp = new String[MediaContent.length + 1];

        for (int i = 0; i < MediaContent.length; i++){
            temp[i] = MediaContent[i];
        }
        MediaContent = temp;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    //////////////////////////VIEWHOLDER////////////////////////////////////

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mediaContentURL;
        ImageView URLtypeImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mediaContentURL=(TextView)itemView.findViewById(R.id.mediacontent_listrow_url);
            URLtypeImage = (ImageView)itemView.findViewById(R.id.mediacontent_listrow_socialmedia);

            itemView.setOnClickListener(v -> {
                int viewId = v.getId();

                int pos = getAdapterPosition();
                listener.onItemClick(pos,v,viewId);

            });


        }

        void handleSendText(Intent intent) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                // Update UI to reflect text being shared
                mediaContentURL.setText(sharedText);
            }
        }

        public void bind(){
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent); // Handle text being sent
                }
        }

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
            View view = getLayoutInflater().inflate(R.layout.fragment_mediacontent_list_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
       //     if(offersFromSearch == null){
            //    return 0;
         //   }
        //    return offersFromSearch.length;
            return 1;
        }
    }

    public String [] ChangeToArray(List<String> array){
        String [] arrayList = new String [array.size()];
        for(int i=0;i<array.size();i++){
            arrayList[i]=array.get(i);
        }

        return arrayList;
    }


}