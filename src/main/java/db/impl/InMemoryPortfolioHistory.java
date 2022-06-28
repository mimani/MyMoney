package db.impl;

import db.PortfolioHistoryDBInterface;
import entities.PortfolioChangeReason;
import entities.PortfolioHistory;
import exceptions.PortfolioExistsException;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryPortfolioHistory implements PortfolioHistoryDBInterface {
    private List<PortfolioHistory> portfolioHistoryArrayList = new ArrayList<>();

    public PortfolioHistory find(String userId, YearMonth ym) {     // in DB there will be index on userId for fast query
        return portfolioHistoryArrayList.stream()
                .filter(p -> p.getUserId().equals(userId) && p.getDate().equals(ym))
                .findFirst()
                .orElse(null);
    }

    public List<PortfolioHistory> findAll(String userId) {
        return portfolioHistoryArrayList.stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void add(PortfolioHistory portfolioHistory) throws PortfolioExistsException {
        if (portfolioHistoryArrayList.stream().anyMatch(
                p -> p.getUserId().equals(portfolioHistory.getUserId())
                        && p.getDate().equals(portfolioHistory.getDate())
                        && p.getPortfolioChangeReason().equals(portfolioHistory.getPortfolioChangeReason())
                ))  // In DB this can be just unique index or equivalent
            throw new PortfolioExistsException("Invalid action: Portfolio already exists for user: " + portfolioHistory.getUserId());
        portfolioHistoryArrayList.add(portfolioHistory);
    }

    public PortfolioHistory findLastRebalance(String id) {
        return portfolioHistoryArrayList.stream()
                .sorted(Comparator.comparing(o -> -o.getDate().getMonthValue()))
                .collect(Collectors.toList())
                .stream()
                .filter(p -> p.getUserId().equals(id) && p.getPortfolioChangeReason().equals(PortfolioChangeReason.REBALANCE))
                .findFirst()
                .orElse(null);
    }
}
