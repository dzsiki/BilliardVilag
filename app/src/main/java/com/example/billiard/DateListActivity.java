package com.example.billiard;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class DateListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<Idopont> mItemsData;
    private DateAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private NotificationHelper mNotificationHelper;
    private JobScheduler mJobScheduler;

    private boolean viewRow = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_list);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { finish(); }

        mRecyclerView = findViewById(R.id.recyclerView);

        int gridNumber = 1;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemsData = new ArrayList<>();

        mAdapter = new DateAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("idopontok");
        queryData();

        mNotificationHelper = new NotificationHelper(this);
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        setJobScheduler();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryData() {
        mItemsData.clear();
        mFirestore.collection("idopontok").whereEqualTo("foglalt", false).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Idopont idopont = document.toObject(Idopont.class);
                        mItemsData.add(idopont);
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    public void lefoglalas(Idopont item) {
        DocumentReference ref = mItems.document(item.getId());
        ref.update("foglalt", true)
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Sikeres foglalas: " + item.getInfo(), Toast.LENGTH_LONG).show();
                    mNotificationHelper.send("Sikeres foglalas: " + item.getInfo());
                })
                .addOnFailureListener(fail -> Toast.makeText(this, "error", Toast.LENGTH_LONG).show());

        queryData();
        mNotificationHelper.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.deleteFiveButton:
                mItems.orderBy("info", Query.Direction.DESCENDING).limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        mItems.document(document.getId()).delete();
                    }
                    queryData();
                });
                return true;
            case R.id.addnew_button:
                Random rand = new Random();
                String kep;
                switch (rand.nextInt(4)) {
                    case 0:
                        kep = "https://firebasestorage.googleapis.com/v0/b/mobilbilliard.appspot.com/o/images%2F1.jpg?alt=media&token=7e566268-a31d-464c-a697-4ec8393ed0fe";
                        break;
                    case 1:
                        kep = "https://firebasestorage.googleapis.com/v0/b/mobilbilliard.appspot.com/o/images%2F2.jpg?alt=media&token=9d775cc3-5f1d-4ede-bbf5-233db986a8ab";
                        break;
                    case 2:
                        kep = "https://firebasestorage.googleapis.com/v0/b/mobilbilliard.appspot.com/o/images%2F3.jpg?alt=media&token=34cc596b-ae90-4f7d-8e60-cb0d9ac4a4e4";
                        break;
                    default:
                        kep = "https://firebasestorage.googleapis.com/v0/b/mobilbilliard.appspot.com/o/images%2F4.jpg?alt=media&token=95742b39-a77e-4846-8272-ad1f62ebee96";
                }
                Idopont newido = new Idopont(UUID.randomUUID().toString(), "2022-05-" + (rand.nextInt(31) + 1) + " " + (rand.nextInt(10) + 10) + ":00", kep, false);
                mItems.document(newido.getId()).set(newido);
                queryData();
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        Objects.requireNonNull(layoutManager).setSpanCount(spanCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJobScheduler() {

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(10000);

        JobInfo jobInfo = builder.build();
        mJobScheduler.schedule(jobInfo);

    }
}
