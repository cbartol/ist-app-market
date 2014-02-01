package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class UserController extends Controller {
    static Form<User> UserForm = Form.form(User.class);

    public static Result user(String username) {
        User user = User.get(username);
        if (user != null) {
            return ok(views.html.usercommon.user.render(user));
        }
        return redirect(routes.Application.index());
    }

    public static Result users() {
        return ok(views.html.userprivate.userManager.render(User.all(), UserForm));
    }

    public static Result deleteUser(String username) {
        if (session("username").equals(username)) {
            User.deleteUser(username);
            return redirect(routes.Application.logout());
        }
        return redirect(routes.UserController.users());
    }

}
