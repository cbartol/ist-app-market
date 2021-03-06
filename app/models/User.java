package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity()
@Table(name = "fenix_user")
public class User extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public String username;

    public String name;
    public String email;

    @ManyToMany()
    public Set<App> applications;

    private static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    public static List<User> all() {
        return find.all();
    }

    public static User get(String username) {
        return (username != null) ? find.byId(username) : null;
    }

    public static void deleteUser(String username) {
        User user = get(username);
        for (App app : user.applications) {
            app.authors.remove(user);
            app.update();
        }
        user.applications.clear();
        user.update();
        user.delete();
    }

    public static void create(User user) {
        user.save();
    }

    public static void removeApp(User user, App app) {
        app.authors.remove(user);
        app.update();
        user.applications.remove(app);
        user.update();
    }

    public List<App> getApplications(Comparator<App> comparator) {
        List<App> result = new ArrayList<App>();
        result.addAll(applications);
        Collections.sort(result, comparator);
        return result;
    }

    public List<App> getApplications(Comparator<App> comparator, int maxCount) {
        List<App> allUserApps = getApplications(comparator);
        return allUserApps.subList(0, Math.min(allUserApps.size(), maxCount));
    }
}
