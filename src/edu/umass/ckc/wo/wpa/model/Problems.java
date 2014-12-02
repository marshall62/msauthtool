package edu.umass.ckc.wo.wpa.model;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 9:50:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Problems {
    private List<Problem> problems;
    private static Problems instance=null;

    public static Problems getInstance() {
        if (instance == null)
            instance = new Problems();
        return instance;
    }

    public static void resetInstance () {
        instance = null;
    }

    private Problems() {
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public void deleteHint(Hint hint) {
        Iterator iterator = problems.iterator();
        while (iterator.hasNext()) {
            Problem problem = (Problem) iterator.next();
            Iterator it2 = problem.getHintPaths().iterator();
            while (it2.hasNext()) {
                HintPath hintPath = (HintPath) it2.next();
                System.out.println("Deleting hint " + hint + " from path " + hintPath + " in problem " + problem );
                hintPath.deleteHint(hint);
                System.out.println("Resulting path: " + hintPath);
            }
        }
    }
}
