package db;

import entities.Portfolio;
import exceptions.PortfolioNotFoundException;

import java.util.List;

public interface PortfolioDBInterface {
    Portfolio findByUserId(String userId);
    void addPortfolio(Portfolio p) throws PortfolioNotFoundException;
    void updatePortfolio(Portfolio p) throws PortfolioNotFoundException;
    List<Portfolio> findAll();
}
