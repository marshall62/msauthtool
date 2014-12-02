package edu.umass.ckc.wo.wpa.model;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 9:51:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Skill implements Comparable {
    private int id;
    private String name;
    private boolean isVisual;
    private SemiAbsSkill semiAbstractSkill;

    public Skill(int id, String name, boolean isVisual, SemiAbsSkill semiAbsSkill) {
        this.name = name;
        this.id = id;
        this.isVisual = isVisual;
        this.semiAbstractSkill=semiAbsSkill;
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

    public int compareTo(Object o) {
            return name.compareTo(((Skill) o).getName());
    }

    public String toString() {
        return name;
    }

    public boolean isVisual() {
        return isVisual;
    }

    public void setVisual(boolean visual) {
        isVisual = visual;
    }

    public String toInternalString() {
        return "name: " + name + "id: " + id;
    }

    public boolean isNew() {
        return id == -1;

    }
//
//    public boolean equals (Object o) {
//        if (o == null) return false;
//        return ((Skill) o).getId() == this.id;
//    }

    public void setSemiAbstractSkill(SemiAbsSkill semiAbsSkill) {
        this.semiAbstractSkill = semiAbsSkill;
    }

    public SemiAbsSkill getSemiAbstractSkill () {
        return this.semiAbstractSkill;
    }
}
