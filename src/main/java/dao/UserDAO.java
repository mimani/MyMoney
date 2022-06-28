package dao;

import db.InMemoryUser;
import db.UserDBInterface;
import entities.AssetType;
import entities.User;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;

import java.util.Map;


public class UserDAO {

    private static UserDAO singleInstance = null;
    private UserDAO () {}
    public static UserDAO getInstance()
    {
        if (singleInstance == null)
            singleInstance = new UserDAO();

        return singleInstance;
    }

    private UserDBInterface userDBInterface = new InMemoryUser();

    public User findByUserId(String userId) {
        return userDBInterface.findByUserId(userId);
    }

    public void add(User user) throws UserExistsException {
        userDBInterface.add(user);
    }

    public void updateSip(String id, Map<AssetType, Integer> sip) throws UserNotExistsException {
        userDBInterface.updateSip(id, sip);
    }
}
