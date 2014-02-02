package models;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

@Entity
public class App extends Model implements Comparable<App> {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    @Required
    public String name;

    @ManyToMany()
    public Set<User> authors;

    public String description;

    private Date creationDate;

    /**
     * Date comparator to sort apps
     * from the newest to the oldest
     */
    public static final Comparator<App> DATE_COMPARATOR = new Comparator<App>() {

        @Override
        public int compare(App o1, App o2) {
            return o2.getCreationDate().compareTo(o1.getCreationDate());
        }
    };

    private static Finder<Long, App> find = new Finder<Long, App>(Long.class, App.class);

    public static List<App> all() {
        return find.all();
    }

    public static List<App> all(Comparator<App> comparator) {
        List<App> result = find.all();
        Collections.sort(result, comparator);
        return result;
    }

    public static List<App> getNewest(int amount) {
        List<App> result = all(DATE_COMPARATOR);
        return result.subList(0, Math.min(result.size(), amount));
    }

    public static App get(Long id) {
        return (id != null) ? find.byId(id) : null;
    }

    public static List<App> search(String query) {
        return find.where().ilike("name", "%" + query + "%").findList();
    }

    @Transactional
    public static void removeAuthor(App app, User user) {
        user.applications.remove(app);
        user.update();
        app.authors.remove(user);
        app.update();
    }

    @Transactional
    public static void deleteApp(App app) {
        for (User user : app.authors) {
            user.applications.remove(app);
            user.update();
        }
        app.authors.clear();
        app.update();
        app.delete();
    }

    @Transactional
    public static void create(App app, User user) {
        app.authors.add(user);
        app.creationDate = new Date();
        app.save();
        user.applications.add(app);
        user.update();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public int compareTo(App o) {
        return this.creationDate.compareTo(o.creationDate);
    }
}
