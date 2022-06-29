package services;

import dao.PortfolioDAO;
import dao.PortfolioHistoryDAO;
import dao.UserDAO;
import entities.AssetType;
import entities.Portfolio;
import entities.PortfolioHistory;
import entities.User;

import exceptions.PortfolioExistsException;
import exceptions.PortfolioNotFoundException;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;


class PortfolioServiceTest {
    private static PortfolioDAO portfolioDAO = PortfolioDAO.getInstance();
    private static UserDAO userDAO;
    private static PortfolioHistoryDAO portfolioHistoryDAO = PortfolioHistoryDAO.getInstance();
    private static PortfolioService portfolioService;
    private static UserService userService;
    private static User testUser;
    private static String userId = "user123";

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws UserExistsException {
        portfolioService = new PortfolioService();
        userService = new UserService();
        testUser = new User(userId, "BJHPM12345", "+919123456789", "abc#navi.com");
        userService.addUser(testUser);  // added dummy user to DB, for which all commands coming from file in absence of user in input
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        userService.clearTable();
        portfolioService.clearTable();
    }

    @Test
    void applyReturns() throws PortfolioNotFoundException, PortfolioExistsException, UserNotExistsException {
        portfolioService.allocate(testUser, 1000, 1000, 1000);
        float equityReturn = 5;
        float debtReturn = 7;
        float goldReturn = 9;
        YearMonth ym = YearMonth.of(2022, 2);
        portfolioService.applyReturns(equityReturn, debtReturn, goldReturn, ym);
        PortfolioHistory ph = portfolioService.getPortfolioHistory(testUser, ym);

        Assertions.assertEquals((int)Math.floor(1000 * (100 + equityReturn) / 100), ph.getBreakup().get(AssetType.EQUITY));
        Assertions.assertEquals((int)Math.floor(1000 * (100 + debtReturn) / 100), ph.getBreakup().get(AssetType.DEBT));
        Assertions.assertEquals((int)Math.floor(1000 * (100 + goldReturn) / 100), ph.getBreakup().get(AssetType.GOLD));
    }

    @Test
    void getBalance() {
    }

    @Test
    void allocate() throws PortfolioNotFoundException, UserNotExistsException {
        int equityAmount = 500;
        int debtAmount = 300;
        int goldAmount = 200;
        portfolioService.allocate(testUser, equityAmount, debtAmount, goldAmount);

        Portfolio portfolio = portfolioDAO.findByUserId(userId);
        Assertions.assertEquals(equityAmount, portfolio.getBreakup().get(AssetType.EQUITY));
        Assertions.assertEquals(debtAmount, portfolio.getBreakup().get(AssetType.DEBT));
        Assertions.assertEquals(goldAmount, portfolio.getBreakup().get(AssetType.GOLD));

        User user = userService.findByUserId(userId);
        Assertions.assertEquals(0.5f, user.getPortfolioAllocation().get(AssetType.EQUITY));
    }

    @Test
    void rebalance() {
    }
}