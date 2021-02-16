package com.example.firebase_addfields_automatique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Result extends AppCompatActivity {

    // instantiate Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SharedPreferences preferences;

    private String valueholder = "itemid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Instantiate shared preferences to hold our item info ( ID )
        preferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // Load our item ID
        int preferencesholder = preferences.getInt(valueholder,0);

        TextView items_result = findViewById(R.id.result_textview);

        if (isconnected()){
            db.collection("/items").get().addOnCompleteListener(task -> {

                //get document data from firestore
                QuerySnapshot querySnapshot = task.getResult();
                assert querySnapshot != null;
                final List<DocumentSnapshot> items = querySnapshot.getDocuments();

                // get the id of the item from firestore , same id which is stored in shared preferences
                items_result.setText("this is result of item number :" + items.get(preferencesholder).get("item_id")
                        .toString() + " \n which content is :" + items.get(preferencesholder).get("item_content"));

                items_result.setTextColor(Color.parseColor("#FFFFFF"));



            }).addOnFailureListener(e -> {


                showtoast(e.getMessage());  // show what was wrong
            });
        }

    }



    private void showtoast(String toastmessage){
        Toast.makeText(Result.this , toastmessage , Toast.LENGTH_LONG ).show();
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