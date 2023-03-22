import java.io.*;
import java.sql.*;
import jakarta.servlet.*;             // Tomcat 10
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.logging.*;


@WebServlet("/foodshoporder")

public class OrderServlet extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();

      String[] prodName = request.getParameterValues("cart-product-title");  // Possibly more than one values
      String custName = request.getParameter("name");
      boolean hasCustName = custName != null && ((custName = custName.trim()).length() > 0);
      String custEmail = request.getParameter("email").trim();
      boolean hasCustEmail = custEmail != null && ((custEmail = custEmail.trim()).length() > 0);
      String custPhone = request.getParameter("phone").trim();
      boolean hasCustPhone = custPhone != null && ((custPhone = custPhone.trim()).length() > 0);

      out.println("<html><head><title>Order Confirmation</title></head><body>");
      out.println("<h2>Paimon's Delicacies - Order Confirmation</h2>");
     
      try(

      	// Step 1: Allocate a database 'Connection' object
      	Connection conn = DriverManager.getConnection(
      	      "jdbc:mysql://localhost:3306/foodshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
      	      "myuser", "xxxx");   // For MySQL
      	      // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

      	// Step 2: Allocate a 'Statement' object in the Connection
      	Statement stmt = conn.createStatement();

      	) {

         // Validate inputs
         if (prodName == null || prodName.length == 0) {
            out.println("<h3>Please Select a Dish!</h3>");
         } else if (!hasCustName) {
            out.println("<h3>Please Enter Your Name!</h3>");
         } else if (!hasCustEmail || (custEmail.indexOf('@') == -1)) {
            out.println("<h3>Please Enter Your e-mail (user@host)!</h3>");
         } else if (!hasCustPhone || (custPhone.length() != 8)) {
            out.println("<h3>Please Enter an 8-digit Phone Number!</h3>");
         } else {
            // Display the name, email and phone (arranged in a table)
            out.println("<table>");
            out.println("<tr><td>Customer Name:</td><td>" + custName + "</td></tr>");
            out.println("<tr><td>Customer Email:</td><td>" + custEmail + "</td></tr>");
            out.println("<tr><td>Customer Phone Number:</td><td>" + custPhone + "</td></tr></table>");

 
            // Print the book(s) ordered in a table
            out.println("<br />");
            out.println("<table border='1' cellpadding='6'>");
            out.println("<tr><th>FOOD</th><th>PRICE</th><th>QTY</th></tr>");
 
            int totalPrice = 0;
            for (String title : prodName) {
               String sqlStr = "SELECT * FROM food WHERE title ="+ "'" + title + "'";
               //System.out.println(sqlStr);  // for debugging
               ResultSet rset = stmt.executeQuery(sqlStr);
 
               // Expect only one row in ResultSet
               rset.next();
   
               String foodName = rset.getString("title");
               int price = rset.getInt("price");
 
               int qtyOrdered = Integer.parseInt(request.getParameter("cart-quantity" ));
 
               sqlStr = "INSERT INTO orders values (" + "'" 
                       + foodName + "', " + qtyOrdered + ", '" + custName + "', '"
                       + custEmail + "', '" + custPhone + "')";
               //System.out.println(sqlStr);  // for debugging
               stmt.executeUpdate(sqlStr);
 
               // Display this book ordered
               out.println("<tr>");
               out.println("<td>" + foodName + "</td>");
               out.println("<td>" + price + "</td>");
               out.println("<td>" + qtyOrdered + "</td></tr>");
               totalPrice += price * qtyOrdered;
            }
 
            out.println("<tr><td colspan='4' align='right'>Total Price: ");
            out.printf("%d", totalPrice);
            out.println(" mora</td></tr>");
            out.println("</table>");
 
            out.println("<h3>Thank you. We will contact you for more information.</h3>");
            
         }
         
         out.println("</body></html>");
      } catch (SQLException ex) {
          out.println("<p>Error: " + ex.getMessage() + "</p>");
                        out.println("<p>Check Tomcat console for details.</p>");
                        ex.printStackTrace();
       
      } 
      out.println("<p><a href='http://localhost:9999/foodshop/#'>Back to Main page</a></p>");
      out.close();

   }
 
   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      doGet(request, response);
   }
}