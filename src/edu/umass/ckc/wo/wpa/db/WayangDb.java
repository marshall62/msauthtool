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

import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;
import java.sql.*;

import edu.umass.ckc.wo.wpa.gui.Settings;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Feb 8, 2005
 * Time: 10:55:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class WayangDb implements BackEnd {
    Connection conn;
    public static String  dbPrefix = "jdbc:mysql";
//    String dbHost = "localhost";
//    public static String dbHost = Settings.dbHost;

//    public static String dbSource = "wayangoutpostdb";
    public static String dbDriver = "com.mysql.jdbc.Driver";
//    public static String dbUser = "WayangServer";
    public static String dbPassword = "jupiter";

    public static String dbHost = "cadmium.cs.umass.edu";
    public static String dbSource = "wayangoutpostdb";
    public static String dbUser = "WayangServer";
    private static Logger logger = Logger.getLogger(WayangDb.class.getName());

    public static final int duplicateRowError = 2627;
    public static final int keyConstraintViolation = 1062;



    public static void setDbParams (String host, String schema, String user, String password) {
        dbHost = host;
        dbSource = schema;
        dbUser = user;
        dbPassword = password;
    }

    public static void setHost (String host) {
        dbHost = host;
    }
    /**
     * Establish a connection to the database.  The connection is placed in a member variable which is
     * used by the other methods in this class
     */
    public boolean init() {
        loadDbDriver();
        try {
            this.conn = getConnection();
            loadTables();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }

        return true;
    }

    public static void loadDbDriver() {
        try {
            Driver d = (Driver) Class.forName(dbDriver).newInstance(); // MySql
            System.out.println(d);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static Connection getConnection() throws SQLException {
        String url;
        if (dbPrefix.equals("jdbc:mysql"))
            url = dbPrefix + "://" + dbHost + "/" + dbSource + "?user=" + dbUser + "&password=" + dbPassword; // preferred by MySQL
        else // JDBCODBCBridge
            url = dbPrefix + ":" + dbSource;
//        url = "jdbc:mysql://localhost:3306/test";
//        url = "jdbc:mysql://localhost/rashidb"; // this works
        try {
            logger.info("connecting to edu.umass.ckc.wo.wpa.db on url " + url);
            return DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
    }

    public void loadTables() throws Exception {
        //NB order of these loads matters
//        ProblemMgr.loadAllProblems(conn);
    }

    public List<Problem> loadProblems() throws Exception {
        SkillMgr.getAllSkills(conn); // load skills into cache
        return ProblemMgr.getProblems(conn);
    }

//    public List loadSkills() throws SQLException {
//        return SkillMgr.getSkills(conn);
//    }

    /**
     * insert the problem into the problem table
     *
     * @param p
     * @return the id of the new row
     */
    public int insertProblem(Problem p) throws SQLException {
        int pid= ProblemMgr.insertProblem(conn,p);
        return pid;
    }

    public List getProblemHintPaths(int problemId) throws Exception {
        return ProblemMgr.getProblem(problemId).getHintPaths();
    }

    public boolean deleteProblem(Problem p) throws SQLException {
        return ProblemMgr.deleteProblem(conn,p);
    }

    public Problem getProblem (int id) throws Exception {
        return ProblemMgr.getProblem(id);
    }

    public void updateProblem(Problem p) throws SQLException {
        ProblemMgr.updateProblem(conn,p);
    }

    public Collection getSkills() throws SQLException {
        return SkillMgr.getAllSkills(conn);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int updateHint(Hint h) throws SQLException {
        return HintMgr.updateHint(conn,h);
    }

    public int insertHint(Hint h) throws SQLException {
        return HintMgr.insertHint(conn,h);
    }

    public int insertSkill(Skill s) throws SQLException {
        return SkillMgr.insertSkill(conn, s);
    }


    /**
     * To update a Hint path we walk the HintPath's backup version of the orginal hint path and delete all the
     * solutionpath rows and then we insert all new ones
     *
     * @param problem
     * @param hintPath
     * @return
     */
    public boolean updateHintPath(Problem problem, HintPath hintPath) throws SQLException {
        deleteHintPath(problem, hintPath);
        hintPath.makeBackup();
        return insertHintPath(problem, hintPath);
    }

    public boolean insertHintPath(Problem problem, HintPath path) throws SQLException {
        List hints = path.getHints();
        if (hints.isEmpty()) return false;
        path.makeBackup(); // make the backup reflect what is in the edu.umass.ckc.wo.wpa.db
        Hint prev = hints.isEmpty() ? null : (Hint) hints.get(0);
        Iterator itr = hints.iterator();
        itr.next(); // advance pointer
        while (itr.hasNext()) {
            Hint next = (Hint) itr.next();
            String q = "select * from solutionpath where sourceHint=? and targetHint=?";
            final PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, prev.getId());
            ps.setInt(2, next.getId());
            ResultSet rs = ps.executeQuery();
            // if a link already exists between prev Hint and next Hint, then don't do anything
            if (!rs.next()) {
                String q2 = "insert into solutionpath (sourceHint,targetHint) values (?,?)";
                final PreparedStatement ps2 = conn.prepareStatement(q2);
                ps2.setInt(1, prev.getId());
                ps2.setInt(2, next.getId());
                ps2.executeUpdate();
            }
            prev = next;
        }
        return true;
    }

    // deleting a hint path in the database requires that we walk down the original path of hints
    // that we read in from the database (not the currently linked set).  The HintPath object keeps the
    // orginal path in the safePath
    public boolean deleteHintPath(Problem problem, HintPath path) throws SQLException {
        List hints = path.getSafePath(); // the original chain of hints
        if (hints == null) return false;
        Hint prev = hints.isEmpty() ? null : (Hint) hints.get(0);
        Iterator itr = hints.iterator();
        itr.next(); // advance pointer
        while (itr.hasNext()) {
            Hint next = (Hint) itr.next();
            String q = "delete from solutionpath where sourceHint=? and targetHint=?";
            final PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, prev.getId());
            ps.setInt(2, next.getId());
            int n = ps.executeUpdate();
            prev = next;
        }
        return true;
    }

    public Collection getAllHints() throws SQLException {
        return HintMgr.getAllHintsFromCache();
//        return HintMgr.getAllHints(conn);
    }

    public Set getHintSet () throws SQLException {
        return HintMgr.getHintSet(conn);
    }

    public boolean deleteHint(Hint hint) throws SQLException {
       return HintMgr.deleteHint(conn,hint);
    }

//    public Hint getHint (int id) throws SQLException {
//        return HintMgr.getHint(conn,id);
//    }

    public boolean deleteSkill(Skill skill) throws SQLException {
        return SkillMgr.deleteSkill(conn, skill);
    }

    public List getSkillHints(int skillId) throws SQLException {
        return HintMgr.getHintsWithSkill(conn,skillId);

    }

    public boolean updateSkill(Skill skill) throws SQLException {
        return SkillMgr.updateSkill(conn, skill);
    }

    // unused
//    public Hint getHint(int id) throws SQLException;
    public Collection getProblemHints(int problemId) throws SQLException {
        return HintMgr.getProblemHintsFromCache(problemId);
    }

    public int insertPrePostProblem (PrePostProblemDefn p) throws SQLException {
         return PrePostTestMgr.insertPrePostProblem(conn, p);
    }

    public int deletePrePostProblem (PrePostProblemDefn p) throws SQLException {
        return  PrePostTestMgr.deletePrePostProblem(conn,p);
    }

     public void updatePrePostProblem (int id, PrePostProblemDefn p) throws SQLException {
         PrePostTestMgr.updatePrePostProblem(conn,id,p);
     }

    public PrePostProblemDefn selectPrePostProblem (String name, int problemSet) throws SQLException {
         return PrePostTestMgr.selectPrePostProblem(conn,name, problemSet);
     }

    public Vector<PrePostTest> getPrePostTests () throws SQLException {
        return PrePostTestMgr.getPrePostTests(conn);
    }

    public Vector<PrePostProblemDefn> getPrePostProblems (int id)  throws SQLException {
      return PrePostTestMgr.getPrePostProblems(conn,id);
    }

    public int insertPrePostTest(PrePostTest test) throws SQLException {
        return PrePostTestMgr.insertPrePostTest(conn,test);
    }

    public int deletePrePostTest(PrePostTest test) throws SQLException {
        return PrePostTestMgr.deletePrePostTest(conn,test);
    }

    public void updatePrePostTest(PrePostTest test) throws SQLException {
        PrePostTestMgr.updatePrePostTest(conn,test);
    }

    public List<Topic> getTopics(int problemId) throws SQLException {
        return ProblemMgr.getProblemTopics(conn,problemId);
    }

    public List<Topic> getAllTopics() throws SQLException {
        return ProblemMgr.getAllTopics(conn);
    }

    public List<SemiAbsSkill> getSemiAbstractSkills() throws SQLException {
        return SkillMgr.getSemiAbstractSkills(conn);
    }

    public PretestPool getPretestPool(int poolId) throws SQLException {
        return PrePostTestMgr.getPool(conn,poolId);  
    }

     public List<PretestPool> getPretestPools() throws SQLException {
         return PrePostTestMgr.getPools(conn);
     }

    public void setProblemSetPool(int pretestId, int poolId) throws SQLException {
        PrePostTestMgr.setProblemSetPool(conn,pretestId,poolId);
    }

    // return all the pretests that a problem lives in
    public List<Integer> getPrePostTests(int probId) throws SQLException {
        return PrePostTestMgr.getPrePostTests(conn,probId);
    }

    public Vector<PrePostProblemDefn> getAllPrePostProblems() throws SQLException {
        return PrePostTestMgr.getAllPrePostProblems(conn);
    }

    private void tryit () throws SQLException {
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            String q = "select count(*) from yazduser";
            try {
                ps = conn.prepareStatement(q);
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                rs = ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            while (rs.next()) {
                rs.getInt(1);
            }
            System.out.println("done");
        }
        finally {
            rs.close();
            ps.close();
        }
    }

    public Topic saveTopic(Topic t) throws SQLException {
        if (t.getId() != -1) {
            return updateTopic(t);
        }
        else return insertTopic(t);
    }

    private Topic updateTopic(Topic t) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String q = "update problemgroup set intro=?, summary=?, description=?, type=?,active=?,isCCMapped=?  where id=?";
            stmt = conn.prepareStatement(q);
            stmt.setString(1, t.getIntro());
            stmt.setString(2, t.getSummary());
            stmt.setString(3, t.getName());
            stmt.setString(4, t.getType());
            stmt.setInt(5, t.isActive()?1:0);
            stmt.setInt(6, t.isActive()?1:0);
            stmt.setInt(7,t.getId());
            stmt.executeUpdate();
            return t;
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }



    private Topic insertTopic(Topic t) throws SQLException {
        ResultSet rs = null;
        PreparedStatement s = null;
        try {
            String q = "insert into problemgroup (intro,summary,description,type,active,isCCMapped) " +
                    "values (?,?,?,?,?,?)";
            s = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
            s.setString(1, t.getIntro());
            s.setString(2, t.getSummary());
            s.setString(3,t.getName());
            s.setString(4,t.getType());
            s.setInt(5,t.isActive()?1:0);
            s.setInt(6,t.isCCMapped()?1:0);
            s.execute();
            rs = s.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            t.setId(id);
            return t;
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            if (e.getErrorCode() == duplicateRowError || e.getErrorCode() == keyConstraintViolation)
                return null;
            else throw e;
        } finally {
            if (rs != null)
                rs.close();
            if (s != null)
                s.close();
        }
    }

    public static void main(String[] args) {
        WayangDb db = new WayangDb();
        db.init();
        try {
            db.tryit();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
