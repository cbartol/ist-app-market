package controllers;

import models.User;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class UserController extends Controller {
    static Form<User> UserForm = Form.form(User.class);

    public static Result user(String username) {
        User user = User.get(username);
        if (user != null) {
            if (getCurrentUsername().equals(username)) {
                return ok(views.html.userprivate.userManager.render(user));
            } else {
                return ok(views.html.usercommon.user.render(user, Html.empty()));
            }
        }
        return redirect(routes.Application.index());
    }

    public static Result deleteUser(String username) {
        if (getCurrentUsername().equals(username)) {
            User.deleteUser(username);
            return redirect(routes.Application.logout());
        }
        return redirect(routes.Application.index());
    }

    public static String getCurrentUsername() {
        String username = session("username");
        if (username == null) {
            username = "";
        }
        return username;
    }
}
