package db.impl;

import db.UserDBInterface;
import entities.AssetType;
import entities.User;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryUser implements UserDBInterface {
    private List<User> userList = new ArrayList<>();

    public User findByUserId(final String id) {     // in DB there will be index on userId for fast query
        return userList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void add(User user) throws UserExistsException {
        if (userList.stream().anyMatch(p -> p.getEmail().equals(user.getEmail())))  // In DB this can be just unique index or equivalent
            throw new UserExistsException("Invalid action: User already exists for email: " + user.getEmail());
        userList.add(user);
    }

    public void updateSip(String id, Map<AssetType, Integer> sip) throws UserNotExistsException {
        if (userList.stream().noneMatch(p -> p.getId().equals(id)))  // In DB this can be just unique index or equivalent
            throw new UserNotExistsException("Invalid action: User does not exists for id: " + id);
        userList.stream()
                .filter(p -> p.getId().equals(id))
                .forEach(p -> p.setSip(sip));
    }

    public void updatePortfolioAllocation(String id, Map<AssetType, Float> portfolioAllocation) throws UserNotExistsException {
        if (userList.stream().noneMatch(p -> p.getId().equals(id)))  // In DB this can be just unique index or equivalent
            throw new UserNotExistsException("Invalid action: User does not exists for id: " + id);
        userList.stream()
                .filter(p -> p.getId().equals(id))
                .forEach(p -> p.setPortfolioAllocation(portfolioAllocation));
    }

    public void clearTable() {
        userList.clear();
    }

    public List<User> findAll() {
        return userList;
    }
}
