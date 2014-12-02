package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.model.Skill;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: May 26, 2005
 * Time: 11:27:02 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SkillLister extends JDialog {

    public SkillLister (JDialog owner, String title, boolean modal) {
        super(owner,title,modal);
    }

    public SkillLister (JFrame owner, String title, boolean modal) {
        super(owner,title,modal);
    }

    public abstract void updateSkillList(Skill sk);
}
