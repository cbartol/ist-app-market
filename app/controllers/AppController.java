package controllers;

import java.util.Set;

import models.App;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.common.collect.Sets;

public class AppController extends Controller {
    static Form<App> AppForm = Form.form(App.class);

    public static Result apps() {
        return ok(views.html.apps.render(Sets.newHashSet(App.all())));
    }

    public static Result userApps(String username) {
        return ok(views.html.userApps.render(User.get(username)));
    }

    public static Result deleteApp(Long id) {
        App.deleteApp(App.get(id));
        return redirect(routes.AppController.userApps(getUsername()));
    }

    public static Result newApp() {
        Form<App> filledForm = AppForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.newApp.render(User.get(getUsername()), filledForm.bindFromRequest()));
        } else {
            App app = filledForm.get();
            App.create(app, User.get(getUsername()));
            return redirect(routes.AppController.userApps(getUsername()));
        }
    }

    public static Result newApplication(String username) {
        return ok(views.html.newApp.render(User.get(username), AppForm));
    }

    public static Result app(Long id) {
        return ok(views.html.app.render(App.get(id)));
    }

    private static String getUsername() {
        return session().get("username");
    }

    public static Result search(String query) {
        Set<App> apps = App.search(query);
        return ok(views.html.apps.render(apps));
    }
}
