package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.ebean.Model;
import play.db.ebean.Transactional;

import com.google.common.collect.Lists;

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

    @OneToMany(mappedBy = "author")
    private Set<Comment> commentsMade;

    @ManyToMany
    private Set<Comment> likedComments;

    private static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    public static List<User> all() {
        return find.all();
    }

    public static User get(String username) {
        return (username != null) ? find.byId(username) : null;
    }

    public static List<User> search(String query) {
        Set<User> result = find.where().ilike("username", '%' + query).findSet();
        result.addAll(find.where().ilike("name", '%' + query.replace(' ', '%') + '%').findSet());
        return Lists.newArrayList(result);
    }

    @Transactional
    public static void deleteUser(String username) {
        User user = get(username);
        Set<Comment> commentsToDelete = user.getCommentsMade();
        for (Comment comment : commentsToDelete) {
            Comment.delete(comment);
        }
        user.update();
        Set<Comment> commentsToUnlike = user.getLikedComments();
        for (Comment comment : commentsToUnlike) {
            comment.getLikedUsers().remove(user);
            comment.update();
        }
        commentsToDelete.clear();
        user.update();
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

    @Transactional
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

    public String getSmallName() {
        String[] arr = name.split(" ");
        if (arr.length > 1) {
            return arr[0] + " " + arr[arr.length - 1];
        } else {
            return name;
        }
    }

    public void addCommentMade(Comment c) {
        commentsMade.add(c);
    }

    public Set<Comment> getLikedComments() {
        return likedComments;
    }

    public Set<Comment> getCommentsMade() {
        return commentsMade;
    }
}
