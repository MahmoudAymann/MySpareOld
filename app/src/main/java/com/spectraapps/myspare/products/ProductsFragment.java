package com.spectraapps.myspare.products;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.kimkevin.cachepot.CachePot;
import com.michael.easydialog.EasyDialog;
import com.spectraapps.myspare.bottomtabscreens.additem.AddItemActivity;
import com.spectraapps.myspare.model.AddToFavModel;
import com.spectraapps.myspare.model.BrandsModel;
import com.spectraapps.myspare.model.CountriesModel;
import com.spectraapps.myspare.model.ModelsModel;
import com.spectraapps.myspare.utility.ListSharedPreference;
import com.spectraapps.myspare.MainActivity;
import com.spectraapps.myspare.R;
import com.spectraapps.myspare.adapters.adpProducts.AllProductsAdapter;
import com.spectraapps.myspare.adapters.adpProducts.ProductsRecyclerAdapter;
import com.spectraapps.myspare.api.Api;
import com.spectraapps.myspare.bottomtabscreens.home.Home;
import com.spectraapps.myspare.helper.BaseBackPressedListener;
import com.spectraapps.myspare.model.inproducts.ProductsAllModel;
import com.spectraapps.myspare.model.inproducts.ProductsModel;
import com.spectraapps.myspare.network.MyRetrofitClient;
import com.spectraapps.myspare.products.productdetail.ProductDetail;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment {

    myCall_Back myCall_back;

    FloatingActionButton fabButton;

    EditText editText;

    Spinner spinner_brand, spinner_country, spinner_year, spinner_model;

    ArrayList<Integer> year_array = new ArrayList<>();

    RecyclerView recyclerView;

    AlertDialog.Builder alertDialogBuilder;


    AllProductsAdapter mAllProductsAdapter;

    ProductsRecyclerAdapter productsAdapter;

    ArrayList<ProductsModel.DataBean> mProductDataList;

    ArrayList<ProductsAllModel.DataBean> mProductAllDataList;

    PullRefreshLayout pullRefreshLayout;

    Calendar mCalendar;

    FButton fButton;

    String mUEmail,mCategory,lang,spin;

    ListSharedPreference.Set setSharedPref;

    ListSharedPreference.Get getSharedPref;
    private ArrayList<String> countries_array,brand_array,models_array;
    private ArrayList<String> countriesId_array,modelsId_array,brandId_array;

    public ProductsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_products, container, false);

        setSharedPref = new ListSharedPreference.Set(ProductsFragment.this.getContext().getApplicationContext());
        getSharedPref = new ListSharedPreference.Get(ProductsFragment.this.getContext().getApplicationContext());

        MainActivity.mToolbarText.setText("Products");

        try {
            if (getArguments() != null) {
                Toast.makeText(getActivity(), getArguments().getString("yearpop"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getUserInfo();
        setAlertDialog();
        fireBackButtonEvent();
        initUI(rootView);
        initRecyclerView();

        return rootView;
    }//end onCreateView()

    private void setAlertDialog() {
        alertDialogBuilder = new AlertDialog.Builder(ProductsFragment.this.getContext());

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
    }//end setAlertDialog

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCall_back = (myCall_Back) context;
    }

    private void initRecyclerView() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }
    }//end initRecyclerView()

    private void initUI(View rootView) {
        initPullRefreshLayout(rootView);

        fabButton = rootView.findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();
            }
        });
        YoYo.with(Techniques.ZoomIn)
                .duration(700)
                .playOn(fabButton);

        recyclerView = rootView.findViewById(R.id.products_recycler);

    }//end initUI

    private void initPullRefreshLayout(View rootView) {
        pullRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayoutProducts);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (getSharedPref.getLoginStatus())
                    turnOnServers(1);
                else if (!getSharedPref.getLoginStatus()) {
                    turnOnServers(3);
                }
            }
        });
    }

    private void showPopUp() {

        final View popupView = this.getLayoutInflater().inflate(R.layout.popup_filter_layout, null);
        new EasyDialog(getActivity())
                .setLayout(popupView)
                .setBackgroundColor(ProductsFragment.this.getResources().getColor(R.color.app_background_color))
                .setLocationByAttachedView(fabButton)
                .setGravity(EasyDialog.GRAVITY_TOP)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 800, -600, 100, -50, 50, 0)
                .setAnimationAlphaShow(300, 0.3f, 1.0f)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                .setAnimationAlphaDismiss(300, 1.0f, 0.0f)
                .setTouchOutsideDismiss(true)
                .setMatchParent(true)
                .setMarginLeftAndRight(30, 30)
                .show();

        spinner_brand = popupView.findViewById(R.id.spinner_brand_popup);
        spinner_country = popupView.findViewById(R.id.spinner_country_popup);
        spinner_year = popupView.findViewById(R.id.spinner_year_popup);
        spinner_model = popupView.findViewById(R.id.spinner_model_popup);

        fButton = popupView.findViewById(R.id.flatButton);
        fButton.setButtonColor(getResources().getColor(R.color.dark_yellow));
        fButton.setShadowColor(getResources().getColor(R.color.app_background_color));
        fButton.setCornerRadius(10);
        fButton.setShadowEnabled(true);
        fButton.setShadowHeight(7);

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spin = spinner_year.getSelectedItem().toString();
                myCall_back.ProudctSFrag(spin);
            }
        });

        editText = popupView.findViewById(R.id.editText1_pop);
        mCalendar = Calendar.getInstance();
        addYears(popupView);

        serverCountries(popupView.getContext(), spinner_country);
        serverModels(popupView.getContext(), spinner_model);
        serverBrands(popupView.getContext(), spinner_brand);
    }

    private void serverCountries(final Context popup, final Spinner spinner) {
        Api retrofit = MyRetrofitClient.getBase().create(Api.class);

        Call<CountriesModel> countriesCall = retrofit.countries(getSharedPref.getLanguage());

        countriesCall.enqueue(new Callback<CountriesModel>() {
            @Override
            public void onResponse(Call<CountriesModel> call, Response<CountriesModel> response) {
                if (response.isSuccessful()) {

                    getCountries(response.body().getData(),popup,spinner);
                    getCountriesId(response.body().getData());
                    Log.v("res", response.body().getData() + "");
                } else
                    // Toast.makeText(AddItemActivity.this, response.body().getStatus().getTitle(),
                    // Toast.LENGTH_SHORT).show();
                    Log.v("res", response.body().getData() + "");
            }

            @Override
            public void onFailure(Call<CountriesModel> call, Throwable t) {
                Toast.makeText(ProductsFragment.this.getContext(), t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }//end serverCountries()

    private void getCountriesId(List<CountriesModel.DataBean> data) {
        countriesId_array = new ArrayList<>();
        countriesId_array.add(0,"addItem");
        for (int i = 0; i < data.size(); i++) {
            countriesId_array.add(data.get(i).getId());
        }
    }
    private void getCountries(List<CountriesModel.DataBean> data,Context popupView, Spinner spinner) {
        countries_array = new ArrayList<>();
        countries_array.add(0,"Choose Country");
        for (int i = 0; i < data.size(); i++) {
            countries_array.add(data.get(i).getName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (popupView, android.R.layout.simple_spinner_item,
                        countries_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void serverBrands(final Context context, final Spinner spinner) {
        Api retrofit = MyRetrofitClient.getBase().create(Api.class);

        Call<BrandsModel> brandsCall = retrofit.brands(getLang());

        brandsCall.enqueue(new Callback<BrandsModel>() {
            @Override
            public void onResponse(Call<BrandsModel> call, Response<BrandsModel> response) {
                if (response.isSuccessful()) {

                    getBrands(response.body().getData(),context,spinner);
                    getBrandsId(response.body().getData());

                } else
                    Toast.makeText(getContext(), response.body().getStatus().getTitle(),
                            Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BrandsModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }//end serverBrands()

    private void getBrands(List<BrandsModel.DataBean> data, Context context, Spinner spinner) {
        brand_array = new ArrayList<>();
        brand_array.add(0,"Choose Brand");
        for (int i = 0; i < data.size(); i++) {
            brand_array.add(data.get(i).getName());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        brand_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

    }

    private void getBrandsId(List<BrandsModel.DataBean> data) {
        brandId_array = new ArrayList<>();
        brandId_array.add(0,"addItem");
        for (int i = 0; i < data.size(); i++) {
            brandId_array.add(data.get(i).getId());
        }
    }

    private void serverModels(final Context context, final Spinner spinner) {

        Api retrofit = MyRetrofitClient.getBase().create(Api.class);

        Call<ModelsModel> modelsCall = retrofit.models("2");

        modelsCall.enqueue(new Callback<ModelsModel>() {
            @Override
            public void onResponse(Call<ModelsModel> call, Response<ModelsModel> response) {
                if (response.isSuccessful()) {
                    getModels(response.body().getData(), context, spinner);
                    getModelsId(response.body().getData());
                } else
                    Toast.makeText(getContext(), response.body().getStatus().getTitle(),
                            Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ModelsModel> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }//end serverModels()

    private void getModels(List<ModelsModel.DataBean> data, Context context, Spinner spinner) {
        models_array = new ArrayList<>();
        models_array.add(0,"Choose Model");
        for (int i = 0; i < data.size(); i++) {
            models_array.add(data.get(i).getName());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (context, android.R.layout.simple_spinner_item,
                        models_array); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void getModelsId(List<ModelsModel.DataBean> data) {
        modelsId_array = new ArrayList<>();
        modelsId_array.add(0,"addItem");
        for (int i = 0; i < data.size(); i++) {
            modelsId_array.add(data.get(i).getId());
        }
    }

    private void addYears(View view) {
        int current_year = mCalendar.get(Calendar.YEAR);

        for (int i = current_year; i >= 1990; i--) {
            year_array.add(i);
        }

        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(
                view.getContext(), android.R.layout.simple_spinner_item, year_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(spinnerArrayAdapter);
    }//end addYears();

    private void serverProductsAll() {
        try {
            Api retrofit = MyRetrofitClient.getBase().create(Api.class);

            final Call<ProductsAllModel> productsCall = retrofit.productsAll(getLang(), mCategory);

            productsCall.enqueue(new Callback<ProductsAllModel>() {
                @Override
                public void onResponse(Call<ProductsAllModel> call, Response<ProductsAllModel> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), R.string.loading, Toast.LENGTH_SHORT).show();

                        mProductAllDataList.addAll(response.body().getData());
                        recyclerView.setAdapter(mAllProductsAdapter);
                        mAllProductsAdapter.notifyDataSetChanged();
                        Log.v("jkjk", response.body().getData().size() + "");
                        pullRefreshLayout.setRefreshing(false);

                    } else {
                        pullRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "" + response.body().getStatus().getTitle() + " ", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ProductsAllModel> call, Throwable t) {

                    Log.v("tagy", t.getMessage());
                    pullRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            pullRefreshLayout.setRefreshing(false);
            alertDialogBuilder.setMessage(e.getMessage());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private String getLang() {
        return getSharedPref.getLanguage();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getSharedPref.getLoginStatus()) {
            turnOnServers(1);
        } else if (!getSharedPref.getLoginStatus()) {
            turnOnServers(3);
        }
    }

    private void serverProductsWithMail() {

        mProductDataList = new ArrayList<>();
        Api retrofit = MyRetrofitClient.getBase().create(Api.class);

        final Call<ProductsModel> productsCall = retrofit.productsWithMail(getLang(), mCategory, mUEmail);

        productsCall.enqueue(new Callback<ProductsModel>() {
            @Override
            public void onResponse(Call<ProductsModel> call, Response<ProductsModel> response) {

                if (response.isSuccessful()) {
                    mProductDataList.addAll(response.body().getData());
                    pullRefreshLayout.setRefreshing(false);
                    recyclerView.setAdapter(productsAdapter);
                    productsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "" + response.body().getStatus().getTitle() + " ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProductsModel> call, Throwable t) {
                Log.v("tagy", t.getMessage());
                pullRefreshLayout.setRefreshing(false);
                alertDialogBuilder.setMessage(t.getMessage());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void turnOnServers(Integer key) {
        switch (key) {
            case 1:
                serverProductsWithMail();
                initAdapterWith();
                break;
            case 3:
                serverProductsAll();
                initAdapterAllProducts();

                break;
            default:
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void initAdapterWith() {
        productsAdapter = new ProductsRecyclerAdapter(getContext(), mProductDataList,
                new ProductsRecyclerAdapter.ListAllListeners() {
                    @Override
                    public void onCardViewClick(ProductsModel.DataBean produtsModel) {

                        Log.e("plz", produtsModel.getProductName());

                        CachePot.getInstance().push("pName", produtsModel.getProductName());
                        CachePot.getInstance().push("pId", produtsModel.getProductNumber());
                        CachePot.getInstance().push("pPrice", produtsModel.getProductPrice());
                        CachePot.getInstance().push("pNumber", produtsModel.getProductNumber());
                        CachePot.getInstance().push("pCurrency", produtsModel.getCurrency());
                        CachePot.getInstance().push("pImage1", produtsModel.getImage1());
                        CachePot.getInstance().push("pImage2", produtsModel.getImage2());
                        CachePot.getInstance().push("pDate", produtsModel.getDate());
                        CachePot.getInstance().push("pCountry", produtsModel.getCountry());
                        CachePot.getInstance().push("pBrand", produtsModel.getBrand());
                        CachePot.getInstance().push("pModel", produtsModel.getModel());

                        CachePot.getInstance().push("uId", produtsModel.getId());
                        CachePot.getInstance().push("uMobile", produtsModel.getMobile());
                        CachePot.getInstance().push("uName", produtsModel.getName());
                        CachePot.getInstance().push("uImage", produtsModel.getImage());
                        CachePot.getInstance().push("langy", lang);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.main_frameLayout, new ProductDetail()).commit();
                    }

                    @Override
                    public void onFavButtonClick (View v, int position, boolean isFav) {
                        if (isFav){
                            serverAddToFav(position);
                            Toast.makeText(getContext(), ""+isFav, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), ""+isFav, Toast.LENGTH_SHORT).show();
                            serverRemoveFromFav(position);
                        }
                    }
                });
    }

    private void serverRemoveFromFav(int position) {

        Api retrofit = MyRetrofitClient.getBase().create(Api.class);

        final Call<AddToFavModel> productsCall = retrofit.addToFavourite(mUEmail, mProductDataList.get(position).getPid(),false);

        productsCall.enqueue(new Callback<AddToFavModel>() {
            @Override
            public void onResponse(Call<AddToFavModel> call, Response<AddToFavModel> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), ""+response.body().getStatus().getTitle(), Toast.LENGTH_SHORT).show();
                    if (getSharedPref.getLoginStatus()) {
                        turnOnServers(1);
                    } else if (!getSharedPref.getLoginStatus()) {
                        turnOnServers(3);
                    }

                } else {
                    Toast.makeText(getActivity(), "" + response.body().getStatus().getTitle() + " ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AddToFavModel> call, Throwable t) {
                Log.v("tagy", t.getMessage());
                pullRefreshLayout.setRefreshing(false);
                alertDialogBuilder.setMessage(t.getMessage());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void serverAddToFav(int position) {
        Api retrofit = MyRetrofitClient.getBase().create(Api.class);

        final Call<AddToFavModel> productsCall = retrofit.addToFavourite(mUEmail, mProductDataList.get(position).getPid(),true);

        productsCall.enqueue(new Callback<AddToFavModel>() {
            @Override
            public void onResponse(Call<AddToFavModel> call, Response<AddToFavModel> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), ""+response.body().getStatus().getTitle(), Toast.LENGTH_SHORT).show();
                    if (getSharedPref.getLoginStatus()) {
                        turnOnServers(1);
                    } else if (!getSharedPref.getLoginStatus()) {
                        turnOnServers(3);
                    }

                } else {
                    Toast.makeText(getActivity(), "" + response.body().getStatus().getTitle() + " ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AddToFavModel> call, Throwable t) {
                Log.v("tagy", t.getMessage());
                pullRefreshLayout.setRefreshing(false);
                alertDialogBuilder.setMessage(t.getMessage());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void initAdapterAllProducts() {
        mAllProductsAdapter = new AllProductsAdapter(getContext(), mProductAllDataList,
                new AllProductsAdapter.ListAllListeners() {
                    @Override
                    public void onCardViewClick(ProductsAllModel.DataBean produtsAllModel) {

                        Log.e("plz", produtsAllModel.getProductName());

                        CachePot.getInstance().push("pName", produtsAllModel.getProductName());
                        CachePot.getInstance().push("pId", produtsAllModel.getProductNumber());
                        CachePot.getInstance().push("pPrice", produtsAllModel.getProductPrice());
                        CachePot.getInstance().push("pNumber", produtsAllModel.getProductNumber());
                        CachePot.getInstance().push("pCurrency", produtsAllModel.getCurrency());
                        CachePot.getInstance().push("pImage1", produtsAllModel.getImage1());
                        CachePot.getInstance().push("pImage2", produtsAllModel.getImage2());
                        CachePot.getInstance().push("pDate", produtsAllModel.getDate());
                        CachePot.getInstance().push("pCountry", produtsAllModel.getCountry());
                        CachePot.getInstance().push("pBrand", produtsAllModel.getBrand());
                        CachePot.getInstance().push("pModel", produtsAllModel.getModel());

                        CachePot.getInstance().push("uId", produtsAllModel.getId());
                        CachePot.getInstance().push("uMobile", produtsAllModel.getMobile());
                        CachePot.getInstance().push("uName", produtsAllModel.getName());
                        CachePot.getInstance().push("langy", lang);


                        getFragmentManager().beginTransaction()
                                .replace(R.id.main_frameLayout, new ProductDetail()).commit();
                    }
                });
    }

    private void fireBackButtonEvent() {
        ((MainActivity) getActivity()).setOnBackPressedListener(new BaseBackPressedListener(getActivity()) {
            @Override
            public void onBackPressed() {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new Home())
                        .commit();
            }
        });
    }//end back pressed

    private void getUserInfo() {
        mUEmail = getSharedPref.getEmail();
        mCategory = getSharedPref.getCategory();
    }

    public interface myCall_Back {
        void ProudctSFrag(String year);
    }
}