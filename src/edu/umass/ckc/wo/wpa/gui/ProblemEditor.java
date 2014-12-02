package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.db.BackEnd;
import edu.umass.ckc.wo.wpa.db.ProblemMgr;
import edu.umass.ckc.wo.wpa.model.Hint;
import edu.umass.ckc.wo.wpa.model.HintPath;
import edu.umass.ckc.wo.wpa.model.Problem;
import edu.umass.ckc.wo.wpa.model.Topic;

import javax.swing.*;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 7:57:36 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * ProblemEditor Class is a dialog that allows the user to edit the details of a problem, view/edit hint paths, and
 * launch other dialogs to edit hints and skills.
 */
public class ProblemEditor extends JDialog implements ActionListener, HintEdCallback {
    private JButton saveBut;
    private JButton cancelBut;
    private JButton delHintPathBut;
    private JButton newHintPathBut;
    private JTextField idTxt;
    private JTextField lastModifiedTxt;
    private JComboBox answerPulldown;
    private JTextField probNameTxt;
    private JPanel mainPanel;
    private MainFrame owner;
    private DefaultListModel hintSequenceModel = null;
    private int pathCounter = 0;


    public static final TreeSet TYPES = new TreeSet(Arrays.asList(new String[]{"analytic", "visual", "other"}));


    private Problem problem;
    private HintPath hintPath;
    private BackEnd backEnd;
    private JList hintSeqList;
    private JButton edHintBut;
    private JButton addHintBut;
    private JButton delHintBut;
    private JButton downBut;
    private JButton upBut;
    private JTextField nicknameTxt;
    private JTextField resourceTxt;
    private JTextField diffLevTxt;
    private JComboBox pathTypePulldown;
    private DefaultComboBoxModel hintPathListModel;
    private DefaultListModel topicListModel;
    private boolean modelChanging = false;
    private List alteredHints = new ArrayList();
    private JList orphanedHintList; // hints that go with this problem but are not in a hintpath.
    private DefaultListModel orphanedHintListModel;
    private JButton delAllOrphanedHintsBut;
    private JTextField avgIncTxt;
    private JTextField avgHintsTxt;
    private JTextField avgTimeTxt;
    private JLabel topicsLbl;
    private JLabel avgTimeLbl;
    private JLabel avgHintsLbl;
    private JLabel avgIncLabel;
    private JList topicList;
    private JCheckBox hasStrategicHintCheckBox;
    private JRadioButton unreleasedRadioButton;
    private JRadioButton readyRadioButton;
    private JTextField videoUrlText;
    private JTextField exampleProbIdText;
    private JRadioButton htmlRadioButton;
    private JRadioButton flashRadioButton;


    public ProblemEditor(MainFrame owner, String title, boolean modal, Problem p, BackEnd backEnd) throws HeadlessException {
        super(owner, title, modal);
        this.owner = owner;
        setSize(700, 600);
        if (p == null) {
            problem = new Problem(-1, "", null, "", Settings.user, "ready", null, null, "1", "0.5","0.5","0.5","0.5", false,"flash", null, null);
            pathCounter = 0;
        } else {
            problem = p;
            pathCounter = p.getHintPaths().size();
        }
        this.backEnd = backEnd;
        init(problem);
    }

