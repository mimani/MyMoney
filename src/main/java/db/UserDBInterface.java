package db;

import entities.AssetType;
import entities.User;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;

import java.util.List;
import java.util.Map;

public interface UserDBInterface {
    User findByUserId(String userId);
    void add(User u) throws UserExistsException;
    void updateSip(String id, Map<AssetType, Integer> sip) throws UserNotExistsException;
    void updatePortfolioAllocation(String id, Map<AssetType, Float> portfolioAllocation) throws UserNotExistsException;
    List<User> findAll();
    void clearTable();
}
