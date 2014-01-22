package controllers;

import models.App;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import actions.CreateAppAction;
import actions.DeleteAppAction;

public class AppController extends Controller {
    static Form<App> AppForm = Form.form(App.class);
    private static User selectedUser;

    public static Result apps(String username) {
        selectedUser = User.get(username);
        return ok(views.html.newApp.render(selectedUser, AppForm));
    }

    public static Result deleteApp(Long id) {
        (new DeleteAppAction(App.get(id))).run();
        return apps(selectedUser.username);
    }

    public static Result newApp() {
        Form<App> filledForm = AppForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.newApp.render(selectedUser, filledForm.bindFromRequest()));
        } else {
            App app = filledForm.get();
            (new CreateAppAction(selectedUser, app)).run();
            return apps(selectedUser.username);
        }
    }
}
