package aksenchyk.englishgrow.models;

public class User {
    private int experience;
    private String image;
    private String name;
    private int satiation;


    public User() {
    }

    public User(int experience, String image, String name, int satiation) {
        this.experience = experience;
        this.image = image;
        this.name = name;
        this.satiation = satiation;
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

    public int getSatiation() {
        return satiation;
    }

    public void setSatiation(int satiation) {
        this.satiation = satiation;
    }
}
