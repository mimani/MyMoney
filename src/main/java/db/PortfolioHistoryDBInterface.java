package db;

import entities.PortfolioHistory;
import exceptions.PortfolioExistsException;

import java.time.YearMonth;
import java.util.List;

public interface PortfolioHistoryDBInterface {
    PortfolioHistory find(String userId, YearMonth ym);
    void add(PortfolioHistory ph) throws PortfolioExistsException;
    List<PortfolioHistory> findAll(String id);
    PortfolioHistory findLastRebalance(String id);
}
