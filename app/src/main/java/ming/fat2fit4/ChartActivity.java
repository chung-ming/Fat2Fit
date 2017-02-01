package ming.fat2fit4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static ming.fat2fit4.Modules.EncoderDecoder.EncodeString;

/**
 * Created by Ernie&Ming on 29-Jan-17.
 */

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();;
    private DatabaseReference users = root.child("users");
    private DatabaseReference name;
    private DatabaseReference timestamps;
    private String username = "";
    private String emailEncode = "";
    private ListView listViewTimestamp;
    private ArrayList<String> listTimestamp = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapterTimestamp;
    Set<String> setTimestamp = new HashSet<String>();
//    private ListView listViewChild;
//    private ArrayList<String> listChild = new ArrayList<>();
//    private ArrayAdapter<String> arrayAdapterChild;
//    Set<String> setChild = new HashSet<String>();
//    private DatabaseReference userExercises;
//    private DatabaseReference userTimestamp;
//    private DatabaseReference categories;
//    private LineChart mChart;
//    private Button addEntryButton;
//    private Button toastButton;
//    private SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//    private String timestamp = s.format(new Date());
//    private String duration, sport, calorie;
//    Set<String> setMessage = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_linechart);
//        addEntryButton = (Button) findViewById(R.id.addEntryButton);
//        toastButton = (Button) findViewById(toastButton);
//        mChart = (LineChart) findViewById(R.id.chart1);
//        mChart.setOnChartValueSelectedListener(this);

