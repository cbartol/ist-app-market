package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

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
        	User.create(filledForm.get());
            return redirect(routes.UserController.users());
        }
    }

    public static Result deleteUser(String username) {
    	User.deleteUser(username);
        return redirect(routes.UserController.users());
    }
    
}
