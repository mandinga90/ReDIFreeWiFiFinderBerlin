package com.github.mandinga90.redifreewififinderberlin.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.github.mandinga90.redifreewififinderberlin.entities.Venue;
import com.github.mandinga90.redifreewififinderberlin.functional.Consumer;
import com.github.mandinga90.redifreewififinderberlin.http.RestClient;
import com.github.mandinga90.redifreewififinderberlin.http.VenueService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetainFragment extends Fragment {

    private VenueService service = RestClient.getInstance().createService(VenueService.class);
    private List<Venue> venues;
    private Consumer getConsumer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void getPlaces(final Consumer<List<Venue>> consumer) {
        getConsumer = consumer;
        if (venues == null) {
            Call<List<Venue>> call = service.getVenues();
            call.enqueue(new Callback<List<Venue>>() {
                @Override
                public void onResponse(Call<List<Venue>> call, Response<List<Venue>> response) {
                    if (response.isSuccessful()) {
                        venues = response.body();
                        getConsumer.apply(venues);
                    } else {
                        showNetError(response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Venue>> call, Throwable t) {
                    showNetError(t.getMessage());
                }
            });
        } else {
            getConsumer.apply(venues);
        }
    }

    public void createPlace(final Consumer<Void> consumer) {
        getConsumer = consumer;
        Call<Void> call = service.createVenue((Venue) getConsumer.get());
        call.enqueue(new Callback<Void>(){

            @Override
            public void onResponse(Call<Void> call, final Response<Void> response) {

                if(response.isSuccessful()){
                    if (venues == null) {
                        // get venues in case it is null
                        getPlaces(new Consumer<List<Venue>>() {
                            @Override
                            public void apply(List<Venue> venues) {
                                consumer.apply(response.body());
                            }

                            @Override
                            public Object get() {
                                return null;
                            }
                        });
                    }
                    else{

                        getConsumer.apply(response.body());
                    }

                }
                else{
                    showNetError(response.message());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showNetError(t.getMessage());
            }
        });
    }

    public void updatePlace(final Consumer<Void> consumer) {
        getConsumer = consumer;
        Venue currentVenue = (Venue) getConsumer.get();
        Call<Void> call = service.updateVenue(currentVenue.getId(), currentVenue);
        call.enqueue(new Callback<Void>(){

            @Override
            public void onResponse(Call<Void> call, final Response<Void> response) {

                if(response.isSuccessful()){
                    if (venues == null) {
                        // get venues in case it is null
                        getPlaces(new Consumer<List<Venue>>() {
                            @Override
                            public void apply(List<Venue> venues) {
                                consumer.apply(response.body());
                            }

                            @Override
                            public Object get() {
                                return null;
                            }
                        });
                    }
                    else{
                        getConsumer.apply(response.body());
                    }

                }
                else{
                    showNetError(response.message());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showNetError(t.getMessage());
            }
        });

    }

    public void deletePlace(final Consumer<Void> consumer) {
        getConsumer = consumer;
        final Venue currentVenue = (Venue) getConsumer.get();
        Call<Void> call = service.deleteVenue(currentVenue.getId());
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, final Response<Void> response) {
                if (response.isSuccessful()) {
                    if (venues == null) {
                        // get venues in case it is null
                        getPlaces(new Consumer<List<Venue>>() {
                            @Override
                            public void apply(List<Venue> venues) {

                                venues.remove(currentVenue);
                                consumer.apply(response.body());

                            }

                            @Override
                            public Object get() {
                                return null;
                            }
                        });
                    } else {
                        // remove successfully removed venue from local list of venues
                        // generate equals() and hashCode() --> id !!!
                        venues.remove(currentVenue);
                        getConsumer.apply(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showNetError(t.getMessage());
            }
        });
    }

    private void showNetError(String errorMessage){
        Toast.makeText(getActivity(), "Network error!", Toast.LENGTH_SHORT).show();
        Log.d("NetworkError", errorMessage);
    }
}
