package actions;

import models.App;
import models.User;

public class CreateAppAction extends Action {
    private User user;
    private App app;

    public CreateAppAction(User user, App app) {
        this.user = user;
        this.app = app;
    }

    @Override
    protected void execute() {
        app.authors.add(user);
        app.save();
        user.applications.add(app);
        user.update();
    }

}
