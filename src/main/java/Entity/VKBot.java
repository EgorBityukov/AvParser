package Entity;

import DAO.UserDAO;
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

import java.sql.SQLException;
import java.util.Random;
import java.util.Set;

import static DAO.UserDAO.users;

public class VKBot {
    private static final String TOKEN = "d86984fb575c8111a85171c045fdfbf0737ec2f88e8b41fce607e2d303d88655f2ea8726dbf9623823486";
    private static final int GROUP_ID = 177987255;

    private static TransportClient transportClient = HttpTransportClient.getInstance();
    private static VkApiClient vk = new VkApiClient(transportClient);
    private static GroupActor actor = new GroupActor(GROUP_ID, TOKEN);
    private static UserDAO userDAO;

    private Random random = new Random();

    public VKBot() throws ClientException, ApiException, SQLException {
        userDAO = new UserDAO();
        Listen();
    }

    private void Listen() throws ClientException, ApiException, SQLException {
        while (true) {

            LongPollServer longPollResponse = vk.groups().getLongPollServer(actor, GROUP_ID).execute();
            String key = longPollResponse.getKey();
            String server = longPollResponse.getServer();
            int ts = Integer.parseInt(longPollResponse.getTs());
            LongPoll longPoll = new LongPoll(vk);

            GetLongPollEventsResponse getLongPollEventsResponse = longPoll.getEvents(server, key, ts).waitTime(10).execute();

            if (!getLongPollEventsResponse.getUpdates().isEmpty()) {
                VKUser user = getMessage(server, key, ts);

                switch (user.getLastMessage().charAt(0)) {
                    case '1':
                        try {
                            if (userCheckSubscribe(user)) {

                                vk.messages().send(actor).userId(user.getId()).message("Вы уже подписаны").randomId(random.nextInt()).execute();
                                vk.messages().send(actor).userId(user.getId()).message("Для отмены подписки отправьте '2'").randomId(random.nextInt()).execute();

                            } else {

                                vk.messages().send(actor).userId(user.getId()).message("Введите марку авто для подписки:").randomId(random.nextInt()).execute();
                                ts = getLongPollEventsResponse.getTs();

                                if (!getLongPollEventsResponse.getUpdates().isEmpty()) {
                                    userDAO.setCar(user.getId(), user.getLastMessage());
                                }

                                vk.messages().send(actor).userId(user.getId()).message("Подписка оформлена. Для отмены подписки отправьте 2").randomId(random.nextInt()).execute();
                                vk.messages().send(actor).userId(user.getId()).stickerId(10402).randomId(random.nextInt()).execute();
                            }
                        } catch (Exception ex) {
                            vk.messages().send(actor).userId(user.getId()).message("Соре, проблемки, не подписалось").randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.getId()).stickerId(8471).randomId(random.nextInt()).execute();
                            //vk.messages().send(actor).userId(user.getId()).message(ex.getMessage()).randomId(random.nextInt()).execute();
                        }
                        break;
                    case '2':
                        try {
                            userDAO.delete(user);
                            vk.messages().send(actor).userId(user.getId()).message("Подписка отменена. Для подписки отправьте '1'").randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.getId()).stickerId(8490).randomId(random.nextInt()).execute();
                        } catch (Exception ex) {
                            vk.messages().send(actor).userId(user.getId()).message("Соре, проблемки").randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.getId()).stickerId(8471).randomId(random.nextInt()).execute();
                            vk.messages().send(actor).userId(user.getId()).message(ex.getMessage()).randomId(random.nextInt()).execute();
                        }
                        break;
                    default:
                        vk.messages().send(actor).userId(user.getId()).message("Какую-то фигню написал").randomId(random.nextInt()).execute();
                        vk.messages().send(actor).userId(user.getId()).stickerId(10448).randomId(random.nextInt()).execute();
                        break;
                }
            }

        }
    }

    public static VKUser getMessage(String server, String key, int ts) throws ApiException, ClientException, SQLException {
        String message=null;
        int userId=0;
        VKUser user;

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

        user = new VKUser(userId, null, message);

        if(!users.contains(user)){
            users.add(user);
        } else{
            userDAO.setLastMessage(user.getId(), message);
        }

        return user;
    }

    public boolean userCheckSubscribe(VKUser user){
        boolean subscribe = false;
        if(users.contains(user)){
            subscribe = true;
        }
        return subscribe;
    }
}
