package org.definilizer.asm;

import static org.definilizer.asm.StaticInitSearchingStateMachine.State.*;

/**
 * User: dima
 * Date: Sep 13, 2008
 * Time: 11:23:54 AM
 */
class StaticInitSearchingStateMachine {
    enum State {
        WAIT_NEW,
        WAIT_DUP,
        WAIT_INVOKESPECIAL,
        WAIT_PUTSTATIC,
        DONE
    }

    private final String className;
    State state;

    public StaticInitSearchingStateMachine(final String className) {
        this.className = className;
        this.state = WAIT_NEW;
    }

    public boolean skipInstruction() {
        return state != WAIT_NEW;
    }

    public void gotNew(final String desc) {
        if (state == WAIT_NEW && className.equals(desc))
            state = WAIT_DUP;
        else
            state = WAIT_NEW;
    }

    public void gotDup() {
        if (state == WAIT_DUP)
            state = WAIT_INVOKESPECIAL;
        else
            state = WAIT_NEW;
    }

    public void gotInvokeSpecial(final String owner) {
        if (state == WAIT_INVOKESPECIAL && owner.contains(className)) {
            state = WAIT_PUTSTATIC;
        } else {
            state = WAIT_NEW;
        }
    }

    public void gotPutStatic(final String owner) {
        if (state == WAIT_PUTSTATIC && owner.contains(className)) {
            System.out.println("StaticInitSearchingStateMachine.gotInvokeSpecial");
            state = DONE;
        } else {
            state = WAIT_NEW;
        }
    }

    public void gotSomethingWrong() {
        state = WAIT_NEW;
    }

}
