package github.hmasum52.campusdeal.dagger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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

    // firebase fire store provider
    @Provides
    @Singleton
    public static FirebaseFirestore provideFirebaseFirestore(){
        return FirebaseFirestore.getInstance();
    }

    // firebase storage provider
    @Provides
    @Singleton
    public static FirebaseStorage provideFirebaseStorage(){
        return FirebaseStorage.getInstance();
    }
}
