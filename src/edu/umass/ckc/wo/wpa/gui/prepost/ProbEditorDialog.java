package edu.umass.ckc.wo.wpa.gui.prepost;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.PrePostProblemDefn;
import edu.umass.ckc.wo.wpa.model.PrePostTest;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

public class ProbEditorDialog extends JDialog implements ActionListener {
    private JPanel contentPane;
    private JPanel butPan;
    private JButton cancelBut;
    private JButton saveBut;
    private JPanel multiChoicePan;
    private JTextField probURLTxt;
    private JTextField probNameTxt;
//    private JComboBox probSetComboBox;
    private JRadioButton multAnsRadBut;
    private JRadioButton shortAnsRadBut;
    private JTextField shortAnsTxt;
    private JTextPane probDescrPane;
    private JTextField CURLTxt;
    private JCheckBox CCheckBox;
    private JTextField EURLTxt;
    private JTextField DURLTxt;
    private JTextField BURLTxt;
    private JCheckBox ECheckBox;
    private JCheckBox DCheckBox;
    private JCheckBox BCheckBox;
    private JTextField AURLTxt;
    private JTextField ETxt;
    private JTextField DTxt;
    private JTextField CTxt;
    private JTextField BTxt;
    private JCheckBox ACheckBox;
    private JTextField ATxt;

    private BackEnd backEnd;
    private Connection conn;
    private PrePostProblemDefn curProb;
    private ProblemListDialog owner;
    private JTextField idTxt;
    private JList probSetList;
    private boolean isNewProblem;

