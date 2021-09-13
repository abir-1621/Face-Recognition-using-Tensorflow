package com.abir.voting.view_models;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.abir.voting.view.fragments.LoginFragment;

public class LoginSignUpViewModel extends AndroidViewModel {

    private final Application application;
    private boolean isSuccessLogin = false;
    private OnSuccessLoginApiCall onSuccessfullyApiCall;

    public LoginSignUpViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

    }

    public void setOnLoginClickListener(OnSuccessLoginApiCall loginFragment) {
        this.onSuccessfullyApiCall=loginFragment;
    }

    public interface OnSuccessLoginApiCall {

    }
}