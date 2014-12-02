package edu.umass.ckc.wo.wpa.db;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: Jim Peterson
 * Date: Feb 6, 2012
 */

import edu.umass.ckc.wo.wpa.model.Topic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopicMgr {
    public static final String ID = "id";
    public static final String INTRO = "intro";
    public static final String SUMMARY = null;
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "swf";
    public static final int ACTIVE = 1;

    public static int insertTopic(Connection conn, Topic t) throws SQLException {
        String q = "insert into problemgroup " +
                "(intro,summary,description,type,active)" +
                " values (?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, t.getIntro());
        ps.setString(2, t.getSummary());
        ps.setString(3, t.getDescription());
        ps.setString(4, t.getType());
        ps.setInt(5, t.isActive()?1:0);

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        t.setId(id);
        return id;
    }

    public static int updateTopic(Connection conn, Topic t) throws SQLException {
        String q = "update problemgroup " +
                "set intro=?,summary=?,description=?," +
                "type=?,active=? where id=?";

        PreparedStatement ps = conn.prepareStatement(q);
        ps.setString(1, t.getIntro());
        ps.setString(2, t.getSummary());
        ps.setString(3, t.getDescription());
        ps.setString(4, t.getType());
        ps.setInt(5, t.isActive()?1:0);
        ps.setInt(6, t.getId());

        return ps.executeUpdate();
    }

    public static boolean deleteTopic(Connection conn, Topic t) throws SQLException {
        String q = "delete from problemgroup where id=?";
        PreparedStatement ps = conn.prepareStatement(q);
        ps.setInt(1, t.getId());
        return ps.executeUpdate() > 0;

    }

    public static List<Topic> getAllTopics(Connection conn) throws SQLException {
        String q = "select id,intro,summary,description,type," +
                "active,isCCMapped from problemgroup";
        PreparedStatement ps = conn.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        List<Topic> l = new ArrayList<Topic>();
        while (rs.next())   {
            boolean isActive = rs.getBoolean(6);
            l.add(new Topic("", rs.getInt(1),rs.getString(2),rs.getString(4),rs.getString(3),rs.getString(5),isActive, rs.getBoolean(7)));
        }
        return l;
    }


}
