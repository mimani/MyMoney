package entities;

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

@Data
public class User {
    @NonNull
    String id;

    @NonNull
    String PAN;

    @NonNull
    String PhoneNum;

    @NonNull
    String email;

    Map<AssetType, Float> portfolioAllocation;

    Map<AssetType, Integer> sip;
}
