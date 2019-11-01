package club.charliefeng.kafkademoappengine.mapper;

import club.charliefeng.avro.User;
import club.charliefeng.kafkademoappengine.dto.UserRequest;

public final class UserMapper {

    public static User map(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setIncome(request.getIncome());
        return user;
    }

    public static UserRequest map(User user) {
        UserRequest request = new UserRequest();
        request.setName(user.getName().toString());
        request.setAge(user.getAge());
        request.setIncome(user.getIncome());
        return request;
    }
}
