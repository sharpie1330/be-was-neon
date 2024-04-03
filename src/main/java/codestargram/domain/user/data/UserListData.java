package codestargram.domain.user.data;

import webserver.exception.server.InternalServerErrorException;
import webserver.http.type.HttpBody;
import webserver.utils.Delimiter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserListData {
    private final List<UserData> users;

    public UserListData(List<UserData> userDataList) {
        this.users = userDataList;
    }

    public HttpBody fromUserListData(String filePath) {
        File file = new File(filePath);

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] body = new byte[(int) file.length()];
            int readLen = fileIn.read(body);
            String string = new String(body, StandardCharsets.UTF_8);

            StringBuilder stringBuilder = new StringBuilder();
            for (UserData userData : users) {
                stringBuilder.append("<tr>")
                        .append("<td>").append(userData.getUserId()).append("</td>")
                        .append("<td>").append(userData.getNickname()).append("</td>")
                        .append("<td>").append(userData.getEmail()).append("</td>")
                        .append("</tr>").append(Delimiter.NEW_LINE);
            }
            return new HttpBody(string.replaceAll("<!--user list-->", stringBuilder.toString()).getBytes());
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }
}
