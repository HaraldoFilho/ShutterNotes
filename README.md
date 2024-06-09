# **Shutter Notes**

With **_Shutter Notes_** you can take notes while you are photographing with any camera and mark a sequence of photos where the notes apply. This is done by taking a picture of the phone screen displaying the note, in the start and at the end of the sequence. This is useful on that situation when you use an accessory on a trip, like a ND filter for example, and when you are back home and start processing the photos you realize that you have completely forgotten what filter you used on each photo. In this case, using the app you will have a shot marking the start of the sequence where the filter was used and another marking the end. You can also write a title, a description and a list of tags (accessories used) for photos that you will upload to Flickr™ and have the notes applied to these photos when they are online, together with (if you want) the location where the sequence was shot, automatically taken by the app.

*Flickr is a trademark of SmugMug, Inc.*

### APIs

This application uses *Flickr™* and *Google Maps™* APIs, that for obvious reasons, the keys were removed from this repository. To get and use these APIs follow the instructions below.

#### Flickr API key

An API Key can be obtained following the instructions here:
https://www.flickr.com/services/apps/create/

After get the key, the file **flickr_api.xml** must be created at **_./app/src/debug/res/values/_** with the following content:

```xml
<resources>
    
    <string name="flickr_key" templateMergeStrategy="preserve" translatable="false">
        ### API Key ###
    </string>
    <string name="flickr_secret" templateMergeStrategy="preserve" translatable="false">
        ### API Secret ###
    </string>
    
</resources>
```

#### Google Maps API key

An API Key can be obtained following the instructions here:
https://developers.google.com/maps/documentation/android/signup

After get the key, the file **google_maps_api.xml** must be created at **_./app/src/debug/res/values/_** with the following content:

```xml
<resources>

    <!--Google Maps API Debug Key-->
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
        ### API Key (it starts with "AIza") ###
    </string>

</resources>
```

####
