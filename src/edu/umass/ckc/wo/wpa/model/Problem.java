package edu.umass.ckc.wo.wpa.model;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 9:51:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class Problem implements Comparable {
    public static final String STATUS_READY = "ready";
    public static final String STATUS_UNRELEASED = "unreleased";
    private int id;
    private String name;
    private String answer;
    private String lastModifier;
    private List hintPaths;
    private String nickname;
    private String flashfile;
    private String sourceId;
    private String diffLevel;
    private String avgIncorrect;
    private String avgHints;
    private String avgTime;
    private List<Topic> topics;
    private boolean hasStrategicHint;
    private String type;
    private String status;
    private String exampleId;
    private String videoURL;

    public static final String HTML5 = "html5";
    public static final String FLASH = "flash";


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Problem(int id, String name, String nickname, String answer, String lastModifier, String status,
                   List hintPaths, String flashfile, String sourceId, String diffLev, String avgIncorrect, String avgHints,
                   String avgTime, boolean hasStrategicHint, String type, String videoURL, String exampleId) {
        this.id = id;
        this.name = name;
        this.answer = answer;
        this.lastModifier = lastModifier;
        this.status = status;
        if (hintPaths == null)
            this.hintPaths = new ArrayList();
        else this.hintPaths = hintPaths;
        this.nickname =nickname;
        this.flashfile = flashfile;
        this.sourceId = sourceId;
        this.diffLevel=diffLev;
        this.avgIncorrect=avgIncorrect;
        this.avgHints=avgHints;
        this.avgTime=avgTime;
        this.topics=new ArrayList<Topic>();
        this.hasStrategicHint=hasStrategicHint;
        this.type = type;
        this.videoURL=videoURL;
        this.exampleId=exampleId;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFlashfile() {
        return flashfile;
    }

    public void setFlashfile(String flashfile) {
        this.flashfile = flashfile;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getLastModifier() {
        return lastModifier==null?"":lastModifier;
    }

    public void setLastModified(String lastModified) {
        this.lastModifier = lastModified;
    }



    public Set getAllHints () {
        Iterator itr = hintPaths.iterator();
        Set result = new TreeSet();
        while (itr.hasNext()) {
            HintPath hintPath = (HintPath) itr.next();
            List pathHints= hintPath.getHints();
            result.addAll(pathHints);
        }
        return result;
    }

    public List<HintPath> getHintPaths() {
        return hintPaths;
    }

    public void setHintPaths(List hintPaths) {
        this.hintPaths = hintPaths;
    }

    public String toStringInternal () {
        return "id:"+ this.id + " name:" + this.name + " answer:" + this.answer + " ready:" + this.isReady();
    }

    public String toString () {
        return this.id + ": " + this.name + ":" + this.nickname;
    }

    public HintPath getHintPath(int i) {
        return (HintPath) hintPaths.get(i);
    }

    public boolean isNew() {
        return this.id == -1;
    }

    public int compareTo(Object o) {
        if (((Problem) o).getName() != null && this.name != null)
            return this.name.compareTo(((Problem) o).getName());
        else return -1;
    }

    public void addHintPath(HintPath p) {
        hintPaths.add(p);
    }

    public boolean removeHintPath(HintPath path) {
        return hintPaths.remove(path);
    }

    public String getDiffLevel() {
        return diffLevel;
    }

    public void setDiffLevel(String diffLevel) {
        this.diffLevel = diffLevel;
    }

    public String getAvgIncorrect() {
        return avgIncorrect;
    }

    public void setAvgIncorrect(String avgIncorrect) {
        this.avgIncorrect = avgIncorrect;
    }

    public String getAvgHints() {
        return avgHints;
    }

    public void setAvgHints(String avgHints) {
        this.avgHints = avgHints;
    }

    public String getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(String avgTime) {
        this.avgTime = avgTime;
    }

    public void addTopic (Topic t) {
        this.topics.add(t);
    }

    public void clearTopics () {
        this.topics.clear();
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return this.topics;
    }

    public void setHasStrategicHint(boolean hasStrategicHint) {
        this.hasStrategicHint = hasStrategicHint;
    }

    public boolean hasStrategicHint () {
        return this.hasStrategicHint;

    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean isReady () {
        return status.equalsIgnoreCase("ready");
    }

    public boolean isUnreleased () {
        return status.equalsIgnoreCase("unreleased");
    }

    public void setExample(String x) {
        this.exampleId=x;
    }

    public void setVideoURL(String text) {
        this.videoURL=text;
    }

    public String getVideo() {
        return this.videoURL== null ? "" : this.videoURL;
    }

    public String getExampleId() {
        return this.exampleId == null ? "" : this.exampleId;
    }
}
