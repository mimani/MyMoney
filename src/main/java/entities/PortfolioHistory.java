package entities;

import lombok.Data;
import lombok.NonNull;

import java.time.YearMonth;
import java.util.Map;

@Data
public class PortfolioHistory {
    @NonNull String userId;
    @NonNull Map<AssetType, Integer> breakup;
    @NonNull YearMonth date;
    @NonNull PortfolioChangeReason portfolioChangeReason;
}
