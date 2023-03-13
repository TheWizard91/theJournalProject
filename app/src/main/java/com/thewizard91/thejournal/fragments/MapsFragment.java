package com.thewizard91.thejournal.fragments;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.thewizard91.thejournal.R;
import java.io.IOException;

public class MapsFragment extends Fragment {
    private Context context;
    /* access modifiers changed from: private */
    public String location;

    public MapsFragment(String location2) {
        this.location = location2;
    }

    public MapsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        this.context = container.getContext();
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    public void onMapReady(GoogleMap googleMap) {
                        MapsFragment mapsFragment = MapsFragment.this;
                        GeoPoint geoPoint = mapsFragment.getLocationFromAddress(mapsFragment.location);
                        LatLng latitudeAndLongitude = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latitudeAndLongitude));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latitudeAndLongitude, 5.0f));
                        googleMap.addMarker(new MarkerOptions().position(latitudeAndLongitude).title(MapsFragment.this.location));
                    }
                });
            } else {
                Toast.makeText(this.context, "There Is No Such Address, Sorry.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public GeoPoint getLocationFromAddress(String location2) {
        try {
            Address address = new Geocoder(this.context).getFromLocationName(location2, 5).get(0);
            return new GeoPoint(address.getLatitude(), address.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}