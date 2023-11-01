package com.thewizard91.thejournal.models.image;

import com.google.firebase.firestore.FieldValue;

//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class ImageModel extends ImageId {
    private String description;
    private String title;
    private String image_uri;
//    private Date timestamp;
    private FieldValue server_timestamp;

    public ImageModel() {}
    public ImageModel(String description, String title, String image_uri, FieldValue server_timestamp)  {
        this.description = description;
        this.title = title;
        this.image_uri = image_uri;
        this.server_timestamp = server_timestamp;
    }
//    public void setDescription (String description) {this.description = description;}
//    public String getDescription () {return description;}
//    public void setTitle () {this.title = title;}
//    public String getTitle () {return title;}
    public void setImageURI (String image_uri) {this.image_uri = image_uri;}
    public String getImageURI () {return image_uri;}
//    public void seTimestamp (Date timestamp) {this.timestamp = timestamp;}
//    public Date getTimestamp () {return timestamp;}

    public Map<String, Object> imageFirebaseDatabaseMap() {
        Map<String, Object> image_map = new HashMap<>();
        image_map.put("description", description);
        image_map.put("title", title);
        image_map.put("imageURI", image_uri);
        image_map.put("timestamp", server_timestamp);
        return image_map;
    }
}
