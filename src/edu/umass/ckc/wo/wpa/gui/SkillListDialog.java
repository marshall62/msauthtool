package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Skill;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: May 25, 2005
 * Time: 3:35:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SkillListDialog extends SkillLister implements ActionListener, ListSelectionListener {
    private JPanel skillsPanel;
    private JButton delBut;
    private JButton modBut;
    private JButton newBut;
    private JList skillList;
    private Collection allSkills;
    private JPanel buttonPanel;
    private JButton cancelBut;
    private JButton saveBut;
    private DefaultListModel skillsModel;
    private BackEnd backEnd;

    private List deletedSkillsBuffer = new ArrayList();

    public SkillListDialog (JFrame owner, BackEnd be) {
        super(owner,"Skill Selector",false);
        try {
            allSkills = be.getSkills();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.backEnd = be;
        init();
    }

    private void init () {
        delBut.addActionListener(this);
        modBut.addActionListener(this);
        newBut.addActionListener(this);
        buttonPanel = new JPanel();
        saveBut = new JButton("Save");
        cancelBut = new JButton("Cancel");
        saveBut.addActionListener(this);
        cancelBut.addActionListener(this);
        buttonPanel.add(saveBut);
        buttonPanel.add(cancelBut);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(skillsPanel,BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
        setSkills();
    }

    private void setSkills() {
        Object[] a = allSkills.toArray();
        Arrays.sort(a);
        skillsModel = new DefaultListModel();
        for (int i = 0; i < a.length; i++) {
            Skill sk = (Skill) a[i];
            skillsModel.add(i,sk);
        }
        skillList.setModel(skillsModel);
        skillList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
        skillList.addListSelectionListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if (b == saveBut) {
            try {
                commitDeletes();
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            setVisible(false);
            this.dispose();
        }
        else if (b == cancelBut){
            setVisible(false);
            this.dispose();
        }
        else if (b == newBut)
            showSkillEditor();
        else if (b == modBut)
            showSelectedSkillEditor();
        else if (b == delBut)
            addSelectedSkillToDeleteList();
    }

    private void commitDeletes() throws SQLException {
        Iterator itr = this.deletedSkillsBuffer.iterator();
        while (itr.hasNext()) {
            Skill skill = (Skill) itr.next();
            try {
                this.backEnd.deleteSkill(skill);
            } catch (SQLException e) {
                // failing to delete the Skill could be because the skill is referenced
                // by rows in the Hint table
                List hints = this.backEnd.getSkillHints(skill.getId());
                Iterator ii = hints.iterator();
                StringBuffer hintNames = new StringBuffer();
                while (ii.hasNext()) {
                    Hint h = (Hint) ii.next();
                    hintNames.append(h.getId() + ":" + h.getName() + "\n ");
                }
                JOptionPane.showMessageDialog(this,"Deleting the skill " + skill.getId() + ":" + skill.getName() + " failed.  \nThis skill may be referenced" +
                        " by a hint.  \nThese are the hints that use this skill:\n" + hintNames.toString());
                e.printStackTrace();
            }
        }
    }

    private void showSelectedSkillEditor() {
        Skill sk = (Skill) skillList.getSelectedValue();
        SkillEditor ske = new SkillEditor(this,this.backEnd, sk);
        ske.setVisible(true);
        ske.pack();
    }

    private void showSkillEditor() {
        SkillEditor ske = new SkillEditor(this,this.backEnd, null);
        ske.setVisible(true);
    }

    private void addSelectedSkillToDeleteList() {
        Skill sk = (Skill) skillList.getSelectedValue();
        this.deletedSkillsBuffer.add(sk);
        skillsModel.removeElement(sk);
    }

    public void valueChanged(ListSelectionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateSkillList(Skill sk) {
        skillsModel.addElement(sk);
    }
}
