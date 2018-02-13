package com.spectraapps.myspare.bottomtabscreens.home;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spectraapps.myspare.MainActivity;
import com.spectraapps.myspare.SplashScreen;
import com.spectraapps.myspare.api.Api;
import com.spectraapps.myspare.helper.BaseBackPressedListener;
import com.spectraapps.myspare.network.MyRetrofitClient;
import com.spectraapps.myspare.model.CategoriesModel;
import com.spectraapps.myspare.bottomtabscreens.home.products.ProductsFragment;
import com.spectraapps.myspare.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    ImageView image1, image2, image3, image4, image5, image6;
    TextView textAccessories, textBattery, textInside, textMechanic, textOutside, textTires;
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        fireBackButtonEvent();
        initUI(rootView);
        initCardViewClickListener();

        serverCategories();

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return rootView;
    }

    private void serverCategories() {

        Api retrofit = MyRetrofitClient.getBase().create(Api.class);
        String lang_key = "";
        switch (SplashScreen.LANG_NUM) {
            case 1:
                lang_key = "en";
                break;
            case 2:
                lang_key = "ar";
                break;
        }

        Call<CategoriesModel> categoriesCall = retrofit.categories(lang_key);

        categoriesCall.enqueue(new Callback<CategoriesModel>() {
            @Override
            public void onResponse(Call<CategoriesModel> call, Response<CategoriesModel> response) {

                if (response.isSuccessful()) {
                    textInside.setText(response.body().getData().get(0).getName());//internel
                    Picasso.with(getContext()).load(response.body().getData().get(0).getImage()).into(image1);
                    textOutside.setText(response.body().getData().get(1).getName());//externel
                    Picasso.with(getContext()).load(response.body().getData().get(1).getImage()).into(image2);
                    textMechanic.setText(response.body().getData().get(2).getName());//mechanic
                    Picasso.with(getContext()).load(response.body().getData().get(2).getImage()).into(image4);
                    textTires.setText(response.body().getData().get(3).getName());//tires
                    Picasso.with(getContext()).load(response.body().getData().get(3).getImage()).into(image6);
                    textAccessories.setText(response.body().getData().get(4).getName()); //accessories
                    Picasso.with(getContext()).load(response.body().getData().get(4).getImage()).into(image5);
                    textBattery.setText(response.body().getData().get(5).getName()); //electric
                    Picasso.with(getContext()).load(response.body().getData().get(5).getImage()).into(image3);

                } else {
                    Toast.makeText(getActivity(), "" + response.body().getStatus().getTitle() + " ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriesModel> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }//end serverCategories

    private void fireBackButtonEvent() {
        ((MainActivity) getActivity()).setOnBackPressedListener(new BaseBackPressedListener(getActivity()) {
            @Override
            public void onBackPressed() {
                getActivity().finish();
            }
        });
    }//end back pressed

    private void initCardViewClickListener() {

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new ProductsFragment()).commit();
                MainActivity.mToolbarText.setText(getString(R.string.main_inside));
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new ProductsFragment()).commit();
                MainActivity.mToolbarText.setText(getString(R.string.main_outside));
            }
        });


        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new ProductsFragment()).commit();
                MainActivity.mToolbarText.setText(getString(R.string.main_electricity));
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new ProductsFragment()).commit();
                MainActivity.mToolbarText.setText(getString(R.string.main_mechanic));
            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new ProductsFragment()).commit();
                MainActivity.mToolbarText.setText(getString(R.string.main_accessories));
            }
        });

        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frameLayout, new ProductsFragment()).commit();
                MainActivity.mToolbarText.setText(getString(R.string.main_tires));
            }
        });

    }//end initCardViewClickListener()

    private void initUI(View rootView) {
        cardView1 = rootView.findViewById(R.id.card_1);
        cardView2 = rootView.findViewById(R.id.card_2);
        cardView3 = rootView.findViewById(R.id.card_3);
        cardView4 = rootView.findViewById(R.id.card_4);
        cardView5 = rootView.findViewById(R.id.card_5);
        cardView6 = rootView.findViewById(R.id.card_6);

        image1 = rootView.findViewById(R.id.image_1);
       // Picasso.with(getContext()).load(R.drawable.car_inside).into(image1);
        image2 = rootView.findViewById(R.id.image_2);
       // Picasso.with(getContext()).load(R.drawable.car_outside).into(image2);
        image3 = rootView.findViewById(R.id.image_3);
       // Picasso.with(getContext()).load(R.drawable.car_electric).into(image3);
        image4 = rootView.findViewById(R.id.image_4);
       // Picasso.with(getContext()).load(R.drawable.car_mechanic).into(image4);
        image5 = rootView.findViewById(R.id.image_5);
      //  Picasso.with(getContext()).load(R.drawable.car_accessories).into(image5);
        image6 = rootView.findViewById(R.id.image_6);
       // Picasso.with(getContext()).load(R.drawable.car_tires).into(image6);

        textAccessories = rootView.findViewById(R.id.text_accessories);
        textBattery = rootView.findViewById(R.id.text_electrisity);
        textInside = rootView.findViewById(R.id.text_insideBody);
        textOutside = rootView.findViewById(R.id.text_outsideBody);
        textMechanic = rootView.findViewById(R.id.text_mechanic);
        textTires = rootView.findViewById(R.id.text_tires);


    }//end initUI


}//end Home