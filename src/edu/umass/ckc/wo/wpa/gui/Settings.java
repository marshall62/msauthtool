package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.WayangDb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Jun 2, 2005
 * Time: 5:01:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Settings {
    public static String user = null;
//    public static String dbHost = "localhost";
    public static String dbHost = "wayang.cs.umass.edu";
    public static String dbName = "wayangoutpostdb";

    public static void readInitFile(String initFilePath) throws Exception {
        Properties p = new Properties();
        // Reads the ini file from the project root folder (e.g. U:/role/wpa).   When wpa is packaged as
        // a jar file, reads the ini file from the dir where the jar lives.
        File f = new File(initFilePath);
        if (f.exists())     {
            p.load(new FileInputStream(initFilePath));
            System.out.println("Using init file: " + f.getAbsolutePath())  ;
            WayangDb.setDbParams(p.getProperty("dbHost"),p.getProperty("dbName"),p.getProperty("dbUser"),p.getProperty("dbPassword"));
        }
        else  {
            System.out.println("Cannot find: " + initFilePath);
            System.exit(0);
        }
        Settings.dbHost = p.getProperty("dbHost");

    }
}
