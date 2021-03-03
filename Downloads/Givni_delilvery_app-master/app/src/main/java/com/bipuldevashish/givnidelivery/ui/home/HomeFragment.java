package com.bipuldevashish.givnidelivery.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.bipuldevashish.givnidelivery.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = "Map";
    private static final int PICK_CONTACT = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;
    EditText search, et_getName,et_getPhoneNumber;
    geoModel model;
    TextView mapAddress;
    EditText houseName, landMark;
    Button addAddress;
    GeoPoint geoPoint;
    LatLng latLng;
    ImageView gps;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            //                setUserLocationMarker(locationResult.getLastLocation());
        }
    };
    BottomSheetDialogView bottomSheetDialogView;
    private GoogleMap mMap;
    private Geocoder geocoder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);


        geocoder = new Geocoder(getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        search = root.findViewById(R.id.search);
        mapAddress = root.findViewById(R.id.mapAddress);
        addAddress = root.findViewById(R.id.addAddress);
//        houseName = root.findViewById(R.id.houseName);
//        landMark = root.findViewById(R.id.landMark);
        gps = root.findViewById(R.id.gps);
        et_getName = root.findViewById(R.id.et_getName);
        et_getPhoneNumber = root.findViewById(R.id.et_getPhoneNumber);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return root;
        }
        Task CurrentLocation = fusedLocationProviderClient.getLastLocation();
        CurrentLocation.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = (Location) task.getResult();

                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert addresses != null;
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String streetAddress = address.getAddressLine(0);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(streetAddress)
                            .draggable(true));
                    mapAddress.setText(String.valueOf(streetAddress));
                }
//                    moveCamera(new LatLng(location.getLatitude(),location.getLongitude()),16,"");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            }

        });


        search.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                geoLocation();
            }

            return false;
        });

//        addAddress.setOnClickListener(view -> {

//            String housename = houseName.getText().toString();
//            String landmark = landMark.getText().toString();
//
//            if (housename.isEmpty()) {
//                houseName.requestFocus();
//                houseName.setError("Fill This");
//            }
//            if (landmark.isEmpty()) {
//                landMark.requestFocus();
//                landMark.setError("Fill This");
//            } else {
//                sendAddress(geoPoint, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName(), mapAddress.getText().toString(), houseName.getText().toString(), landMark.getText().toString());


//            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),
//                    R.style.DialogStyle);
//
//            View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.geo_location_bottom_sheet,
//                    bottomSheetDialog.findViewById(R.id.rootLayout), false);
//
//            bottomSheetDialog.setContentView(bottomSheetView);
//            bottomSheetDialog.show();
//
//
////            }
//        });


        et_getName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((et_getName.getRight() - et_getName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) <= event.getRawX()) {
                        // your action here
                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(i, PICK_CONTACT);

                        return true;
                    }
                }
                return false;
            }
        });


        gps.setOnClickListener(view ->
                moveCamera(latLng, ""));

        return root;
    }

    private void geoLocation() {

        String searchString = search.getText().toString();
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocation: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }

    private void stopLocationUpdates() {

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }


    private void moveCamera(LatLng latLng, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {
            mMap.clear();
            if (userLocationMarker == null) {
                //create a new marker
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("My Location");
                mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            } else {
                //use the previously marker
                userLocationMarker.setPosition(latLng);
                userLocationMarker.setRotation(location.getBearing());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }


            if (userLocationAccuracyCircle == null) {
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.strokeWidth(4);
                circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                circleOptions.fillColor(Color.argb(32, 255, 0, 0));
                circleOptions.radius(location.getAccuracy());
                userLocationAccuracyCircle = mMap.addCircle(circleOptions);
            } else {
                userLocationAccuracyCircle.setCenter(latLng);
                userLocationAccuracyCircle.setRadius(location.getAccuracy());
            }

        });
    }


    void sendAddress(GeoPoint geoPoint, String name, String address, String houseName, String landMark) {

        model = new geoModel(geoPoint, name, address, houseName, landMark);
        FirebaseFirestore.getInstance().collection("User")
                .document(FirebaseAuth.getInstance().getUid())
                .set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getContext(), "Address Added !", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            //tou need to request permission
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true));
                mapAddress.setText(String.valueOf(streetAddress));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult()");
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                CursorLoader loader = new CursorLoader(getContext(), contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                if (c.moveToFirst()) {
                    Log.i(TAG, "Contacts ID: " + c.getString(c.getColumnIndex(ContactsContract.Contacts._ID)));
                    Log.i(TAG, "Contacts Name: " + c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    Log.i(TAG, "Contacts Phone Number: " + c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    Log.i(TAG, "Contacts Photo Uri: " + c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI)));
                    et_getName.setText(String.valueOf(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))));
                    et_getPhoneNumber.setText(String.valueOf(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                }
            }
        }
    }
}