package codestargram.domain.user.handler;

import codestargram.domain.user.data.UserSaveData;
import codestargram.domain.user.db.UserDatabase;
import codestargram.exception.user.UserAlreadyExistsException;
import codestargram.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {

    }

    public void userCreate(UserSaveData userSaveData) {
        // 이미 존재하는 유저인지 확인;
        if (UserDatabase.findUserById(userSaveData.getUserId()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        // 유저 저장
        User user = userSaveData.makeUser();
        UserDatabase.addUser(user);

        logger.info("user created successfully! - userId : {}", user.getUserId());
    }
}
