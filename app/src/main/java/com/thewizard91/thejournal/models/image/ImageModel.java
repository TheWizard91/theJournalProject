package com.thewizard91.thejournal.models.image;

import com.google.firebase.firestore.FieldValue;

//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class ImageModel extends ImageId {
    private String description;
    private String title;
    private String imageURI;
//    private Date timestamp;
    private String time;

    public ImageModel() {}
    public ImageModel(String description, String title, String imageURI, String time)  {
        this.description = description;
        this.title = title;
        this.imageURI = imageURI;
        this.time = time;
    }
//    public void setDescription (String description) {this.description = description;}
//    public String getDescription () {return description;}
//    public void setTitle () {this.title = title;}
//    public String getTitle () {return title;}
    public void setImageURI (String imageURI) {this.imageURI = imageURI;}
    public String getImageURI () {return imageURI;}
//    public void seTimestamp (Date timestamp) {this.timestamp = timestamp;}
//    public Date getTimestamp () {return timestamp;}

    public Map<String, Object> imageFirebaseDatabaseMap() {
        Map<String, Object> image_map = new HashMap<>();
        image_map.put("description", description);
        image_map.put("title", title);
        image_map.put("imageURI", imageURI);
        image_map.put("timestamp", time);
        return image_map;
    }
}
