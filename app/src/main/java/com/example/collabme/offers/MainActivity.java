package com.example.collabme.offers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.ui.NavigationUI;

import com.example.collabme.model.Model;
import com.example.collabme.R;
import com.example.collabme.sigupprocess.LoginActivity;

public class MainActivity extends AppCompatActivity {
    NavController navCtl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHost navHost = (NavHost)getSupportFragmentManager().findFragmentById(R.id.nav_main);
        navCtl = navHost.getNavController();

        // TODO: 3/13/2022 do not deletee!!!!!
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        ////


        NavigationUI.setupActionBarWithNavController(this, navCtl);
        // this is the main activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    private void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(!super.onOptionsItemSelected(item)){

            switch (item.getItemId()){
                case R.id.profiel_fragment:
                    navCtl.navigate(R.id.action_global_userProfile2);

                    break;

                case R.id.home_fragment:

                    // Model.instance.getUserName(email);
                    navCtl.navigate(R.id.action_global_homeFragment2);
                    break;

                case R.id.menu_logout:

                    // Model.instance.getUserName(email);
                    Model.instance.logout(new Model.logout() {
                        @Override
                        public void onComplete(int code) {
                            if(code==200) {
                                toLoginActivity();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "boo boo", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;

                case R.id.addoffer:
                    // Model.instance.getUserName(email);
                    navCtl.navigate(R.id.action_global_addOfferDetailsFragemnt);
                    break;



            }


        }

        return false;
    }
}