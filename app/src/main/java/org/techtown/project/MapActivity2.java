package org.techtown.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity2 extends AppCompatActivity
        implements OnMapReadyCallback {

    ActionBar actionBar;
    Toolbar toolbar;

    String mapx;
    String mapy;
    String title;
    String addr ;

    double mapxx;
    double mapyy;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        actionBar.setTitle("Search Result");
        // actionBar.setIcon(R.drawable.menu_settings);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mapx = intent.getStringExtra("mapx");
        mapy = intent.getStringExtra("mapy");
        title = intent.getStringExtra("title");
        addr = intent.getStringExtra("addr");

        mapxx =Double.parseDouble(mapx);
        mapyy =Double.parseDouble(mapy);

        //intent한 내용들을 제대로 출력하고 있음
        System.out.println("("+mapxx+" "+mapyy+")" +title +addr +" 출력");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap=googleMap;

        LatLng SEACH = new LatLng(mapyy, mapxx); //이게 반대로 설정되어있어서 이렇게해야 지도가 알맞게 뜸!!

        System.out.println(SEACH);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEACH);
        markerOptions.title(title);
        markerOptions.snippet(addr);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(SEACH,16);
        mMap.moveCamera(cameraUpdate);

    }

}