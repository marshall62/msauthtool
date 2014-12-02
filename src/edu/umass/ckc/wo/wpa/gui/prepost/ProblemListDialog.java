package edu.umass.ckc.wo.wpa.gui.prepost;

import edu.umass.ckc.wo.wpa.db.BackEnd;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

import edu.umass.ckc.wo.wpa.model.PrePostTest;
import edu.umass.ckc.wo.wpa.model.PrePostProblemDefn;
import edu.umass.ckc.wo.wpa.model.PretestPool;
import edu.umass.ckc.wo.wpa.gui.prepost.ProbEditorDialog;

public class ProblemListDialog extends JDialog {
    private JButton delBut;
    private JButton modBut;
    private JButton newBut;
    private JList problemList;
    private JButton doneBut;
    private JButton cancelBut;
    private JPanel contentPane;
    private JTextField problemSetText;
    private JComboBox testPoolPulldown;
    private BackEnd backEnd;
    private PrePostProblemDefn curProb;
    private DefaultListModel model;
    private PrePostTest curTest;
    private ArrayList deletedProblems = new ArrayList();

    public ProblemListDialog (JDialog owner, BackEnd be ) {
        super(owner,"Pre/Post Test Problems",true);
        curTest = null;
        backEnd = be;
        doInit(null);
        setButtons();
    }

    public ProblemListDialog(JDialog owner, BackEnd be, PrePostTest test) {
        super(owner,"Pre/Post Test Problems",true);
        curTest = test;
        backEnd = be;
        doInit(test);
        setButtons();

    }

    private void setButtons() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(doneBut);

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
        modBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onModify();
            }
        });
        newBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNew();
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



    PrePostTest getCurTest () {
        return this.curTest;
    }

    private void doInit(PrePostTest test) {
        try {
            if (test != null) {
                problemSetText.setText(test.getId() + " " + test.getName());
                setTestPool(backEnd);
            }
            else  {
                problemSetText.setText("All problems");
                testPoolPulldown.setEnabled(false);
            }

            model = new DefaultListModel();
            // get all the PrePostProblems in the test
            Vector<PrePostProblemDefn> v;
            if (test != null)
                v= backEnd.getPrePostProblems(test.getId());
            else v=backEnd.getAllPrePostProblems();
            for (PrePostProblemDefn p : v) {
                model.addElement(p);
            }
           
            problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            problemList.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void setTestPool(BackEnd backEnd) {
        try {
            List<PretestPool> pools = backEnd.getPretestPools();
            PretestPool selectedPool = getSelectedPool(pools,curTest.getPoolID());
            PretestPool[] poolArray = pools.toArray(new PretestPool[pools.size()]);
            DefaultComboBoxModel m = new DefaultComboBoxModel(poolArray);
            testPoolPulldown.setModel(m);
            if (selectedPool != null)
                m.setSelectedItem(selectedPool);

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private PretestPool getSelectedPool(List<PretestPool> pools, int poolID) {
        for (PretestPool p : pools) {
            if (p.getId() == poolID)
                return p;
        }
        return null;
    }

    private void onOK() {
        setPretestPool();
        commitDeletes();
        dispose();
    }

    private void setPretestPool() {

        PretestPool p = (PretestPool) testPoolPulldown.getSelectedItem();
        curTest.setPoolID(p.getId());
        try {
            backEnd.setProblemSetPool(curTest.getId(),p.getId());
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void commitDeletes() {
        Iterator itr = deletedProblems.iterator();
        try {
            while (itr.hasNext()) {
                PrePostProblemDefn ppp = (PrePostProblemDefn) itr.next();
                backEnd.deletePrePostProblem(ppp);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onModify() {
        curProb = (PrePostProblemDefn) problemList.getSelectedValue();
        if (curProb == null) return;
        ProbEditorDialog d = new ProbEditorDialog(this,backEnd,curProb);
        d.setSize(750,500);
        d.setLocationRelativeTo(this);
        d.setLocation(150,0);
        d.setVisible(true);
    }

    private void onNew () {
        curProb = new PrePostProblemDefn();
        curProb.addProblemSet(curTest);
        model.addElement(curProb);
        ProbEditorDialog d = new ProbEditorDialog(this,backEnd,curProb);
        d.setSize(750,500);
        d.setLocationRelativeTo(this);
        d.setLocation(150,0);
        d.setVisible(true);
    }

    // callback from ProbEditorDialog when save button is clicked.
    public void updateList () {
        this.repaint();
    }

    // callback from ProbEditorDialog when edit of a problem is cancelled.  If the problem is new (its id is -1)
    // we want to remove the placeholder from this dialog's jlist.  If its not new, leave it alone.
    public void cancelEdit () {
        if (curProb.isNew())
            model.removeElement(curProb);
    }

    // Deleting the selected problem will put the problem into a delete list so that when
    // the dialog is done, it will delete the problem from the db.   If the problem
    // is part of more than one pretest, we warn the user because deleting a problem will
    // cause it to leave all the pretests that it lives in.   If the user just wants to take
    // it out of one pretest rather than delete it, she should use the pulldown menu for
    // problem sets in the actual problem editor dialog.
    private void onDelete () {
        curProb = (PrePostProblemDefn) problemList.getSelectedValue();
        if (curProb == null) return;
        // if the problem lives in more than one problem set, warn the user before adding to the
        // list of problems to delete
        try {
            List<Integer> tests = backEnd.getPrePostTests(curProb.getId());
            if (tests.size() > 1) {
                int opt = JOptionPane.showConfirmDialog(this,"The problem lives in more than one pretest. Are you sure you want to delete it?") ;
                if (opt == JOptionPane.YES_OPTION) {
                    deletedProblems.add(curProb);
                    model.removeElement(curProb);
                }

            }
            else {
                deletedProblems.add(curProb);
                model.removeElement(curProb);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void main(String[] args) {
        ProblemListDialog dialog = new ProblemListDialog(null, null,null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
