package com.phoenix.otlobbety.Retrofit;

import com.phoenix.otlobbety.Model.Category;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IMyApi {

    @GET("/Category.json")
    Call<Category> getData();

    @GET("/SubCategory.json")
    Call<Category> getSubCategoryData();
}
