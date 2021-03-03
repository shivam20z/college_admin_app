package com.bipuldevashish.givnidelivery.ui.gallery;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bipuldevashish.givnidelivery.R;
import com.bipuldevashish.givnidelivery.ui.home.HomeFragment;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    List<SliderItem> sliderItems;
    TextView rotationText;
    Spinner packageSpinner;
    LinearLayout addPickupAddress;
    String[] item = new String[]{"Tap to select category"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        packageSpinner = root.findViewById(R.id.packageSpinner);
        addPickupAddress = root.findViewById(R.id.addPickupAddress);


        sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem("We Don't Purchase", "Our boy's won't pay and buy items on your behalf.", R.color.wheat, R.drawable.delivery_boy));
        sliderItems.add(new SliderItem("Watch The Weight", "Maximum allowed weight per order is 5kgs.", R.color.wheat, R.drawable.weight));
        sliderItems.add(new SliderItem("Cash payment Available", "Cash payment is available at both pickup or drop locations.", R.color.wheat, R.drawable.cash));
        SliderView sliderView = root.findViewById(R.id.imageSlider);
        sliderView.setSliderAdapter(new SliderAdapterExample(getContext(), sliderItems));

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();
        sliderView.setScrollTimeInMillis(3000);

        packageSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, item));

        packageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addPickupAddress.setOnClickListener(view -> {

            NavDirections action = GalleryFragmentDirections.actionNavGalleryToNavHome();
            Navigation.findNavController(view).navigate(action);

        });

        return root;
    }

}