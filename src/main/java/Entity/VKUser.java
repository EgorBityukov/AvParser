package Entity;

import java.util.Objects;

public class VKUser {
    private int id;
    private String car;
    private String lastMessage;

    public VKUser(int id, String car, String message){
        this.id = id;
        this.car = car;
        this.lastMessage = message;
    }

    public int getId() {
        return id;
    }
    public String getCar(){
        return car;
    }
    public void setCar(String car){
        this.car = car;
    }
    public String getLastMessage(){
        return lastMessage;
    }
    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VKUser vkUser = (VKUser) o;
        return id == vkUser.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
