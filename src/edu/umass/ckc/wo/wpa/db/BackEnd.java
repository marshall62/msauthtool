package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Skill;
import edu.umass.ckc.wo.wpa.model.HintPath;
import edu.umass.ckc.wo.wpa.model.PrePostProblemDefn;
import edu.umass.ckc.wo.wpa.model.PrePostTest;
import edu.umass.ckc.wo.wpa.model.Topic;
import edu.umass.ckc.wo.wpa.model.SemiAbsSkill;
import edu.umass.ckc.wo.wpa.model.PretestPool;

import javax.swing.ComboBoxModel;
import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.Set;
import java.sql.SQLException;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 10:08:36 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BackEnd {

    public boolean init () ;


    /** Return a list of Problem objects
     *
     * @return a list of Problem objects
     */
    public List<Problem> loadProblems () throws Exception;

    /**
     * Return a list of Skill objects.   One for each skill in the database
     * @return
     */
//    public List loadSkills () throws SQLException ;

    /**
     * Insert the new problem and hints into the database.
     * @param p
     * @return the id of the new row
     */
    public int insertProblem (Problem p) throws SQLException;

    /**
     * Get all the hints from the database for a given problem.  Return them as a List
     * of HintPath objects
     * @param problemId of a Problem row in the database
     * @return a List of Hint objects
     */
    public List getProblemHintPaths (int problemId) throws Exception;

    public boolean deleteProblem (Problem p) throws SQLException;

    /**
     *    Update the problem and hint tables from the given problem object
     * @param p
     */
    public void updateProblem (Problem p) throws SQLException;


    /**
     *    Gets all the skills in the database
     * @return a List of Skill objects
     */
    public Collection getSkills() throws SQLException;

    /**
     * Update the given hint in the database
     * @param hint
     */
    public int updateHint(Hint hint) throws SQLException;

    /**
     * Insert the Hint into the database and return its id.
     * @param hint
     * @return the id of the new row
     */
    public int insertHint(Hint hint) throws SQLException;

    /**
     * Insert a new Skill into the database.  Needs to check if it already exists.
     * If it does return its id; else create new one and return its id.
     * @param skill
     * @return
     */
    public int insertSkill(Skill skill) throws SQLException;

    public boolean updateHintPath(Problem problem, HintPath hintPath) throws SQLException;

    /**
     *
     * @param problem
     * @param path
     * @return true if the path is built
     * @throws SQLException
     */
    public boolean insertHintPath(Problem problem, HintPath path) throws SQLException;

    public boolean deleteHintPath(Problem problem, HintPath path) throws SQLException;

    /**
     * Get all the Hints from the database.  Return them as a List of Hint objects
     * @return
     */
    public Collection getAllHints() throws SQLException;
    public Set getHintSet() throws SQLException;

    /** Delete the hint from the database.  THis means removing it from all Hint paths that refer to it.
     * 
     * @param hint
     * @return
     */
    public boolean deleteHint(Hint hint) throws SQLException;

    public boolean updateSkill(Skill skill) throws SQLException;
    public boolean deleteSkill(Skill skill) throws SQLException;
    public Problem getProblem (int id) throws Exception;

    public List getSkillHints(int skillId) throws SQLException;

    // unused
//    public Hint getHint(int id) throws SQLException;
    
    public Collection getProblemHints(int problemId) throws SQLException;

    public int insertPrePostProblem (PrePostProblemDefn p) throws SQLException;
    public int deletePrePostProblem (PrePostProblemDefn p) throws SQLException;
    public void updatePrePostProblem (int id, PrePostProblemDefn p) throws SQLException;
    public PrePostProblemDefn selectPrePostProblem (String name, int problemSet) throws SQLException ;

    public Vector<PrePostTest> getPrePostTests () throws SQLException;
    public Vector<PrePostProblemDefn> getPrePostProblems (int id)  throws SQLException;

    public int insertPrePostTest(PrePostTest test) throws SQLException;
    public int deletePrePostTest(PrePostTest test) throws SQLException;
    public void updatePrePostTest(PrePostTest test) throws SQLException;

    public List<Topic> getTopics(int problemId) throws SQLException;
    public List<Topic> getAllTopics() throws SQLException;

    public List<SemiAbsSkill> getSemiAbstractSkills() throws SQLException;

    public List<PretestPool> getPretestPools() throws SQLException;
    public PretestPool getPretestPool(int poolId) throws SQLException;

    public void setProblemSetPool(int pretestId, int poolId) throws SQLException;

    public List<Integer> getPrePostTests(int probId) throws SQLException;

    public Vector<PrePostProblemDefn> getAllPrePostProblems() throws SQLException;

    public Topic saveTopic(Topic t) throws SQLException;
}
