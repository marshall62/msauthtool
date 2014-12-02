package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.HintPath;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Skill;

import java.util.*;
import java.sql.Timestamp;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 10:11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class MockDb {

    private static int probId=0;
    private static int hintId=0;
    private static int skillId=0;
    List<Problem> problems;
    List skills;
    Hashtable hints = new Hashtable();

    public MockDb() {
        buildTestCase();
    }

    private void addHint (Hint h) {
        Object o = hints.get(h.getName());
        if (o == null)
            hints.put(h.getName(),h);
    }

    private void buildTestCase() {

        this.skills = buildSkills();
        this.problems = buildProblems();

    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    private List buildHintPaths(HintPath p, HintPath p2) {
        List l = new ArrayList();
        l.add(p);
        l.add(p2);
        return l;
    }

    private List buildHintPaths(HintPath p) {
        List l = new ArrayList();
        l.add(p);
        return l;
    }

    private HintPath buildHintPath(String[] hintNames1, Skill[] skills,
                                   boolean[] givesAnswer,
                                   boolean[] isRoot,
                                   boolean[] isDisabled, String type) {
        List hintList = new ArrayList();
        for (int i = 0; i < hintNames1.length; i++) {
            Hint h = (Hint) this.hints.get(hintNames1[i]) ;
            if (h == null) {
                h = new Hint(hintId++,hintNames1[i], skills[i], givesAnswer[i], isRoot[i], isDisabled[i]);
                this.hints.put(hintNames1[i],h);
            }
            hintList.add(h);
            addHint(h);
        }
        return new HintPath(hintList, type);
    }

    private List buildSkills() {
        List l = new ArrayList();
        l.add(new Skill(skillId++, "skill 1",true, null));
        l.add(new Skill(skillId++, "skill 2",false, null));
        l.add(new Skill(skillId++, "skill 3",false, null));
        l.add(new Skill(skillId++, "skill 4",false, null));
        l.add(new Skill(skillId++, "skill 5",true, null));
        l.add(new Skill(skillId++, "skill 6",true, null));
        return l;
    }


    private List buildProblems() {
        List l = new ArrayList();
        HintPath p1 = buildHintPath(new String[]{"h1", "h2", "h3"},
                new Skill[] {(Skill) skills.get(0), (Skill)skills.get(1), (Skill)skills.get(2)},
                new boolean[]{false, false, true},
                new boolean[]{true, false, false},
                new boolean[]{false, false, false}, "analytic");
        HintPath p2 = buildHintPath(new String[]{"h1", "h2", "h3", "h4", "h5"},
                new Skill[]{(Skill) skills.get(0),(Skill)skills.get(1),(Skill)skills.get(2),(Skill)skills.get(3),(Skill)skills.get(4)},
                new boolean[]{false, false, false, false, true},
                new boolean[]{true, false, false, false, false},
                new boolean[]{false, false, false, true, false}, "visual");
        List paths1 = buildHintPaths(p1);
        List paths2 = buildHintPaths(p1, p2);
        l.add(buildTestProblem(probId++, "prob 3", "three", "a", "dave", true, paths1,"flash3.swf","30"));
        l.add(buildTestProblem(probId++, "prob 1", "one","c", "dave", true, paths2,"flash1.swf","10"));
        l.add(buildTestProblem(probId++, "prob 2", "two", "a", "dave", false, null,"flash2.swf","20"));
        l.add(buildTestProblem(probId++, "prob 4", "four","d", "dave", false, null,"flash4.swf","40"));
        return l;
    }

    private Problem buildTestProblem(int id, String name, String nickname, String answer, String lastMod,
                                     boolean ready, List hintPaths, String flashfile,String sourceId ) {

        return new Problem(id, name, nickname,answer, lastMod, ready?"ready":"unreleased", hintPaths,flashfile,sourceId, ".5",".5",".5",".5", false,"flash", null, null);
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public List getSkills() {
        return this.skills;
    }

    public int insertProblem(Problem p) {
        probId++;
        problems.add(p);
        return probId;
    }

    public Problem getProblem (int id) {
        Iterator iterator = problems.iterator();
        while (iterator.hasNext()) {
            Problem problem = (Problem) iterator.next();
            if (problem.getId() == id)
                return problem;
        }
        return null;
    }

    public List getProblemHintPaths(int probId) {
        throw new UnsupportedOperationException();
    }

    public int insertHint(Hint hint) {
        hintId++;
        addHint(hint);
        hint.setId(hintId);
        return hintId;
    }

    public int insertSkill(Skill skill) {
       skills.add(skill);
        return skillId++;

    }

    public boolean updateHintPath(HintPath hintPath, Problem problem) {
        return true;
    }

    public void insertHintPath (HintPath hintPath, Problem problem) {
        
    }

    public Collection getAllHints() {
        return hints.values();
    }

    public boolean deleteHint(Hint hint) {
        hints.remove(hint.getName());
        return true;
    }

    public void updateSkill(Skill skill) {
    }

    public boolean deleteSkill(Skill skill) {
        return skills.remove(skill);
    }

    public List getHintsForSkill(int skillId) {
        List result = new ArrayList();
        Set s = hints.entrySet();
        Iterator itr = s.iterator();
        while (itr.hasNext()) {
            Hint hint = (Hint) itr.next();
            if (hint.getSkill() != null && hint.getSkill().getId() == skillId)
                result.add(hint);
        }
        return result;
    }

    public Hint getHint(int id) {
        return (Hint) hints.get(new Integer(id));
    }

    public Collection getProblemHints(int problemId) {
        Problem p = this.getProblem(problemId);
        Iterator paths = p.getHintPaths().iterator();
        List result = new ArrayList();
        while (paths.hasNext()) {
            HintPath path = (HintPath) paths.next();
            result.addAll(path.getHints());
        }
        return result;
    }
}
