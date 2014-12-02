package edu.umass.ckc.wo.wpa.model;


import java.util.Iterator;
import java.util.List;

public class Topics {
    private List<Topic> topics;
    private static Topics instance=null;

    public static Topics getInstance() {
        if (instance == null)
            instance = new Topics();
        return instance;
    }

    private Topics() {}

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

}
