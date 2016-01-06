package de.cineaste.android.fragment;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import de.cineaste.android.R;

public class UserInputFragment extends DialogFragment
        implements TextView.OnEditorActionListener, View.OnClickListener {

    private EditText editText;

    public interface UserNameListener {
        void onFinishUserDialog( String userName );
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        View view = inflater.inflate( R.layout.fragment_user_input, container );
        TextView textview = (TextView) view.findViewById( R.id.ok_tv );
        editText = (EditText) view.findViewById( R.id.username_et );

        textview.setOnClickListener( this );
        editText.setOnEditorActionListener( this );
        editText.requestFocus();
        getDialog().getWindow()
                .setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        getDialog().setTitle( R.string.enter_username );
        getDialog().setCancelable( false );

        return view;
    }

    @Override
    public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
        getNameAndDismiss();

        return true;
    }

    @Override
    public void onClick( View v ) {
        getNameAndDismiss();
    }

    private void getNameAndDismiss() {
        String input = editText.getText().toString().trim();

        if( !input.isEmpty() ) {
            UserNameListener activity = (UserNameListener) getActivity();
            activity.onFinishUserDialog( input );
            this.dismiss();
        }
    }
}
