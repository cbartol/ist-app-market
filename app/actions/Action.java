package actions;

public abstract class Action {
    final public void run() {
        checkPreConditions();
        execute();
        checkPosConditions();
    }

    protected abstract void execute();

    protected void checkPreConditions() {

    }

    protected void checkPosConditions() {

    }
}
