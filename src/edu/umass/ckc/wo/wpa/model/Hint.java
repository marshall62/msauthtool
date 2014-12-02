package edu.umass.ckc.wo.wpa.model;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 9:51:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class Hint implements Comparable {
    private String name;
    private int id;
    private Skill skill;
    private boolean givesAnswer;
    private boolean isRoot;
    private boolean isReady=true;
    private int probId;
    private String probName;
    private String type; // a hint may have type of visual,analytic, or null

    public Hint(int id, String name, Skill skill, boolean givesAnswer, boolean isRoot, String type) {
        this(id,name,skill,givesAnswer,isRoot,true,-1,type);
    }

    public Hint(int id, String name, Skill skill, boolean givesAnswer, boolean isRoot, boolean isReady) {
        this(id,name,skill,givesAnswer,isRoot,isReady,-1,null);
    }

    public Hint(int id, String name, Skill skill, boolean givesAnswer, boolean isRoot, boolean isReady, int probId,
                String type) {
        this.id = id;
        this.name = name;
        this.skill = skill;
        this.givesAnswer = givesAnswer;
        this.isRoot = isRoot;
        this.isReady = isReady;
        this.probId = probId;
        this.type = type;
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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public boolean isGivesAnswer() {
        return givesAnswer;
    }

    public void setGivesAnswer(boolean givesAnswer) {
        this.givesAnswer = givesAnswer;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        isReady = isReady;
    }

    public int getProbId() {
        return probId;
    }

    public void setProbId(int probId) {
        this.probId = probId;
    }

    public String getType() {
        return type;
    }

    public void setType (String type) {
        this.type = type;

    }

    public String toString () {
        return this.name;
    }

    public String getProbName() {
        return probName;
    }

    public void setProbName(String probName) {
        this.probName = probName;
    }

    public boolean isNew() {
        return this.id == -1;
    }

    public String toStringInternal() {
        return "name: " + name + " id: " + id + " skill: " + skill.getName() + " isRoot: " + isRoot  +
                " gives answer: " + givesAnswer + " isReady: " + isReady  ;
    }

//    public boolean equals (Object o) {
//        if (o == null) return false;
//        if (!(o instanceof Hint)) return false;
//        return (((Hint) o).getId() == this.id) ;
//    }

    public int compareTo(Object o) {
        return new String(this.getProbId() + ":" + this.getName()).compareTo(((Hint) o).getProbId()
        + ":" + ((Hint) o).getName());
    }
}
