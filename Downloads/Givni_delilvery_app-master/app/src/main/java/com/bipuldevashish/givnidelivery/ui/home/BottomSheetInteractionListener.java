package com.bipuldevashish.givnidelivery.ui.home;

import android.view.View;

public interface BottomSheetInteractionListener {

  void initiateUi(View view);

  void setCustomStyle(View view);

  void setSearchEditText();

  void setupRecyclerView(View view);
}