//        listViewChild = (ListView) findViewById(R.id.listViewChild);
//        arrayAdapterChild = new ArrayAdapter<String>(ChartActivity.this, android.R.layout.simple_list_item_1, listChild);
//        listViewChild.setAdapter(arrayAdapterChild);

        listViewTimestamp = (ListView) findViewById(R.id.listViewTimestamp);
        arrayAdapterTimestamp = new ArrayAdapter<String>(ChartActivity.this, android.R.layout.simple_list_item_1, listTimestamp);
        listViewTimestamp.setAdapter(arrayAdapterTimestamp);

        // Load Navigation Drawer
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationIcon(R.drawable.ic_more_vert);
        // Drawer slides out when icon on toolbar is clicked
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Int Firebase
        auth = FirebaseAuth.getInstance();
        // Get user's email address. Note: Firebase does not allow '.', '#', '$', '[', or ']'
        if(auth.getCurrentUser() != null){
            emailEncode = EncodeString(auth.getCurrentUser().getEmail());
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        getTimestampAndChild();
//        toastButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = "Calorie is "+calorie+
//                        ".\nSport is "+sport+
//                        ".\nDuration is "+duration;
//                Toast.makeText(ChartActivity.this, message, Toast.LENGTH_LONG).show();
//            }
//        });

//        getTimestamp();
//        getChild();

        // Create a chart layout with all its features
//        setMChartFeatures();

//        Entry e = new Entry();
//        e.setX();

//        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);

//        setData(45, 100);

        // add empty data
//        mChart.setData(data);

//        addEntryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addEntry();
//            }
//        });

        // get the legend (only possible after setting data)
//        Legend legend = mChart.getLegend();

        // Set features for the legend
//        setChartLegend(legend);
    }

    private void getTimestampAndChild() {
        name = users.child(emailEncode);
        timestamps = name.child("userExercises");
        timestamps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    setTimestamp.add(((DataSnapshot)i.next()).getKey()+"\n--> "+((DataSnapshot)i.next()).getValue().toString());
                }
                listTimestamp.clear();
                listTimestamp.addAll(setTimestamp);
                // Gets notified when the data in list view has been changed.
                arrayAdapterTimestamp.notifyDataSetChanged();
                // Sorts data in list view in ascending order.
                arrayAdapterTimestamp.sort(new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return rhs.compareTo(lhs);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTimestamp() {
        name = users.child(emailEncode);
        timestamps = name.child("userExercises");
        timestamps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    setTimestamp.add(((DataSnapshot)i.next()).getKey());
                }
                listTimestamp.clear();
                listTimestamp.addAll(setTimestamp);
                // Gets notified when the data in list view has been changed.
                arrayAdapterTimestamp.notifyDataSetChanged();
                // Sorts data in list view in ascending order.
                arrayAdapterTimestamp.sort(new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return rhs.compareTo(lhs);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChild() {
        name = users.child(emailEncode);
        timestamps = name.child("userExercises");
        timestamps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    setTimestamp.add(((DataSnapshot)i.next()).getValue().toString());
                }
                listTimestamp.clear();
                listTimestamp.addAll(setTimestamp);
                // Gets notified when the data in list view has been changed.
                arrayAdapterTimestamp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void setData(int count, float range) {
//        ArrayList<Entry> values = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range) + 3;
//            values.add(new Entry(i, val));
//        }
//        LineDataSet set;
//
//        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
//            set = (LineDataSet)mChart.getData().getDataSetByIndex(0);
//            set.setValues(values);
//            mChart.getData().notifyDataChanged();
//            mChart.notifyDataSetChanged();
//        } else {
//            // create a dataset and give it a type
//            set = new LineDataSet(values, "DataSet 1");
//
//            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//            dataSets.add(set); // add the datasets
//
//            // create a data object with the datasets
//            LineData data = new LineData(dataSets);
//
//            // set data
//            mChart.setData(data);
//        }
//    }
//
//    private void addEntry() {
//        LineData data = mChart.getData();
//
//        if (data != null) {
//            ILineDataSet set = data.getDataSetByIndex(0);
//            // set.addEntry(...); // can be called as well
//
//            if (set == null) {
//                set = createSet();
//                data.addDataSet(set);
//            }
//
//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
//            data.notifyDataChanged();
//
//            // let the chart know it's data has changed
//            mChart.notifyDataSetChanged();
//
//            // limit the number of visible entries
//            mChart.setVisibleXRangeMaximum(50);
//            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
//
//            // move to the latest entry
//            mChart.moveViewToX(data.getEntryCount());
//
//            // this automatically refreshes the chart (calls invalidate())
//            // mChart.moveViewTo(data.getXValCount()-7, 55f,
//            // AxisDependency.LEFT);
//        }
//    }
//
//    private LineDataSet createSet() {
//        LineDataSet set = new LineDataSet(null, "Dynamic Data");
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColor(ColorTemplate.getHoloBlue());
//        set.setCircleColor(Color.WHITE);
//        set.setLineWidth(2f);
//        set.setCircleRadius(4f);
//        set.setFillAlpha(65);
//        set.setFillColor(ColorTemplate.getHoloBlue());
//        set.setHighLightColor(Color.rgb(244, 117, 117));
//        set.setValueTextColor(Color.WHITE);
//        set.setValueTextSize(9f);
//        set.setDrawValues(false);
//        set.setLabel("Calories Burned Over Time");
//        return set;
//    }
//
//    private void setMChartFeatures() {
//        // enable description text
//        mChart.getDescription().setEnabled(true);
//
//        // enable touch gestures
//        mChart.setTouchEnabled(true);
//
//        // enable scaling and dragging
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
//        mChart.setDrawGridBackground(false);
//
//        // if disabled, scaling can be done on x- and y-axis separately
//        mChart.setPinchZoom(true);
//
//        // set an alternative background color
//        mChart.setBackgroundColor(Color.rgb(208, 208, 208));
//    }
//
//    private void setChartLegend(Legend legend) {
//        // modify the legend ...
//        legend.setForm(Legend.LegendForm.LINE);
//        legend.setTextColor(Color.WHITE);
//
//        XAxis xl = mChart.getXAxis();
//        xl.setTextColor(Color.WHITE);
//        xl.setDrawGridLines(false);
//        xl.setAvoidFirstLastClipping(true);
//        xl.setEnabled(true);
//
//        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTextColor(Color.WHITE);
//        leftAxis.setAxisMaximum(100f);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.setDrawGridLines(true);
//
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setEnabled(false);
//    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if(id == R.id.nav_details) {
            Intent i = new Intent(this, DetailsActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_exercise) {
            Intent i = new Intent(this, ExerciseActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_run) {
            Intent i = new Intent(this, RunActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_chart) {
            Intent i = new Intent(this, ChartActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_chat) {
            Intent i = new Intent(this, ChatHouseActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_camera) {
            openCamera();
        } else if(id == R.id.nav_gallery) {
            openGallery();
        } else if(id == R.id.nav_change_password) {
            Intent i = new Intent(this, DashBoard.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_logout) {
            logoutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChartActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Int Firebase
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                if(auth.getCurrentUser() == null) {
                    startActivity(new Intent(ChartActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openCamera() {
        startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }
}
