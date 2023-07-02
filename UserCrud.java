import java.util.List;

public interface UserCrud {
    // Извличане на потребител по ID
    User getUserById(int id);

    // Извличане на всички потребители
    List<User> getAllUsers();

    // Промяна на данните за потребителя
    void updateUser(User user);

}
