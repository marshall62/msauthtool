package edu.umass.ckc.wo.wpa.model;

/**
 * <p> Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 18, 2008
 * Time: 1:10:08 PM
 */
public class Topic implements Comparable {
    private String name;
    private int id;
    private String intro;
    private String summary;
    private String description;
    private String type;
    private boolean isActive;
    private boolean CCMapped;

    public Topic (String name, int id) {
        this.name = name;
        this.id = id;
        this.intro = "";
        this.description = "";
        this.type = "swf";
        this.isActive = true;
    }

    public Topic(String name, int id, String intro, String description, String summary, String type, boolean isActive, boolean isCCMapped) {
        this.name = name;
        this.id = id;
        this.intro = intro;
        this.description = description;
        this.summary = summary;
        this.type = type;
        this.isActive = isActive;
        this.CCMapped = isCCMapped;
    }

    public int compareTo(Object o) {
        return this.getDescription().compareToIgnoreCase(((Topic)o).getDescription());
    }

    public String toString () {
        return this.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDescription() {
        return description;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return this.isActive;
    }


    public boolean isCCMapped() {
        return CCMapped;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public void setIsCCMapped(boolean CCMapped) {
        this.CCMapped = CCMapped;
    }
}
