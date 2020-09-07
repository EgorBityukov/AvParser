import com.vk.api.sdk.actions.LongPoll;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.groups.LongPollServer;
import org.json.JSONObject;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class Bot {


    private static final String TOKEN = "d86984fb575c8111a85171c045fdfbf0737ec2f88e8b41fce607e2d303d88655f2ea8726dbf9623823486";
    private static final int GROUP_ID = 177987255;

    private static TransportClient transportClient = HttpTransportClient.getInstance();
    private static VkApiClient vk = new VkApiClient(transportClient);
    private static GroupActor actor = new GroupActor(GROUP_ID, TOKEN);


    public static void main(String[] args) throws ApiException, ClientException {
        Random random = new Random();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ParsingCars parsingCars = new ParsingCars();
                while(true) {
                    try {
                        parsingCars.parsing();
                    } catch (Exception ex) {
                        System.out.println("Problem in parsing");
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

        while (true) {

            LongPollServer longPollResponce = vk.groups().getLongPollServer(actor, GROUP_ID).execute();
            String key = longPollResponce.getKey();
            String server = longPollResponce.getServer();
            int ts = Integer.parseInt(longPollResponce.getTs());
            LongPoll longPoll = new LongPoll(vk);

            GetLongPollEventsResponse getLongPollEventsResponse = longPoll.getEvents(server, key, ts).waitTime(10).execute();

            if (!getLongPollEventsResponse.getUpdates().isEmpty()) {
                User user = getMessage(server, key, ts);

                switch (user.message.charAt(0)) {
                    case '1':
                        try {
                            if (user.userCheck(user.id)) {

                                vk.messages().send(actor).userId(user.id).message("Вы уже подписаны").randomId(random.nextInt()).execute();
                                vk.messages().send(actor).userId(user.id).message("Для отмены подписки отправьте '2'").randomId(random.nextInt()).execute();

                            } else {

                                vk.messages().send(actor).userId(user.id).message("Введите марку авто для подписки:").randomId(random.nextInt()).execute();
                                ts = getLongPollEventsResponse.getTs();

                                if (!getLongPollEventsResponse.getUpdates().isEmpty()) {

                                    user.userAdd(user.id, getMessage(server, key, ts).message);

                                }
                                vk.messages().send(actor).userId(user.id).message("Подписка оформлена. Для отмены подписки отправьте 2").randomId(random.nextInt()).execute();
                                vk.messages().send(actor).userId(user.id).stickerId(10402).randomId(random.nextInt()).execute();
                            }
                        } catch (Exception ex) {
                            vk.messages().send(actor).userId(user.id).message("Соре, проблемки, не подписалось").randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.id).stickerId(8471).randomId(random.nextInt()).execute();
                            //vk.messages().send(actor).userId(user.id).message(ex.getMessage()).randomId(random.nextInt()).execute();
                        }
                        break;
                    case '2':
                        try {
                            user.userDelete(user.id);
                            vk.messages().send(actor).userId(user.id).message("Подписка отменена. Для подписки отправьте '1'").randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.id).stickerId(8490).randomId(random.nextInt()).execute();
                        } catch (Exception ex) {
                            vk.messages().send(actor).userId(user.id).message("Соре, проблемки").randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.id).stickerId(8471).randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.id).message(ex.getMessage()).randomId(random.nextInt()).execute();
                        }
                        break;
                    default:
                        vk.messages().send(actor).userId(user.id).message("Какую-то фигню написал").randomId(random.nextInt()).execute();
                        vk.messages().send(actor).userId(user.id).stickerId(10448).randomId(random.nextInt()).execute();
                        break;
                }
            }

        }
    }


    public static User getMessage(String server, String key, int ts) throws ApiException, ClientException {
        String message=null;
        int userId=0;
        User user;

        LongPoll longPoll = new LongPoll(vk);
        GetLongPollEventsResponse getLongPollEventsResponse = longPoll.getEvents(server, key, ts).waitTime(25).execute();

        JSONObject jsonObject = new JSONObject(getLongPollEventsResponse.getUpdates().get(0).toString());
        JSONObject jsonObject1 = new JSONObject(jsonObject.get("object").toString());
        JSONObject jsonObject2 = new JSONObject(jsonObject1.get("message").toString());

        if (jsonObject2.get("text").toString().equals("")) {
            message = "0";
        } else {
            message = jsonObject2.get("text").toString();
        }
        userId = Integer.parseInt(jsonObject2.get("from_id").toString());
        System.out.println("********MESSAGE - " + message + "      USER ID - " + userId);
        user = new User(userId, message);
        return user;
    }

}

class User {
    String message;
    int id;

    private static final String TOKEN = "d86984fb575c8111a85171c045fdfbf0737ec2f88e8b41fce607e2d303d88655f2ea8726dbf9623823486";
    private static final int GROUP_ID = 177987255;
    private static TransportClient transportClient = HttpTransportClient.getInstance();
    private static VkApiClient vk = new VkApiClient(transportClient);
    private static GroupActor actor = new GroupActor(GROUP_ID, TOKEN);
    private static final String CON_URL = "jdbc:mysql://localhost/av";
    private String query;
    private static final String PASS = "password";
    private static final String USE_SSL = "useSSL";
    private static final String AUTO_RECON = "autoReconnect";
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    Properties properties = new Properties();

    User(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public void userAdd(int id, String car) throws SQLException {
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");
        Connection connection = DriverManager.getConnection(CON_URL, properties);
        Statement statement = connection.createStatement();
        query = "INSERT INTO user(id, car) VALUES('" + id + "','" + car + "')";
        statement.executeUpdate(query);
        connection.close();
    }

    public void userDelete(int id) throws SQLException {
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");
        Connection connection = DriverManager.getConnection(CON_URL, properties);
        Statement statement = connection.createStatement();
        query = "DELETE FROM user WHERE id='" + id + "'";
        statement.executeUpdate(query);
        connection.close();
    }

    public boolean userCheck(int id) throws SQLException {
        boolean check;
        properties.setProperty("user", "root");
        properties.setProperty("password", "4241");
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("serverTimezone","UTC");
        Connection connection = DriverManager.getConnection(CON_URL, properties);
        Statement statement = connection.createStatement();
        query = "SELECT COUNT(*) FROM user WHERE id = " + id + "";
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        check = rs.getInt("COUNT(*)") != 0;
        connection.close();
        return check;
    }

    public void sendCar() throws ApiException, ClientException {
        Random random = new Random();
        vk.messages().send(actor).userId(id).message(message).randomId(random.nextInt()).execute();
    }
}