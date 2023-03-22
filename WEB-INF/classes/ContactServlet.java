import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/foodshopcontact")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class ContactServlet extends HttpServlet {

   @Override
   public void doGet (HttpServletRequest request, HttpServletResponse response)
                   throws ServletException, IOException {

                        response.setContentType("text/html;charset=UTF-8");

                                String custName= request.getParameter("name");
                                boolean hasCustName = custName != null && ((custName = custName.trim()).length() > 0);
                                String custEmail = request.getParameter("email");
                                boolean hasCustEmail = custEmail != null && ((custEmail = custEmail.trim()).length() > 0);
                                String custPhone = request.getParameter("number");
                                boolean hasCustPhone = custPhone != null && ((custPhone = custPhone.trim()).length() > 0);
                                String message = request.getParameter("message");
                                boolean hasMessage = message != null && ((message  = message .trim()).length() > 0);

                                PrintWriter out = response.getWriter();

        

                         try (
                            // Step 1: Allocate a database 'Connection' object
                            Connection conn = DriverManager.getConnection(
                                  "jdbc:mysql://localhost:3306/foodshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                                  "myuser", "xxxx");   // For MySQL
                                  // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                            // Step 2: Allocate a 'Statement' object in the Connection
                            Statement stmt = conn.createStatement();
                        ) {
                           if (!hasCustName) {
                                        out.println("<h3>Please Enter Your Name!</h3>");
                                        
                                } else if (!hasCustEmail || (custEmail.indexOf('@') == -1)) {
                                        out.println("<h3>Please Enter Your e-mail (user@host)!</h3>");
                                        
                                } else if (!hasCustPhone || (custPhone.length() != 8)) {
                                        out.println("<h3>Please Enter an 8-digit Phone Number!</h3>");
                                } else if (!hasMessage || (message.length() > 500)) {
                                        out.println("<h3>Please Enter a message not more than 500 characters!</h3>");
                                } else {

                                        String htmlResponse = "<html>";
                                        htmlResponse += "<h2>Name: " + custName + "<br/>";      
                                        htmlResponse += "Email: " + custEmail + "<br/>";  
                                        htmlResponse += "Phone Number: " + custPhone + "<br/>"; 
                                        htmlResponse += "Message: " + message + "</h2>";    
                                        htmlResponse += "</html>";
                                                 
                                        // return response
                                        out.println(htmlResponse);



                                        String sqlStr = "INSERT INTO queries values ('"
                                        + custName + "', '" + custEmail+ "', '" + custPhone+ "', '"
                                        + message + "')";

                                       //System.out.println(sqlStr);  // for debugging
                                       stmt.executeUpdate(sqlStr);
                                }
                                
                        }catch(Exception ex) {
                      out.println("<p>Error: " + ex.getMessage() + "</p>");
                        out.println("<p>Check Tomcat console for details.</p>");
                        ex.printStackTrace();
                        }
                
                  out.println("<p><a href='http://localhost:9999/foodshop/#'>Back to Home Page</a></p>");
                        // Commit for ALL the books ordered.
         
                                 out.close();
        }

        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
           doGet(request, response);
        }
   
}