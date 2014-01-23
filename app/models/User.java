package models;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;

@Entity
public class User extends Model {

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
        User user = get(username);
        user.removeAllAplications();
        user.delete();
    }

    public void removeApplication(Long id) {
      applications.remove(App.get(id));
    }

    public void removeAllAplications() {
      applications.clear();
    }

}
