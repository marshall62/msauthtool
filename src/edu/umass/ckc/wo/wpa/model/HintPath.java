package edu.umass.ckc.wo.wpa.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall   fgdfg
 * Date: Nov 19, 2004
 * Time: 9:51:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class HintPath {
    private List hints;
    private List safePath=null;
    private String type;
    private boolean modified=false;
    private boolean deleted =false;
    private boolean newPath =false;

    public static final String[] PATH_TYPES = new String[] {"analytic", "visual", "other"};

    public HintPath(List hints, String type) {
        if (hints == null)
            this.hints = new ArrayList();
        else {
            this.hints = hints;
            this.makeBackup();
        }
        setType(type);
    }

    /** Build a new hint path without any hints in it */
    public HintPath (String type) {
        this.hints = new ArrayList();
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List getHints() {
        return hints;
    }


    public void addHint(Hint hint) {
        hints.add(hint);
    }

    public boolean removeHint(Hint hint) {
        return hints.remove(hint);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNewPath() {
        return newPath;
    }

    public void setNewPath(boolean newPath) {
        this.newPath = newPath;
    }

    public String toString () {
        StringBuffer sb = new StringBuffer();
        sb.append("<");
        Iterator iterator = hints.iterator();
        while (iterator.hasNext()) {
            Hint h = (Hint) iterator.next();
            sb.append(h.getName() + ",");
        }
        sb.append(">");
        return sb.toString();
    }

    public void deleteHint(Hint hint) {
        hints.remove(hint);
    }

    /** Only call this method if you are constructing a hintPath from the database. It will build a
     * backup copy of the path so that a future call to update the hintpath will work
     * @param child
     */
    public void addToPath(Hint child) {
        hints.add(child);
        if (safePath != null)
            safePath.add(child);
    }

    /**
     * Called at particular times so that the backup copy represents what is in the database.
     * This is necessary so that when an update occurs the backup copy is used to delete the solutionPath
     * from the edu.umass.ckc.wo.wpa.db and then a new one is inserted
     */
    public void makeBackup () {
        setSafePath(hints);
    }

    public List getSafePath() {
        return safePath;
    }

    public void removeAll () {
        while (!this.hints.isEmpty()) {
            this.hints.remove(0);
        }
    }

    // make safePath be a clone of the given list of hints
    public void setSafePath(List hints) {
        safePath = new ArrayList();
        Iterator itr = hints.iterator();
        while (itr.hasNext()) {
            Object o = itr.next();
            safePath.add(o);
        }
    }

    public Hint getHint(int i) {
        if (i < hints.size())
            return (Hint) hints.get(i);
        else return null;
    }


    public void setHints(List hints) {
        this.hints = hints;
    }

    public boolean isEmpty() {
        return hints.isEmpty();
    }

    public int indexOf (Hint h) {
        return hints.indexOf(h);
    }

}
