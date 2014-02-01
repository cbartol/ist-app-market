package controllers;

import java.util.TreeSet;

import models.App;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.common.collect.Sets;

public class AppController extends Controller {
    static Form<App> AppForm = Form.form(App.class);

    public static Result apps() {
        return ok(views.html.app.apps.render(Sets.newTreeSet(App.all())));
    }

    public static Result userApps(String username) {
        return ok(views.html.usercommon.userApps.render(User.get(username)));
    }

    public static Result deleteApp(Long id) {
        App.deleteApp(App.get(id));
        return redirect(routes.AppController.userApps(getUsername()));
    }

    public static Result newApp() {
        if (getUsername() == null) {
            return badRequest();
        }
        Form<App> filledForm = AppForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.userprivate.newApp.render(User.get(getUsername()), filledForm.bindFromRequest()));
        } else {
            App app = filledForm.get();
            App.create(app, User.get(getUsername()));
            return redirect(routes.AppController.userApps(getUsername()));
        }
    }

    public static Result newApplication() {
        String username = getUsername();
        if (username != null) {
            return ok(views.html.userprivate.newApp.render(User.get(username), AppForm));
        }
        return redirect(routes.Application.index());
    }

    public static Result app(Long id) {
        return ok(views.html.app.app.render(App.get(id)));
    }

    private static String getUsername() {
        return session().get("username");
    }

    public static Result search(String query) {
        TreeSet<App> apps = App.search(query);
        return ok(views.html.app.apps.render(apps));
    }
}
