package com.github.mandinga90.redifreewififinderberlin.http;

import com.github.mandinga90.redifreewififinderberlin.entities.Venue;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VenueService {

    @GET("/venues")
    Call<List<Venue>> getVenues();

    @POST("/venues")
    Call<Void> createVenue(@Body Venue venue);

    @PATCH("/venues/{id}")
    Call<Void> updateVenue(@Path("id") long id, @Body Venue venue);

    @DELETE("/venues/{id}")
    Call<Void> deleteVenue(@Path("id") long id);
}
