import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.sql.*;

public class ParsingCars {
    private static final String URL = "https://cars.av.by/search?year_from=&year_to=&currency=USD&price_from=&price_to=&sort=date&order=desc";
    String selector = "body > div.page > div.page-content > main > div > div:nth-child(3) > div.listing > div > div > div.listing-item-body > div > div.listing-item-main > div.listing-item-title > h4 > a";
    String connectionURL = "jdbc:mysql://localhost/av";
    String query;
    String selectUsers;
    String brand;
    String[] subBrand;
    int userId;
    Set<String> carsLink = new LinkedHashSet<String>();

    public void parsing() throws IOException, SQLException, ApiException, ClientException {

        System.out.println("Вызвался");
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");


        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        String modelInDB;

        Document doc = Jsoup.connect(URL).get();
        Elements element = doc.select(selector);

        for (String s : element.eachAttr("href")) {
            if (carsLink.add(s)) {
                System.out.println("** New car: " + s);
                Document mashina = Jsoup.connect(s).get();
                Elements carNumber = mashina.select("body > div.page > div.page-content > main > div >" +
                        " div.main-section > div > div.card-header > ul > li.card-about-item.card-about-item-strong > span");

                Elements model = mashina.select("body > div.page > div.page-content > main > div >" +
                        " div.main-section > div > div.card-header > h1");

                modelInDB = model.text().replaceAll("'", "");

                    query = "INSERT INTO cars(car_number, URL, model) VALUES ('" + carNumber.text() + "', '" + s + "', '" + modelInDB + "')";
                    statement.executeUpdate(query);

                brand = model.text();
                subBrand = brand.split(" ");
                System.out.println(brand + " " + subBrand[0]);
                selectUsers = "SELECT id FROM user WHERE car LIKE '" + subBrand[0] + "%' OR car LIKE '%"+ subBrand[1] +"%'";     //subBrand[0]
                ResultSet rs = statement.executeQuery(selectUsers);
                System.out.println("***********" + subBrand[1]);

                while (rs.next()) {
                    userId = rs.getInt("id");
                    System.out.println(userId);
                    User user = new User(userId, s);
                    user.sendCar();
                }
            }
        }
        connection.close();
    }

}


