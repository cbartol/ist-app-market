package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

@Entity
public class App extends Model implements Comparable<App> {

    private static final String STORE_FOLDER = "file-storage";
    private static final long serialVersionUID = 1L;
    public static final long MAX_IMAGE_SIZE = 2097152;

    @Id
    public Long id;

    @Required
    public String name;

    @ManyToMany()
    public Set<User> authors;

    public String description;

    public String fileLogo;

    private Date creationDate;

    @OneToMany(mappedBy = "app")
    private Set<Comment> comments;

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
        List<Comment> commentsToDelete = app.getComments();
        for (Comment comment : commentsToDelete) {
            Comment.delete(comment);
        }
        for (User user : app.authors) {
            user.applications.remove(app);
            user.update();
        }
        app.authors.clear();
        new File(STORE_FOLDER, app.fileLogo).delete();
        app.update();
        app.delete();
    }

    @Transactional
    public static void create(App app, File appLogo, User user) {
        app.authors.add(user);
        app.creationDate = new Date();
        app.save();
        String fileName = app.id + ".png";
        File destFile = new File(STORE_FOLDER, fileName);
        appLogo.renameTo(destFile);
        app.fileLogo = destFile.getName();
        app.update();
        user.applications.add(app);
        user.update();
    }

    public void addComment(Comment c) {
        comments.add(c);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public File getLogo() {
        return new File(STORE_FOLDER, this.fileLogo);
    }

    public List<Comment> getComments() {
        List<Comment> result = new ArrayList<Comment>();
        result.addAll(comments);
        return result;
    }

    @Override
    public int compareTo(App o) {
        return this.creationDate.compareTo(o.creationDate);
    }
}
