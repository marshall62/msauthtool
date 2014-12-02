package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Problems;
import edu.umass.ckc.wo.wpa.model.Skill;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 5:19:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class HintEditor extends SkillLister implements ActionListener {
    private JPanel mainPanel;
    private JButton cancelBut;
    private JButton saveBut;
    private JCheckBox givesAnswerCheckBox;
    private JCheckBox isRootCheckBox;
    private JTextField nameTxt;
    private JTextField idTxt;
    private Hint hint;
    private JComboBox skillPulldown;
    private BackEnd backEnd;
    HintEdCallback owner;
    DefaultComboBoxModel skillsModel;
    Collection allSkills;
    private JButton newSkillBut;
    private JCheckBox readyCheckBox;
    private JComboBox hintCombo;
    private DefaultComboBoxModel hintSelectionModel;    // holds Hint objects
    private Collection allHints;
    private JButton delHintBut;
    private Problem problem;
    private JPanel buttonPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel probIdPane;
    private JTextField probIdTxt;
    private JTextField probNameTxt;


    public HintEditor(JDialog owner, String title, boolean modal, Hint hint, BackEnd backEnd, Problem problem) throws HeadlessException {
        super(owner, title, modal);
        this.problem = problem;
        this.owner = (HintEdCallback) owner;
        if (hint != null)
            this.hint = hint;
        else
            this.hint = new Hint(-1, "", null, false, false, false, problem.getId(), null);
        this.backEnd = backEnd;
        try {
            allSkills = backEnd.getSkills();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        init();
        this.pack();
    }

    public HintEditor(JDialog owner, BackEnd backEnd, Hint hint) {
        super(owner, "Hint Editor", true);
        try {
            this.problem = backEnd.getProblem(hint.getProbId());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.hint = hint;

        this.backEnd = backEnd;
        try {
            allSkills = backEnd.getSkills();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        init();
        this.pack();
    }


    private void init() {
        this.getContentPane().add(mainPanel);
        saveBut.addActionListener(this);
        cancelBut.addActionListener(this);
        newSkillBut.addActionListener(this);
        delHintBut.addActionListener(this);
        setSkillPulldown();
        setHintPulldown();
        setFields(this.hint);

    }

    private void setProblemFields() {
        int probId = this.hint.getProbId();
        try {
            Problem p = this.backEnd.getProblem(probId);
            if (p != null) {
                probNameTxt.setText(p.getName());
                probIdTxt.setText(Integer.toString(probId));
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void setHintPulldown() {
        try {
            allHints = backEnd.getProblemHints(problem.getId());
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        try {
////            allHints = backEnd.getAllHints();
//
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        Object[] sortedHints = allHints.toArray();
        Arrays.sort(sortedHints);
        hintSelectionModel = new DefaultComboBoxModel(sortedHints);
        hintCombo.setModel(hintSelectionModel);
        hintCombo.addActionListener(this);
    }

    // since the allskills list is built every time the dialog starts the list of Skill objects
    // is new.  So we need to find the Skill object which is the same as the skill in the hint
    private Skill getSkill (Skill sk) {
        if (sk == null) return null;
        Iterator itr= allSkills.iterator();
        while (itr.hasNext()) {
            Skill skill = (Skill) itr.next();
            if (skill.getId() == sk.getId())
                return skill;
        }
        return null;
    }

    private Hint findHint (String name) {
        Iterator itr = allHints.iterator();
        while (itr.hasNext()) {
            Hint h = (Hint) itr.next();
            if (h.getName().equals(name))
                return h;
        }
        return null;
    }

    private void setSkillPulldown() {
        Object[] a = allSkills.toArray();
        Arrays.sort(a);
        skillsModel = new DefaultComboBoxModel();
        for (int i = 0; i < a.length; i++) {
            Skill sk = (Skill) a[i];
            skillsModel.addElement(sk);
        }
        skillPulldown.setModel(skillsModel);
        skillPulldown.setSelectedItem(getSkill(hint.getSkill()));

    }

    private void setFields(Hint hint) {
        if (hint == null) {
//            hintCombo.setSelectedItem(null);
//            skillPulldown.setSelectedItem(null);
            return;
        }
        hintCombo.setSelectedItem(hint);
        if (hint.getSkill() != null)
            skillPulldown.setSelectedItem(hint.getSkill());
        else
            skillPulldown.setSelectedItem(null);
        idTxt.setText(Integer.toString(hint.getId()));
        givesAnswerCheckBox.setSelected(hint.isGivesAnswer());
        isRootCheckBox.setSelected(hint.isRoot());
        readyCheckBox.setSelected(hint.isReady());
        if (hint.getId() == -1)
            delHintBut.setEnabled(false);
        else
            delHintBut.setEnabled(true);
        setProblemFields();
    }

    private void getFields() {
        Hint h = (Hint) hintCombo.getSelectedItem();
        this.hint.setName(h.getName());
        this.hint.setSkill((Skill) skillPulldown.getSelectedItem());
        this.hint.setRoot(isRootCheckBox.isSelected());
        this.hint.setGivesAnswer(givesAnswerCheckBox.isSelected());
        this.hint.setIsReady(readyCheckBox.isSelected());
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveBut) {
            getFields();
            if (!validateHint(hint))
                return;
            if (hint.isNew())
                try {
                    int id = backEnd.insertHint(hint);
                } catch (SQLException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    JOptionPane.showMessageDialog(this,"Failed to insert new hint \n" + e1.getMessage());

                }
            else
                try {
                    backEnd.updateHint(hint);
                } catch (SQLException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    JOptionPane.showMessageDialog(this,"Failed to insert new hint \n" + e1.getMessage());
                }
            owner.hintModified(hint);
            this.setVisible(false);
            this.dispose();
        } else if (e.getSource() == cancelBut) {
            this.setVisible(false);
            this.dispose();
        } else if (e.getSource() == newSkillBut) {
            new SkillEditor(this, backEnd, null).setVisible(true);
        } else if (e.getSource() == hintCombo) {
            if (hintCombo.getSelectedItem() instanceof Hint)
                updateForm((Hint) hintCombo.getSelectedItem());
            else
                updateForm((String) hintCombo.getSelectedItem());
        } else if (e.getSource() == delHintBut) {
            deleteHintFromDb(hint);
        }
    }

    private boolean validateHint(Hint hint) {
        if (hint.getName() == null || hint.getName().trim().equals("") || hint.getSkill() == null) {
            JOptionPane.showMessageDialog(this, "You must set the name of the hint and its skill before it can be saved.");
            return false;
        } else
            return true;
    }

    private void deleteHintFromDb(Hint hint) {
        int opt = JOptionPane.showConfirmDialog(this,
                "Once you delete this hint, all hint paths that contain it will be modified.\n" +
                "Are you sure you want to delete this hint from the database?", "Delete Hint",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            Problems.getInstance().deleteHint(hint);
            try {
                backEnd.deleteHint(hint);
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.setVisible(false);
            this.dispose();
            owner.hintDeleted(hint);
        }
    }

    private void updateForm(String selectedHint) {
        Hint h = findHint(selectedHint);
        if (h == null)
            this.hint = new Hint(-1, selectedHint, null, false, false, true, this.problem.getId(), null);
        else this.hint = h;
        this.setFields(this.hint);
    }

    // called when a hint is selected from the combo box.  Need to fetch the Hint object for this hint
    private void updateForm(Hint selectedHint) {
        this.hint = selectedHint;
        this.setFields(selectedHint);
    }


    public void updateSkillList(Skill sk) {
        skillsModel.removeAllElements();
        Collection allSkills = null;
        try {
            allSkills = backEnd.getSkills();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Object[] a = allSkills.toArray();
        Arrays.sort(a);
        for (int i = 0; i < a.length; i++) {
            Skill o = (Skill) a[i];
            skillsModel.addElement(o);
        }
        skillPulldown.setSelectedItem(sk);
    }

}
