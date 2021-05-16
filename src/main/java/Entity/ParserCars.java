package Entity;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static DAO.UserDAO.users;

public class ParserCars {
    private static final String URL = "https://cars.av.by/filter?sort=4";
    String selector =  "a[class=\"listing-item__link\"]";
    String connectionURL = "jdbc:mysql://localhost/av";
    String query;
    String selectUsers;
    String brand;
    String[] subBrand;
    int userId;
    Set<String> carsLink = new HashSet<String>();

    public void parsing() throws IOException, SQLException, ApiException, ClientException {

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");


        Connection connection = DriverManager.getConnection(connectionURL, properties);
        Statement statement = connection.createStatement();

        Document doc = Jsoup.connect(URL).get();
        Elements element = doc.select(selector);
        String carNumberStr, brandStr, modelStr, yearStr;

        for (String carLink : element.eachAttr("href")) {
            carLink = "https://cars.av.by" + carLink;
            if (carsLink.add(carLink)) {
                System.out.println("** New car: " + carLink);
                Document car = Jsoup.connect(carLink).get();
                Elements carNumber = car.select("li[class=\"card__stat-item\"]");
                carNumberStr = carNumber.eachText().get(2).substring(2);
                System.out.println(carNumberStr);
                Elements model = car.select("ol[class=\"breadcrumb-list\"] > li");
                brandStr = model.eachText().get(1);
                modelStr = model.eachText().get(2);
                Elements year = car.select("div[class=\"card__params\"]");
                yearStr = year.eachText().get(0);
                System.out.println(brandStr + " " + modelStr + " " + yearStr);

                query = "INSERT INTO cars(car_number, URL, model) VALUES ('" + carNumberStr + "', '" + carLink + "', '" + modelStr + "')";
                statement.executeUpdate(query);

                brand = model.text();
                subBrand = brand.split(" ");
                System.out.println(brand + " " + subBrand[0]);
                selectUsers = "SELECT id FROM user WHERE car LIKE '" + subBrand[0] + "%' OR car LIKE '%"+ subBrand[1] +"%'";     //subBrand[0]
                ResultSet rs = statement.executeQuery(selectUsers);

                while (rs.next()) {
                    userId = rs.getInt("id");
                    System.out.println(userId);
                    VKBot.sendCarForUser(userId, carLink);
                }
            }
        }
        connection.close();
    }

}
