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

public class CreateVenueActivity extends AppCompatActivity {

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

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double latitude = (Double) getIntent().getExtras().get("latitude");
                double longitude = (Double) getIntent().getExtras().get("longitude");

                Venue venue = new VenueBuilder()
                        .addName(edName.getText().toString())
                        .addAddress(edAddress.getText().toString())
                        .addDescription(edDescription.getText().toString())
                        .addCategory(spCategory.getSelectedItem().toString().toLowerCase().replace(" ", "_"))
                        .addLatitude(latitude)
                        .addLongitude(longitude)
                        .build();

                addVenue(venue);

            }
        });
    }

    private void addVenue(final Venue venue) {

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

            retainFragment.createPlace(new Consumer<Void>() {

                   @Override
                   public void apply(Void v) {
                       Toast.makeText(getApplicationContext(), "Successfully created.", Toast.LENGTH_SHORT).show();
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
