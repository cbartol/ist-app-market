package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class UserController extends Controller {
    static Form<User> UserForm = Form.form(User.class);

    public static Result user(String username) {
        //return redirect(routes.AppController.userApps(username));
        return ok(views.html.user.render(User.get(username)));
    }

    public static Result users() {
        return ok(views.html.usersManager.render(User.all(), UserForm));
    }

    public static Result deleteUser(String username) {
        User.deleteUser(username);
        return redirect(routes.UserController.users());
    }

}
