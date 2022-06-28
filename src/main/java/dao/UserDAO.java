package dao;

import db.impl.InMemoryUser;
import db.UserDBInterface;
import entities.AssetType;
import entities.User;
import exceptions.UserExistsException;
import exceptions.UserNotExistsException;

import java.util.Map;


public class UserDAO {

    private static UserDAO singleInstance = null;
    private UserDBInterface userDBInterface;
    private UserDAO () {
        userDBInterface = new InMemoryUser();
    }
    public static UserDAO getInstance()
    {
        synchronized(UserDAO.class) {
            if (singleInstance == null)
                singleInstance = new UserDAO();
        }
        return singleInstance;
    }

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
