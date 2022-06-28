package entities;

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

import static entities.AssetType.*;

@Data
public class Portfolio {
    @NonNull String userId;
    @NonNull Map<AssetType, Integer> breakup;

    public int getTotal() {
        return getBreakup().get(EQUITY) +  getBreakup().get(DEBT) +  getBreakup().get(GOLD);
    }
}
