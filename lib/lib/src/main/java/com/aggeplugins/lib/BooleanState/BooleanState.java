/**
 * @file BooleanState.java
 * @class BooleanState
 * Virtual interface for BooleanStates to inherit.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-07-02
 *
 */

package com.aggeplugins.lib.BooleanState;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.BooleanState.*;
import com.aggeplugins.lib.export.*;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public abstract class BooleanState<T> {
    /**
     * All virtual methods are boolean, return TRUE when State is complete.
     */
    public abstract boolean run();
    //public abstract boolean handleEvent();

    protected T ctx;

    public BooleanState(T ctx) {
        this.ctx = ctx;
    }
}
