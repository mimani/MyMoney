package db.impl;

import db.PortfolioDBInterface;
import entities.Portfolio;
import exceptions.PortfolioNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class InMemoryPortfolio implements PortfolioDBInterface {
    private List<Portfolio> portfolioList = new ArrayList<>();

    public Portfolio findByUserId(final String userId) {     // in DB there will be index on userId for fast query
        return portfolioList.stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public void addPortfolio(Portfolio portfolio) throws PortfolioNotFoundException {
        if (portfolioList.stream().anyMatch(p -> p.getUserId().equals(portfolio.getUserId())))  // In DB this can be just unique index or equivalent
            throw new PortfolioNotFoundException("Invalid action: Portfolio already exists for user: " + portfolio.getUserId());
        portfolioList.add(portfolio);
    }

    public void updatePortfolio(Portfolio portfolio) throws PortfolioNotFoundException {
        if (portfolioList.stream().noneMatch(p -> p.getUserId().equals(portfolio.getUserId())))  // In DB this can be just unique index or equivalent
            throw new PortfolioNotFoundException("Invalid action: Portfolio does not exists for user: " + portfolio.getUserId());
        portfolioList.stream()
                .filter(p -> p.getUserId().equals(portfolio.getUserId()))
                .forEach(p -> p.setBreakup(portfolio.getBreakup()));
    }

    public List<Portfolio> findAll() {
        return portfolioList;
    }
}
