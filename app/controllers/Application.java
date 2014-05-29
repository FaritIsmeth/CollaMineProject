package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.data.validation.*;

import play.*;
import play.mvc.*;
import play.data.*;
import play.db.ebean.*;
import static play.data.Form.*;

import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.MultipartFormData;

import models.*;
import views.html.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.ExpressionList;

import java.io.IOException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import java.lang.Object;
import java.util.List;

import java.util.Date;

import org.mindrot.jbcrypt.BCrypt;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Homepage"));
    }

    public static Result uploadlogin() {
        return ok(uploadlogin.render(form(UploadLogin.class)));
    }

    public static class UploadLogin {
        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                flash("badlogin", "");
                return "Invalid user or password";
            }
            if (BCrypt.checkpw(password, User.password) == false) {
                flash("badlogin", "");
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result uploadlogin_authenticate() {
        Form<UploadLogin> uploadloginForm = Form.form(UploadLogin.class).bindFromRequest(); 
         
        if (uploadloginForm.hasErrors()) {
            return badRequest(uploadlogin.render(uploadloginForm));
        } else {
            session().clear();
            session("email", uploadloginForm.get().email);
            return redirect (routes.Application.upload());            
        }
    }

    @Security.Authenticated(UploadSecured.class)
    public static Result upload() {
        User user = User.find.byId(request().username());
        return ok(upload.render(user));
    }

    public static byte[] read(java.io.File file) throws IOException {
    
        byte []buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if ( ios.read(buffer) == -1 ) {
                throw new IOException("EOF reached while trying to read the whole file");
            }        
        } finally { 
            try {
                 if ( ios != null ) 
                      ios.close();
            } catch ( IOException e) {
            }
        }
    
        return buffer;
    }    
    
    @Security.Authenticated(UploadSecured.class)
    public static Result check_upload() {   
        User user = User.find.byId(request().username());
        
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart filePath = body.getFile("file");
        if (filePath != null) {
            try {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Logger.info("file path not null.");
                String fileName = filePath.getFilename();
                String contentType = filePath.getContentType(); 
                String uploadedBy = user.name.toString();
                String datetime = sdf.format(dt);
                java.io.File file = filePath.getFile();
                byte[] content = read(file);
                File f = new File(fileName, content, uploadedBy, datetime);
                f.save();
                return redirect(routes.Application.upload());
            }
            catch (Exception e) {
                Logger.info("file upload exception");
                flash("fileExist", "");
                return redirect(routes.Application.upload());
            } 
        } else {
            return redirect(routes.Application.upload());    
        }        
    }

    //download starts here

    public static Result downloadlogin() {
        return ok(downloadlogin.render(form(DownloadLogin.class)));
    }

    public static class DownloadLogin {
        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                flash("badlogin", "");
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result downloadlogin_authenticate() {
        Form<DownloadLogin> downloadloginForm = Form.form(DownloadLogin.class).bindFromRequest();
        if (downloadloginForm.hasErrors()) {
            return badRequest(downloadlogin.render(downloadloginForm));
        } else {
            session().clear();
            session("email", downloadloginForm.get().email);
            return redirect (routes.Application.download());            
        }
    }

    @Security.Authenticated(DownloadSecured.class)
    public static Result download() {
        User user = User.find.byId(request().username());
        List<File> files = File.fileUploadedBy(user.name);
        return ok(download.render(user, files));
    }

    @Security.Authenticated(DownloadSecured.class)
    public static Result downloadfile(String filename) {
        File file = File.filenameExists(filename);
        if (file == null) 
        {
            return ok("file is null");
        }
        InputStream is = new ByteArrayInputStream(file.content);
        response().setContentType("application/x-download");  
        response().setHeader("Content-disposition","attachment; filename="+filename); 
        return ok(is);
    }

    public static Result signup() {
        return ok(signup.render(form(Signup.class)));
    }

    public static class Signup {
        public String email;
        public String password;
        public String username;

        public String validate() {
            if (!(User.emailExists(email) == null)) {
                flash("emailExists", "");
                return "Email already taken";
            }

            if (!(User.nameExists(username) == null)) {
                flash("nameExists", "");
                return "Name already taken";
            }
            return null;
        }    
    }

    public static Result signup_authenticate() {   
        Form<Signup> signupForm = Form.form(Signup.class).bindFromRequest();   
        Logger.info("signup");
        if (signupForm.hasErrors()) {
            Logger.info("form has errors");
            return badRequest(signup.render(signupForm));
        } else {
            flash("goodsignup", "");
            Logger.info("form is ok");
            String email     = signupForm.get().email;
            //String password  = signupForm.get().password;
            String password  = BCrypt.hashpw(signupForm.get().password, BCrypt.gensalt(10));
            String username  = signupForm.get().username;
            User u           = new User(email, username, password);
            Logger.info("user object created.");
            u.save();          
        }
        return redirect (routes.Application.uploadlogin());
    }

    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }
}
