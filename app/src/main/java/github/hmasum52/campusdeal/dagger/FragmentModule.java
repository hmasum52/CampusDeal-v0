package github.hmasum52.campusdeal.dagger;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(FragmentComponent.class)
public class FragmentModule {

    @Provides
    @FragmentScoped
    public static FirebaseUser provideFirebaseUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
