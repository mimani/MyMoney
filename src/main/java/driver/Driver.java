package driver;

import entities.AssetType;
import entities.User;
import exceptions.PortfolioExistsException;
import exceptions.PortfolioNotFoundException;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;
import services.PortfolioService;
import services.UserService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Month;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.UUID;

import static entities.AssetType.*;

public class Driver {
    public static void main(String[] args) throws IOException, UserExistsException {
        PortfolioService portfolioService = new PortfolioService();  // use injector
        UserService userService = new UserService();  // use injector
        String file = "input.txt";
        final int year = 2022;  //Assumed all for this year, ideally should be part of input

        User testUser = new User(UUID.randomUUID().toString().replace("-", ""), "BJHPM12345", "+919123456789", "abc#navi.com");
        userService.addUser(testUser);  // added dummy user to DB, for which all commands coming from file in absence of user in input


        BufferedReader br = new BufferedReader(new FileReader(file));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (line.isEmpty())
                System.out.println("Empty line, not expected.");
            String[] inputParts = line.split(" ");
            try {
                switch(inputParts[0]) {
                    case "ALLOCATE":
                        int equityAmount = Integer.parseInt(inputParts[1]);
                        int debtAmount = Integer.parseInt(inputParts[2]);
                        int goldAmount = Integer.parseInt(inputParts[3]);
                        portfolioService.allocate(testUser, equityAmount, debtAmount, goldAmount);
                        break;
                    case "SIP":
                        int equitySIPAmount = Integer.parseInt(inputParts[1]);
                        int debtSIPAmount = Integer.parseInt(inputParts[2]);
                        int goldSIPAmount = Integer.parseInt(inputParts[3]);
                        userService.updateSip(testUser.getId(),
                                new HashMap<AssetType, Integer>(){{
                                    put(EQUITY, equitySIPAmount);
                                    put(DEBT, debtSIPAmount);
                                    put(GOLD, goldSIPAmount);
                        }});
                        break;
                    case "BALANCE":
                        YearMonth yearMonth = YearMonth.of(year, Month.valueOf(inputParts[1]));
                        portfolioService.printPortfolio(testUser, yearMonth);
                        break;
                    case "REBALANCE":
                        portfolioService.printRebalanceInfo(testUser);
                        break;
                    case "CHANGE":
                        float equityReturn = Float.parseFloat(inputParts[1].replaceAll("[^\\d.-]", "")); // should have better way to remove % from the input
                        float debtReturn = Float.parseFloat(inputParts[2].replaceAll("[^\\d.-]", ""));
                        float goldReturn = Float.parseFloat(inputParts[3].replaceAll("[^\\d.-]", ""));
                        YearMonth ym = YearMonth.of(year, Month.valueOf(inputParts[4]));
                        portfolioService.applyReturns(equityReturn, debtReturn, goldReturn, ym);
                        break;
                    default:
                        System.out.println("Not Supported: " + line);
                }
            } catch (PortfolioNotFoundException | UserNotExistsException | PortfolioExistsException e) {
                System.out.println("Got exception: " + e.getMessage() + " while processing commands " + inputParts[0]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Parameters not passed for command: " + inputParts[0]);
            }
        }
    }

}
