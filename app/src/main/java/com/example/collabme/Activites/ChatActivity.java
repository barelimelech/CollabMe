package com.example.collabme.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collabme.R;
import com.example.collabme.model.ModelChatUser;
import com.example.collabme.objects.ChatUserConvo;
import com.example.collabme.objects.MessageAdapter;
import com.example.collabme.objects.Messege;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 *
 * the connection to sockets in the server
 * sending massege to the user
 * recived messeges from the user
 * before,after,on, sending messeges in the chat itself
 * able to send text and image messeges
 *
 */

public class ChatActivity extends AppCompatActivity  {



   private Socket mSocket;
   private List<Messege> mMessages = new ArrayList<Messege>();
   private RecyclerView.Adapter mAdapter;
   private RecyclerView mMessagesView;

   private TextInputEditText mInputMessageView;
   private Handler mTypingHandler = new Handler();

   private boolean mTyping = false;

   private static final int TYPING_TIMER_LENGTH = 600;
   private String mUsername;
   private String mUsernametexting;
   Button cancel;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chat);

      mUsername = getIntent().getStringExtra("name");
      mUsernametexting = getIntent().getStringExtra("usernametext");
      cancel = findViewById(R.id.chat_cancel);
      try {
         mSocket = IO.socket("http://10.0.2.2:3000");
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }
      mSocket.on("newMessage", onNewMessage);

      mSocket.connect();
      mAdapter = new MessageAdapter(this, mMessages);

      mMessagesView = findViewById(R.id.recycler_gchat);
      mMessagesView.setLayoutManager(new LinearLayoutManager(this));
      mMessagesView.setAdapter(mAdapter);
     mInputMessageView = findViewById(R.id.edit_gchat_message);
      ChatUserConvo chatUserConvo =new ChatUserConvo();
      chatUserConvo.setUsernameConnect(mUsername);
      chatUserConvo.setUserNameYouWrite(mUsernametexting);

      cancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
         }
      });
      ModelChatUser.instance3.getChatOtherSide(chatUserConvo, new ModelChatUser.GetUserChatWithAnother() {
         @Override
         public void onComplete(List<ChatUserConvo> list1) {
            if(list1!=null || list1.size()!=0) {
               for (int i = 0; i < list1.size(); i++) {
                  addMessage(list1.get(i).getUsernameConnect(), list1.get(i).getTheChat());
               }
            }
            chatUserConvo.setUsernameConnect(mUsernametexting);
            chatUserConvo.setUserNameYouWrite(mUsername);
            ModelChatUser.instance3.getChatOtherSide(chatUserConvo, new ModelChatUser.GetUserChatWithAnother() {
               @Override
               public void onComplete(List<ChatUserConvo> list) {
                  if(list!=null || list.size()!=0) {
                     for (int i = 0; i < list.size(); i++) {
                        addMessage(list.get(i).getUsernameConnect(), list.get(i).getTheChat());
                     }
                  }
               }
            });

         }
      });



      AppCompatButton sendButton = findViewById(R.id.button_gchat_send);
      sendButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            attemptSend();
         }
      });
   }

   private void attemptSend() {
      if (null == mUsername) return;
      if (!mSocket.connected()) return;

      mTyping = false;

      String message = mInputMessageView.getText().toString().trim();
      if (TextUtils.isEmpty(message)) {
         mInputMessageView.requestFocus();
         return;
      }

      mInputMessageView.setText("");
      addMessage(mUsername, message);

      try {

         JSONObject data = new JSONObject();
         data.put("username",mUsername);
         data.put("messageContent",message);
         data.put("usernametext",mUsernametexting);
         // perform the sending message attempt.
         mSocket.emit("newMessage", data);

      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   private Emitter.Listener onNewMessage = new Emitter.Listener() {
      @Override
      public void call(final Object... args) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
               JSONObject data = (JSONObject) args[0];
               String username;
               String message;

               try {
                  username = data.getString("username");
                  message = data.getString("messageContent");

               } catch (JSONException e) {

                  return;
               }

              //removeTyping(username);
               addMessage(username, message);
            }
         });
      }
   };



   private void addMessage(String username, String message) {
      mMessages.add(new Messege.Builder(Messege.TYPE_MESSAGE)
              .username(username).message(message).build());
      mAdapter.notifyItemInserted(mMessages.size() - 1);
      scrollToBottom();
   }



   private void scrollToBottom() {
      mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      mSocket.disconnect();
   }


}