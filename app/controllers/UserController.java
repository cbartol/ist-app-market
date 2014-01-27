package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import actions.CreateUserAction;
import actions.DeleteUserAction;

public class UserController extends Controller {
    static Form<User> UserForm = Form.form(User.class);
    
    public static Result user(String username) {
    	//return redirect(routes.AppController.userApps(username));
    	return ok(views.html.user.render(User.get(username)));
    }
    
    public static Result users() {
        return ok(views.html.usersManager.render(User.all(), UserForm));
    }

    public static Result newUser() {
        Form<User> filledForm = UserForm.bindFromRequest();
        
        if(filledForm.hasErrors() || User.get(filledForm.field("username").value()) != null){
        	return badRequest(views.html.register.render(filledForm));
        } else {
            (new CreateUserAction(filledForm.get())).run();
            return redirect(routes.Application.login());
        }
    }

    public static Result deleteUser(String username) {
        (new DeleteUserAction(User.get(username))).run();
        return redirect(routes.UserController.users());
    }

}
