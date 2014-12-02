package edu.umass.ckc.wo.wpa.gui.prepost;

import edu.umass.ckc.wo.wpa.db.BackEnd;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.Frame;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.SQLException;

import edu.umass.ckc.wo.wpa.model.PrePostTest;

public class TestListDialog extends JDialog {
    private JButton delBut;
    private JButton modBut;
    private JButton newBut;
    private JList problemSetList;
    private JPanel contentPane;
    private JButton doneBut;
    private JButton cancelBut;
    private BackEnd backEnd;
    private PrePostTest curTest;
    private DefaultListModel model;
    private ArrayList deletedTests = new ArrayList();

    public TestListDialog(Frame owner, BackEnd be) {
        super(owner,"Pre Post Tests",true);
        backEnd = be;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(doneBut);
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Edit");
//        JMenuItem mu = new JMenuItem(new AbstractAction("Set User") {
//            public void actionPerformed(ActionEvent e) {
//                Settings.user = JOptionPane.showInputDialog(TestListDialog.this, "Enter user name:");
//            }
//        });
//        JMenuItem msk = new JMenuItem(new AbstractAction("Skills") {
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });
//        JMenuItem mh = new JMenuItem(new AbstractAction("Hints") {
//            public void actionPerformed(ActionEvent e) {
//            }
//        });
        JMenuItem pp = new JMenuItem(new AbstractAction("View All Pretest Problems") {
            public void actionPerformed(ActionEvent e) {
                ProblemListDialog d = new ProblemListDialog(TestListDialog.this, backEnd);
                d.setSize(500,500);
                d.setLocationRelativeTo(TestListDialog.this);
                d.setLocation(100,0);
                d.setVisible(true);
            }
        });
//        menu.add(mu);
//        menu.add(msk);
//        menu.add(mh);
        menu.add(pp);
        menubar.add(menu);
        this.setJMenuBar(menubar);



        doInit();
        doneBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        newBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNew();
            }
        });

        modBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onModify();
            }
        });
        delBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }




    private void doInit() {
        Vector v;
        model = new DefaultListModel();
        try {
            v = backEnd.getPrePostTests();
            Iterator itr = v.iterator();
            while (itr.hasNext()) {
                Object o = itr.next();
                model.addElement(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        problemSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        problemSetList.setModel(model);
    }

    private void onOK() {
        commitDeletes();
        dispose();
    }

    private void commitDeletes() {
        Iterator itr = deletedTests.iterator();
        try {
            while (itr.hasNext()) {
                PrePostTest prePostTest = (PrePostTest) itr.next();
                backEnd.deletePrePostTest(prePostTest);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onNew() {
        curTest  = new PrePostTest();
        model.addElement(curTest);
        TestEditorDialog d = new TestEditorDialog(this,backEnd,curTest);
        d.setSize(300,150);
        d.setLocationRelativeTo(this);
        d.setLocation(100,0);
        d.setVisible(true);
    }

    private void onModify() {
        curTest = (PrePostTest) problemSetList.getSelectedValue();
        if (curTest == null) return;
        ProblemListDialog d = new ProblemListDialog(this, backEnd,curTest);
        d.setSize(500,500);
        d.setLocationRelativeTo(this);
        d.setLocation(100,0);
        d.setVisible(true);
    }

    private void onDelete() {
        Object o = problemSetList.getSelectedValue();
        if (o == null) return;
        deletedTests.add(o);
        model.removeElement(o);
    }

    public static void main(String[] args) {
        Vector v = new Vector();
        v.add("test 1");
        v.add("test 2");
        TestListDialog dialog = new TestListDialog(null,null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    // callback method from the TestEditorDialog which tells this dialog to refresh its list of tests
    public void updateList(Object test) {
       this.repaint();
    }

    // callback method from TestEditorDialog which tells this dialog that user canceled editing of the new test.
    // If the test was new (id is -1) we want to remove the placeholder entry in the list of tests
    public void cancelEdit () {
        if (curTest.isNew())
            model.removeElement(curTest);
    }
}
