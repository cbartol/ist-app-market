package controllers;

import actions.CreateUserAction;

import com.google.gson.JsonObject;

import models.Login;
import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import pt.ist.fenixedu.sdk.*;

public class Application extends Controller {
	
	public static FenixEduClient client = FenixEduClientFactory.getSingleton();

    public static Result index() {

        return ok(views.html.index.render(Form.form(User.class)));
    }

    public static Result login() {
        return ok(views.html.login.render(Form.form(Login.class)));
    }

    public static Result logout() {
        Login.logout(request().username());
        session().clear();
        flash("success", Messages.get("label.logout.message"));
        return redirect(routes.Application.index());
    }

    public static Result authenticate() {
        Form<Login> form = Form.form(Login.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(views.html.login.render(form));
        } else {
            session().clear();
            session("username", form.field("username").value());
            return redirect(routes.AppController.userApps(form.field("username").value()));
        }
    }

    public static Result register() {
        return ok(views.html.register.render(Form.form(User.class)));
    }

    public static Result changeLang(String lang, String url) {
        changeLang(lang);
        return redirect(url);
    }
    
    public static Result signIn() {
    	return redirect(client.getAuthenticationUrl());
    }
    
    public static Result callback() {
    	String code = request().getQueryString("code");
    	client.setCode(code);
    	JsonObject obj = client.getPerson();
    	User user = User.get(obj.get("username").getAsString());
    	
    	if (user == null) {
    		user = new User();
    		user.istID = obj.get("username").getAsString();
        	user.name = obj.get("name").getAsString();
        	user.email = obj.get("email").getAsString();
        	
        	CreateUserAction action = new CreateUserAction(user);
        	action.run();
    	}
    	
    	session("username", user.istID);
    	
    	return redirect(routes.AppController.userApps(user.istID));
    }

}
