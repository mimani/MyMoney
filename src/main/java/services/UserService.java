package services;

import dao.UserDAO;
import entities.AssetType;
import entities.User;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;

import java.util.Map;

public class UserService {
    private UserDAO userDAO = UserDAO.getInstance(); // use injector

    public void addUser(User user) throws UserExistsException {
        userDAO.add(user);
    }

    public void updateSip(String id, Map<AssetType, Integer> sip) throws UserNotExistsException {
        userDAO.updateSip(id, sip);
    }
}
