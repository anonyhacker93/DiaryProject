package com.example.dineshkumar.diary;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dineshkumar.diary.DB.CategoryDatabase;
import com.example.dineshkumar.diary.DB.DiaryDatabase;
import com.example.dineshkumar.diary.Model.Diary;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String INTENT_DIARY_KEY = "Diary";
    public static final String INTENT_LOGOUT_KEY = "Logout";
    int appUseCount;
    @BindView(R.id.homeRecycler)
    RecyclerView homeRecycler;
    SearchView searchView;
    //DiaryDatabase database;
    CustomHomeRecyclerAdapter homeListAdapter;
    private ArrayList<Diary> _diaryArrayList;
    public static String orderBy = "created_date desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _diaryArrayList = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        //   database = new DiaryDatabase(this);
        //  addData();
        setFAB();
        setHomeRecycler();
        String userName = getUserNameFromSharedPreference();
        loadFromFirebase(userName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.addCatgMenu) {
            startActivity(new Intent(this, AddCategory.class));
            return true;
        }
        if (id == R.id.showCatgMenu) {
            ShowCategoryFilterDialog("Title", "Msg is here");
            return true;
        }
        if(id== R.id.showLogoutMenu){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.putExtra(INTENT_LOGOUT_KEY,true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    void setFAB() {
        FloatingActionButton fab = findViewById(R.id.writeDiaryFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteDiary.class);
                startActivity(intent);
            }
        });
    }

    void setHomeRecycler() {
        //    ArrayList<Diary> diaryArrayList = database.getData(orderBy);
        homeListAdapter = new CustomHomeRecyclerAdapter(MainActivity.this, _diaryArrayList,
                new CustomHomeRecyclerAdapter.OnCardClickListener() {
                    public void onClick(Diary diary) {
                        Intent viewDiaryIntent = new Intent(MainActivity.this, ViewDiary.class);
                        viewDiaryIntent.putExtra(INTENT_DIARY_KEY, (Serializable) diary);
                        startActivity(viewDiaryIntent);
                    }

                    public void onLongClick(Diary diary) {
                        showAlertDialogForDeleteRecord(diary);

                    }
                }
        );

        homeRecycler.setLayoutManager(new LinearLayoutManager(this));
        homeRecycler.setHasFixedSize(true);
        homeRecycler.setAdapter(homeListAdapter);

    }

    private void deleteRecordFromFirebase(final Diary diary) {
        final String userName = getUserNameFromSharedPreference();

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference reference = initFirebase(Constants.FIREBASE_ROOT_REFERENCE);
        reference.child(userName)
                .child(Constants.FIREBASE_DIARY_REFERENCE)
                .child(diary.getCategory())
                .child(diary.getTitle()).removeValue();
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.setQuery("", false);
        }
        // refreshHomeRecycler(orderBy);
    }

    public void refreshHomeRecycler(String orderBy) {
      /*  Log.i("refresh", "Data refreshed");
        ArrayList<Diary> diaryArrayList = database.getData(orderBy);
        homeListAdapter.addData(diaryArrayList);
        homeListAdapter.notifyDataSetChanged();*/
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        inflateSearchMenu(menu);
        getMenuInflater().inflate(R.menu.filter_spinner, menu);
        getMenuInflater().inflate(R.menu.home_settings, menu);

        checkAppUseCount();


        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner searchFilterSpinner = (Spinner) MenuItemCompat.getActionView(item);

        // String values[]={"By Date","By Title","Modified Date"};
        //  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        String filterValues[] = {"Filter By", "Title", "Category", "Created Date", "Modified Date"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.catg_filter_spinner_layout, R.id.catg_spinner_row, filterValues);
        searchFilterSpinner.setAdapter(adapter);

        setSearchFilter(searchFilterSpinner);

        return true;
    }

    private void checkAppUseCount() {
        SharedPreferences prefs = this.getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appUseCount = prefs.getInt("appUseCount", 0);
        if (appUseCount == 0) {

            CategoryDatabase categoryDatabase = new CategoryDatabase(this);
            categoryDatabase.addCategory("Note");
            categoryDatabase.addCategory("Work");
            categoryDatabase.addCategory("Expenses");
            categoryDatabase.addCategory("Personal");
            categoryDatabase.addCategory("Event");
        }
        appUseCount++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("appUseCount", appUseCount);
        editor.commit();
    }

    private void setSearchFilter(final Spinner searchFilterSpinner) {

        searchFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  searchFilterSpinner.setSelection(6);

                switch (position) {
                    case 1:
                        orderBy = "created_date desc";
                        refreshHomeRecycler(orderBy);
                        break;
                    case 2:
                        orderBy = "title asc";
                        refreshHomeRecycler(orderBy);
                        break;
                    case 3:
                        orderBy = "modified_date desc";
                        refreshHomeRecycler(orderBy);
                        break;
                    case 4: //show category list for filteration
                        searchFilterSpinner.setSelection(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
    }


    // When user filter by category. It shows a list of available categories in a Dialogbox
    void ShowCategoryFilterDialog(String title, String msg) {
        final Dialog dialogCatg = new Dialog(this);
        dialogCatg.setContentView(R.layout.category_dialog);

        CategoryDatabase categoryDatabase = new CategoryDatabase(this);
        final ArrayList<String> cat_list = categoryDatabase.getCategories();

        final ListView list = (ListView) dialogCatg.findViewById(R.id.dialog_category_list);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cat_list);
        list.setAdapter(adapter);

        //choose category from dialog's list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String catgItem = (String) list.getItemAtPosition(position);

                //Refreshing Home Recycler Adapter by Category
                DiaryDatabase db = new DiaryDatabase(MainActivity.this);
                ArrayList<Diary> catgList = db.getData("category", catgItem);
                homeListAdapter.addData(catgList);
                homeListAdapter.notifyDataSetChanged();

                dialogCatg.dismiss();
            }
        });

        //to delete existing category from category list
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new CategoryDatabase(MainActivity.this).deleteCategory(cat_list.get(pos));
                        dialog.dismiss();
                        dialogCatg.dismiss();
                        Toast.makeText(MainActivity.this, "Category deleted successfully !", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialogDeleteConfirm = builder.create();
                dialogDeleteConfirm.setTitle("Want to delete it ?");
                dialogDeleteConfirm.show();
                return true;
            }
        });
        dialogCatg.show();
    }

    void inflateSearchMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        setSearchBox(menu);

    }

    private void setSearchBox(Menu menu) {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //         ArrayList<Diary> diariesList = database.getDataByWordMatch(newText);
              /*  if (diariesList != null) {
                    homeListAdapter.addData(diariesList);
                    homeListAdapter.notifyDataSetChanged();
                }*/
                return true;
            }
        });
    }

    private void loadFromFirebase(final String userName) {
        DatabaseReference reference = initFirebase(Constants.FIREBASE_ROOT_REFERENCE);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.child(userName).child(Constants.FIREBASE_DIARY_REFERENCE).getChildren().iterator();
                Diary diary;
                _diaryArrayList.clear();
                while (iterator.hasNext()) { //Loading Category
                    DataSnapshot dataShot1 = iterator.next();
                    Iterator<DataSnapshot> itr1 = dataShot1.getChildren().iterator();
                    while ((itr1.hasNext())) { //Loading Title
                        DataSnapshot dataShot3 = itr1.next();
                        diary = dataShot3.getValue(Diary.class);
                        _diaryArrayList.add(diary);
                    }
                }
                homeListAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("DiaryTag : ", "Exception : " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Exception : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

    void showAlertDialogForDeleteRecord(final Diary diary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Do you want to delete it ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecordFromFirebase(diary);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private DatabaseReference initFirebase(String ref) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = firebaseDatabase.getReference(ref);
        return usersReference;
    }


    private String getUserNameFromSharedPreference() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREF_USERNAME, MODE_PRIVATE);
        String userName = preferences.getString(Constants.SHARED_PREF_USERNAME, null);
        return userName;
    }

}

