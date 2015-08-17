package com.example.edwin.drive;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {


    private TextView jumlahPenumpangView;
    private int jumlahPenumpang = 1;

    //Google Places API
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapterAsal;
    private PlaceAutocompleteAdapter mAdapterTujuan;
    private AutoCompleteTextView mAutocompleteAsalView;
    private AutoCompleteTextView mAutocompleteTujuanView;
    private static final LatLngBounds BOUNDS_INDONESIA = new LatLngBounds(
            new LatLng(-10.833306, 93.339844), new LatLng(4.039618, 140.625000));
    private String namalokasiAsal = "";
    private String namalokasiTujuan = "";
    private String statuslokasi = "";

    private EditText descAsal;
    private EditText descTujuan;

    //DATE & TIME PICKER
    private EditText dateEditText;
    int year_x, month_x, day_x;
    int DIALOGDATE_ID = 0;
    int DIALOGTIME_ID = 1;
    private EditText timeEditText;
    int hour_x, minute_x;

    //LIST
    List<PostActivity> posts = new ArrayList<PostActivity>();
    //Setup Variable from PostXML
    private TextView fromTextView;
    private TextView descFromTextView;
    private TextView toTextView;
    private TextView descToTextView;
    private TextView jumlahPenumpangTextView;
    private TextView dateForTextView;
    private TextView statusTextView;

    //Setup Variable for PostActivity
    private String fromLocation = "";
    private String descFromLocation = "";
    private String toLocation = "";
    private String descToLocation = "";
    private Integer jumlahOrang;
    private Date dateFor;
    private Boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_main);

        //Check who's connected
        if (ParseUser.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, "" + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Nothing in here", Toast.LENGTH_SHORT).show();
        }

        //Tab
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("create");
        tabSpec.setContent(R.id.tabCreate);
        tabSpec.setIndicator("Create");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabPost);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        //Google Places API Lokasi Asal
        mAutocompleteAsalView = (AutoCompleteTextView)
                findViewById(R.id.lokasiasal_places);
        mAutocompleteAsalView.setOnItemClickListener(mAutocompleteClickListenerAsal);
        mAdapterAsal = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_INDONESIA, null);
        mAutocompleteAsalView.setAdapter(mAdapterAsal);

        //Google Places API Lokasi Tujuan
        mAutocompleteTujuanView = (AutoCompleteTextView)
                findViewById(R.id.lokasitujuan_places);
        mAutocompleteTujuanView.setOnItemClickListener(mAutocompleteClickListenerTujuan);
        mAdapterTujuan = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_INDONESIA, null);
        mAutocompleteTujuanView.setAdapter(mAdapterTujuan);

        //Description Section Asal && Tujuan
        descAsal = (EditText) findViewById(R.id.descasal_editText);
        descTujuan = (EditText) findViewById(R.id.desctujuan_editText);
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        showDialogDate();

        //TimePicker
        showDialogTime();

        //Jumlah Penumpang
        jumlahPenumpangView = (TextView) findViewById(R.id.jumlahpenumpang_textview);
        Button buttonPlus = (Button) findViewById(R.id.tambah_button);
        buttonPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumlahPenumpang == 5) {
                    jumlahPenumpang = 5;
                    jumlahPenumpangView.setText("" + jumlahPenumpang);
                } else {
                    jumlahPenumpang += 1;
                    jumlahPenumpangView.setText("" + jumlahPenumpang);
                }
            }
        });
        Button buttonMinus = (Button) findViewById(R.id.kurang_button);
        buttonMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumlahPenumpang == 1) {
                    jumlahPenumpang = 1;
                    jumlahPenumpangView.setText("" + jumlahPenumpang);
                } else {
                    jumlahPenumpang -= 1;
                    jumlahPenumpangView.setText("" + jumlahPenumpang);
                }
            }
        });
        Button buttonCariAnjem = (Button) findViewById(R.id.carianjem_button);
        buttonCariAnjem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postParse();
                mAutocompleteAsalView.setText("");
                mAutocompleteTujuanView.setText("");
                descAsal.setText("");
                descTujuan.setText("");
                jumlahPenumpang = 1;
                jumlahPenumpangView.setText("" + jumlahPenumpang);
                dateEditText.setText("");
                timeEditText.setText("");
            }
        });



        //PostActivity
        fromTextView = (TextView)findViewById(R.id.from_textView);
        descFromTextView = (TextView)findViewById(R.id.descFrom_textView);
        toTextView = (TextView)findViewById(R.id.to_textView);
        descToTextView = (TextView)findViewById(R.id.descTo_textView);
        jumlahPenumpangTextView = (TextView)findViewById(R.id.jumlahPenumpang_postView);
        dateForTextView = (TextView)findViewById(R.id.dateFor_textView);
        statusTextView = (TextView)findViewById(R.id.status_textView);

        getParse();

    }

    public void getParse(){
        ParseQuery<PostActivity> query = ParseQuery.getQuery("Request");
        //String myUser = ParseUser.getCurrentUser().getUsername();

        //query.whereEqualTo("User", myUser);
        query.findInBackground(new FindCallback<PostActivity>() {
            public void done(List<PostActivity> myList, ParseException e) {
                if (e == null) {
                    //fromTextView.setText(myList.get(1).getString("LokasiAsal"));
                    for (PostActivity post : myList) {
                        //PostActivity newpost = new PostActivity();
                        //newpost.setLokasiAsal(post.getLokasiAsal());
                        fromTextView.setText(post.getLokasiAsal());
                        descFromTextView.setText(post.getLokasiAsalDesc());
                        toTextView.setText(post.getLokasiTujuan());
                        descToTextView.setText(post.getLokasiTujuanDesc());
                        jumlahPenumpangTextView.setText(post.getJumlahPenumpang().toString());
                        dateForTextView.setText(post.getPostDate().toString());
                        //posts.add(newpost);

                        //fromTextView.setText(myList.get(i).getLokasiAsal());
                        //descFromTextView.setText(myList.get(i).getString("LokasiAsalDesc"));
                        //toTextView.setText(myList.get(i).getString("LokasiTujuan"));
                        //descFromTextView.setText(myList.get(i).toString());
                        //jumlahPenumpangTextView.setText(myList.get(i).getInt("JumlahPenumpang"));
                        //dateForTextView.setText(myList.get(i).getDate("Date").toString());
                        //statusTextView.setText(myList.get(i).getBoolean("Status"));


                    }
                    //ArrayAdapter<PostActivity> adapter = new ArrayAdapter<PostActivity>(MainActivity.this,)
                    //setListAdapter(adapter);
                    //Log.d("score", "Retrieved " + scoreList.size() + " scores");
                } else {
                    //Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    //Parse Send Data
    public void postParse() {
        //Setup Variable
        String lokasiAsal = namalokasiAsal;
        String descAsalPost = descAsal.getText().toString().trim();
        String lokasiTujuan = namalokasiTujuan;
        String descTujuanPost = descTujuan.getText().toString().trim();
        Boolean status = false;
        Date date = new Date(year_x - 1900, month_x, day_x - 30, hour_x - 17, minute_x);
        final ParseObject post = new ParseObject("Request");
        //Insert Data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

        if (lokasiAsal.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_lokasiasal));
        }
        if (descAsalPost.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_descasal));
        }
        if (lokasiTujuan.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_lokasitujuan));
        }
        if (descTujuanPost.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_desctujuan));
        }
        if (date == null) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_date));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(MainActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();

        //Send Data
        post.put("Status", status);
        post.put("LokasiAsal", lokasiAsal);
        post.put("LokasiAsalDesc", descAsalPost);
        post.put("LokasiTujuan", lokasiTujuan);
        post.put("LokasiTujuanDesc", descTujuanPost);
        post.put("Date", date);
        post.put("JumlahPenumpang", jumlahPenumpang);
        final ParseUser user = ParseUser.getCurrentUser();
        final ParseRelation relationRequest = post.getRelation("User");
        relationRequest.add(user);

        //Send To Parse
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    // Show the error message
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            dialog.dismiss();
                            if (e != null) {
                                // Show the error message
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Request sent, see in the post tab", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //Date Picker
    public void showDialogDate() {
        dateEditText = (EditText) findViewById(R.id.date_editText);
        dateEditText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int i = 0;
                showDialog(i);
            }
        });
    }

    public void showDialogTime() {
        timeEditText = (EditText) findViewById(R.id.time_editText);
        timeEditText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int i = 1;
                showDialog(i);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOGDATE_ID) {
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        } else if (id == DIALOGTIME_ID) {
            return new TimePickerDialog(this, dpickerListenerTime, hour_x, minute_x, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            String datetext = day_x + "/" + month_x + "/" + year_x;
            dateEditText.setText(datetext);
        }
    };
    private TimePickerDialog.OnTimeSetListener dpickerListenerTime
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hour_x = hour;
            minute_x = minute;
            String timetext = hour_x + ":" + minute_x;
            timeEditText.setText(timetext);
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListenerAsal
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapterAsal.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            //Log.i(TAG, "Autocomplete item selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            namalokasiAsal = item.description.toString();
            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            //Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListenerTujuan
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapterTujuan.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            //Log.i(TAG, "Autocomplete item selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            namalokasiTujuan = item.description.toString();
            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            //Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                //Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "+ connectionResult.getErrorCode());
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(MainActivity.this, DispatchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
