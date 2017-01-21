package com.github.mandinga90.redifreewififinderberlin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mandinga90.redifreewififinderberlin.R;
import com.github.mandinga90.redifreewififinderberlin.entities.Venue;
import com.github.mandinga90.redifreewififinderberlin.entities.VenueBuilder;
import com.github.mandinga90.redifreewififinderberlin.functional.Consumer;
import com.github.mandinga90.redifreewififinderberlin.ui.fragments.RetainFragment;

import java.util.HashMap;

public class UpdateVenueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        // layout elements
        final EditText edName = (EditText) this.findViewById(R.id.ed_name);
        final EditText edAddress = (EditText) this.findViewById(R.id.ed_address);
        final EditText edDescription = (EditText) this.findViewById(R.id.ed_description);
        final Spinner spCategory = (Spinner) this.findViewById(R.id.sp_category);
        Button btnCreate = (Button) this.findViewById(R.id.btn_create_venue);

        // to be improved by API call (which categories exist...)
        HashMap<String,Integer> spinnerPositions = new HashMap<>();
        spinnerPositions.put( "bar", 0 );
        spinnerPositions.put( "restaurant", 1 );
        spinnerPositions.put( "coworking space", 2 );

        edName.setText((String) getIntent().getExtras().get("name"));
        edAddress.setText((String) getIntent().getExtras().get("address"));
        edDescription.setText((String) getIntent().getExtras().get("description"));
        spCategory.setSelection(spinnerPositions.get((String) getIntent().getExtras().get("category")));

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double latitude = (Double) getIntent().getExtras().get("latitude");
                double longitude = (Double) getIntent().getExtras().get("longitude");

                Venue venue = new VenueBuilder()
                        .addId((Long) getIntent().getExtras().get("id"))
                        .addName(edName.getText().toString())
                        .addAddress(edAddress.getText().toString())
                        .addDescription(edDescription.getText().toString())
                        .addCategory(spCategory.getSelectedItem().toString().toLowerCase().replace(" ", "_"))
                        .addLatitude(latitude)
                        .addLongitude(longitude)
                        .build();

                updateVenue(venue);

            }
        });
    }

    private void updateVenue(final Venue venue) {

        RetainFragment retainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.NETWORK_FRAGMENT_TAG));
        if (retainFragment == null) {
            retainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction().add(retainFragment, getString(R.string.NETWORK_FRAGMENT_TAG)).commit();
        }

        if(        venue != null
                && venue.getName() != null && ! venue.getName().isEmpty()
                && venue.getAddress() != null && ! venue.getAddress().isEmpty()
                && venue.getDescription() != null && ! venue.getDescription().isEmpty()
                && venue.getCategory() != null && ! venue.getCategory().isEmpty()
                && venue.getLatitude() != 0
                && venue.getLongitude() != 0) {

            retainFragment.updatePlace(new Consumer<Void>() {

                   @Override
                   public void apply(Void v) {
                       Toast.makeText(getApplicationContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();
                       // get back to main activity
                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
                   }

                   @Override
                   public Object get() {
                       return venue;
                   }
                }
            );

        }
    }
}
