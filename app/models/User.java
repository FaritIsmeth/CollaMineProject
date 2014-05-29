package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;
import java.text.*;
import play.data.validation.*;
import play.data.format.*;

import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name="user")
public class User extends Model {

    @Id
    public String email;
    public String name;
    public String password;
    
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
    
    public static Finder<String,User> find = new Finder<String,User> (
        String.class, User.class    
    );
    
    public static User authenticate(String email, String password) {
        /*Form<Signup> signupForm = Form.form(Signup.class).bindFromRequest();
        String enc_password  = BCrypt.hashpw(signupForm.get().password, BCrypt.gensalt(10));*/
        /*if (BCrypt.checkpw("password", password)) {
            User user = User.find.where().eq("email", email).findUnique();
            return user;
        }
        else {
            return null;
        }*/
        return find.where().eq("email", email).eq("password", password).findUnique();
        // retrieve user by email
        // get the user.password
        // compare user.password with enc_password
    }

    public static User emailExists(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static User nameExists(String name) {
        return find.where().eq("name", name).findUnique();
    }

    /*public static User create(String email, String password) {
        User user = User.find.where().eq("email", email).findUnique();
        user.email = email;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt(10));
        user.save();
        return user;
    }*/
  
    /*public static User authenticate(String email, String password) {
        User user = User.find.where().eq("email", email).findUnique();
        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
  }*/
}