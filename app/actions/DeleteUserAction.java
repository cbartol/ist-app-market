package actions;

import models.User;
import play.db.ebean.Model.Finder;

public class DeleteUserAction extends Action {
    private static Finder<String, User> find = new Finder<String, User>(String.class, User.class);
    private User user;

    public DeleteUserAction(User user) {
        this.user = user;
    }

    @Override
    protected void checkPreConditions() {
        //TODO check if user has apps
        //cannot delete user if he has apps 
    }

    @Override
    protected void execute() {
        //TODO it will throw an error if user has apps
        find.byId(user.istID).delete();
    }

}
