package controllers;

import java.util.HashMap;
import java.util.Map;

import models.App;
import models.Search;
import models.User;
import play.Routes;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import pt.ist.fenixedu.sdk.FenixEduClient;
import pt.ist.fenixedu.sdk.FenixEduClientFactory;
import pt.ist.fenixedu.sdk.auth.OAuthAuthorization;
import pt.ist.fenixedu.sdk.exception.FenixEduClientException;

import com.google.gson.JsonObject;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render(null));
    }

    public static Result logout() {
        //TODO somehow invalidate the token on fénix (can't be done yet)
        session().clear();
        flash("success", Messages.get("label.logout.message"));
        return redirect(routes.Application.index());
    }

    public static Result changeLang(String lang, String url) {
        changeLang(lang);
        return redirect(url);
    }

    /**
     * The callback url is to redirect the user to the page where he was before he clicked the login button
     * (it has nothing to do with the fenixedu.callback.url property value)
     */
    public static Result signIn(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.equals("")) {
            return redirect(routes.Application.index());
        }
        session("callback", callbackUrl);
        String url = "";
        try {
            FenixEduClient client = FenixEduClientFactory.getSingleton();
            url = client.getAuthenticationUrl();
        } catch (FenixEduClientException e) {
            e.printStackTrace();
            return badRequest();
        }
        return redirect(url);
    }

    public static Result callback() {
        String url = session("callback");
        session().remove("callback");
        String code = request().getQueryString("code");
        JsonObject obj;
        String accessToken = "";
        String refreshToken = "";
        try {
            FenixEduClient client = FenixEduClientFactory.getSingleton();
            client.setCode(code);
            accessToken = client.getAuthorization().asOAuthAuthorization().getOAuthAccessToken();
            refreshToken = client.getAuthorization().asOAuthAuthorization().getOAuthRefreshToken();
            obj = client.getPerson();
        } catch (FenixEduClientException e) {
            e.printStackTrace();
            return badRequest();
        }
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
        session(OAuthAuthorization.ACCESS_TOKEN, accessToken);
        session(OAuthAuthorization.REFRESH_TOKEN, refreshToken);

        if (url != null) {
            return redirect(url);
        } else {
            return redirect(routes.AppController.userApps(user.username));
        }
    }

    public static Result search() {
        Form<Search> filledForm = Form.form(Search.class).bindFromRequest();
        String originalQuery = filledForm.get().query;
        if (originalQuery.replace(" ", "").isEmpty()) {
            return redirect(routes.Application.index());
        }
        String query = originalQuery.replace("_", "\\_").replace("%", "\\%");
        //avoiding trolls
        if (originalQuery.length() > 40) {
            originalQuery = originalQuery.substring(0, 40) + "...";
        }
        return ok(views.html.searchResult.render(App.search(query), User.search(query), originalQuery));
    }

    public static String getCurrentUsername() {
        String username = session("username");
        if (username == null) {
            username = "";
        }
        return username;
    }

    /**
     * Returns a FenixEduClient with the current user credentials.
     * If it returns null the user must be redirected to the login page
     */
    public static FenixEduClient getCurrentFenixEduClient() {
        String username = session("username");
        String accessToken = session(OAuthAuthorization.ACCESS_TOKEN);
        String refreshToken = session(OAuthAuthorization.REFRESH_TOKEN);
        User user = User.get(username);
        if (user == null || accessToken == null || refreshToken == null) {
            return null;
        }
        FenixEduClient client = null;
        try {
            client = FenixEduClientFactory.getSingleton();
            Map<String, String> credentialMap = new HashMap<String, String>();
            credentialMap.put(OAuthAuthorization.ACCESS_TOKEN, accessToken);
            credentialMap.put(OAuthAuthorization.REFRESH_TOKEN, refreshToken);
            client.getAuthorization().setCredentials(credentialMap);
        } catch (FenixEduClientException e) {
            e.printStackTrace();
            return null;
        }
        return client;
    }

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("myJsRoutes", routes.javascript.AppController.toggleLikeOnComment(),
                routes.javascript.AppController.addComment(), routes.javascript.AppController.rate()));
    }
}
