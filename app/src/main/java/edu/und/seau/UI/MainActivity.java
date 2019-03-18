package edu.und.seau.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import edu.und.seau.uav.R;

public class MainActivity extends AppCompatActivity {


    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_UAVs = new ArrayList<>();
    private final String name="REMOTE";
    private DatabaseReference root;


    private static final String TAG = MainActivity.class.getSimpleName();

    //Main Function


    private static final String[] LOCATION_PERMS={
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int LOCATION_REQUEST=1338;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference().getRoot();
        setContentView(R.layout.activity_main);

        if (!canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }

        ListView listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_of_UAVs);

        listView.setAdapter(arrayAdapter);


        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();

                for (DataSnapshot o : dataSnapshot.getChildren()) {
                    set.add((o).getKey());
                }

                list_of_UAVs.clear();
                list_of_UAVs.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), control_screen.class);
                intent.putExtra("UAV_name",((TextView)view).getText().toString() );
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });
    }

    private boolean canAccessLocation() {
        return((hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)) && (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}
