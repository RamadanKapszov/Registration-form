import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;


@WebServlet("/register.jsp")
public class RegisterServlet extends HttpServlet {

    //Регулярни изрази за име,имейл и парола.
    public final String patternU="^[a-zA-Z0-9_-]{3,16}$";

    public final String patternE = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-]+)(\\.[a-zA-Z]{2,5}){1,2}$";

    public final String patternP = "^.{6,}$";


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("Username");
        String email = request.getParameter("Email");
        String password = request.getParameter("Password");

        try {
            // Свързваме се с нашата база данни (като поставим съответните име на базата, потребител и парола);
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_name", "dbuser", "dbpassword");

            // Създаваме заявка за вмъкване на стойностите
            String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);


            if(validateData(username, patternU) && validateData(email, patternE) && validateData(password, patternP)) {


                // Задаваме стойностите
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, password);

                // Изпълняваме заявката
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    // Препращаме към страница за успешно записване
                    response.sendRedirect("success.html");
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

}
