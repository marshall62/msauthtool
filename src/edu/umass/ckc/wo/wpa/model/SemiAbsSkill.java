package edu.umass.ckc.wo.wpa.model;

/**
 * <p> Created by IntelliJ IDEA.
 * User: david
 * Date: Sep 18, 2008
 * Time: 8:16:19 PM
 */
public class SemiAbsSkill {
    private int id;
    private String name;

    public SemiAbsSkill(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString () {
        return name;
    }

}
