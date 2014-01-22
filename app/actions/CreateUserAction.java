package actions;

import models.User;

public class CreateUserAction extends Action {
    private User user;

    public CreateUserAction(User user) {
        this.user = user;
    }

    @Override
    protected void execute() {
        user.save();
    }
}
