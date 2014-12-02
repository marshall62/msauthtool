package edu.umass.ckc.wo.wpa.gui;

import edu.umass.ckc.wo.wpa.model.Hint;

/**
 * Copyright (c) University of Massachusetts.  All rights reserved.
 * User: David Marshall
 * Date: Nov 19, 2004
 * Time: 5:59:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HintEdCallback {

    public void hintModified (Hint hint);

    public void hintDeleted(Hint hint);
}
