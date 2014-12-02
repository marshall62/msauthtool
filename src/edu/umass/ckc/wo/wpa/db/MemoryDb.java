package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.Problems;
import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Skill;
import edu.umass.ckc.wo.wpa.model.HintPath;
import edu.umass.ckc.wo.wpa.model.PrePostProblemDefn;
import edu.umass.ckc.wo.wpa.model.PrePostTest;
import edu.umass.ckc.wo.wpa.model.Topic;
import edu.umass.ckc.wo.wpa.model.SemiAbsSkill;
import edu.umass.ckc.wo.wpa.model.PretestPool;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 9:59:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class MemoryDb implements BackEnd {

    MockDb mdb ;

    public boolean init() {
        mdb = new MockDb();
        return true;
    }

    public List<Problem>  loadProblems() {
        return mdb.getProblems();
    }

    public List loadSkills() {
       return mdb.getSkills();
    }

    public int insertProblem(Problem p) {
        System.out.println("Inserting Problem: " + p.toStringInternal());
        return mdb.insertProblem(p);
    }

    public boolean deleteProblem(Problem p) {
        System.out.println("Deleting Problem: " + p.toStringInternal());
        return true;
    }

    public void updateProblem(Problem p) {
        System.out.println("Updating Problem: " + p.toStringInternal());
    }

    public Collection getSkills() {
        return mdb.getSkills();
    }

    public int updateHint(Hint hint) {
        System.out.println("Updating Hint: " + hint.toStringInternal());
        return 1;
    }

    public int insertHint(Hint hint) {

        System.out.println("Inserting Hint: " + hint.toStringInternal());
        return mdb.insertHint(hint);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int insertSkill(Skill skill) {
        System.out.println("inserting skill: " + skill.toInternalString());
        return mdb.insertSkill(skill);
    }

    public boolean updateHintPath(Problem problem, HintPath hintPath) {
        System.out.println("Updating hint path:" + hintPath + " in problem " + problem);
        return mdb.updateHintPath(hintPath, problem);
    }

    public boolean deleteHintPath(Problem problem, HintPath hintPath) {
        System.out.println("Deleting hint path:" + hintPath + " in problem " + problem );
        return true;
    }

    public Collection getAllHints() {
        return mdb.getAllHints();
    }

     public Set getHintSet () {
         return new TreeSet(mdb.getAllHints());
     }

    public boolean deleteHint(Hint hint) {
        System.out.println("Deleting hint:" + hint);
        return mdb.deleteHint(hint);
    }

    public boolean insertHintPath(Problem problem, HintPath hintPath) {
        System.out.println("Inserting hint path:" + hintPath + " in problem " + problem );
        return true;
    }

    public List getProblemHintPaths (int probId) {
        return mdb.getProblemHintPaths(probId);
    }

    public boolean updateSkill(Skill skill) {
        mdb.updateSkill(skill);
        return true;
    }

    public boolean deleteSkill(Skill skill) throws SQLException {
       return  mdb.deleteSkill (skill);
    }

    public Problem getProblem(int id) throws Exception {
        return mdb.getProblem(id);
    }

    public Hint getHint(int id) throws SQLException {
       return  mdb.getHint(id);
    }

    public List getSkillHints(int skillId) throws SQLException {
        return mdb.getHintsForSkill(skillId);
    }

    public Collection getProblemHints(int problemId) throws SQLException {
        return mdb.getProblemHints(problemId);
    }

    public int insertPrePostProblem (PrePostProblemDefn p) throws SQLException {
        return -1;
    }
    public int deletePrePostProblem (PrePostProblemDefn p) throws SQLException {
        return 1;
    }
    public void updatePrePostProblem (int id, PrePostProblemDefn p) throws SQLException {}
    public PrePostProblemDefn selectPrePostProblem (String name, int problemSet) throws SQLException {
         return null;
     }

    public Vector<PrePostTest> getPrePostTests () throws SQLException {
        return new Vector<PrePostTest>();
    }

    public Vector<PrePostProblemDefn> getAllPrePostProblems() throws SQLException {
        return new Vector<PrePostProblemDefn>();
    }

    public Vector<PrePostProblemDefn> getPrePostProblems(int id) throws SQLException {
        return new Vector<PrePostProblemDefn>();
    }

    public int insertPrePostTest(PrePostTest test) throws SQLException {
        return -1;
    }
    public int deletePrePostTest(PrePostTest test) throws SQLException {
        return 1;
    }

    public void updatePrePostTest(PrePostTest test) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Topic> getTopics(int problemId) throws SQLException {
        List l =new ArrayList<Topic>();
        l.add(new Topic("Fake topic A",1));
        l.add(new Topic("Fake topic B",2));
        return l;
    }

    public List<Topic> getAllTopics() throws SQLException {
        List l =new ArrayList<Topic>();
        l.add(new Topic("Fake topic A",1));
        l.add(new Topic("Fake topic B",2));
        l.add(new Topic("Fake topic C",3));
        return l;
    }

    public List<SemiAbsSkill> getSemiAbstractSkills() throws SQLException {
        List<SemiAbsSkill> s = new ArrayList<SemiAbsSkill>();
        s.add(new SemiAbsSkill(1,"semiAbsSkill-1"));
        s.add(new SemiAbsSkill(2,"semiAbsSkill-2"));
        return s;
    }

     public List<PretestPool> getPretestPools() throws SQLException {
         List<PretestPool> pools = new ArrayList<PretestPool>();
         pools.add(new PretestPool(1,"pool 1"));
         pools.add(new PretestPool(2,"pool 2"));
         return pools;
     }

    public PretestPool getPretestPool(int poolId) throws SQLException {
        return new PretestPool(1,"pool 1");
    }

    public void setProblemSetPool(int pretestId, int poolId) throws SQLException {

    }

    public List<Integer> getPrePostTests(int probId) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Topic saveTopic(Topic t) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
