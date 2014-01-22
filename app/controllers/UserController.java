package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import actions.CreateUserAction;
import actions.DeleteUserAction;

public class UserController extends Controller {
    static Form<User> UserForm = Form.form(User.class);

    public static Result users() {
        return ok(views.html.index.render(User.all(), UserForm));
    }

    public static Result newUser() {
        Form<User> filledForm = UserForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.index.render(User.all(), filledForm));
        } else {
            (new CreateUserAction(filledForm.get())).run();
            return redirect(routes.UserController.users());
        }
    }

    public static Result deleteUser(String username) {
        (new DeleteUserAction(User.get(username))).run();
        return redirect(routes.UserController.users());
    }

}
