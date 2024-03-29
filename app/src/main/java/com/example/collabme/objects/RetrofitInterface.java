
package com.example.collabme.objects;

/**
 *
 * * the retrofitinterface object  -object for the connection to the server with functions
 * every function have an url that leads to the function in the server
 * there is get -get offer and offers and more
 * there is post - add offer or edit offer and more
 * every function is interfacese with the function in the server acording to the url and
 *  if its post or get in the server and here
 *
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @Multipart
    @POST("/image/upload")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("upload") RequestBody name);

    @GET("/image/file/{filename}")
    Call<ResponseBody> getimage(@Path("filename") String namefile);

    @GET("/users/getUser/{username}")
    Call<User> getUser(@Path("username") String username,@Header("authorization") String token);

    @GET("/auth/getUserByUserNameInSignIn/{username}")
    Call<User> getUserByUserNameInSignIn(@Path("username") String username);

    @POST("/auth/login")
    Call<tokenrespone> executeLogin(@Body HashMap<String, String> map);

    @POST("/auth/logout")
    Call<Void> excutelogout(@Header("authorization") String token);

    @POST("/auth/register")
    Call<Void> executeSignup(@Body HashMap<String, Object> map);

    @POST("/users/editUser/{username}")
    Call<User> editUser(@Path("username") String username,@Header("authorization") String token, @Body Map<String, Object> newUser);

    @POST("/offer/addNewOffer")
    Call<Offer> executenewOffer(@Body Map<String, Object> map,@Header("authorization") String token);

    @POST("/payment/addpayment")
    Call<Payment> executenewPayment(@Body Map<String, Object> map,@Header("authorization") String token);

    @GET("/offer/getOfferById/{id}")
    Call<Offer> getOfferById(@Path("id") String offerId,@Header("authorization") String token);

    @GET("/offer/getoffers")
    Call<List<Offer>> getoffers(@Header("authorization") String token);


    @GET("/users/getusers")
    Call<List<User>> getusers(@Header("authorization") String token);

    @POST("/offer/editOffer/{id}")
    Call<Offer> editOffer(@Path("id") String offerId,@Header("authorization") String token, @Body Map<String, Object> newOffer);

    @POST("/offer/deleteOffer/{id}")
    Call<Void> deleteoffer(@Path("id") String offerId,@Header("authorization") String token);


    @GET("/users/authenticate")
    Call<Boolean> getuserislogin(@Header("authorization") String token);

    @GET("/candidates/getCandidates/{id}")
    Call<List<User>> getCandidates(@Path("id") String offerId,@Header("authorization") String token);

    @GET("/auth/refreshToken")
    Call<tokenrespone> getnewtoken(@Header("authorization") String token);

    @GET("/search/getOfferFromFreeSearch/{freesearch}")
    Call<List<Offer>> getOfferFromFreeSearch(@Path("freesearch") String freesearch,@Header("authorization") String token);

    @POST("/search/getOfferFromSpecificSearch")
    Call<List<Offer>> getOfferFromSpecificSearch(@Body Map<String, Object> offer, @Header("authorization") String token);

    @GET("/users/getUser/getUserByEmail/{email}")
    Call<User> getUserByEmail(@Path("email") String email,@Header("authorization") String token);

    @GET("/candidates/getoffersofUsers/{username}")
    Call<List<Offer>> getUserOffersByOffersCandidates(@Path("username") String username, @Header("authorization") String token);

    @GET("/mediacontent/getMediaContentOfAnOffer/{id}")
    Call<List<String>> getMediaContentOfAnOffer(@Path("id") String offerId,@Header("authorization") String token);

    @POST("/mediacontent/addMediaContent")
    Call<Void> addMediaContent(@Body Map<String, Object> offer, @Header("authorization") String token);

    @POST("/users/deleteuser/{username}")
    Call<User> deleteUser(@Path("username") String username,@Header("authorization") String token);

    @GET("/candidates/getoffersofUsers/{username}")
    Call<List<Offer>> getoffersfromuserinCandidates(@Path("username") String username,@Header("authorization") String token);

    @GET("/candidates/getCandidateFromSearch/{candidatesearch}")
    Call<User> getCandidateFromSearch(@Path("candidatesearch") String candidatesearch,@Header("authorization") String token);

    @POST("/users/editUserWithoutAuth/{username}")
    Call<User> editUserWithoutAuth(@Path("username") String username, @Body Map<String, Object> newUser);

    @POST("/UserChatConvo/getusersChat")
    Call<List<ChatUserConvo>> getchatConvo(@Body Map<String, Object> usernamesConvo, @Header("authorization") String token);


}