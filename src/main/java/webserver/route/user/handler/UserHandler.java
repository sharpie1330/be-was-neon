package webserver.route.user.handler;

import db.Database;
import exception.CustomErrorType;
import exception.CustomException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.route.user.data.UserSaveData;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {

    }

    public void userCreate(UserSaveData userSaveData) {
        // 이미 존재하는 유저인지 확인
        User findUser = Database.findUserById(userSaveData.getUserId());
        if (findUser != null) {
            throw new CustomException(CustomErrorType.USER_ALREADY_EXISTS);
        }

        // 유저 저장
        User user = userSaveData.makeUser();
        Database.addUser(user);

        logger.info("user created successfully! - userId : {}", user.getUserId());
    }
}
