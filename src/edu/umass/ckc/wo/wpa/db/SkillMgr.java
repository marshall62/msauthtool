package edu.umass.ckc.wo.wpa.db;

import edu.umass.ckc.wo.wpa.model.Skill;
import edu.umass.ckc.wo.wpa.model.SemiAbsSkill;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Feb 8, 2005
 * Time: 3:46:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SkillMgr {

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String VISUAL = "visual";


    private static Hashtable skillTable = null;
    private static final String SEMIABS_ID = "semiabsskillid";
    private static final String SEMIABS_NAME = "name";


    public static Skill getSkillFromCache(int id) {
        return (Skill) skillTable.get(new Integer(id));

    }

    public static int insertSkill(Connection conn, Skill s) throws SQLException {

        String q = "insert into skill " +
                "(name,visual,semiabsskillid) values (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, s.getName());
        ps.setInt(2, s.isVisual() ? 1 : 0);
        ps.setInt(3,s.getSemiAbstractSkill().getId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        skillTable.put(new Integer(id), s);
        rs.close();
        return id;
    }

    public static boolean deleteSkill(Connection conn, Skill skill) throws SQLException {
        skillTable.remove(new Integer(skill.getId()));
        String q = "delete from Skill where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, skill.getId());
        int n = ps.executeUpdate();
        return n > 0;
    }

    public static boolean updateSkill(Connection conn, Skill skill) throws SQLException {
        deleteSkill(conn, skill);
        insertSkill(conn, skill);
        return true;
    }

    private static boolean isVisual(int vis) {
        return vis == 1;
    }

    public static Collection getAllSkills (Connection conn) throws SQLException {
        if (skillTable == null) {
            skillTable = new Hashtable();
            getSkills(conn);
        }
        return skillTable.values();
    }

    private static void getSkills(Connection conn) throws SQLException {
        String q = "select s.id,s.name,s.visual, ss.id, ss.name from Skill s, semiabstractskill ss where ss.id = s.semiabsskillid";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        List result = new ArrayList();
        while (rs.next()) {
            String name = rs.getString(NAME);
            int id = rs.getInt(ID);
            int isVis = rs.getInt(VISUAL);
            int semiAbsId = rs.getInt(4);
            String semiAbsName = rs.getString(5);
            Skill s = new Skill(id, name, isVisual(isVis), new SemiAbsSkill(semiAbsId,semiAbsName));
            skillTable.put(new Integer(id),s);
        }
    }

//    public static Skill getSkill(Connection conn, int id) throws SQLException {
//        String q = "select * from Skill where id=?";
//        PreparedStatement ps = conn.prepareStatement(q);
//        ps.setInt(1, id);
//        ResultSet rs = ps.executeQuery();
//        if (rs.next()) {
//            String name = rs.getString(NAME);
//            int isVis = rs.getInt(VISUAL);
//            Skill s = new Skill(id, name, isVisual(isVis));
//            return s;
//        }
//        return null;
//
//    }


    public static List<SemiAbsSkill> getSemiAbstractSkills(Connection conn) throws SQLException {
        String q = "select id,name from semiabstractskill";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        List<SemiAbsSkill> result = new ArrayList<SemiAbsSkill>();
        while (rs.next()) {
            result.add(new SemiAbsSkill(rs.getInt(1),rs.getString(2)));
        }
        return result;
    }
}
