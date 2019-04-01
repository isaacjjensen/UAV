package edu.und.seau.di.module;


import dagger.Binds;
import dagger.Module;
import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.firebase.database.FirebaseDatabaseManager;

@Module(includes = FirebaseModule.class)
abstract public class InteractionModule {

    @Binds
    abstract FirebaseDatabaseInterface bindDatabaseInterface(FirebaseDatabaseManager firebaseDatabaseManager);
}
