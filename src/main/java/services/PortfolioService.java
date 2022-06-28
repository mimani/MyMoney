package services;

import dao.PortfolioDAO;
import dao.PortfolioHistoryDAO;
import dao.UserDAO;
import entities.*;
import exceptions.PortfolioExistsException;
import exceptions.PortfolioNotFoundException;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import static entities.AssetType.*;

public class PortfolioService {
    private PortfolioDAO portfolioDAO = PortfolioDAO.getInstance(); // use injector
    private UserDAO userDAO = UserDAO.getInstance(); // use injector
    private PortfolioHistoryDAO portfolioHistoryDAO = PortfolioHistoryDAO.getInstance(); // or use injector

    public void applyReturns(float equityReturn, float debtReturn, float goldReturn, YearMonth ym) throws PortfolioNotFoundException, PortfolioExistsException {
        for(Portfolio portfolio: portfolioDAO.all()) {
            applySip(ym, portfolio);
            updateReturns(equityReturn, debtReturn, goldReturn, ym, portfolio);
            rebalance(portfolio, ym);
        }

    }

    private void updateReturns(float equityReturn, float debtReturn, float goldReturn, YearMonth ym, Portfolio portfolio) throws PortfolioExistsException, PortfolioNotFoundException {
        Map<AssetType, Integer> updatedPortfolioBreakup = new HashMap<AssetType, Integer>(){{
            put(EQUITY, (int)Math.floor(portfolio.getBreakup().get(EQUITY) * (100 + equityReturn)/100));
            put(DEBT, (int)Math.floor(portfolio.getBreakup().get(DEBT) * (100 + debtReturn)/100));
            put(GOLD, (int)Math.floor(portfolio.getBreakup().get(GOLD) * (100 + goldReturn)/100));
        }};

        portfolio.setBreakup(updatedPortfolioBreakup);
        PortfolioHistory portfolioHistory = new PortfolioHistory(portfolio.getUserId(), updatedPortfolioBreakup, ym, PortfolioChangeReason.RETURN_ADJUSTMENT);
        portfolioHistoryDAO.add(portfolioHistory);
        portfolioDAO.update(portfolio);
    }

    private void applySip(YearMonth ym, Portfolio portfolio) {
        User user = userDAO.findByUserId(portfolio.getUserId());
        if (user.getSip() != null && !ym.equals(YearMonth.of(2022, 1) )) {   // to avoid adding SIP amount as per the examples in Jan month with assumption allocation has happened in Jan
            Map<AssetType, Integer> updatedPortfolioBreakup = new HashMap<AssetType, Integer>(){{
                put(EQUITY, portfolio.getBreakup().get(EQUITY) + user.getSip().get(EQUITY));
                put(DEBT, portfolio.getBreakup().get(DEBT) + user.getSip().get(DEBT));
                put(GOLD, portfolio.getBreakup().get(GOLD) + user.getSip().get(GOLD));
            }};
            portfolio.setBreakup(updatedPortfolioBreakup);
        }
    }

    public PortfolioHistory getBalance(User user, YearMonth yearMonth) {
        return portfolioHistoryDAO.find(user.getId(), yearMonth);
    }

    public void printPortfolio(User user, YearMonth yearMonth) {
        PortfolioHistory ph = portfolioHistoryDAO.find(user.getId(), yearMonth);
        if (ph == null) {
            System.out.println("Data not found for date: " + yearMonth);
            return;
        }
        Map<AssetType, Integer> portfolioBreakup = ph.getBreakup();
//        System.out.println(org.apache.commons.lang3.StringUtils.join(portfolioBreakup.values(), " "));
        System.out.println(portfolioBreakup.get(EQUITY) + " " + portfolioBreakup.get(DEBT) + " " + portfolioBreakup.get(GOLD));
    }

    public void printPortfolio(User user) {
        Portfolio p = portfolioDAO.findByUserId(user.getId());
        if (p == null) {
            System.out.println("Data not found for user: " + user.getId());
            return;
        }
        Map<AssetType, Integer> portfolioBreakup = p.getBreakup();
//        System.out.println(org.apache.commons.lang3.StringUtils.join(portfolioBreakup.values(), " "));
        System.out.println(portfolioBreakup.get(EQUITY) + " " + portfolioBreakup.get(DEBT) + " " + portfolioBreakup.get(GOLD));
    }

    public void allocate(User user, int equity, int debt, int gold) throws PortfolioNotFoundException {
        int total = equity + debt + gold;
        user.setPortfolioAllocation(
                new HashMap<AssetType, Float>(){{
                    put(EQUITY, (float)equity / total);
                    put(DEBT, (float)debt / total);
                    put(GOLD, (float)gold / total);
                }}
        );
        Portfolio portfolio = new Portfolio(user.getId(), new HashMap<AssetType, Integer>(){{
            put(EQUITY, equity);
            put(DEBT, debt);
            put(GOLD, gold);
        }});
        portfolioDAO.add(portfolio);
    }

    public void rebalance(Portfolio portfolio, YearMonth yearMonth) throws PortfolioNotFoundException, PortfolioExistsException {
        if (yearMonth.getMonthValue() % 6 == 0) {
            int total = portfolio.getTotal();
            User user = userDAO.findByUserId(portfolio.getUserId());
            Map<AssetType, Float> portfolioAllocation = user.getPortfolioAllocation();
            if (!alreadyRebalanced(portfolio, portfolioAllocation)) {
                int equityAmount = (int) Math.floor(total * portfolioAllocation.get(EQUITY));
                int debtAmount = (int) Math.floor(total * portfolioAllocation.get(DEBT));
                portfolio.setBreakup(
                        new HashMap<AssetType, Integer>() {{
                            put(EQUITY, equityAmount);
                            put(DEBT, debtAmount);
                            put(GOLD, total - equityAmount - debtAmount);
                        }}
                );
                PortfolioHistory portfolioHistory = new PortfolioHistory(portfolio.getUserId(), portfolio.getBreakup(), yearMonth, PortfolioChangeReason.REBALANCE);
                portfolioHistoryDAO.add(portfolioHistory);
                portfolioDAO.update(portfolio);
                //        Some integration with service which will actually do rebalancing
            }
        }  // else Rebalance not needed, as it happned inly in 6th and 12th month
    }

    public void printRebalanceInfo(User user) {
        PortfolioHistory portfolioHistory = portfolioHistoryDAO.findLastRebalance(user.getId());
        if (portfolioHistory == null) {
            System.out.println("CANNOT_REBALANCE");
        }
        else {
            Map<AssetType, Integer> portfolioBreakup = portfolioHistory.getBreakup();
            System.out.println(portfolioBreakup.get(EQUITY) + " " + portfolioBreakup.get(DEBT) + " " + portfolioBreakup.get(GOLD));
        }
    }

    private boolean alreadyRebalanced(Portfolio portfolio, Map<AssetType, Float> portfolioAllocation) {
        int acceptableDeviation = 10;  // Asuumed a constant value, can be a % of total amount as well
        int total = portfolio.getTotal();
        return total * portfolioAllocation.get(EQUITY) - portfolio.getBreakup().get(EQUITY) < acceptableDeviation
                & total * portfolioAllocation.get(DEBT) - portfolio.getBreakup().get(DEBT) < acceptableDeviation
                & total * portfolioAllocation.get(GOLD) - portfolio.getBreakup().get(GOLD) < acceptableDeviation;
    }

}
