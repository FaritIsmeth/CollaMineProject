package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;
import java.text.*;
import play.data.validation.*;
import play.data.format.*;
import java.util.List;
import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.ExpressionList;

import java.io.IOException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import java.util.ArrayList;

import play.data.validation.Constraints;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
@Table(name="file")
public class File extends Model {

    @Id
    public String filename;

    @Lob
    public byte[] content;

    public String uploadedBy;

    public String datetime;


    java.io.File file;

    public File(String filename) {
        this.filename = filename;
    }

    public File(String filename, byte[] content, String uploadedBy, String datetime) {
        this.filename = filename;
        this.content  = content;
        this.uploadedBy = uploadedBy;
        this.datetime = datetime;
    }
    
    public static Finder<String,File> find = new Finder<String,File> (
        String.class, File.class    
    );

    /*public static File fileMissing(String filename) {
        return find.where().eq("filename", filename).findUniqule();
    }*/

    public static File filenameExists(String filename) {
        return find.where().eq("filename", filename).findUnique();
    }
    
    public static List <File> fileUploadedBy(String uploadedBy) {
        Query<File> query = Ebean.createQuery(File.class);
        query.where().eq("uploaded_by",uploadedBy);
        List<File> fileList = query.findList();
        return fileList;
    }
}