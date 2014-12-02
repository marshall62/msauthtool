package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.Skill;
import edu.umass.ckc.wo.wpa.model.SemiAbsSkill;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshalll
 * Date: Nov 23, 2004
 * Time: 9:53:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class SkillEditor extends JDialog implements ActionListener {
    private JPanel mainPanel;
    private JCheckBox isVisualCheckBox;
    private JTextField idTxt;
    private JTextField nameTxt;
    private Skill skill;
    private JButton saveBut;
    private JButton cancelBut;
    private BackEnd backEnd;
    private SkillLister owner;
    private JComboBox semiAbstractSkillPulldown;

//    public SkillEditor(SkillLister owner, String title, boolean modal, Skill skill, BackEnd backEnd) throws HeadlessException {
//        super(owner, title, modal);
//        this.owner = owner;
//        setSize(300,300);
//        this.backEnd = backEnd;
//        if (skill != null)
//            this.skill = skill;
//        else
//            this.skill = new Skill(-1, "", false);
//        load();
//    }

    public SkillEditor (SkillLister owner, BackEnd backEnd, Skill skill) {
        super(owner,"Skill Editor",true);
        this.owner = owner;
        setSize(500,300);
        if (skill != null)
            this.skill = skill;
        else
            this.skill = new Skill(-1, "", false, null);
        this.backEnd = backEnd;
        init();
    }

    private void init() {
        this.getContentPane().add(mainPanel);
        setFields(skill.getName(), skill.getId(), skill.isVisual());
        saveBut.addActionListener(this);
        cancelBut.addActionListener(this);
        semiAbstractSkillPulldown.addActionListener(this);
        try {
            semiAbstractSkillPulldown.setModel(buildSSPulldownModel(backEnd.getSemiAbstractSkills()));
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private ComboBoxModel buildSSPulldownModel(List<SemiAbsSkill> semiAbstractSkills) {
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        for (SemiAbsSkill s : semiAbstractSkills)
            m.addElement(s);
        return m;
    }

    private void setFields(String name, int id, boolean visual) {
        idTxt.setText(Integer.toString(id));
        nameTxt.setText(name);
        isVisualCheckBox.setSelected(visual);
    }

    private boolean getFields() {
        skill.setName(nameTxt.getText().trim());
        skill.setVisual(isVisualCheckBox.isSelected());
        if  (semiAbstractSkillPulldown.getSelectedItem() == null) {

            return false;
        }
        skill.setSemiAbstractSkill((SemiAbsSkill) semiAbstractSkillPulldown.getSelectedItem());
        return true;
    }

    private boolean skillExists(String name) {
        Collection allSkills = null;
        try {
            allSkills = backEnd.getSkills();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Iterator iterator = allSkills.iterator();
        while (iterator.hasNext()) {
            Skill sk = (Skill) iterator.next();
            if (sk.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveBut) {
            if (!getFields())
                JOptionPane.showMessageDialog(this,"The skill must have a semi abstract skill selected for it.");
            else if (skill.isNew() && skillExists(skill.getName()))
                JOptionPane.showMessageDialog(this, "The skill " + skill.getName() + " already exists.");
            else if (skill.getName() == null || skill.getName().trim().equals(""))
                JOptionPane.showMessageDialog(this, "The skill must have a name");
            else if (skill.isNew()) {
                try {
                    backEnd.insertSkill(skill);
                } catch (SQLException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                owner.updateSkillList(skill);
                this.setVisible(false);
                this.dispose();
            }
            else if (e.getSource() == semiAbstractSkillPulldown) {

            }
            else {
                try {
                    backEnd.updateSkill(skill);
                } catch (SQLException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                this.setVisible(false);
                this.dispose();
            }

        } else if (e.getSource() == cancelBut) {
            this.setVisible(false);
            this.dispose();
        }
    }


}
