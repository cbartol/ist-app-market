package controllers;

import java.io.File;
import java.text.DecimalFormat;

import models.App;
import models.Comment;
import models.User;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class AppController extends Controller {
    static Form<App> AppForm = Form.form(App.class);

    public static Result userApps(String username) {
        return ok(views.html.usercommon.userApps.render(User.get(username)));
    }

    public static Result deleteApp(Long id) {
        String currentUser = Application.getCurrentUsername();
        App app = App.get(id);
        if (app != null && app.authors.contains(User.get(currentUser))) {
            App.deleteApp(app);
        } else {
            //Don't have permissions
            return unauthorized();
        }
        return redirect(routes.UserController.user(currentUser));
    }

    public static Result newApp() {
        String currentUsername = Application.getCurrentUsername();
        if (currentUsername.isEmpty()) {
            return unauthorized();
        }
        Form<App> filledForm = AppForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.userprivate.newApp.render(User.get(currentUsername), filledForm.bindFromRequest()));
        } else {
            App app = filledForm.get();
            File file = getAppLogoFromRequest();
            if (file == null) {
                return badRequest(views.html.userprivate.newApp.render(User.get(currentUsername), filledForm.bindFromRequest()));
            }
            App.create(app, file, User.get(Application.getCurrentUsername()));
            return redirect(routes.UserController.user(currentUsername));
        }
    }

    public static Result newApplication() {
        String username = Application.getCurrentUsername();
        if (!username.isEmpty()) {
            return ok(views.html.userprivate.newApp.render(User.get(username), AppForm));
        }
        return redirect(routes.Application.index());
    }

    public static Result app(Long id) {
        App app = App.get(id);
        if (app == null) {
            return redirect(routes.Application.index());
        } else if (app.authors.contains(User.get(Application.getCurrentUsername()))) {
            return ok(views.html.userprivate.appManager.render(App.get(id)));
        } else {
            return ok(views.html.app.app.render(App.get(id), Html.empty()));
        }
    }

    private static File getAppLogoFromRequest() {
        FilePart filePart = request().body().asMultipartFormData().getFile("app.logo");
        if (filePart == null) {
            flash("error", "No picture for upload");
            return null;
        }
        String contentType = filePart.getContentType();
        if (!contentType.contains("image/png")) {
            flash("error", "Invalid file type (must be '.png')");
            return null;
        }
        File file = filePart.getFile();
        if (file.length() > App.MAX_IMAGE_SIZE) {
            flash("error", "File too large (max: " + readableFileSize(App.MAX_IMAGE_SIZE) + ")");
            return null;
        }
        return file;
    }

    public static Result getLogo(long id) {
        final App app = App.get(id);
        return ok(app.getLogo());
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static Result addComment(Long id) {
        String currentUsername = Application.getCurrentUsername();
        if (currentUsername.isEmpty()) {
            return unauthorized();
        }
        Form<Comment> filledForm = Form.form(Comment.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest();
        } else {
            Comment comment = filledForm.get();
            Comment.create(comment, App.get(id), User.get(currentUsername));
            return ok(views.html.app.comment.render(comment));
        }
    }

    public static Result toggleLikeOnComment(Long commentId) {
        String currentUsername = Application.getCurrentUsername();
        Comment comment = Comment.get(commentId);
        if (!currentUsername.isEmpty()) {
            Comment.toggleLike(comment, User.get(currentUsername));
        }
        return ok(views.html.app.comment.render(comment));
    }

    public static Result rate(Long appId, Integer rate) {
        App app = App.get(appId);
        User user = User.get(Application.getCurrentUsername());
        App.rate(app, user, rate.shortValue());
        //TODO try to use ajax to refresh the rate section
        //see 'addComment' and 'toggleLikeOnComment' functions
        return redirect(routes.AppController.app(appId));
    }
}
