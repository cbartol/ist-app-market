package models;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class App extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    @Required
    public String name;

    @ManyToMany()
    public Set<User> authors;

    public String description;

    private static Finder<Long, App> find = new Finder<Long, App>(Long.class, App.class);

    public static List<App> all() {
        return find.all();
    }

    public static App get(Long id) {
        return find.byId(id);
    }
}
