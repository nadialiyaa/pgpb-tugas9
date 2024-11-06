package com.example.pgpb_9;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;



public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    public MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Mapbox
        Mapbox.getInstance(this);

        setContentView(R.layout.activity_main);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(mapboxMap -> {
            // Set the map style to use TMS tiles
            String tmsUrl = "https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}";
            String styleJson = "{\n" +
                    "  \"version\": 8,\n" +
                    "  \"sources\": {\n" +
                    "    \"tms-tiles\": {\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"tiles\": [\"" + tmsUrl + "\"],\n" +
                    "      \"tileSize\": 256\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"layers\": [\n" +
                    "    {\n" +
                    "      \"id\": \"tms-tiles\",\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"source\": \"tms-tiles\",\n" +
                    "      \"minzoom\": 0,\n" +
                    "      \"maxzoom\": 22\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            mapboxMap.setStyle(new Style.Builder().fromJson(styleJson), style -> {
                // Add the marker image to the style
                style.addImage("marker_icon_id", getResources().getDrawable(R.drawable.marker_icon_id, null));

                // Create a GeoJsonSource with the marker location
                LatLng location = new LatLng(-7.782920627825076, 110.3670853697517); // Example coordinates for Eiffel Tower
                Feature feature = Feature.fromGeometry(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
                FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[]{feature});
                GeoJsonSource geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
                style.addSource(geoJsonSource);

                // Add a SymbolLayer with the marker icon
                SymbolLayer symbolLayer = new SymbolLayer("symbol-layer-id", "marker-source")
                        .withProperties(
                                PropertyFactory.iconImage("marker_icon_id"),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconSize(0.2f)
                        );
                style.addLayer(symbolLayer);


                // Set camera position to the marker location
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(location)
                        .zoom(15.0)
                        .build());

                ImageButton locationButton = findViewById(R.id.geolocation);
                locationButton.setOnClickListener(v -> enableLocationComponent(mapboxMap));

                ImageButton zoomInButton = findViewById(R.id.zoom_in_button);
                ImageButton zoomOutButton = findViewById(R.id.zoom_out_button);

                zoomInButton.setOnClickListener(v ->{
                    if (mapboxMap.getCameraPosition().zoom < 22) {
                        mapboxMap.animateCamera(CameraUpdateFactory.zoomIn());
                    }
                });
                zoomOutButton.setOnClickListener(v ->{
                    if (mapboxMap.getCameraPosition().zoom > 0) {
                        mapboxMap.animateCamera(CameraUpdateFactory.zoomOut());
                    }
                });
            });
        });

    }

    private void enableLocationComponent(MapboxMap mapboxMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        return;
        }
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, mapboxMap.getStyle()).build()
        );
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this::enableLocationComponent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
