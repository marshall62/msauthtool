package edu.umass.ckc.wo.wpa.model;

/**
 * Copyright (c) University of Massachusetts
 * Written by: David Marshall
 * Date: Jul 12, 2005
 * Time: 4:04:51 PM
 */
public class PrePostTest {
    int id;
    String name;
    boolean active;
    int poolID;

    public PrePostTest () {
        id=-1;
        active=true;
    }

    public PrePostTest(int id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public PrePostTest(int id, String name, boolean active, int poolId) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.poolID = poolId;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString () {
        return id + ":" + name;
    }

    public boolean isNew() {
        return id == -1;
    }

    public int getPoolID() {
        return poolID;
    }

    public void setPoolID(int poolID) {
        this.poolID = poolID;
    }
}
