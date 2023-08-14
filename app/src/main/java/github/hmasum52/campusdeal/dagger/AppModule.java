package github.hmasum52.campusdeal.dagger;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

// https://developer.android.com/training/dependency-injection/hilt-android
@Module
@InstallIn(SingletonComponent.class)
public class AppModule{

    // firebase auth provider
    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }
}
