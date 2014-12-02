package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Skill;
import edu.umass.ckc.wo.wpa.model.HintPath;

import java.sql.*;
import java.util.*;


/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Feb 8, 2005
 * Time: 3:11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class HintMgr {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GIVES_ANSWER = "givesAnswer";
    public static final String PROBLEM_ID = "problemId";
    public static final String SKILL_ID = "skillId";
    public static final String IS_ROOT = "is_root";

    private static Hashtable hintTable = new Hashtable();

    public static void resetCache () {
        hintTable = new Hashtable();
    }

    private static boolean isARoot(int isRoot) {
        return isRoot == 1;
    }

    private static boolean doesGiveAnswer(int givesAnswer) {
        return givesAnswer == 1;
    }
//
//    public static Hint getHint(int id) {
//        return (Hint) hintTable.get(new Integer(id));
//    }

    /**
     * Given a problem id return a list of its HintPath objects.  There will be either 1 or 2 of them
     *
     * @param conn
     * @param probId
     * @return
     * @throws Exception
     */
    public static List getHintPaths(Connection conn, int probId) throws Exception {
        List hints = getProblemHints(conn, probId);   // get all the Hints for the problem
        // get back a list of at most 2 HintPath objects; maybe 1.
        return getHintPaths(conn, hints);
    }


    /**
     * For a given problem id, get all its hints and stick them in the HintMgr's hashtable
     * <p/>
     * NB :  heres a way to get all hints with their type (note types will be null for all but the leaders in a chain)
     * select id,name,givesAnswer,skillId,is_root,value from hint LEFT  outer JOIN hintattributes ON hint.id = hintattributes.hintId  and attribute='visual' where problemId=7 order by is_root ASC
     *
     * @param conn
     * @param probId
     * @return a list of Hint objects that are associated with the problem
     * @throws SQLException
     */
    public static List getProblemHints(Connection conn, int probId) throws SQLException {
        List hints = new ArrayList();

        // the outer join brings back column "value" as NULL when there is no hintattributes=visual that joins with a given hint row
        String q = "select id,name,givesAnswer,skillId,is_root,value from hint LEFT  outer JOIN hintattributes ON hint.id = hintattributes.hintId  and attribute='visual' where problemId=? order by is_root DESC";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, probId);
        ResultSet rs = ps.executeQuery();
        // first gather up all the hints that are related to this problem, build the Hint object and put it
        // in the hashtable
        while (rs.next()) {
            int id = rs.getInt(ID);
            String name = rs.getString(NAME);
            int givesAnswer = rs.getInt(GIVES_ANSWER);
            int skillId = rs.getInt(SKILL_ID);
            int isRoot = rs.getInt(IS_ROOT);
            int isVis = rs.getInt("value");
            String type = null;
            if (!rs.wasNull())
                type = (isVis == 1) ? "visual" : "analytic";
            Skill sk = SkillMgr.getSkillFromCache(skillId);
            Hint h = new Hint(id, name, sk, doesGiveAnswer(givesAnswer), isARoot(isRoot), true, probId, type);
            hintTable.put(new Integer(id), h);
            hints.add(h);
        }
        return hints;
    }

    public static List<Hint> getProblemHintsFromCache (int probId) {
        Iterator hints = hintTable.values().iterator();
        List result = new ArrayList();
        while (hints.hasNext()) {
            Hint hint = (Hint) hints.next();
            if (hint.getProbId() == probId)
                result.add(hint);
        }
        return result;
    }


    /**
     * Given the list of hints for a problem, return all the HintPaths.
     * If there are multiple roots at the head of the list, create a path from
     * each of these.  If there is only one hint that is a root, then it may have a
     * fork later on and two paths will be returned; otherwise one path will be returned.
     *
     * @param conn
     * @param hints
     * @return a list of 1 or 2 HintPath objects
     * @throws Exception
     */
    private static List getHintPaths(Connection conn, List hints) throws Exception {


        HintPath path1 = new HintPath(new ArrayList(), "1");
        HintPath path2 = new HintPath(new ArrayList(), "2");
        boolean multiplePathsFound = false;
        if (hints.size() > 0) {
            if (hints.size() > 1 &&
                    ((Hint) hints.get(0)).isRoot() &&
                    ((Hint) hints.get(1)).isRoot()) {
                // set the type of the path from the type of the first hint in each path
                // it will either be visual or analytic
//                path1.setType(((Hint) hints.get(0)).getType());
//                path1.setType(((Hint) hints.get(1)).getType());
                multiplePathsFound = true;
                path1.addToPath((Hint) hints.get(0));
                path2.addToPath((Hint) hints.get(1));
                makePath(conn, (Hint) hints.get(0), hints, path1);
                makePath(conn, (Hint) hints.get(1), hints, path2);
            } else if (((Hint) hints.get(0)).isRoot()) {
                path1.addToPath((Hint) hints.get(0));
                path2.addToPath((Hint) hints.get(0));
                multiplePathsFound = makePaths(conn, (Hint) hints.get(0), hints, path1, path2);
            }
            else return new ArrayList<HintPath>();
            List result = new ArrayList();
            result.add(path1);
            if (multiplePathsFound)
                result.add(path2);
            return result;
        } else
            return new ArrayList<HintPath>();
    }


    // make a single path given that we know there will be no forks in it.  All
    // hints are added to the path arg.
    private static void makePath(Connection conn, Hint parent, List probHints, HintPath path) throws Exception {
        Integer[] children = getHintChildren(conn, parent.getId());
        // no children means we're done
        if (children.length == 0)
            return;
        else {
            Hint child = getHintFromCache(children[0].intValue());
            if (child == null) {
                System.out.println("Hint " + parent.getId() + " says hint " + children[0].intValue() + " is a child , but can't find it");
                return;
            }
            path.addToPath(child); // add in the child
            makePath(conn, child, probHints, path); // call on the child
        }
    }

    // Given the root h create a HintPath starting from that root and add it to the paths arg.
    // This path may have a fork in it in which case two paths get added to the paths arg.
    private static boolean makePaths(Connection conn, Hint parent, List probHints, HintPath path1, HintPath path2) throws Exception {
        Integer[] children = getHintChildren(conn, parent.getId());
        if (children.length == 0)
            return false;
        else if (children.length == 1) {
            path1.addToPath(getHintFromCache(children[0].intValue())); // add in the child
            path2.addToPath(getHintFromCache(children[0].intValue())); // add in the child
            return makePaths(conn, getHintFromCache(children[0].intValue()), probHints, path1, path2); // call on the child
        }
        // there is a fork
        else if (children.length == 2) {
            final Hint child0 = getHintFromCache(children[0].intValue());
            final Hint child1 = getHintFromCache(children[1].intValue());
            // the type of the HintPath is determined by the type of the hint that starts each path in the fork
//            path1.setType(child0.getType());
//            path2.setType(child1.getType());
            path1.addToPath(child0);
            makePath(conn, child0, probHints, path1);
            path2.addToPath(child1);
            makePath(conn, child1, probHints, path2);
            return true;
        }
        return false;

    }

    private static Integer[] getHintChildren(Connection conn, int id) throws Exception {
        String q = "select targetHint from solutionpath where sourceHint=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        List children = new ArrayList();
        while (rs.next()) {
            int child = rs.getInt("targetHint");
            children.add(new Integer(child));
        }
        return (Integer[]) children.toArray(new Integer[children.size()]);
    }
