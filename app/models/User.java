package models;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Entity
public class User extends Model {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    public String username;

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
        find.byId(username).delete();
    }

    public Set<App> getApplications() {
        return Sets.newHashSet(Iterables.filter(App.all(), isUserApp));
    }

    private static Predicate<App> isUserApp = new Predicate<App>() {
        @Override
        public boolean apply(App app) {
            return app != null && app.authors.contains(this);
        }
    };
}
