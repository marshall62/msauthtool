package edu.umass.ckc.wo.wpa.model;

import org.jdom.Element;
import org.jdom.CDATA;

import java.util.List;
import java.util.ArrayList;


/**
 * Copyright (c) University of Massachusetts
 * Written by: David Marshall
 * Date: Jul 11, 2005
 * Time: 11:09:23 AM
 */
public class PrePostProblemDefn {
    private int id;
    private String name;
    private String descr;
    private String url;
    private int ansType;
    private String answer;
    private List<PrePostTest> problemSets;
    private String aAns;
    private String bAns;
    private String cAns;
    private String dAns;
    private String eAns;
    private String aURL;
    private String bURL;
    private String cURL;
    private String dURL;
    private String eURL;
    public static final int SHORT_ANSWER = 0;
    public static final int MULTIPLE_CHOICE = 1;
    private int numProbsInTest;   // these two variables really don't belong here but at the time we
    private int numProbsSeen;     // create the problem we know these two things and need a package for returning them from the selector
    private int preNumProbsCorrect=0;
    private int postNumProbsCorrect=0;

    public PrePostProblemDefn () {
        id = -1;
        this.problemSets = new ArrayList<PrePostTest>();
    }

    private String cleanAns (String a) {
        if (a == null) return null;
        if (a.trim().equals(""))
            return null;
        else return a;
    }

    public PrePostProblemDefn(int id, String name, String descr, String url, int ansType, String answer, 
                              List<PrePostTest> problemSets,
                          String aAns, String bAns, String cAns, String dAns, String eAns, String aURL,
                          String bURL, String cURL, String dURL, String eURL) {
        this.id = id;
        this.name = name;
        this.descr = descr;
        this.url = url;
        this.ansType = ansType;
        this.answer = cleanAns(answer);
        this.problemSets = problemSets;
        this.aAns = cleanAns(aAns);
        this.bAns = cleanAns(bAns);
        this.cAns = cleanAns(cAns);
        this.dAns = cleanAns(dAns);
        this.eAns = cleanAns(eAns);
        this.aURL = cleanAns(aURL);
        this.bURL = cleanAns(bURL);
        this.cURL = cleanAns(cURL);
        this.dURL = cleanAns(dURL);
        this.eURL = cleanAns(eURL);
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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAnsType() {
        return ansType;
    }

    public void setAnsType(int ansType) {
        this.ansType = ansType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<PrePostTest> getProblemSets() {
        return problemSets;
    }

    public void setProblemSets(List<PrePostTest> problemSets) {
        this.problemSets = problemSets;
    }

    public void addProblemSet (PrePostTest problemSet) {
        this.problemSets.add(problemSet);
    }



    public String getaAns() {
        return aAns;
    }

    public void setaAns(String aAns) {
        this.aAns = aAns;
    }

    public String getbAns() {
        return bAns;
    }

    public void setbAns(String bAns) {
        this.bAns = bAns;
    }

    public String getcAns() {
        return cAns;
    }

    public void setcAns(String cAns) {
        this.cAns = cAns;
    }

    public String getdAns() {
        return dAns;
    }

    public void setdAns(String dAns) {
        this.dAns = dAns;
    }

    public String geteAns() {
        return eAns;
    }

    public void seteAns(String eAns) {
        this.eAns = eAns;
    }

    public String getaURL() {
        return aURL;
    }

    public void setaURL(String aURL) {
        this.aURL = aURL;
    }

    public String getbURL() {
        return bURL;
    }

    public void setbURL(String bURL) {
        this.bURL = bURL;
    }

    public String getcURL() {
        return cURL;
    }


    public void setcURL(String cURL) {
        this.cURL = cURL;
    }

    public String getdURL() {
        return dURL;
    }

    public void setdURL(String dURL) {
        this.dURL = dURL;
    }

    public String geteURL() {
        return eURL;
    }

    public void seteURL(String eURL) {
        this.eURL = eURL;
    }


    private boolean isShortAnswer() {
        return this.ansType == SHORT_ANSWER;
    }
    public boolean isMultiChoice () {
        return getAnsType() == MULTIPLE_CHOICE;
    }

    public String toString () {
        return id + ":" + name;
    }

    public boolean isNew() {
        return id == -1;
    }

    public int getNumProbsInTest() {
        return numProbsInTest;
    }

    public void setNumProbsInTest(int numProbsInTest) {
        this.numProbsInTest = numProbsInTest;
    }

    public int getNumProbsSeen() {
        return numProbsSeen;
    }

    public void setNumProbsSeen(int numProbsSeen) {
        this.numProbsSeen = numProbsSeen;
    }

    public void setPrePostNumProbsCorrect(int prenumCorrect, int postnumCorrect) {
        this.preNumProbsCorrect = prenumCorrect;
        this.postNumProbsCorrect = postnumCorrect;
    }

    public int getPreNumProbsCorrect() {
        return preNumProbsCorrect;
    }

    public int getPostNumProbsCorrect() {
        return postNumProbsCorrect;
    }
}
