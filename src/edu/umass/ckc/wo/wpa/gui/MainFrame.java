package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.*;
import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Problems;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;

import edu.umass.ckc.wo.wpa.gui.prepost.TestListDialog;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * written by: David Marshall
 * date: 6/13/05
 * time: 10:20 AM
 */


/**
 * The main class of the app.  Also the main JFrame.
 */
public class MainFrame extends JFrame implements ListSelectionListener, ActionListener {
    private JList probList;
    private JButton newBut;
    private JButton modBut;
    private JButton delBut;
    private JPanel mainPanel;
    DefaultListModel problemList;
    private Problem selectedProblem = null;

    private Problems model;
    private BackEnd backEnd = new MemoryDb();


    public MainFrame(BackEnd backEnd) {
        this.backEnd = backEnd;
        mainPanel.setPreferredSize(new Dimension(400, 400));
        buildModel();
        initGUI(true);
    }

    private void initGUI(boolean firstTime) {
        setTitle("Wayang Problem List");
        problemList = new DefaultListModel();
        Collections.sort(model.getProblems());
        Iterator itr = model.getProblems().iterator();
        int i = 0;
        while (itr.hasNext()) {
            Problem p = (Problem) itr.next();
            problemList.add(i++, p);
        }
        probList.setModel(problemList);
        probList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        probList.addListSelectionListener(this);
        if (firstTime) {
            newBut.addActionListener(this);
            modBut.addActionListener(this);
            delBut.addActionListener(this);
        }
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Edit");
        JMenuItem mu = new JMenuItem(new AbstractAction("Set User") {
            public void actionPerformed(ActionEvent e) {
                Settings.user = JOptionPane.showInputDialog(MainFrame.this, "Enter user name:");
            }
        });
        JMenuItem mdb = new JMenuItem(new AbstractAction("Set Db") {
            public void actionPerformed(ActionEvent e) {
                String db = JOptionPane.showInputDialog(MainFrame.this, "Enter db (e.g. rose, localhost):");
                if (db.equals("rose"))
                    Settings.dbHost = "rose.cs.umass.edu";
                else if (db.equals("localhost"))
                    Settings.dbHost = "localhost";
                else if (db.equals("cadmium"))
                    Settings.dbHost = "cadmium.cs.umass.edu";
                else {
                    try {
                        throw new Exception("Dont know this db host");
                    } catch (Exception e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                WayangDb.setHost(db);
                HintMgr.resetCache();
                ProblemMgr.resetCache();
                Problems.resetInstance();

                buildModel();
                initGUI(false);
                MainFrame.this.setTitle("Content Author Database is: " + Settings.dbHost);
//                repaint();
                validate();
            }
        });
        JMenuItem msk = new JMenuItem(new AbstractAction("Skills") {
            public void actionPerformed(ActionEvent e) {
                SkillListDialog d = new SkillListDialog(MainFrame.this, MainFrame.this.backEnd);
                d.setSize(400, 500);
                d.setVisible(true);
            }
        });
        JMenuItem mh = new JMenuItem(new AbstractAction("Hints") {
            public void actionPerformed(ActionEvent e) {
                HintListDialog d = new HintListDialog(MainFrame.this, MainFrame.this.backEnd);
                d.setSize(400, 500);
                d.setVisible(true);
            }
        });
        JMenuItem mt = new JMenuItem(new AbstractAction("Topics") {
            public void actionPerformed(ActionEvent e) {
                TopicListDialog d = new TopicListDialog(MainFrame.this, MainFrame.this.backEnd);
                d.setSize(400, 500);
                d.setVisible(true);
            }
        });
        JMenuItem mpp = new JMenuItem(new AbstractAction("Pre/Post Tests") {
            public void actionPerformed(ActionEvent e) {
                TestListDialog d = new TestListDialog(MainFrame.this, MainFrame.this.backEnd);
//                ProbEditorDialog d = new ProbEditorDialog(MainFrame.this, MainFrame.this.backEnd);
                d.setLocationRelativeTo(MainFrame.this);
                d.setLocation(50, 0);
                d.setSize(600, 500);
                d.setVisible(true);
            }
        });
        menu.add(mu);
        menu.add(mdb);
        menu.add(msk);
        menu.add(mh);
        menu.add(mt);
        menu.add(mpp);
        menubar.add(menu);
        this.setJMenuBar(menubar);
        if (firstTime)
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
    }

    private void buildModel() {
        if (!backEnd.init())
            JOptionPane.showMessageDialog(this, "Failed to connect to database");
        model = Problems.getInstance();
        try {
            model.setProblems(backEnd.loadProblems());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * remove the selected problem from the database and from the view.
     */
    private void deleteSelectedProblem() {
        Problem p = selectedProblem;
        problemList.removeElement(selectedProblem);
        if (!p.isNew())
            try {
                backEnd.deleteProblem(p);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
    }

    /**
     * callback from ProblemEditor.  When a new problem is edited there and saved, this callback
     * will stick the problem in this components list of problems.
     *
     * @param problem
     */
    public void addNewProblem(Problem problem) {
        problemList.addElement(problem);

    }

    public void valueChanged(ListSelectionEvent e) {
        selectedProblem = (Problem) probList.getSelectedValue();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modBut) {
            Problem p = selectedProblem;
            new ProblemEditor(this, "Problem Editor", true, p, backEnd).setVisible(true);
        } else if (e.getSource() == newBut) {
            new ProblemEditor(this, "Problem Editor", true, null, backEnd).setVisible(true);
        } else if (e.getSource() == delBut && selectedProblem != null) {
            deleteSelectedProblem();
        }
    }

    public static void main(String[] args) {
        final MainFrame f;
        try {
            if (args.length > 0)
                Settings.readInitFile(args[0]);
            else  {
                Settings.readInitFile("wpa.ini");
            }
        } catch (Exception e) {
            System.out.println("You must provide full path to wpa.ini as an argument to MainFrame");
        }
        System.out.println("Database host is: " + Settings.dbHost);

        BackEnd be;
        if (args.length > 0 && args[0].equalsIgnoreCase("test"))
            be = new MemoryDb();
        else
            be = new WayangDb();

        f = new MainFrame(be);
        f.setTitle("Content Author Database is: " + Settings.dbHost);

        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(f.mainPanel, BorderLayout.CENTER);
        f.pack();
        f.setSize(500, 500);
        f.setVisible(true);
        if (Settings.user == null)
            Settings.user = JOptionPane.showInputDialog(f, "Enter user name:");

    }


}
