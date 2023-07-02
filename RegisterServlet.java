import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;



@WebServlet("/register.jsp")
public class RegisterServlet extends HttpServlet {

    //Регулярни изрази за име,имейл и парола.
    public final String patternU = "^[a-zA-Z0-9_-]{3,16}$";

    public final String patternE = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-]+)(\\.[a-zA-Z]{2,5}){1,2}$";

    public final String patternP = "^.{6,}$";


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws  IOException {

        String username = request.getParameter("Username");
        String email = request.getParameter("Email");
        String password = request.getParameter("Password");

        try {
            // Свързваме се с нашата база данни (като поставим съответните име на базата, потребител и парола);
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_name", "databaseUser", "databasePassword");

            // Създаваме заявка за вмъкване на стойностите
            String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);


            if (validateData(username, patternU) && validateData(email, patternE) && validateData(password, patternP)) {


                // Задаваме стойностите
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, password);

                // Изпълняваме заявката
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    // Препращаме към страница за успешно записване и изпращане на имейл
                    response.sendRedirect("success.html");
                    sendConfirmationEmail(email);
                } else {
                    // Препращаме към страница за неуспешно записване
                    response.sendRedirect("error.html");
                }
            }
            // Затваряме ресурсите
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html");
        }
    }


    public static boolean validateData(String input, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        return matcher.matches();
    }




    public static void sendConfirmationEmail(String recipientEmail) {
        final String senderEmail = "kapszovr@gmail.com";
        final String senderPassword = "email-password";

        // Задаваме хост и порт на SMTP сървър
        String host = "smtp.example.com";
        int port = 587;

        // Задаваме свойствата на SMTP сървъра
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Създаване на сесия
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        // Създаване на съобщението
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Registration Confirmation");
            message.setText("Dear User,\n\nThank you for registering on our website.\n\nBest regards,\nYour Website Team");

            // Изпращане на съобщението
            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public static void main(String[] args) {


        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_name", "databaseUser", "databasePassword");

            UserCrudImpl userCrud = new UserCrudImpl(connection);

            // Извличане по ID
            User retrievedUser = userCrud.getUserById(1);
            System.out.println("Retrieved User: " + retrievedUser.getUsername());

            // Извличане на всички
            List<User> allUsers = userCrud.getAllUsers();
            for (User u : allUsers) {
                System.out.println("User: " + u.getUsername());
            }

            // Ъпдейт
            User userToUpdate = userCrud.getUserById(1);
            userToUpdate.setEmail("updated_email@example.com");
            userCrud.updateUser(userToUpdate);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