    private void init(Problem p) {
        this.getContentPane().add(mainPanel);
        saveBut.addActionListener(this);
        cancelBut.addActionListener(this);
        edHintBut.addActionListener(this);
        addHintBut.addActionListener(this);
        delHintBut.addActionListener(this);
        delAllOrphanedHintsBut.addActionListener(this);
        upBut.addActionListener(this);
        upBut.setIcon(new ImageIcon(getClass().getResource("Up24.gif")));
        downBut.setIcon(new ImageIcon(getClass().getResource("Down24.gif")));
        downBut.addActionListener(this);
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        m.addElement("a");
        m.addElement("b");
        m.addElement("c");
        m.addElement("d");
        m.addElement("e");
        idTxt.setText(Integer.toString(p.getId()));
        answerPulldown.setModel(m);
        newHintPathBut.addActionListener(this);
        delHintPathBut.addActionListener(this);
        pathTypePulldown.addActionListener(this);
        pathTypePulldown.setModel(buildHintPathsModel(p.getHintPaths()));
        hasStrategicHintCheckBox.setSelected(p.hasStrategicHint());
        hasStrategicHintCheckBox.addActionListener(this);
        htmlRadioButton.addActionListener(this);
        flashRadioButton.addActionListener(this);
        ButtonGroup g = new ButtonGroup();
        g.add(readyRadioButton);
        g.add(unreleasedRadioButton);
        readyRadioButton.addActionListener(this);
        unreleasedRadioButton.addActionListener(this);
        unreleasedRadioButton.setSelected(true);
        ButtonGroup g2 = new ButtonGroup();
        g2.add(htmlRadioButton);
        g2.add(flashRadioButton);

        videoUrlText.setText(p.getVideo());
        exampleProbIdText.setText(p.getExampleId());


        setFields(p);
        // If there isn't a problem,  hint paths cannot be edited or created.   If this is a new problem, the user must save
        // and then modify it to put in a hint path
        if (p.getId() == -1) {
            newHintPathBut.setEnabled(false);
            delHintPathBut.setEnabled(false);
            hasStrategicHintCheckBox.setEnabled(false);

        }
    }



    private void setFields(Problem p) {
        if (p != null) {
            probNameTxt.setText(p.getName());
            nicknameTxt.setText(p.getNickname());
            resourceTxt.setText(p.getFlashfile());
            diffLevTxt.setText(p.getDiffLevel());
            avgIncTxt.setText(p.getAvgIncorrect());
            avgHintsTxt.setText(p.getAvgHints());
            avgTimeTxt.setText(p.getAvgTime());
            String status = p.getStatus();
            if (p.isReady())
                readyRadioButton.setSelected(true);
            else if (p.isUnreleased())
                unreleasedRadioButton.setSelected(true);
            if (p.getType().equalsIgnoreCase(Problem.FLASH))
                flashRadioButton.setSelected(true);
            else if (p.getType().equalsIgnoreCase(Problem.HTML5))
                htmlRadioButton.setSelected(true);
            answerPulldown.setSelectedItem(p.getAnswer());
            lastModifiedTxt.setText(p.getLastModifier().toString());

            List paths = p.getHintPaths();
            HintPath path = setFirstPath(paths);
            setHintPathFields(paths, path);
            setHintSequence();
            setOrphanedHints();
            setTopics(p);

        }
    }



