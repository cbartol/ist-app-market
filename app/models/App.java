package models;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.google.common.collect.Sets;

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

    public static final Comparator<App> DATE_COMPARATOR = new Comparator<App>() {

        @Override
        public int compare(App o1, App o2) {
            return o1.getCreationDate().compareTo(o2.getCreationDate());
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

    public static App get(Long id) {
        return find.byId(id);
    }

    public static TreeSet<App> search(String query) {
        //TODO
        return Sets.newTreeSet(all());
    }

    public static void removeAuthor(App app, User user) {
        user.applications.remove(app);
        user.update();
        app.authors.remove(user);
        app.update();
    }

    public static void deleteApp(App app) {
        for (User user : app.authors) {
            user.applications.remove(app);
            user.update();
        }
        app.delete();
    }

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
