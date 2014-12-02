package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.PrePostProblemDefn;
import edu.umass.ckc.wo.wpa.model.PrePostTest;
import edu.umass.ckc.wo.wpa.model.PretestPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (c) University of Massachusetts
 * Written by: David Marshall
 * Date: Jul 12, 2005
 * Time: 11:34:07 AM
 */
public class PrePostTestMgr {

    public static PrePostProblemDefn selectPrePostProblem(Connection conn, String name, int probSet) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String q = "select id from PrePostProblem ppp, prepostproblemtestmap m where name=? and ppp.id = m.probId and m.testId=?";
            ps = conn.prepareStatement(q);
            ps.setString(1, name);
            ps.setInt(2, probSet);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                PrePostProblemDefn ppp = new PrePostProblemDefn();
                ppp.setId(id);
                return ppp;
            } else
                return null;
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
        }
    }


    /**
     * Insert a PrePostProblem into the edu.umass.ckc.wo.wpa.db.
     *
     * @param conn
     * @param curProb
     * @return the id of the new problem
     * @throws SQLException
     */
    public static int insertPrePostProblem(Connection conn, PrePostProblemDefn curProb) throws SQLException {
        String q = "insert into PrePostProblem (name,description,url,answer,ansType,aChoice,bChoice,cChoice" +
                ",dChoice,eChoice,aURL,bURL,cURL,dURL,eURL) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps2 = conn.prepareStatement(q);
        try {
            ps2.setString(1, curProb.getName());
            ps2.setString(2, curProb.getDescr());
            if (curProb.getUrl() != null)
                ps2.setString(3, buildPartialURL(curProb.getUrl()));
            else
                ps2.setNull(3, Types.VARCHAR);
            ps2.setString(4, curProb.getAnswer());
            ps2.setInt(5, curProb.getAnsType());
//            ps2.setInt(6, curProb.getProblemSet());
            if (curProb.getAnsType() == PrePostProblemDefn.MULTIPLE_CHOICE) {
                ps2.setString(6, curProb.getaAns());
                ps2.setString(7, curProb.getbAns());
                ps2.setString(8, curProb.getcAns());
                ps2.setString(9, curProb.getdAns());
                // sometimes choice E is not used
                if (curProb.geteAns() != null)
                    ps2.setString(10, curProb.geteAns());
                else
                    ps2.setNull(10, Types.VARCHAR);
            } else {
                ps2.setNull(6, Types.VARCHAR);
                ps2.setNull(7, Types.VARCHAR);
                ps2.setNull(8, Types.VARCHAR);
                ps2.setNull(9, Types.VARCHAR);
                ps2.setNull(10, Types.VARCHAR);
            }

            if (curProb.getaURL() != null)
                ps2.setString(11, buildPartialURL(curProb.getaURL()));
            else
                ps2.setNull(11, Types.VARCHAR);
            if (curProb.getbURL() != null)
                ps2.setString(12, buildPartialURL(curProb.getbURL()));
            else
                ps2.setNull(12, Types.VARCHAR);
            if (curProb.getcURL() != null)
                ps2.setString(13, buildPartialURL(curProb.getcURL()));
            else
                ps2.setNull(13, Types.VARCHAR);
            if (curProb.getdURL() != null)
                ps2.setString(14, buildPartialURL(curProb.getdURL()));
            else
                ps2.setNull(14, Types.VARCHAR);
            if (curProb.geteURL() != null)
                ps2.setString(15, buildPartialURL(curProb.geteURL()));
            else
                ps2.setNull(15, Types.VARCHAR);
            ps2.executeUpdate();
            ResultSet rs = ps2.getGeneratedKeys();
            rs.next();
            int newID= rs.getInt(1);
            // place the problem in all its problem sets (pretests)
            insertProblemProblemSets(conn,newID, curProb);
            return newID;
        } finally {
            ps2.close();
        }
    }


    private static void removeProblemProblemSets (Connection conn, int probId) throws SQLException {
        String q = "delete from prepostProblemTestMap where probId=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1,probId);
        ps.executeUpdate();
        ps.close();
    }

    // For each Pretest that a problem is a member of insert a pair into the pretestProblemTestMap which
    // places the problem into the test.
    private static void insertProblemProblemSets(Connection conn, int probId, PrePostProblemDefn curProb) throws SQLException {

        PreparedStatement ps = null;
        try {
            for (PrePostTest t : curProb.getProblemSets()) {
                String q = "insert into prepostProblemTestMap (probId, testId) values (?,?)";
                ps = conn.prepareStatement(q);
                ps.setInt(1,probId);
                ps.setInt(2,t.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            if (ps != null)
                ps.close();
        }
    }

    public static void updatePrePostProblem(Connection conn, int id, PrePostProblemDefn curProb) throws SQLException {
        String q = "update PrePostProblem set name=?, description=?, url=?, answer=?, ansType=?, aChoice=?," +
                "bChoice=?, cChoice=?, dChoice=?, eChoice=?, aURL=?, bURL=?, cURL=?, dURL=?, eURL=? where id=?";
        PreparedStatement ps2 = conn.prepareStatement(q);
        try {
            ps2.setString(1, curProb.getName());
            ps2.setString(2, curProb.getDescr());
            if (curProb.getUrl() != null)
                ps2.setString(3, buildPartialURL(curProb.getUrl()));
            else
                ps2.setNull(3, Types.VARCHAR);
            ps2.setString(4, curProb.getAnswer());
            ps2.setInt(5, curProb.getAnsType());
            if (curProb.getAnsType() == PrePostProblemDefn.MULTIPLE_CHOICE) {
                ps2.setString(6, curProb.getaAns());
                ps2.setString(7, curProb.getbAns());
                ps2.setString(8, curProb.getcAns());
                ps2.setString(9, curProb.getdAns());
                // sometimes choice E is not used
                if (curProb.geteAns() != null)
                    ps2.setString(10, curProb.geteAns());
                else
                    ps2.setNull(10, Types.VARCHAR);
            } else {
                ps2.setNull(6, Types.VARCHAR);
                ps2.setNull(7, Types.VARCHAR);
                ps2.setNull(8, Types.VARCHAR);
                ps2.setNull(9, Types.VARCHAR);
                ps2.setNull(10, Types.VARCHAR);
            }
            if (curProb.getaURL() != null)
                ps2.setString(11, buildPartialURL(curProb.getaURL()));
            else
                ps2.setNull(11, Types.VARCHAR);
            if (curProb.getbURL() != null)
                ps2.setString(12, buildPartialURL(curProb.getbURL()));
            else
                ps2.setNull(12, Types.VARCHAR);
            if (curProb.getcURL() != null)
                ps2.setString(13, buildPartialURL(curProb.getcURL()));
            else
                ps2.setNull(13, Types.VARCHAR);
            if (curProb.getdURL() != null)
                ps2.setString(14, buildPartialURL(curProb.getdURL()));
            else
                ps2.setNull(14, Types.VARCHAR);
            if (curProb.geteURL() != null)
                ps2.setString(15, buildPartialURL(curProb.geteURL()));
            else
                ps2.setNull(15, Types.VARCHAR);
            ps2.setInt(16, id);
            ps2.executeUpdate();
            removeProblemProblemSets(conn,id); // remove the problems problem sets
            insertProblemProblemSets(conn,id,curProb); // add in the problem sets.
        } finally {
            ps2.close();
        }
    }

    // This deletes a problem and deletes any entries about the problem that are in the
    // prepostproblemtestmap
    public static int deletePrePostProblem(Connection conn, PrePostProblemDefn p) throws SQLException {
        removeFromProblemTestMap(conn,p.getId());
        String q = "delete from PrePostProblem where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, p.getId());
        try {
            return ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    // When we delete a problem we have to make sure that all rows that contain it in the
    // prepostproblemtestmap are deleted too.
    private static void removeFromProblemTestMap(Connection conn, int probId) throws SQLException {
        String q = "delete from prepostproblemtestmap where probId=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, probId);
        ps.executeUpdate();
    }

    /**
     * @param conn
     * @return a Vector of all PrePostTest objects
     * @throws SQLException
     */
    public static Vector<PrePostTest> getPrePostTests(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<PrePostTest> result = new Vector<PrePostTest>();
        try {
            String q = "select id,name,isActive,poolId from PrePostTest";
            ps = conn.prepareStatement(q);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int active = rs.getInt(3);
                int poolId = rs.getInt(4);
                result.add(new PrePostTest(id, name, active == 1,poolId));
            }
            return result;
        } finally {
            rs.close();
            ps.close();
        }
    }

    /**
     * Given a problemId return a PrePostProblem object or null if it doesn't exist
     * @param conn
     * @param probId
     * @return
     * @throws SQLException
     */
    public static PrePostProblemDefn getPrePostProblem (Connection conn, int probId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String q = "select name,url,description,answer,ansType,aChoice,bChoice,cChoice,dChoice,eChoice," +
                    "aURL,bURL,cURL,dURL,eURL from PrePostProblem where id=?";
            ps = conn.prepareStatement(q);
            ps.setInt(1, probId);
            rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String url = rs.getString("url");
                if (rs.wasNull())
                    url = null;
                String description = rs.getString("description");
                if (rs.wasNull())
                    description = null;
                String answer = rs.getString("answer");
                int ansType = rs.getInt("ansType");
                String aURL = null, bURL = null, cURL = null, dURL = null, eURL = null;
                String aChoice = null, bChoice = null, cChoice = null, dChoice = null, eChoice = null;
                PrePostProblemDefn p;
                if (ansType == PrePostProblemDefn.SHORT_ANSWER) {
                    ;
                }
                else {
                    aChoice = rs.getString("aChoice");
                    if (rs.wasNull())
                        aChoice = null;
                    bChoice = rs.getString("bChoice");
                    if (rs.wasNull())
                        bChoice = null;
                    cChoice = rs.getString("cChoice");
                    if (rs.wasNull())
                        cChoice = null;
                    dChoice = rs.getString("dChoice");
                    if (rs.wasNull())
                        dChoice = null;
                    eChoice = rs.getString("eChoice");
                    if (rs.wasNull())
                        eChoice = null;
                    aURL = rs.getString("aURL");
                    if (rs.wasNull())
                        aURL = null;
                    bURL = rs.getString("bURL");
                    if (rs.wasNull())
                        bURL = null;
                    cURL = rs.getString("cURL");
                    if (rs.wasNull())
                        cURL = null;
                    dURL = rs.getString("dURL");
                    if (rs.wasNull())
                        dURL = null;
                    eURL = rs.getString("eURL");
                    if (rs.wasNull())
                        eURL = null;

                }
            List<PrePostTest> inPretests = getProblemProblemSets(conn,probId);
            return new PrePostProblemDefn(probId, name, description, url, ansType, answer, inPretests, aChoice, bChoice, cChoice,
                        dChoice, eChoice, aURL, bURL, cURL, dURL, eURL);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
            ps.close();
        }
        return null;
    }

    // GIven a problem ID, return PrePostTest objects for each pretest the problem is a part of.
    private static List<PrePostTest> getProblemProblemSets(Connection conn, int probId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PrePostTest> tests = new ArrayList<PrePostTest>();
        try {
            String q = "select pt.id,pt.name,pt.poolID from preposttest pt, prepostProblemTestMap m where m.probId=? and m.testId=pt.id ";
            ps = conn.prepareStatement(q);
            ps.setInt(1, probId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int poolId = rs.getInt("poolID");
                tests.add(new PrePostTest(id,name,true,poolId));
            }
            return tests;
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();

        }
    }

    public static Vector<PrePostProblemDefn> getAllPrePostProblems(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<PrePostProblemDefn> result = new Vector<PrePostProblemDefn>();
        try {
            String q = "select id,name,url,description,answer,ansType,aChoice,bChoice,cChoice,dChoice,eChoice," +
                    "aURL,bURL,cURL,dURL,eURL from PrePostProblem";
            ps = conn.prepareStatement(q);
            rs = ps.executeQuery();
            collectPrePostProblems(conn, rs, result);
            return result;
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
        }
    }

    /**
     * @param conn
     * @param testId
     * @return a Vector of PrePostTestProblem objects
     * @throws SQLException
     */
    public static Vector<PrePostProblemDefn> getPrePostProblems(Connection conn, int testId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<PrePostProblemDefn> result = new Vector<PrePostProblemDefn>();
        try {
            String q = "select id,name,url,description,answer,ansType,aChoice,bChoice,cChoice,dChoice,eChoice," +
                    "aURL,bURL,cURL,dURL,eURL from PrePostProblem p, PrepostProblemTestMap m where m.testId=? and m.probId=p.id";
            ps = conn.prepareStatement(q);
            ps.setInt(1, testId);
            rs = ps.executeQuery();
            collectPrePostProblems(conn, rs, result);
            return result;
        } finally {
            if (rs != null)
                rs.close();
            if (ps != null)
            ps.close();
        }
    }

    private static void collectPrePostProblems(Connection conn, ResultSet rs, Vector<PrePostProblemDefn> result) throws SQLException {
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String url = rs.getString(3);
            if (rs.wasNull())
                url = null;
            String description = rs.getString(4);
            if (rs.wasNull())
                description = null;
            String answer = rs.getString(5);
            int ansType = rs.getInt(6);
            String aURL = null, bURL = null, cURL = null, dURL = null, eURL = null;
            String aChoice = null, bChoice = null, cChoice = null, dChoice = null, eChoice = null;
            PrePostProblemDefn p;
            if (ansType == PrePostProblemDefn.SHORT_ANSWER) {
                ;
            } else {
                aChoice = rs.getString(7);
                if (rs.wasNull())
                    aChoice = null;
                bChoice = rs.getString(8);
                if (rs.wasNull())
                    bChoice = null;
                cChoice = rs.getString(9);
                if (rs.wasNull())
                    cChoice = null;
                dChoice = rs.getString(10);
                if (rs.wasNull())
                    dChoice = null;
                eChoice = rs.getString(11);
                if (rs.wasNull())
                    eChoice = null;
                aURL = rs.getString(12);
                if (rs.wasNull())
                    aURL = null;
                bURL = rs.getString(13);
                if (rs.wasNull())
                    bURL = null;
                cURL = rs.getString(14);
                if (rs.wasNull())
                    cURL = null;
                dURL = rs.getString(15);
                if (rs.wasNull())
                    dURL = null;
                eURL = rs.getString(16);
                if (rs.wasNull())
                    eURL = null;
            }
            // get all the pretests that this problem is a part of.
            List<PrePostTest> tests = getProblemProblemSets(conn,id);
            result.add(new PrePostProblemDefn(id, name, description, url, ansType, answer,
                    tests,
                    aChoice, bChoice, cChoice, dChoice, eChoice,
                    aURL, bURL, cURL, dURL, eURL));
        }
    }

    public static int insertPrePostTest(Connection conn, PrePostTest test) throws SQLException {

        String q = "insert into PrePostTest (name, isActive) values (?,?)";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, test.getName());
        ps.setInt(2, test.isActive() ? 1 : 0);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        ps.close();
        rs.close();
        return id;
    }

    public static int deletePrePostTest(Connection conn, PrePostTest test) throws SQLException {
        // When a test is deleted we must delete the mapping from it to all its problems.
        // Note:  The problems now remain in the database and may no longer be associated with any
        // test, but this is safer than deleting the problems as part of deleting a test.

        //  Question:  How to access these orphaned problems from withing the GUI.   They may
        // no longer be in any test so there is no way to edit them.   Perhaps we need a global list of pretest problems?
        deletePretestFromMap(conn,test.getId());
        String q = "delete from PrePostTest where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        try {
            ps.setInt(1, test.getId());
            return ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static int deletePretestFromMap(Connection conn, int testId) throws SQLException {
        String q = "delete from prepostproblemtestmap where testId=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(q);
            ps.setInt(1,testId);
            int n = ps.executeUpdate();
            return n;
        } finally {
            if (ps != null)
                ps.close();
        }
    }

    public static void updatePrePostTest(Connection conn, PrePostTest test) throws SQLException {
        PreparedStatement ps = null;
        try {
            String q = "update PrePostTest set name=?, isActive=? where id=?";
            ps = conn.prepareStatement(q);
            ps.setString(1, test.getName());
            ps.setInt(2, test.isActive() ? 1 : 0);
            ps.setInt(3, test.getId());
            int n = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ps.close();
        }

    }

    public static PretestPool getPool(Connection conn, int poolId) throws SQLException {
        String q = "select id,description from prepostpool where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1,poolId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt(1);
            String descr = rs.getString(2);
            return new PretestPool(id,descr);
        }
        return null;
    }


    public static List<PretestPool> getPools(Connection conn) throws SQLException {
        List<PretestPool> pools = new ArrayList<PretestPool>();
        String q = "select id,description from prepostpool where isActive=1";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt(1);
            String descr = rs.getString(2);
            pools.add(new PretestPool(id,descr));
        }
        return pools;
    }


    public static String buildPartialURL(String imgfilename) {
        if (imgfilename != null) {
            if (imgfilename.startsWith("images/"))
                return imgfilename;
            else
                return "images/" + imgfilename;
        } else
            return null;
    }

    public static void setProblemSetPool(Connection conn, int pretestId, int poolId) throws SQLException {
        String q = "update preposttest set poolID=? where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1,poolId);
        ps.setInt(2,pretestId);
        ps.executeUpdate();
    }

    public static List<Integer> getPrePostTests(Connection conn, int probId) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            List<Integer> testIds = new ArrayList<Integer>();
            String q = "select testId from prepostproblemtestmap where probId=?";
            stmt = conn.prepareStatement(q);
            stmt.setInt(1,probId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int c= rs.getInt(1);
                testIds.add(c);
            }
            return testIds;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }


}
