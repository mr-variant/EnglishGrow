package aksenchyk.englishgrow.models;

import java.util.ArrayList;

public class GrammarUnit {

    private String text;
    private String translate;
    private ArrayList<String> answers;


    public GrammarUnit() { }


    public GrammarUnit(String text, String translate, ArrayList<String> answers) {
        this.text = text;
        this.translate = translate;
        this.answers = answers;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

}
