package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.Topic;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TopicListDialog extends JDialog implements ActionListener, ListSelectionListener {
    private JPanel contentPane;
    private JButton buttonNew;
    private JButton buttonCancel;
    private JButton buttonModify;
    private JButton buttonDelete;
    private DefaultListModel topicsModel;
    private JList list1;
    private BackEnd be;

    private List<Topic> topics;

    public TopicListDialog (JFrame owner, BackEnd be) {
        this.be = be;
        setTitle("Topics");
        try {
            topics = be.getAllTopics();
            init();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public TopicListDialog() {

    }

    public void init () {
        setSize(500,800);
        setContentPane(contentPane);
        setModal(true);

        getRootPane().setDefaultButton(buttonNew);

        buttonNew.addActionListener(this);

        buttonDelete.addActionListener(this);

        buttonModify.addActionListener(this);

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

        setTopics();

    }

    private void setTopics () {
        Object[] a = this.topics.toArray();
        Arrays.sort(a);
        topicsModel = new DefaultListModel();
        for (int i = 0; i < a.length; i++) {
            Topic sk = (Topic) a[i];
            topicsModel.add(i,sk);
        }
        list1.setModel(topicsModel);
        list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
        list1.addListSelectionListener(this);

    }

    public void addTopic (Topic t) {
        topicsModel.add(topicsModel.size(),t);
    }



    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TopicListDialog dialog = new TopicListDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
//        if (b == saveBut) {
//            try {
//                commitDeletes();
//            } catch (SQLException e1) {
//                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//            setVisible(false);
//            this.dispose();
//        }
//        else if (b == cancelBut){
//            setVisible(false);
//            this.dispose();
//        }
         if (b == buttonNew)
            showTopicEditor();
        else if (b == buttonModify)
             showSelectedTopicEditor();
//        else if (b == delBut)
//            addSelectedSkillToDeleteList();
    }


    private void showTopicEditor() {
        TopicEditor ske = new TopicEditor(this,this.be, null);
        ske.setVisible(true);
    }

    private void showSelectedTopicEditor() {
        Topic t = (Topic) list1.getSelectedValue();

        TopicEditor ske = new TopicEditor(this,this.be, t);
        ske.setVisible(true);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