//
//    public static List getAllHints() {
//        Enumeration en = hintTable.elements();
//        List hints = new ArrayList();
//        while (en.hasMoreElements()) {
//            Hint hint = (Hint) en.nextElement();
//            hints.add(hint);
//        }
//        return hints;
//    }

    public static boolean deleteHintFromSolutionPath(Connection conn, int hintId) throws SQLException {
        String q = "delete from solutionPath where sourceHint=? OR targetHint=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, hintId);
        ps.setInt(2, hintId);
        int n = ps.executeUpdate();
        return n > 0;
    }

    public static boolean deleteHintAttributes(Connection conn, int hintId) throws SQLException {
        String q = "delete from hintattributes where hintId=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, hintId);
        int n = ps.executeUpdate();
        return n > 0;
    }

    public static boolean deleteHint(Connection conn, Hint hint) throws SQLException {
        deleteHintAttributes(conn, hint.getId());
        deleteHintFromSolutionPath(conn, hint.getId());
        hintTable.remove(new Integer(hint.getId()));
        String q = "delete from hint where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, hint.getId());
        int n = ps.executeUpdate();
        return n > 0;
    }


    /** Return all the hints that a skill is involved in.  This is useful for telling users what hints
     * must be deleted prior to being able to delete a skill (because a foreign key exists from the skill
     * to the hint)
     * @param conn
     * @param skillId
     * @return
     * @throws SQLException
     */
    public static List getHintsWithSkill(Connection conn, int skillId) throws SQLException {
        String q = "select id, name from hint where skillId = ?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, skillId);
        ResultSet rs = ps.executeQuery();
        List result = new ArrayList();
        while (rs.next()) {
            String name = rs.getString(NAME);
            int id = rs.getInt(ID);
            Hint s = new Hint(id, name, null, false, false, false);
            result.add(s);
        }
        return result;

    }

    public static int updateHint(Connection conn, Hint h) throws SQLException {
        String q = "update hint set name=?, givesAnswer=?, problemId=?, skillId=?, " +
                "is_root=? where id=?";
        final PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, h.getName());
        ps.setInt(2, h.isGivesAnswer() ? 1 : 0);
        ps.setInt(3, h.getProbId());
        Skill sk = h.getSkill();
        if (sk == null)
            ps.setNull(4, Types.INTEGER);
        else ps.setInt(4, sk.getId());
        ps.setInt(5, h.isRoot() ? 1 : 0);
        ps.setInt(6, h.getId());
        return ps.executeUpdate();
    }

    public static Collection getAllHintsFromCache () {
        return hintTable.values();
    }


    // currently not used
    public static Set getHintSet(Connection conn) throws SQLException {
        Set hints = new HashSet();
        String q = "select id,problemId,name,givesAnswer,skillId,is_root,value from hint LEFT  outer JOIN hintattributes ON hint.id = hintattributes.hintId  and attribute='visual' order by is_root DESC";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        // first gather up all the hints that are related to this problem, build the Hint object and put it
        // in the hashtable
        while (rs.next()) {
            int id = rs.getInt(ID);
            int probId = rs.getInt(PROBLEM_ID);
            String name = rs.getString(NAME);
            int givesAnswer = rs.getInt(GIVES_ANSWER);
            int skillId = rs.getInt(SKILL_ID);
            int isRoot = rs.getInt(IS_ROOT);
            int isVis = rs.getInt("value");
            String type = null;
            if (!rs.wasNull())
                type = (isVis == 1) ? "visual" : "analytic";
            Skill sk = SkillMgr.getSkillFromCache(skillId);
            Hint h = new Hint(id, name, sk, doesGiveAnswer(givesAnswer), isARoot(isRoot), true, probId, type);
            hints.add(h);
        }
        return hints;
    }

    // not used
