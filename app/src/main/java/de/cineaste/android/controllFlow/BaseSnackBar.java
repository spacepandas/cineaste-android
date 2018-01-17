package de.cineaste.android.controllFlow;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

public abstract class BaseSnackBar {

    protected final LinearLayoutManager linearLayoutManager;
    protected final View view;

    protected BaseSnackBar(LinearLayoutManager linearLayoutManager, View view) {
        this.linearLayoutManager = linearLayoutManager;
        this.view = view;
    }

    public abstract void getSnackBarLeftSwipe(final int position);
    public abstract void getSnackBarRightSwipe(final int position, final int message);
}
