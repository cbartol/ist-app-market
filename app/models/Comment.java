package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

@Entity
public class Comment extends Model {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Required
    private String text;

    private Date creationDate;

    private Long likes;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    private App app;

    @ManyToMany
    private Set<User> likedUsers;

    private static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);

    public static List<Comment> all() {
        return find.all();
    }

    public static Comment get(long id) {
        return find.byId(id);
    }

    @Transactional
    public static void create(Comment c, App app, User author) {
        c.setApp(app);
        c.setAuthor(author);
        c.creationDate = new Date();
        c.likes = 0L;
        c.save();

        app.addComment(c);
        author.addCommentMade(c);
        app.update();
        author.update();
    }

    @Transactional
    public static void toggleLike(Comment comment, User user) {
        if (comment.likedUsers.contains(user)) {
            comment.removeLike();
            comment.likedUsers.remove(user);
            user.getLikedComments().remove(comment);
        } else {
            comment.addLike();
            comment.likedUsers.add(user);
            user.getLikedComments().add(comment);
        }
        comment.update();
        user.update();
    }

    @Transactional
    public static void delete(Comment comment) {
        User author = comment.getAuthor();
        App app = comment.getApp();
        Set<User> liked = comment.getLikedUsers();
        for (User user : liked) {
            user.getLikedComments().remove(comment);
            user.update();
        }
        liked.clear();
        app.getComments().remove(comment);
        author.getCommentsMade().remove(comment);
        app.update();
        author.update();
        comment.update();
        comment.delete();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Long getLikes() {
        return likes;
    }

    public void addLike() {
        likes++;
    }

    public void removeLike() {
        likes--;
    }

    public Set<User> getLikedUsers() {
        return likedUsers;
    }
}
