package com.example.firebase_addfields_automatique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // instantiate Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    SharedPreferences preferences;

    private String valueholder = "itemid";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // our layout to fill
        LinearLayout itemslayout = findViewById(R.id.mylayout);

        // instantiate shared preferences to hold our item info ( ID )
        preferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);



        if(isconnected()){ // execute only if internet connection exists


            db.collection("/items").get().addOnCompleteListener(task -> {
                // if task of retrieving data succeeded

                QuerySnapshot querySnapshot = task.getResult();
                assert querySnapshot != null;
                final List<DocumentSnapshot> items = querySnapshot.getDocuments();

                // let's loop over all the items we have in the Firestore collection
                for (int i = 0; i < items.size(); i++) {
                    // Create new text view each loop
                    final TextView item_textview = new TextView(MainActivity.this);

                    // give it an ID (if needed)
                    item_textview.setId(i);

                    // set textview content
                    item_textview.setText(items.get(i).get("item_id")+" : "+items.get(i).get("item_content"));
                    // style our text
                    item_textview.setTextSize(15);
                    item_textview.setGravity(Gravity.CENTER);
                    item_textview.setHeight(130);

                    item_textview.setTextColor(Color.parseColor("#FFFFFF"));

                    item_textview.setTypeface(ResourcesCompat.getFont(MainActivity.this,R.font.raleway_medium));

                    // color pair numbers of rows with a colour and odd with an other colour

                    if (i%2 == 0){
                        item_textview.setBackgroundColor(Color.parseColor("#c2b9bb"));
                    }else {
                        item_textview.setBackgroundColor(Color.parseColor("#2f2539"));
                    }

                    // Add our items to our layout one by one
                    itemslayout.addView(item_textview);

                    item_textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isconnected()){

                                // add id of clicked item to our shared preferences holder
                                preferences.edit().putInt(valueholder, item_textview.getId()).apply();

                                // start activity in which we will show out item
                                startActivity(new Intent(MainActivity.this, Result.class));


                            }
                        }
                    });

                }
            }).addOnFailureListener(e -> {
                // if task fails show error
                showtoast(e.getMessage());
            });
        }else{
            showtoast("please make sure you are connected to the internet");
        }
    }

    //just a random method to generate toasts rapidly :D
    private void showtoast(String toastmessage){
        Toast.makeText(MainActivity.this , toastmessage , Toast.LENGTH_LONG ).show();
    }


    // check if user is connected to internet
    private boolean isconnected(){
        // Instantiate connectivity mannager
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // check which connection is used
        NetworkInfo activenetwork = cm.getActiveNetworkInfo();

        // return true if current connection is connected
        return activenetwork != null && activenetwork.isConnected();

    }
}