    private void setTopics(Problem p) {
        // put all topics in the list
        try {
                topicList.setModel(buildTopicsListModel(backEnd.getAllTopics()));
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // if the problem already has selected topics, set the selected indices in the topicList so they are highlighted.
        if (p.getTopics() != null && p.getTopics().size() > 0)  {
            List<Integer> indices = new ArrayList<Integer>();
            for (Topic t : p.getTopics()) {
                for (int i=0; i<topicList.getModel().getSize(); i++) {
                    if (((Topic) topicList.getModel().getElementAt(i)).getId() == t.getId())
                        indices.add(i);
                }
            }
            int[] a = new int[indices.size()];
            for (int i=0;i<indices.size();i++)
                a[i]=indices.get(i);
            topicList.setSelectedIndices(a);
        }

    }

    /**
     * Determine the orphaned hints and stick them in a list so the user can delete them if necessary.
     */
    private void setOrphanedHints() {
        List orphanedHints = new ArrayList();
        try {
            Collection<Hint> hints;
            if (problem.getId() == -1)
                return;
            hints = backEnd.getProblemHints(problem.getId());
            for (Hint hint : hints) {
                List<HintPath> paths = problem.getHintPaths();
                boolean inPath = false;
                for (HintPath path : paths) {
                    if (path.indexOf(hint) != -1)    {
                        inPath = true;
                        break;
                    }
                }
                if (!inPath)
                    orphanedHints.add(hint);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        setOrphanedHintListModel(orphanedHints);
    }

    private void setOrphanedHintListModel(List<Hint> orphanedHints) {
        orphanedHintListModel = new DefaultListModel();
        for (Hint h: orphanedHints)
            orphanedHintListModel.addElement(h);
        orphanedHintList.setModel(orphanedHintListModel);
    }

    private void addToOrphanedHintList(Hint hint) {
        orphanedHintListModel.addElement(hint);
    }

    /**
     * Return the first non-deleted path
     */
    private HintPath setFirstPath(List paths) {
        Iterator itr = paths.iterator();
        while (itr.hasNext()) {
            HintPath path = (HintPath) itr.next();
            if (!path.isDeleted()) {
                return hintPath = path;
            }
        }

        return hintPath = null;
    }

    private int numHintPaths(List paths) {
        Iterator iterator = paths.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            HintPath path = (HintPath) iterator.next();
            if (!path.isDeleted())
                i++;
        }
        return i;
    }

    /**
     * When a hint path is add/mod/del this will alter the GUI to reflect those changes
     */
    private void setHintPathFields(List paths, HintPath selectedPath) {
        int n = numHintPaths(paths); // count the non-deleted paths
        enableHintButtons(n != 0);
        String type = (selectedPath != null) ? selectedPath.getType() : null;
        if (type != null)
            pathTypePulldown.setSelectedItem(selectedPath.getType());
        setHintSequence();

    }

    private void setHintSequence() {
        if (hintPath == null) {
            if (hintSequenceModel != null)
                hintSequenceModel.removeAllElements();
            return;
        }
        List hints = hintPath.getHints();
        hintSequenceModel = new DefaultListModel();
        Iterator iterator = hints.iterator();
        while (iterator.hasNext()) {
            Hint hint = (Hint) iterator.next();
            hintSequenceModel.addElement(hint);
        }
        hintSeqList.setModel(hintSequenceModel);
    }

    private void deleteHintPath() {
        if (hintPath == null) return;
        hintPath.setDeleted(true);
        modelChanging = true;
        hintPathListModel.removeElement(hintPath.getType());
        modelChanging = false;
        setHintPathFields(problem.getHintPaths(), setFirstPath(problem.getHintPaths()));
    }

    private DefaultComboBoxModel buildHintPathsModel(List paths) {
        hintPathListModel = new DefaultComboBoxModel();
        Iterator itr = paths.iterator();
        while (itr.hasNext()) {
            HintPath p = (HintPath) itr.next();
            String type = p.getType();
            if (!p.isDeleted())
                hintPathListModel.addElement(type);
        }
        return hintPathListModel;
    }

    private DefaultListModel buildTopicsListModel(List<Topic> topics) {
        topicListModel = new DefaultListModel();
        for (Topic t : topics)
            topicListModel.addElement(t);
        return topicListModel;
    }


    private void addHintPath() {
        // at most two paths allowed
        if (pathCounter == 2)
            return;
        String pathName = Integer.toString(++pathCounter);
        if (hintPathListModel.getIndexOf(pathName) == -1) {
            modelChanging = true;
            hintPathListModel.addElement(pathName);
            modelChanging = false;
        }
        hintPath = new HintPath(pathName); // makes the new path the selected path
        hintPath.setNewPath(true); //
        problem.addHintPath(hintPath);
        setHintPathFields(problem.getHintPaths(), hintPath);
    }

    private Set getPathTypes() {
        Set s = new HashSet();
        Iterator iterator = problem.getHintPaths().iterator();
        while (iterator.hasNext()) {
            HintPath path = (HintPath) iterator.next();
            if (!path.isDeleted())
                s.add(path.getType());
        }
        return s;
    }



    private void enableHintButtons(boolean b) {
        addHintBut.setEnabled(b);
        delHintBut.setEnabled(b);
        edHintBut.setEnabled(b);
    }


    private void addHint() {
        // note that the hintPath is marked as modified in the callback function hintModified
        new HintEditor(this, "Create Hint", true, null, backEnd, problem).setVisible(true);
    }

    private void deleteSelectedHint() {
        Hint hint = (Hint) hintSeqList.getSelectedValue();
        List hints = hintPath.getHints();
        // if the hint being removed is a root, the next one becomes the root
        if (hints.size() > 1 && hints.get(0) == hint) {
            ((Hint) hints.get(1)).setRoot(true);
            hint.setRoot(false);
            addToAlteredHintList((Hint) hints.get(1));
        }
        hintSequenceModel.removeElement(hint);
        hintPath.removeHint(hint);
        hintPath.setModified(true);
        HintPath other = getOtherHintPath(hintPath);
        if (other == null || (other != null && other.indexOf(hint) == -1)) {
            addToOrphanedHintList(hint);
        }

    }


    private void getProblemFields() {
        problem.setName(this.probNameTxt.getText().trim());
        problem.setNickname(this.nicknameTxt.getText().trim());
        problem.setFlashfile(this.resourceTxt.getText().trim());
        problem.setSourceId("1");
        problem.setAnswer((String) this.answerPulldown.getSelectedItem());
        if (readyRadioButton.isSelected())
            problem.setStatus(Problem.STATUS_READY);
        else if (unreleasedRadioButton.isSelected())
            problem.setStatus(Problem.STATUS_UNRELEASED);

        if (htmlRadioButton.isSelected())
            problem.setType(Problem.HTML5);
        else if (flashRadioButton.isSelected())
            problem.setType(Problem.FLASH);

        problem.setDiffLevel(this.diffLevTxt.getText().trim());
        problem.setAvgIncorrect(this.avgIncTxt.getText().trim());
        problem.setAvgHints(this.avgHintsTxt.getText().trim());
        problem.setAvgTime(this.avgTimeTxt.getText().trim());
        problem.setHasStrategicHint(hasStrategicHintCheckBox.isSelected());
        problem.setType(htmlRadioButton.isSelected() ? Problem.HTML5 : Problem.FLASH);
        problem.setExample(this.exampleProbIdText.getText());
        problem.setVideoURL(this.videoUrlText.getText());
        addSelectedTopics();
//todo need to update the hints too
    }

    /**
     * callback from HintEditor when it's done saving changes to a hint.  This component needs to
     * repaint the hint sequence list.  The hint editor may have created a new hint in which case we
     * need to add the hint to the edu.umass.ckc.wo.wpa.model and we need to update the HintPath to contain this new hint.
     * Alternatively the HintEditor may have just modified a hint already in the HintPath. In that case we
     * just need to repaint the list.
     */
    public void hintModified(Hint hint) {
        if (!hintSequenceModel.contains(hint)) {
            hintSequenceModel.addElement(hint);
            if (hintPath.isEmpty()) {
                hint.setRoot(true);
                addToAlteredHintList(hint);
            }
            hintPath.addHint(hint);
            hintPath.setModified(true);
        }
        hintSeqList.repaint();
    }


    // callback when the hint editor deleted the hint.  Should remove it from the hint sequence edu.umass.ckc.wo.wpa.model.
    public void hintDeleted(Hint hint) {
        hintSequenceModel.removeElement(hint);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveBut) {
            boolean b = false;
            try {
                b = saveProblem();
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if (!b)
                return;
            this.setVisible(false);
            this.dispose();
        } else if (e.getSource() == cancelBut) {
            restoreHintPaths();
            this.setVisible(false);
            this.dispose();
        } else if (e.getSource() == edHintBut)
            editHint();
        else if (e.getSource() == newHintPathBut)
            addHintPath();
        else if (e.getSource() == delHintPathBut)
            deleteHintPath();
        else if (e.getSource() == upBut || e.getSource() == downBut)
            changeHintPosition(e);
        else if (e.getSource() == addHintBut)
            addHint();
        else if (e.getSource() == delHintBut)
            deleteSelectedHint();
        else if (e.getSource() == pathTypePulldown && !modelChanging) {
            setCurrentHintPath((String) pathTypePulldown.getSelectedItem());
        }
        else if (e.getSource() == delAllOrphanedHintsBut) {
            deleteSelectedOrphanedHints();
        }

        else if (e.getSource() == unreleasedRadioButton &&
            unreleasedRadioButton.isSelected())
            problem.setStatus(Problem.STATUS_UNRELEASED) ;
        else if  (e.getSource() == readyRadioButton &&
                readyRadioButton.isSelected())
            problem.setStatus(Problem.STATUS_READY);

        else if (e.getSource() == htmlRadioButton &&
                htmlRadioButton.isSelected())
            problem.setType(Problem.HTML5) ;
        else if  (e.getSource() == flashRadioButton &&
                flashRadioButton.isSelected())
            problem.setType(Problem.FLASH);


    }

    private void addSelectedTopics() {
            problem.clearTopics();
            Object[] topics = topicList.getSelectedValues();
            for (int i = 0; i < topics.length; i++) {
                Topic topic = (Topic) topics[i];
                problem.addTopic(topic);
            }
        }


    /**
     * Handle deletion of orphaned hints.  Remove them from the edu.umass.ckc.wo.wpa.db and from the list of orphaned hints.
     */
    private void deleteSelectedOrphanedHints() {
        Object[] hints = orphanedHintList.getSelectedValues();
        for (int i = 0; i < hints.length; i++) {
            Hint hint = (Hint) hints[i];
            try {
                orphanedHintListModel.removeElement(hint);
                boolean success = backEnd.deleteHint(hint);
                if (!success) {
                    JOptionPane.showMessageDialog(this, "Hint " + hint + " was not successfully deleted.  It must be part" +
                            "of a hint path in some other problem (which is an incorrect use of a hint).");
                }
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void restoreHintPaths() {
        Iterator iterator = problem.getHintPaths().iterator();
        while (iterator.hasNext()) {
            HintPath path = (HintPath) iterator.next();
            path.setHints(path.getSafePath());
            path.makeBackup();
        }
    }


    private HintPath getSelectedHintPath(String hintPathType) {
        Iterator iterator = problem.getHintPaths().iterator();
        while (iterator.hasNext()) {
            HintPath path = (HintPath) iterator.next();
            if (path.getType().equals(hintPathType))
                return path;
        }
        return null;
    }

    private void setCurrentHintPath(String hintPathType) {
        hintPath = getSelectedHintPath(hintPathType);
        setHintSequence();
    }

    private void changeHintPosition(ActionEvent e) {
        hintPath.setModified(true);
        Hint h = (Hint) hintSeqList.getSelectedValue();
        if (h == null) return;
        moveHint(h, e.getSource() == upBut);
        changeHintPath(hintPath, hintSequenceModel);
    }

    private HintPath getOtherHintPath(HintPath thispath) {
        Iterator paths = problem.getHintPaths().iterator();
        HintPath other = null;
        while (paths.hasNext()) {
            HintPath path = (HintPath) paths.next();
            if (path != thispath)
                other = path;
        }
        return other;
    }

    private boolean isRootOfOtherPath(HintPath thispath, Hint h) {
        HintPath other = getOtherHintPath(thispath);
        if (other != null && other.getHints().size() > 0 && other.getHints().get(0) == h)
            return true;
        return false;
    }

    private void moveHint(Hint h, boolean isUp) {
        int ix = hintSequenceModel.indexOf(h);
        int max = hintSequenceModel.getSize();
        if (isUp && ix > 0) {
            Hint prev = (Hint) hintSequenceModel.elementAt(ix - 1);
            // if we are moving the second one in the list up, then the first one is
            // no longer a root unless it is a root in the other path
            if ((ix - 1 == 0) && !isRootOfOtherPath(hintPath, prev)) {
                prev.setRoot(false);
                addToAlteredHintList(prev);
            }
            // if we are moving the second one up, it becomes a root.
            if ((ix - 1) == 0) {
                h.setRoot(true);
                addToAlteredHintList(h);
            }
            hintSequenceModel.removeElementAt(ix);
            hintSequenceModel.add(ix - 1, h);
            hintSeqList.setSelectedIndex(ix - 1);
        } else if (!isUp && ix < max - 1) {
            // if the first one gets moved down in a list with more than one and its not the root
            // of another path, then it is no longer a root
            if (ix == 0 && max > 1 && !isRootOfOtherPath(hintPath, h)) {
                h.setRoot(false);
                addToAlteredHintList(h);
            }
            // if the first one gets moved down then the new #1 is set to a root
            if (ix == 0 && max > 1) {
                ((Hint) hintSequenceModel.elementAt(ix + 1)).setRoot(true);
                addToAlteredHintList((Hint) hintSequenceModel.elementAt(ix + 1));
            }
            hintSequenceModel.removeElementAt(ix);
            hintSequenceModel.add(ix + 1, h);
            hintSeqList.setSelectedIndex(ix + 1);
        }
    }


    // when the hint sequence is altered using the up or down button we need to reorder the current
    // hint Path object that is being edited
    private void changeHintPath(HintPath hintPath, DefaultListModel hintSequenceModel) {
        hintPath.removeAll();
        for (int i = 0; i < hintSequenceModel.size(); i++) {
            Hint h = (Hint) hintSequenceModel.elementAt(i);
            hintPath.addHint(h);
        }
    }


    private void editHint() {
        Hint h = (Hint) hintSeqList.getSelectedValue();
        if (h != null) {
            HintEditor he = new HintEditor(this, "Edit Hint", true, h, backEnd, problem);
            he.setVisible(true);
        }
    }

    private boolean validateProblem() {
        if (!(problem.getName() != null && !problem.getName().trim().equals("") &&
                problem.getAnswer() != null)) {
            JOptionPane.showMessageDialog(this, "Problem must have a name and answer before it can be saved.");
            return false;
        }
        else if (problem.getSourceId() == null || problem.getSourceId().trim().equals(""))
        {
             JOptionPane.showMessageDialog(this, "Problem must have a numeric source_id before it can be saved.");
             return false;
        }
        else if (problem.getExampleId() != null && !problem.getExampleId().trim().equals("")) {
            try {
                int pid= Integer.parseInt(problem.getExampleId());
                Problem p = ProblemMgr.getProblem(pid);
                if (p != null)
                    return true;
                else {
                    JOptionPane.showMessageDialog(this, "Example must the id of another problem in the system.");
                    return false;
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Example must be integer ID.");
                return false;
            }
        }
        else
            return true;
    }

    private boolean saveProblem() throws SQLException {
        getProblemFields();  // write the values from the form into the Problem object
        if (!validateProblem())
            return false;
        problem.setLastModified(Settings.user);
        if (problem.isNew()) {
            int id = backEnd.insertProblem(problem);
            problem.setId(id);
            setProblemIdsOnHints(problem);
            owner.addNewProblem(problem);
        } else
            backEnd.updateProblem(problem);
        saveHintPaths(problem.getHintPaths());
        updateAlteredHints();
        return true;
    }

    private void updateAlteredHints() {
        Iterator i = this.alteredHints.iterator();
        while (i.hasNext()) {
            Hint h = (Hint) i.next();
            try {
                backEnd.updateHint(h);
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    /**
     * When a new problem is created in the GUI its row is not added to the database until the form
     * is closed.  However hints and hintPaths that are part of the problem are built and saved to the
     * database prior to saving the problem.  Therefore at the time the problem is commited to the edu.umass.ckc.wo.wpa.db,
     * the Hints need to be updated to have the problem's ID rather than -1.
     *
     * @param problem
     */
    private void setProblemIdsOnHints(Problem problem) {
        List hintPaths = problem.getHintPaths();
        Iterator itr = hintPaths.iterator();
        while (itr.hasNext()) {
            HintPath path = (HintPath) itr.next();
            Iterator ii = path.getHints().iterator();
            while (ii.hasNext()) {
                Hint hint = (Hint) ii.next();
                hint.setProbId(problem.getId());
                try {
                    this.backEnd.updateHint(hint);
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    private void saveHintPaths(List hintPaths) throws SQLException {
        Iterator it = hintPaths.iterator();
        while (it.hasNext()) {
            HintPath path = (HintPath) it.next();
            if (path.isNewPath() && path.isDeleted())
                it.remove(); // this path was just created and deleted in this editor so only need to remove it in the object
            else if (path.isNewPath()) {
                // make sure the first hint in a new path is marked as a root and all the others are not
//                setHintsInPathRootStatus(path);
                if (backEnd.insertHintPath(problem, path)) {
                    path.setNewPath(false);
                    path.setModified(false);
                }
            } else if (path.isDeleted()) {
                Hint h = path.getHint(0);
                if (h != null && !isRootOfOtherPath(path, h)) {
                    h.setRoot(false);
                    addToAlteredHintList(h);
                }
                if (backEnd.deleteHintPath(problem, path)) {
                    it.remove();
                }
            } else if (path.isModified()) {
//                setHintsInPathRootStatus(path);
                if (backEnd.updateHintPath(problem, path)) {
                    path.setModified(false);
                }
            }
        }
    }

    private void addToAlteredHintList(Hint h) {
        if (this.alteredHints.indexOf(h) == -1)
            this.alteredHints.add(h);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
