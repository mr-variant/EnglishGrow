package aksenchyk.englishgrow.models;


import java.util.ArrayList;

public class Dictionary {
    private String example;
    private String image;
    private String transcription;
    private String translation;
    private ArrayList<String> other_translations;
    private ArrayList<String> set_words;


    public Dictionary() { }


    public Dictionary(String example, String image, String transcription, String translation, ArrayList<String> other_translations, ArrayList<String> set_words) {
        this.example = example;
        this.image = image;
        this.transcription = transcription;
        this.translation = translation;
        this.other_translations = other_translations;
        this.set_words = set_words;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public ArrayList<String> getOther_translations() {
        return other_translations;
    }

    public void setOther_translations(ArrayList<String> other_translations) {
        this.other_translations = other_translations;
    }

    public ArrayList<String> getSet_words() {
        return set_words;
    }

    public void setSet_words(ArrayList<String> set_words) {
        this.set_words = set_words;
    }
}
