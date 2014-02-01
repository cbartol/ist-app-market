package controllers;

import models.App;
import models.User;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class AppController extends Controller {
    static Form<App> AppForm = Form.form(App.class);

    public static Result apps() {
        return ok(views.html.app.apps.render(App.all()));
    }

    public static Result userApps(String username) {
        return ok(views.html.usercommon.userApps.render(User.get(username)));
    }

    public static Result deleteApp(Long id) {
        String currentUser = Application.getCurrentUsername();
        App app = App.get(id);
        if (app != null && app.authors.contains(User.get(currentUser))) {
            App.deleteApp(app);
        } else {
            //Don't have permissions
        }
        return redirect(routes.UserController.user(currentUser));
    }

    public static Result newApp() {
        String currentUser = Application.getCurrentUsername();
        if (currentUser.isEmpty()) {
            return badRequest();
        }
        Form<App> filledForm = AppForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.userprivate.newApp.render(User.get(currentUser), filledForm.bindFromRequest()));
        } else {
            App app = filledForm.get();
            App.create(app, User.get(Application.getCurrentUsername()));
            return redirect(routes.UserController.user(currentUser));
        }
    }

    public static Result newApplication() {
        String username = Application.getCurrentUsername();
        if (!username.isEmpty()) {
            return ok(views.html.userprivate.newApp.render(User.get(username), AppForm));
        }
        return redirect(routes.Application.index());
    }

    public static Result app(Long id) {
        App app = App.get(id);
        if (app != null && app.authors.contains(User.get(Application.getCurrentUsername()))) {
            return ok(views.html.userprivate.appManager.render(App.get(id)));
        } else {
            return ok(views.html.app.app.render(App.get(id), Html.empty()));
        }
    }

    public static Result search(String query) {
        return ok(views.html.app.apps.render(App.search(query)));
    }
}
