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
import javax.persistence.ManyToOne;
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
    private Set<Voter> userRate;

    private Double score;

    @OneToMany(mappedBy = "app")
    private Set<Comment> comments;

    @Entity
    public class Voter extends Model {
        private static final long serialVersionUID = 1L;

        @Id
        public Long id;

        public String username;
        public Short score;

        @ManyToOne
        public App app;

        public Voter(String username, short score, App app) {
            this.username = username;
            this.score = score;
            this.app = app;
        }
    }

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

    /**
     * Score comparator to sort apps
     * from the highest rated to the lowest rated
     */
    public static final Comparator<App> SCORE_COMPARATOR = new Comparator<App>() {

        @Override
        public int compare(App o1, App o2) {
            return o1.getScore().compareTo(o2.getScore());
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
        for (Voter v : app.getUserRate()) {
            v.delete();
        }
        app.getUserRate().clear();
        new File(STORE_FOLDER, app.fileLogo).delete();
        app.update();
        app.delete();
    }

    @Transactional
    public static void create(App app, File appLogo, User user) {
        app.authors.add(user);
        app.creationDate = new Date();
        app.score = 0D;
        app.save();
        String fileName = app.id + ".png";
        File destFile = new File(STORE_FOLDER, fileName);
        appLogo.renameTo(destFile);
        app.fileLogo = destFile.getName();
        app.update();
        user.applications.add(app);
        user.update();
    }

    @Transactional
    public static void rate(App app, User user, short score) {
        String username = user.username;
        Voter voter = null;
        for (Voter v : app.getUserRate()) {
            if (v.app.id.equals(app.id) && v.username.equals(username)) {
                voter = v;
                break;
            }
        }
        if (voter == null) {
            voter = app.createVoter(username, score);
            app.getUserRate().add(voter);
            app.update();
        } else {
            voter.score = score;
            voter.update();
        }

        app.reScore();
        app.update();
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
        return getComments(Comment.DATE_COMPARATOR);
    }

    public List<Comment> getComments(Comparator<Comment> comparator) {
        List<Comment> result = new ArrayList<Comment>();
        result.addAll(comments);
        Collections.sort(result, comparator);
        return result;
    }

    @Override
    public int compareTo(App o) {
        return this.creationDate.compareTo(o.creationDate);
    }

    public Set<Voter> getUserRate() {
        return userRate;
    }

    public Double getScore() {
        return score;
    }

    public void reScore() {
        Set<Voter> voters = this.getUserRate();
        double newScore = 0;
        for (Voter v : voters) {
            newScore += v.score.doubleValue();
        }
        this.score = newScore / voters.size();
    }

    public Voter createVoter(String username, Short s) {
        Voter v = new Voter(username, s, this);
        v.save();
        return v;
    }
}
