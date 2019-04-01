package edu.und.seau.firebase.database;


import java.util.function.Consumer;

import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;

public interface FirebaseDatabaseInterface {

    void initializeUAVDBInstance(Consumer<UAV> onResult);
    void updateUAVName(UAV uav, Consumer<Boolean> onResult);
    void getUAVDBInstance(Consumer<UavDBModel> onResult);
}