    public ProbEditorDialog(ProblemListDialog owner, BackEnd be) {
        super(owner, "Pre/Post Test Problem Editor", true);
        this.owner = owner;
        isNewProblem = true;
        curProb = new PrePostProblemDefn();
        backEnd = be;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(saveBut);

        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(this.shortAnsRadBut);
        bg1.add(this.multAnsRadBut);
        shortAnsRadBut.addActionListener(this);
        multAnsRadBut.addActionListener(this);
        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(this.ACheckBox);
        bg2.add(this.BCheckBox);
        bg2.add(this.CCheckBox);
        bg2.add(this.DCheckBox);
        bg2.add(this.ECheckBox);
        saveBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        cancelBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

    public ProbEditorDialog(ProblemListDialog owner, BackEnd be, PrePostProblemDefn p) {
        this(owner, be);
        curProb = p;
        isNewProblem=false;
        setFields(curProb);
    }

    private boolean copyFieldsIntoModel() {
        curProb.setName(this.probNameTxt.getText());
        curProb.setDescr(this.probDescrPane.getText());
        curProb.setUrl(this.probURLTxt.getText().trim().equals("") ? null : this.probURLTxt.getText().trim());
        if (setSelectedProblemSets())
                return false;
//        int ps = Integer.parseInt((String) this.probSetComboBox.getSelectedItem());
//        curProb.setProblemSet(ps);
        curProb.setAnswer(this.shortAnsTxt.getText());
        curProb.setAnsType(this.shortAnsRadBut.isSelected() ? PrePostProblemDefn.SHORT_ANSWER : PrePostProblemDefn.MULTIPLE_CHOICE);
        if (curProb.getAnsType() == PrePostProblemDefn.MULTIPLE_CHOICE) {

            curProb.setaAns(this.ATxt.getText());
            curProb.setbAns(this.BTxt.getText());
            curProb.setcAns(this.CTxt.getText());
            curProb.setdAns(this.DTxt.getText());
            curProb.seteAns(this.ETxt.getText().trim().equals("") ? null : this.ETxt.getText());
            curProb.setaURL(this.AURLTxt.getText().trim().equals("") ? null : this.AURLTxt.getText());
            curProb.setbURL(this.BURLTxt.getText().trim().equals("") ? null : this.BURLTxt.getText());
            curProb.setcURL(this.CURLTxt.getText().trim().equals("") ? null : this.CURLTxt.getText());
            curProb.setdURL(this.DURLTxt.getText().trim().equals("") ? null : this.DURLTxt.getText());
            curProb.seteURL(this.EURLTxt.getText().trim().equals("") ? null : this.EURLTxt.getText());
            String answer = getSelectedAnswer();
            if (answer == null) {
                JOptionPane.showMessageDialog(this, "The problem does not have an answer set.");
                return false;
            }
            else curProb.setAnswer(answer);
        }
        return true;
    }

    // return true if there is an error with the selections, false otherwise
    private boolean setSelectedProblemSets() {
        Object[] vals = probSetList.getSelectedValues();
        if (vals.length < 1) {
            JOptionPane.showMessageDialog(this, "The problem does not have problem set.");
            return true;
        }
        else {
            List<PrePostTest> selectedTests = new ArrayList<PrePostTest>();
            for (Object v: vals) {
                PrePostTest t = (PrePostTest) v;
                selectedTests.add(t);
            }
            curProb.setProblemSets(selectedTests);
            return false;
        }
    }

    private String getSelectedAnswer() {
        if (this.ACheckBox.isSelected())
            return (curProb.getaAns() != null && !curProb.getaAns().equals("")) ? curProb.getaAns() : curProb.getaURL();
        else if (this.BCheckBox.isSelected())
            return (curProb.getbAns() != null && !curProb.getbAns().equals("")) ? curProb.getbAns() : curProb.getbURL();
        else if (this.CCheckBox.isSelected())
            return (curProb.getcAns() != null && !curProb.getcAns().equals("")) ? curProb.getcAns() : curProb.getcURL();
        else if (this.DCheckBox.isSelected())
            return (curProb.getdAns() != null && !curProb.getdAns().equals("")) ? curProb.getdAns() : curProb.getdURL();
        else if (this.ECheckBox.isSelected())
            return (curProb.geteAns() != null && !curProb.geteAns().equals("")) ? curProb.geteAns() : curProb.geteURL();
        else
            return null;
    }

    private void setFields(PrePostProblemDefn p) {
        this.probNameTxt.setText(p.getName());
        this.idTxt.setText(Integer.toString(p.getId()));
        if (p.getDescr() != null)
            this.probDescrPane.setText(p.getDescr());
        if (p.getUrl() != null)
            this.probURLTxt.setText(p.getUrl());

        setProblemSets(backEnd, p);
        // TODO make this set a selection in the JList
//        this.probSetComboBox.setSelectedItem(Integer.toString(p.getProblemSet()));
        if (p.getAnsType() == PrePostProblemDefn.SHORT_ANSWER) {
            enableMultiChoiceFields(false);
            setAnswerTypeFields(p.getAnswer(), true);
        } else if (p.getAnsType() == PrePostProblemDefn.MULTIPLE_CHOICE) {
            disableShortAnsFields();
            setAnswerTypeFields(null, false);
            setMultiChoiceFields(p);
        }
    }

    private void setProblemSets(BackEnd backEnd, PrePostProblemDefn p) {
        try {
            Vector<PrePostTest> allTests = backEnd.getPrePostTests();
            DefaultListModel dlm = new DefaultListModel();

            for (PrePostTest t : allTests)
                dlm.addElement(t);

            // Give the JList its model
            this.probSetList.setModel(dlm);
            // Now make the JList mark some elements as selected
            ListSelectionModel lsm = probSetList.getSelectionModel();
            for (int i = 0; i < allTests.size(); i++) {
                PrePostTest prePostTest = allTests.elementAt(i);
                if (inTest(p,prePostTest)) {
                    lsm.addSelectionInterval(i,i); // add element i to the selected items    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    // Return whether problem p is within the given prePostTest
    private boolean inTest(PrePostProblemDefn p, PrePostTest prePostTest) {
        for (PrePostTest t : p.getProblemSets()) {
            if (t.getId() == prePostTest.getId())
                return true;
        }
        return false;
    }

    private void setAnswerTypeFields(String answer, boolean shortAnsOn) {
        this.shortAnsTxt.setText(answer);
        this.shortAnsRadBut.setSelected(shortAnsOn);
        if (shortAnsOn)
            this.shortAnsTxt.setEditable(true);
        else
            enableMultiChoiceFields(true);
        this.multAnsRadBut.setSelected(!shortAnsOn);
        if (shortAnsOn)
            enableMultiChoiceFields(false);
        else
            disableShortAnsFields();
    }

    private void setMultiChoiceFields(PrePostProblemDefn p) {
        this.ATxt.setText(p.getaAns());
        this.BTxt.setText(p.getbAns());
        this.CTxt.setText(p.getcAns());
        this.DTxt.setText(p.getdAns());
        this.ETxt.setText(p.geteAns());
        this.AURLTxt.setText(p.getaURL());
        this.BURLTxt.setText(p.getbURL());
        this.CURLTxt.setText(p.getcURL());
        this.DURLTxt.setText(p.getdURL());
        this.EURLTxt.setText(p.geteURL());
        this.ACheckBox.setSelected((p.getaAns() != null && p.getaAns().equals(p.getAnswer())) || (p.getaURL() != null && p.getaURL().equals(p.getAnswer())));
        this.BCheckBox.setSelected((p.getbAns() != null && p.getbAns().equals(p.getAnswer())) || (p.getbURL() != null && p.getbURL().equals(p.getAnswer())));
        this.CCheckBox.setSelected((p.getcAns() != null && p.getcAns().equals(p.getAnswer())) || (p.getcURL() != null && p.getcURL().equals(p.getAnswer())));
        this.DCheckBox.setSelected((p.getdAns() != null && p.getdAns().equals(p.getAnswer())) || (p.getdURL() != null && p.getdURL().equals(p.getAnswer())));
        this.ECheckBox.setSelected((p.geteAns() != null && p.geteAns().equals(p.getAnswer())) || (p.geteURL() != null && p.geteURL().equals(p.getAnswer())));

    }

    private void disableShortAnsFields() {
        this.shortAnsTxt.setEditable(false);
    }

    private void enableMultiChoiceFields(boolean enableFields) {
        this.ATxt.setEditable(enableFields);
        this.BTxt.setEditable(enableFields);
        this.CTxt.setEditable(enableFields);
        this.DTxt.setEditable(enableFields);
        this.ETxt.setEditable(enableFields);
        this.AURLTxt.setEditable(enableFields);
        this.BURLTxt.setEditable(enableFields);
        this.CURLTxt.setEditable(enableFields);
        this.DURLTxt.setEditable(enableFields);
        this.EURLTxt.setEditable(enableFields);
        this.ACheckBox.setEnabled(enableFields);
        this.BCheckBox.setEnabled(enableFields);
        this.CCheckBox.setEnabled(enableFields);
        this.DCheckBox.setEnabled(enableFields);
        this.ECheckBox.setEnabled(enableFields);
    }


    private void onSave() {
        if (!copyFieldsIntoModel())
            return;
        try {
            saveModel();
            owner.updateList();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        dispose();
    }

    private void saveModel() throws SQLException {
        PrePostProblemDefn p;

        // We need to get the problem set we are working on from the owner of this
        // dialog.   It keeps a PrePostTest object which is the test that is currently being worked on 
        // on.
        // TODO  So we need to make it clear when a problem lives in more than one test!
        PrePostTest curProblemSet= this.owner.getCurTest();
        // if the problem with the given name does not exist in the test we are editing, insert it
//        if (curProblemSet != null &&
//                (p = backEnd.selectPrePostProblem(curProb.getName(), curProblemSet.getId())) == null) {

        if (isNewProblem)  {
            int id = backEnd.insertPrePostProblem(curProb);
            curProb.setId(id);
            // it already exists so update it
        } else
            backEnd.updatePrePostProblem(curProb.getId(), curProb);
    }


    private void onCancel() {
        owner.cancelEdit();
        dispose();
    }

    private static PrePostProblemDefn makeTestProblem() {
//        return new PrePostProblem("test prob","What is the color of the sky?",null,PrePostProblem.MULTIPLE_CHOICE,"c",
//                1,"red","green","blue","orange","white",null,null,null,null,null);
        return new PrePostProblemDefn();
    }

    public static void main(String[] args) {
        PrePostProblemDefn p = makeTestProblem();
        ProbEditorDialog dialog = new ProbEditorDialog(null, null, p);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == shortAnsRadBut) {
            setAnswerTypeFields("", true);
        } else if (e.getSource() == multAnsRadBut) {
            setAnswerTypeFields(this.shortAnsTxt.getText(), false); // leave short answer intact
        }

    }


}
