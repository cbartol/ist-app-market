package controllers;

import models.Login;
import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

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

}
