package edu.und.seau.firebase.database;


import java.util.function.Consumer;

import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;

public interface FirebaseDatabaseInterface {

    void DoesUAVExist(String uavID, Consumer<Boolean> OnResult);
    void CreateNewUAV(Consumer<String> OnResult);
}
