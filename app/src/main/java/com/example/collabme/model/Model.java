package com.example.collabme.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Model {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:4000";
    public static final Model instance = new Model();
    public String token;
    public static final String MY_PREFRENCE = "myPrefs";
    public static final String TOKEN = "myToken";
    Context context;
    String userToken;
    //Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostLastUpdateDate",0);
    MutableLiveData<OffersListLoadingState> offersListLoadingState = new MutableLiveData<OffersListLoadingState>();
    MutableLiveData<List<Offer>> offersList = new MutableLiveData<List<Offer>>();
    public String username1="liem";
    public String offerId = "622e2fed8fba1393eee2da12";
    /**
     *
     * the section of the offers
     * interfaces
     */
    public interface signupListener{
        void onComplete(int code);
    }
    public interface addOfferListener{
        void onComplete(int code);
    }
    public interface loginListener{
        void onComplete(int code);
    }

    public interface logout{
        void onComplete(int code);
    }
    public interface getuserconnect{
        void onComplete(User profile);

    }
    public interface GetUserByIdListener{
        void onComplete(User profile);

    }
    public enum OffersListLoadingState{
        loading,
        loaded
    }

    public interface GetOfferListener{
        void onComplete(Offer offer);

    }
    public interface EditOfferListener{
        void onComplete(int code);

    }

    public interface deleteoffer{
        void onComplete(int code);

    }
    public interface EditUserListener{
        void onComplete(int code);

    }

    public Model() {
        offersListLoadingState.setValue(OffersListLoadingState.loaded);;
    }
    public interface GetAllOffersListener{
        void onComplete(List<Offer> list);
    }

    /**
     *
     * the section of the offers
     * add
     * edit
     * get
     * refreshomepage
     */

    public LiveData<OffersListLoadingState> getoffersListLoadingState() {
        return offersListLoadingState;
    }

    public LiveData<List<Offer>> getAll(){
        if (offersList.getValue() == null) { refreshPostList(); };
        return  offersList;
    }
    public void refreshPostList(){
        offersListLoadingState.setValue(OffersListLoadingState.loading);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        String tockenacsses = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenAcsses","");


        Call<List<Offer>> call = retrofitInterface.getoffers("Bearer " + tockenacsses);
        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (response.code() == 200) {
                    List<Offer> stList = response.body();
                    offersList.postValue(stList);
                    offersListLoadingState.postValue(OffersListLoadingState.loaded);

                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                offersList.postValue(null);
            }
        });


    }


    public void addOffer(Offer offer,Model.addOfferListener addOffer) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("Description", offer.getDescription());
        map.put("HeadLine", offer.getHeadline());
        map.put("Price", offer.getPrice());
        map.put("Coupon", offer.getCoupon());
        map.put("IdOffer", offer.getIdOffer());
        map.put("Status", offer.getStatus());
        map.put("Profession", offer.getProfession());
        map.put("User", offer.getUser());
        map.put("IntrestedVerify", offer.getIntrestedVerify());


        String tockenacsses = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenAcsses","");

        Call<Offer> call = retrofitInterface.executenewOffer(map,"Bearer " + tockenacsses);
        call.enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {

                if (response.code() == 200) {

                    addOffer.onComplete(200);

                } else {
                    addOffer.onComplete(400);

                }
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                addOffer.onComplete(400);
            }
        });
    }

    public void editOffer(Offer newOffer, EditOfferListener editOfferListener){
        // getOfferById(offerId, getOfferListener);
        offerId = "622f01aaf5223e5bc4be080a";

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String tokenAccess = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenAcsses","");

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Map<String, Object> map = newOffer.toJson();

        Call<Void> call = retrofitInterface.editOffer(offerId,"Bearer "+tokenAccess,map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                editOfferListener.onComplete(200);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("TAG","basaaaaaa  a a a "+t);
                editOfferListener.onComplete(400);

            }
        });

    }

    public void getOfferById(GetOfferListener getOfferListener) {
        offerId = "622f01aaf5223e5bc4be080a";
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String tokenAccess = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenAcsses","");



        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<Offer> call = retrofitInterface.getOfferById(offerId,"Bearer "+tokenAccess);
        call.enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {
                getOfferListener.onComplete(response.body());
            }
            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                Log.d("TAG","basaaaaaa  a a a "+t);

                getOfferListener.onComplete(null);

            }
        });
    }




    /**
     *
     * the section of the Autantication and login functionalty
     * sighup
     * Login
     * logout
     */
    public void sighup(User profile,Model.signupListener sighup) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
            HashMap<String, Object> map = new HashMap<>();
            map = profile.tojson();
            Call<Void> call = retrofitInterface.executeSignup(map);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) {
                        sighup.onComplete(200);

                    } else {
                        sighup.onComplete(400);

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    sighup.onComplete(400);
                }
            });
        }



    public void Login(String username,String password,Model.loginListener Login){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
          HashMap<String, String> map = new HashMap<>();
            map.put("Username", username);
            map.put("Password", password);
            username1=username;

            Call<tokenrespone> call = retrofitInterface.executeLogin(map);
            call.enqueue(new Callback<tokenrespone>() {
                @Override
                public void onResponse(Call<tokenrespone> call, Response<tokenrespone> response) {
                    if (response.code() == 200) {
                        String tokenResponse = response.body().getaccessToken();
                        String tokenrefresh = response.body().getrefreshToken();
                        MyApplication.getContext()
                                .getSharedPreferences("TAG",Context.MODE_PRIVATE)
                                .edit()
                                .putString("tokenAcsses",tokenResponse)
                                .commit();
                        MyApplication.getContext()
                                .getSharedPreferences("TAG1",Context.MODE_PRIVATE)
                                .edit()
                                .putString("tokenrefresh",tokenrefresh)
                                .commit();
                        Login.onComplete(200);

                    } else  {
                        Login.onComplete(400);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<tokenrespone> call, Throwable t) {
                    Login.onComplete(400);
                }

            });



    }

    public void logout(logout logout){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String tockenrefresh = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenrefresh","");


        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<Void> call = retrofitInterface.excutelogout("Bearer "+ tockenrefresh);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                logout.onComplete(200);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logout.onComplete(400);
            }
        });


    }

    /**
     *
     * the section of the users
     * getuser
     * getuserbyid
     * edituser
     */
    public void getUserById(String id, GetUserByIdListener getUserByIdListener) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String tockenacsses = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenAcsses","");

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.getUserById(id,"Bearer "+tockenacsses);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                getUserByIdListener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                getUserByIdListener.onComplete(null);
            }
        });
    }


    public void getUserConnect(getuserconnect getuserconnect) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String tockenacsses = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenAcsses","");

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.getUser(username1,"Bearer "+tockenacsses);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                getuserconnect.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                getuserconnect.onComplete(null);
            }
        });
    }


    public void EditUser(User profile,EditUserListener editUserListener){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String tockenrefresh = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getString("tokenrefresh","");
        HashMap<String, Object> map = new HashMap<>();
          map = profile.tojson();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.editUser(profile.getUsername(),"Bearer "+ tockenrefresh,map);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                editUserListener.onComplete(200);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                editUserListener.onComplete(400);
            }
        });

    }



}


