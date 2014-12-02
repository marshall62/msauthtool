package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.HintPath;
import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Topic;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.sql.*;
import java.util.Set;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Feb 8, 2005
 * Time: 11:50:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProblemMgr {



    public static final String NAME = "name";
    public static final String NNAME = "nickname";
    public static final String ANIMATION_RESOURCE = "animationResource";
    public static final String ANSWER = "answer";
    public static final String LAST_MODIFIER = "lastModifier";
    public static final String SOURCE_ID = "sourceId";
    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String VIDEO = "video";
    public static final String EXAMPLE = "example";

    private static Hashtable<Integer,Problem> allProblems = new Hashtable<Integer,Problem>();
    private static final String DIFF_LEV = "diff_level";
    private static final String AVG_INC = "avgincorrect";
    private static final String AVG_HINTS = "avghints";
    private static final String AVG_TIME = "avgsecsprob";
    private static final String STRAT_HINT_EXISTS = "strategicHintExists";


    public static void resetCache() {
        allProblems = new Hashtable<Integer, Problem>();
    }

    /** Builds a list of Problem objects.  The Problem objects are fully instantiated with their
     * hint paths.
     * @return
     */
//    public static List loadAllProblems(Connection conn) throws Exception {
//        String q = "select * from Problem";
//        PreparedStatement s = conn.prepareStatement(q);
//        ResultSet rs = s.executeQuery();
//        while (rs.next()) {
//            int id = rs.getInt(ID);
//            String name = rs.getString(NAME);
//            String nname = rs.getString(NNAME);
//            String anResource = rs.getString(ANIMATION_RESOURCE);
//            String answer = rs.getString(ANSWER);
//            String lastModifier = rs.getString(LAST_MODIFIER);
//            int sourceId = rs.getInt(SOURCE_ID);
//            String status = rs.getString(STATUS);
//            List paths = HintMgr.getHintPaths(conn,id);
//            Problem p = new Problem(id,name,nname,answer,lastModifier,isStatusReady(status),
//                    paths,anResource,Integer.toString(sourceId));
//            problems.add(p);
//        }
//        return problems;
//    }

    public static List<Problem> getProblems(Connection conn) throws Exception {
        String q = "select p.id, p.name, p.nickname,p.animationResource,p.answer,p.lastModifier,p.sourceId,p.status,p.video, p.example,o.diff_level,o.avgincorrect,o.avghints,o.avgsecsprob,p.strategicHintExists,p.type from Problem p, overallprobdifficulty o where o.problemId = p.id";
        PreparedStatement s = conn.prepareStatement(q);
        ResultSet rs = s.executeQuery();
        List<Problem> problems = new ArrayList<Problem>();
        while (rs.next()) {
            int id = rs.getInt(ID);
            String name = rs.getString(NAME);
            String nname = rs.getString(NNAME);
            String anResource = rs.getString(ANIMATION_RESOURCE);
            String answer = rs.getString(ANSWER);
            String lastModifier = rs.getString(LAST_MODIFIER);
            int sourceId = rs.getInt(SOURCE_ID);
            String status = rs.getString(STATUS);
            double diffLev = rs.getDouble(DIFF_LEV);
            double avgInc = rs.getDouble(AVG_INC);
            double avgHints = rs.getDouble(AVG_HINTS);
            double avgTime = rs.getDouble(AVG_TIME);
            boolean stratHint = rs.getBoolean(STRAT_HINT_EXISTS);
            String type = rs.getString(TYPE);
            String vid = rs.getString(VIDEO);
            String ex = rs.getString(EXAMPLE);
            // NB this adds the problems hints to the HintMgr's hint cache
            List<HintPath> paths = HintMgr.getHintPaths(conn, id);
            Problem p = new Problem(id,name,nname,answer,lastModifier,status,
                    paths,anResource,Integer.toString(sourceId), Double.toString(diffLev), Double.toString(avgInc),
                    Double.toString(avgHints), Double.toString(avgTime), stratHint,type, vid, ex);
            p.setTopics(getProblemTopics(conn,id));
            allProblems.put(new Integer(id),p);
            problems.add(p);
        }
        return problems;
    }

    public static boolean isStatusReady (String status) {
        return true;
    }

    public static Problem getProblem (int id) {
        return (Problem) allProblems.get(new Integer(id));
    }
//
//    public static Problem getProblem(Connection conn, int problemId) throws Exception {
//        String q= "select * from Problem where ID=?";
//        PreparedStatement s = conn.prepareStatement(q);
//        s.setInt(1,problemId);
//        ResultSet rs = s.executeQuery();
//        if (rs.next()) {
//            String name = rs.getString(NAME);
//            String nname = rs.getString(NNAME);
//            String anResource = rs.getString(ANIMATION_RESOURCE);
//            String answer = rs.getString(ANSWER);
//            String lastModifier = rs.getString(LAST_MODIFIER);
//            int sourceId = rs.getInt(SOURCE_ID);
//            String status = rs.getString(STATUS);
//            List paths = HintMgr.getHintPaths(conn,problemId);
//            Problem p = new Problem(problemId,name,nname,answer,lastModifier,isStatusReady(status),
//                    paths,anResource,Integer.toString(sourceId));
//            return p;
//        }
//        return null;
//    }

    public static boolean deleteProblem(Connection conn, Problem p) throws SQLException {
        allProblems.remove(new Integer(p.getId()));
        deleteProblemHints(conn,p);
        deleteProblemTopicMappings(conn, p);
        String q = "delete from problem where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, p.getId());
        return ps.executeUpdate() > 0;

    }

    private static int deleteProblemTopicMappings(Connection conn, Problem p) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String q = "delete from probprobgroup where probId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1, p.getId());
            return stmt.executeUpdate();
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    private static void deleteProblemHints(Connection conn, Problem p) throws SQLException {
        Set<Hint> hints = p.getAllHints();
        for (Hint h: hints) {
            HintMgr.deleteHint(conn,h);
        }
    }

    public static void insertDiffStats (Connection conn, Problem p)  throws SQLException {
        String q = "insert into overallprobdifficulty " +
                "(problemId,diff_level,avgincorrect,avghints,avgsecsprob)" +
                " values (?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1,p.getId());
        ps.setDouble(2,Double.parseDouble(p.getDiffLevel()));
        ps.setDouble(3,Double.parseDouble(p.getAvgIncorrect()));
        ps.setDouble(4,Double.parseDouble(p.getAvgHints()));
        ps.setDouble(5,Double.parseDouble(p.getAvgTime()));
        ps.executeUpdate();
    }

    public static void updateDiffStats  (Connection conn, Problem p)  throws SQLException {
        String q = "update overallprobdifficulty " +
                "set diff_level=?,avgincorrect=?,avghints=?,avgsecsprob=? where problemId=?" ;
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(5,p.getId());
        ps.setDouble(1,Double.parseDouble(p.getDiffLevel()));
        ps.setDouble(2,Double.parseDouble(p.getAvgIncorrect()));
        ps.setDouble(3,Double.parseDouble(p.getAvgHints()));
        ps.setDouble(4,Double.parseDouble(p.getAvgTime()));
        ps.executeUpdate();
    }

    public static int insertProblem(Connection conn, Problem p) throws SQLException {

        String q = "insert into problem " +
                "(name,nickname,animationResource," +
                "answer,sourceId,creator,lastModifier," +
                "status, strategicHintExists,type, video,example) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, p.getName());
        ps.setString(2, p.getNickname());
        ps.setString(3, p.getFlashfile());
        ps.setString(4, p.getAnswer());
        ps.setString(5, p.getSourceId());
        ps.setString(6, p.getLastModifier());
        ps.setString(7, p.getLastModifier());
        ps.setString(8, p.isReady() ? "ready" : "disabled");
        ps.setBoolean(9,p.hasStrategicHint());
        ps.setString(10,p.getType());
        if (p.getVideo()==null || p.getVideo().equals(""))
            ps.setNull(11,Types.VARCHAR);
        else ps.setString(11,p.getVideo());
        if (p.getExampleId() == null || p.getExampleId().equalsIgnoreCase(""))
            ps.setNull(12,Types.INTEGER);
        else ps.setInt(12, Integer.parseInt(p.getExampleId()));
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        p.setId(id);
        allProblems.put(new Integer(p.getId()),p);    // used to be on first line of this method which seemed wrong
        insertDiffStats(conn,p);
        insertProbTopics(conn,p);
        return id;
    }

    private static void insertProbTopics(Connection conn, Problem p) throws SQLException {
        String q = "insert into probprobgroup (probId,pGroupId) values (?,?)";
        for (Topic t: p.getTopics()) {
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1,p.getId());
            ps.setInt(2,t.getId());
            ps.executeUpdate();
            ps.close();
        }

    }

    private static void removeProbTopics (Connection conn, Problem p) throws SQLException {
        String q = "delete from probprobgroup where probId=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1,p.getId());
        ps.executeUpdate();
    }

    private static void updateProblemTopics (Connection conn, Problem p) throws SQLException {
        removeProbTopics(conn,p);
        insertProbTopics(conn,p);
    }

    public static int updateProblem(Connection conn, Problem p) throws SQLException {

        updateDiffStats(conn,p);
        updateProblemTopics(conn,p);
        String q = "update problem " +
                "set name=?,nickname=?,animationResource=?," +
                "answer=?,sourceId=?,lastModifier=?," +
                "status=?, strategicHintExists=?, type=?, example=?, video=? where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, p.getName());
        ps.setString(2, p.getNickname());
        ps.setString(3, p.getFlashfile());
        ps.setString(4, p.getAnswer());
        ps.setString(5, p.getSourceId());
        ps.setString(6, p.getLastModifier());
        ps.setString(7, p.isReady() ? "ready" : "disabled");
        ps.setBoolean(8,p.hasStrategicHint());
        ps.setString(9, p.getType());
        if (p.getExampleId() == null || p.getExampleId().equalsIgnoreCase(""))
            ps.setNull(10,Types.INTEGER);
        else ps.setInt(10, Integer.parseInt(p.getExampleId()));
        if (p.getVideo() == null || p.getVideo().equalsIgnoreCase(""))
            ps.setNull(11,Types.VARCHAR);
        else ps.setString(11, p.getVideo());
        ps.setInt(12, p.getId());

        return ps.executeUpdate();
    }

    public static List<Topic> getProblemTopics (Connection conn, int probId) throws SQLException {
        String q = "select t.description, t.id from probprobgroup m, problemgroup t where m.probId=? and m.pgroupId=t.id";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1,probId);
        ResultSet rs=ps.executeQuery();
        List<Topic> l = new ArrayList<Topic>();
        while (rs.next())
            l.add(new Topic(rs.getString(1),rs.getInt(2)));
        return l;
    }

    public static List<Topic> getAllTopics(Connection conn) throws SQLException {
        String q = "select id,description,intro,summary,type,active,isCCMapped from problemgroup";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        List<Topic> l = new ArrayList<Topic>();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String intro = rs.getString(3);
            String summary = rs.getString(4);
            String ty = rs.getString(5);
            boolean isActive = rs.getBoolean(6);
            boolean isCCMapped = rs.getBoolean(7);
            l.add(new Topic(name,id,intro,"",summary,ty,isActive, true));
        }
        return l;
    }



}


