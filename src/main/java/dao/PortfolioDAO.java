package dao;

import db.impl.InMemoryPortfolio;
import db.PortfolioDBInterface;
import entities.Portfolio;
import exceptions.PortfolioNotFoundException;

import java.util.List;

public class PortfolioDAO {

    private static PortfolioDAO singleInstance = null;
    private PortfolioDBInterface portfolioDBInterface;
    private PortfolioDAO () {
        portfolioDBInterface = new InMemoryPortfolio();
    }
    public static PortfolioDAO getInstance()
    {
        synchronized(PortfolioDAO.class) {
            if (singleInstance == null)
                singleInstance = new PortfolioDAO();
        }
        return singleInstance;
    }

    public Portfolio findByUserId(String userId) {
        return portfolioDBInterface.findByUserId(userId);
    }

    public void update(Portfolio portfolio) throws PortfolioNotFoundException {
        portfolioDBInterface.updatePortfolio(portfolio);
    }

    public void add(Portfolio portfolio) throws PortfolioNotFoundException {
        portfolioDBInterface.addPortfolio(portfolio);
    }

    public List<Portfolio> all() {
        return portfolioDBInterface.findAll();
    }
}
