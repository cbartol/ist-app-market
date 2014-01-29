package controllers;

import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import pt.ist.fenixedu.sdk.FenixEduClient;
import pt.ist.fenixedu.sdk.FenixEduClientFactory;

import com.google.gson.JsonObject;

public class Application extends Controller {

    public static Result index() {

        return ok(views.html.index.render(Form.form(User.class)));
    }

    public static Result logout() {
        session().clear();
        flash("success", Messages.get("label.logout.message"));
        return redirect(routes.Application.index());
    }

    public static Result changeLang(String lang, String url) {
        changeLang(lang);
        return redirect(url);
    }

    public static Result signIn() {
        FenixEduClient client = FenixEduClientFactory.getSingleton();
        return redirect(client.getAuthenticationUrl());
    }

    public static Result callback() {
        String code = request().getQueryString("code");
        FenixEduClient client = FenixEduClientFactory.getSingleton();
        client.setCode(code);
        JsonObject obj = client.getPerson();
        String username = obj.get("username").getAsString();
        User user = User.get(username);

        if (user == null) {
            user = new User();
            user.username = username;
            user.name = obj.get("name").getAsString();
            user.email = obj.get("email").getAsString();
            User.create(user);
        }

        session("username", username);

        return redirect(routes.AppController.userApps(user.username));
    }

}
