package edu.und.seau.di.module;

import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @Provides
    FirebaseFirestore provideFirebaseDatabase(){
        return FirebaseFirestore.getInstance();
    }

}
