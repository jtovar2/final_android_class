package com.tovar.javier.demolisher.api;

import com.tovar.javier.demolisher.model.MartaBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by javier on 4/9/17.
 */

public interface MartaApi
{
    @GET("BRDRestService/RestBusRealTimeService/GetAllBus")
    Call<List<MartaBus>> getMartaBuses();
}
