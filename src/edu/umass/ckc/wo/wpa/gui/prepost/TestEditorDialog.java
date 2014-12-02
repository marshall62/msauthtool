package edu.umass.ckc.wo.wpa.gui.prepost;

import edu.umass.ckc.wo.wpa.model.PrePostTest;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.Dialog;
import java.sql.SQLException;

import edu.umass.ckc.wo.wpa.db.BackEnd;

public class TestEditorDialog extends JDialog {
    private JPanel contentPane;
    private JButton cancelBut;
    private JButton saveBut;
    private JCheckBox activeCheckBox;
    private JTextField nameTxt;
    private JTextField idTxt;
    private BackEnd backEnd;
    private  TestListDialog owner;
    private PrePostTest test;

    public TestEditorDialog(TestListDialog owner, BackEnd be, PrePostTest test) {
        super(owner,"Pre/Post Test", true);
        this.test = test;
        this.owner=owner;
        backEnd = be;
        doInit(test);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(saveBut);

        saveBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

    private void doInit(PrePostTest test) {
        setFields(test);
    }

    private void setFields(PrePostTest test) {
        this.nameTxt.setText(test.getName());
        this.idTxt.setText(Integer.toString(test.getId()));
        this.activeCheckBox.setSelected(test.isActive());
    }

    private void getFields (PrePostTest test) {
        test.setName(this.nameTxt.getText());
        test.setId(Integer.parseInt(this.idTxt.getText()));
        test.setActive(this.activeCheckBox.isSelected());
    }

    private void onOK() {
        getFields(test);
        try {
            if (test.getId() == -1) {
                int id= backEnd.insertPrePostTest(test);
                test.setId(id);
            }
            else backEnd.updatePrePostTest(test);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        owner.updateList(test);

        dispose();
    }

    private void onCancel() {
        owner.cancelEdit();
        dispose();
    }

    public static void main(String[] args) {
        TestEditorDialog dialog = new TestEditorDialog(null,null,null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
