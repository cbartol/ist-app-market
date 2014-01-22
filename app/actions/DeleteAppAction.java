package actions;

import java.util.Set;

import models.App;
import models.User;
import play.db.ebean.Model.Finder;

public class DeleteAppAction extends Action {
    private static Finder<Long, App> find = new Finder<Long, App>(Long.class, App.class);
    private App app;

    public DeleteAppAction(App app) {
        this.app = app;
    }

    @Override
    protected void execute() {
        Set<User> authors = app.authors;
        for (User user : authors) {
            user.applications.remove(app);
            user.update();
        }
        app.authors.clear();
        app.update();
        find.ref(app.id).delete();
    }
}
