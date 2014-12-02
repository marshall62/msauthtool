package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.Problem;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
 * Time: 3:27:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class HintListDialog extends JDialog implements ActionListener, ListSelectionListener, KeyListener {

    private JPanel hintListPanel;
    private JButton delBut;
    private JButton modBut;

    private JPanel butPanel;
    private JButton saveBut;
    private JButton cancelBut;
    private JList hintList;
    private JPanel contentPane;
    private Collection allHints;
    private DefaultListModel hintsModel;
    private List deletedHintsBuffer = new ArrayList();
    private BackEnd backEnd;

    public HintListDialog(JFrame owner, BackEnd be) {
        setModal(false);
        try {
            allHints = be.getAllHints();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        this.backEnd = be;
        init();
    }

    private void init() {
        hintList.addKeyListener(this);
        this.setTitle("Hint Selector");
        delBut.setMnemonic('D');
        delBut.addActionListener(this);
        modBut.addActionListener(this);
        butPanel = new JPanel();
        butPanel.add(saveBut = new JButton("Save"));
        butPanel.add(cancelBut = new JButton("Cancel"));
        saveBut.addActionListener(this);
        cancelBut.addActionListener(this);
        hintList.setCellRenderer(new MyCellRenderer(this.backEnd));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(hintListPanel, BorderLayout.CENTER);
        this.getContentPane().add(butPanel, BorderLayout.SOUTH);
        setHints();
    }

    private void setHints() {
        Object[] sortedHints = allHints.toArray();
        Arrays.sort(sortedHints);
        hintsModel = new DefaultListModel();
        int j = 0;
        for (int i = 0; i < sortedHints.length; i++) {
            Hint h = (Hint) sortedHints[i];
            hintsModel.add(j++, h);
        }
        hintList.setModel(hintsModel);
        hintList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        hintList.addListSelectionListener(this);

    }


    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if (b == saveBut) {

            deleteHintsFromDb();

            this.setVisible(false);
            this.dispose();
        } else if (b == cancelBut) {
            this.setVisible(false);
            this.dispose();
        } else if (b == modBut) {
            Hint h = (Hint) hintList.getSelectedValue();
            if (h != null) {
                HintEditor he = new HintEditor(this, this.backEnd, h);
                he.setVisible(true);
            }
        } else if (b == delBut)
            addSelectedHintsToDeleteList();
    }

    private void commitDeletes() throws SQLException {
        Iterator itr = this.deletedHintsBuffer.iterator();
        while (itr.hasNext()) {
            Hint hint = (Hint) itr.next();

            this.backEnd.deleteHint(hint);

        }

    }

    private void deleteHintsFromDb() {
        int opt = JOptionPane.showConfirmDialog(this,
                "Once you delete these hints, all hint paths that contain them will be modified.\n" +
                "Are you sure you want to delete these hints from the database?", "Delete Hints",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                commitDeletes();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.setVisible(false);
            this.dispose();
        }
    }

    private void addSelectedHintsToDeleteList() {
        Object[] selHints = hintList.getSelectedValues();
        for (int i = 0; i < selHints.length; i++) {
            Hint h = (Hint) selHints[i];
            this.deletedHintsBuffer.add(h);
            hintsModel.removeElement(h);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_DELETE)
            addSelectedHintsToDeleteList();
    }

    class MyCellRenderer extends JLabel implements ListCellRenderer {
        BackEnd backEnd;

        public MyCellRenderer(BackEnd backEnd) {
            this.backEnd = backEnd;
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            Hint h = (Hint) value;
            String pname = h.getProbName();
            Problem p = null;
            if (pname == null) {
                try {
                    p = backEnd.getProblem(h.getProbId());
                    if (p != null) {
                        pname = p.getName();
                        h.setProbName(pname);
                    }
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (pname != null)
                setText(pname + " : " + h.toString() + ":" + h.getId());
            else
                setText(h.getProbId() + " : " + h.toString() + ":" + h.getId());
            setBackground(isSelected ? Color.red : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }
}