//    public static List getAllHints(Connection conn) throws SQLException {
//        String q = "select id,problemId,name,givesAnswer,skillId,is_root,value from hint LEFT outer JOIN hintattributes ON hint.id = hintattributes.hintId and attribute='visual'";
//        final PreparedStatement ps = conn.prepareStatement(q);
//        ResultSet rs = ps.executeQuery();
//        List result = new ArrayList();
//        while (rs.next()) {
//            int probId = rs.getInt(PROBLEM_ID);
//            int id = rs.getInt(ID);
//            String name = rs.getString(NAME);
//            int givesAnswer = rs.getInt(GIVES_ANSWER);
//            int skillId = rs.getInt(SKILL_ID);
//            int isRoot = rs.getInt(IS_ROOT);
//            int isVis = rs.getInt("value");
//            String type = null;
//            if (!rs.wasNull())
//                type = (isVis == 1) ? "visual" : "analytic";
//            Skill sk = SkillMgr.getSkill(conn, skillId);
//            Hint h = new Hint(id, name, sk, doesGiveAnswer(givesAnswer), isARoot(isRoot), true, probId, type);
//            result.add(h);
//        }
//        return result;
//    }

    public static Hint getHintFromCache(int id) {
        return (Hint) hintTable.get(new Integer(id));
    }

//    public static Hint getHint(Connection conn, int id) throws SQLException {
//        String q = "select id,problemId,name,givesAnswer,skillId,is_root,value from hint LEFT outer JOIN hintattributes ON hint.id = hintattributes.hintId and attribute='visual' where hint.id=?";
//        final PreparedStatement ps = conn.prepareStatement(q);
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            int probId = rs.getInt(PROBLEM_ID);
//            String name = rs.getString(NAME);
//            int givesAnswer = rs.getInt(GIVES_ANSWER);
//            int skillId = rs.getInt(SKILL_ID);
//            int isRoot = rs.getInt(IS_ROOT);
//            int isVis = rs.getInt("value");
//            String type = null;
//            if (!rs.wasNull())
//                type = (isVis == 1) ? "visual" : "analytic";
//            Skill sk = SkillMgr.getSkill(conn, skillId);
//            Hint h = new Hint(id, name, sk, doesGiveAnswer(givesAnswer), isARoot(isRoot), true, probId, type);
//            return h;
//        }
//        return null;
//    }

    public static int insertHint(Connection conn, Hint h) throws SQLException {
        hintTable.put(new Integer(h.getId()),h);
        String q = "insert into hint " +
                "(name,givesAnswer,problemId," +
                "skillId,is_root) values (?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, h.getName());
        ps.setInt(2, h.isGivesAnswer() ? 1 : 0);
        ps.setInt(3, h.getProbId());
        ps.setInt(4, h.getSkill().getId());
        ps.setInt(5, h.isRoot() ? 1 : 0);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        h.setId(id); // alter the hint object so that its ID field is set

        return id;
    }

    public static List<Hint> getOrphanedHints(int probId, List<HintPath> paths) {
        List<Hint> probHints = getProblemHintsFromCache(probId);
        List<Hint> res = new ArrayList<Hint>();
        for (Hint h : probHints) {
            boolean inAPath =false;
            for (HintPath p: paths) {
                if (p.indexOf(h) >= 0)
                    inAPath = true;
            }
            if (!inAPath)
                res.add(h);
        }
        return res;
    }
}
