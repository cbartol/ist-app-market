package models;

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
        return find.byId(username);
    }

    public static void deleteUser(String username) {
        User user = get(username);
        for (App app : user.applications) {
            app.authors.remove(user);
            app.update();
        }
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

}
