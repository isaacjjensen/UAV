package edu.und.seau.firebase.modelmapper;

import com.google.firebase.Timestamp;

import java.util.HashMap;

import edu.und.seau.common.FirebaseConstants;
import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;

public class UavDBMapper {

    private static final String KEY_NAME = FirebaseConstants.KEY_NAME;
    private static final String KEY_ID = FirebaseConstants.KEY_ID;

    public static UavDBModel getUavDBModel(UAV uav)
    {
        UavDBModel model = new UavDBModel(uav.getId(),uav.getName());
        model.setTimestamp(Timestamp.now());
        return model;
    }
}
