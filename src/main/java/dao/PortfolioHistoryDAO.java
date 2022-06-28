package dao;

import db.impl.InMemoryPortfolioHistory;
import db.PortfolioHistoryDBInterface;
import entities.PortfolioHistory;
import exceptions.PortfolioExistsException;

import java.time.YearMonth;
import java.util.List;

public class PortfolioHistoryDAO {

    private static PortfolioHistoryDAO singleInstance = null;
    private PortfolioHistoryDAO () {}
    public static PortfolioHistoryDAO getInstance()
    {
        synchronized(PortfolioHistoryDAO.class) {
            if (singleInstance == null)
                singleInstance = new PortfolioHistoryDAO();
        }
        return singleInstance;
    }

    private PortfolioHistoryDBInterface portfolioHistoryDBInterface = new InMemoryPortfolioHistory();

    public void add(PortfolioHistory ph) throws PortfolioExistsException {
        portfolioHistoryDBInterface.add(ph);
    }

    public PortfolioHistory find(String userId, YearMonth ym) {
        return portfolioHistoryDBInterface.find(userId, ym);
    }

    public List<PortfolioHistory> findAll(String userId) {
        return portfolioHistoryDBInterface.findAll(userId);
    }

    public PortfolioHistory findLastRebalance(String userId) {
        return portfolioHistoryDBInterface.findLastRebalance(userId);
    }
}
