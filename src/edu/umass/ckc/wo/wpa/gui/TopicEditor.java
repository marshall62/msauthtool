package edu.umass.ckc.wo.wpa.gui;
/** will need to add this into MainFrame menu */

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.model.Topic;

import javax.swing.*;
import java.awt.event.*;

public class TopicEditor extends JDialog implements ActionListener {
    private JPanel contentPane;

    private JButton buttonCancel;
    private JTextField idTextfield;
    private JTextField nameTextfield;
    private JTextField resourceTextfield;
    private JCheckBox activeCheckBox;
    private JCheckBox CCMappedCheckBox;
    private JRadioButton SWFRadioButton;
    private JRadioButton HTMLRadioButton;
    private JTextPane summaryTextPane;
    private JButton buttonSave;
    private Topic topic;
    private ButtonGroup typeGroup;
    private BackEnd backEnd;
    private TopicListDialog owner;


    public TopicEditor (TopicListDialog owner, BackEnd be, Topic t) {
        this();
        this.owner = owner;
        this.backEnd = be;
        this.topic = t;
        if (t != null)
            setTopicFields();


    }

    public TopicEditor() {
        setSize(500,500);
        setTitle("Topic Editor");
        idTextfield.setEditable(false);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSave);
        typeGroup = new ButtonGroup();
        typeGroup.add(SWFRadioButton);
        typeGroup.add(HTMLRadioButton);
        HTMLRadioButton.setSelected(true);
        CCMappedCheckBox.setSelected(true);
        activeCheckBox.setSelected(true);
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        buttonSave.addActionListener(this);
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

    private void setTopicFields () {
        idTextfield.setText(Integer.toString(topic.getId()));
        nameTextfield.setText(topic.getName());
        resourceTextfield.setText(topic.getIntro());
        summaryTextPane.setText(topic.getSummary());
        activeCheckBox.setSelected(topic.isActive());
        if (topic.getType().equalsIgnoreCase("SWF"))
            SWFRadioButton.setSelected(true);
        else HTMLRadioButton.setSelected(true);
        CCMappedCheckBox.setSelected(topic.isCCMapped());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
        JButton b = (JButton) e.getSource();
        if (b == buttonSave) {
            Topic t = getTopicFromFields();
            // a t of null means one of the fields was invalid and the topic could not be built
            if (t == null)
                return ;
            boolean isNew = t.getId()==-1;
            this.backEnd.saveTopic(t);
            if (isNew)
                owner.addTopic(t);
            dispose();
        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateFields (String name) {
        boolean valid = true;
        if (name.trim().length() < 1) {
            JOptionPane.showMessageDialog(this,"You must give the topic a name");
            valid = false;
        }

        return valid;
    }

    private Topic getTopicFromFields() {
        String id = idTextfield.getText();
        String name = nameTextfield.getText();
        String resource = resourceTextfield.getText();
        String summary = summaryTextPane.getText();
        boolean valid = validateFields(name);
        if (!valid)
            return null;
        String ty="swf";
        if (HTMLRadioButton.isSelected())
            ty="html";
        boolean active = activeCheckBox.isSelected();
        boolean iscc = CCMappedCheckBox.isSelected();
        if (this.topic == null)
            return new Topic(name,id.equals("")?-1:Integer.parseInt(id),resource,"",summary,ty,active, iscc);
        else {
            topic.setName(name);
            topic.setIntro(resource);
            topic.setType(ty);
            topic.setSummary(summary);
            topic.setIsActive(active);
            topic.setIsCCMapped(iscc);
            return topic;
        }
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
        TopicEditor dialog = new TopicEditor();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
