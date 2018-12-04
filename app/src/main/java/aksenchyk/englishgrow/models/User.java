package aksenchyk.englishgrow.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private int experience;
    private String image;
    private String name;
    private int dayExp;
    private @ServerTimestamp Date dayExpData;


    public User() {
    }

    public User(int experience, String image, String name, int dayExp, Date dayExpData) {
        this.experience = experience;
        this.image = image;
        this.name = name;
        this.dayExp = dayExp;
        this.dayExpData = dayExpData;
    }


    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDayExp() {
        return dayExp;
    }

    public void setDayExp(int dayExp) {
        this.dayExp = dayExp;
    }

    public Date getDayExpData() {
        return dayExpData;
    }

    public void setDayExpData(Date dayExpData) {
        this.dayExpData = dayExpData;
    }
}
