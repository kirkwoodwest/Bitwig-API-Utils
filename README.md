# Kirkwoods Bitwig API Utilities
## Utilities for Bitwig API Nerds 

This is a set of bitwig api scripting utilities that you can merge and import into your own bitwig scripts. 

## *TODO* 
1. *Set up example bitwig extensions to show how these operate.*
1. *with example files*
1. *video*

## ChannelFinder
Provides a method to keep a CursorTrack or Cursor Bank assigned to a specific channel via name. This tool allows you to assign a string and object to the channel finder and call find() in the class. This will set bitwig to start searching through the live set. Banks do not have all their names available all at once so It goes through one by one to assign to the correct space. *I should note I failed to get groups working... I just no longer use groups.*

![Image Of Channel Finder Settings](https://github.com/kirkwoodwest/Bitwig-API-Utils/blob/master/documentation/channel_finder.png)

```Java

 //Create Channel Finder...
 channel_finder = new ChannelFinder(host);
 
 //Init Channel Finder  (This is defered so you can determine where the settings appear in your Controller Script.
 channel_finder.init();
 
 //Create a track bank...
 track_bank = host.createTrackBank(8, 0, 0, true);
 
 cursor_track = host.createCursorTrack("Cursor Track, "Cursor Track", 0, 0, false);
    
 //Setup channel finder with track bank to target track "MY NAME", 
 channel_finder.add(track_bank.getTrackBank(), "MY NAME");
 
  //Setup channel finder with cursor_track to target track "MY NAME", 
 channel_finder.add(cursor_track, "MY NAME");
 
 //Notes: It should auto scan tracks whenever the project has changed.
 //Press "Scan Tracks" if you want to update something or tracks have been moved in the set.
 //Not sure if this works with Groups, I had trouble getting indexes to line up.

```

## ClipData
Store Data into clip objects. Different styles of clip note objects exist for data. There is Note & Velocity datatypes. When playing a clip it updates the parent class with its data set.

## ClipFileData
Similar functionality as Clip Data. But no longer uses note data as its storage. Data is stored to a file and then able to be recalled via playig the clip. It uses the clip name to register and recall the data.  The file is plain text and can be edited later if deemed useful.


![Image of Clip](https://github.com/kirkwoodwest/Bitwig-API-Utils/blob/master/documentation/clip_data_clips.png)

Creating a clip with new data in it is simple. Make a new clip, assign a name and press play. This should update the clip name in the clip data selector. On first go it writes the data you have in memory to the clip. Second time it recalls. 

To Update the data in a specific clip, press play on that clip. Observe that the Clip Name in the panel has changed. Make modifications to the data and press Save clip data.

![Image of Clip Data](https://github.com/kirkwoodwest/Bitwig-API-Utils/blob/master/documentation/clip_data.png)

Shows panel to manage clip data. Path name to your global clip data file and name of clip file. This will need to be modified to your liking and can be customized to your track. *I've requested to bitwig to get the current path to the active track so it would just point to a name based on your project.*

## CV To Osc Module
A little CV to Osc Utility. Requires the Use of a device on a track. This device will receive modulation parameters to each slot and as it receives updates it will send osc out immediatly. 

Here is an example where I hooked up the LFO to modulate knob 1(indexed from 0)... and you can observe how it outputs to the data monitor.

![Image of Bitwig Device Using CV To Osc](https://github.com/kirkwoodwest/Bitwig-API-Utils/blob/master/documentation/cv_to_osc_device.png)
![Image of Data Monitor receiving](https://github.com/kirkwoodwest/Bitwig-API-Utils/blob/master/documentation/cv_to_osc_data_monitor.png)

You can experiment with this extension by opening up this file in bitwig 3.2.8...
https://github.com/kirkwoodwest/Bitwig-API-Utils/tree/master/Bitwig%20Example%20Files/CV%20Monitor

And then selecting the CV To OSC Controller in the Kirkwood West Utils Extension File. This is set up to 

## Leds
Basic class with example to set led values without having to keep track of data manually if an LED has been set up or not. This is useful for tracking LED states and updating them for every flush. This only updates the values that have been changed via Lambda Supplier.

# Additional Utilities
## Log
A simple logger so you don't have to pass in host to all your classes. Instantiate the class once with the host and the use
```Java
host = getHost();
Log.init(host);

//Output to window...
Log.println("") anywhere in your script.
```
Also, helpful to bind a hot key to your bitwig script console when logging stuff. I use SHIFT+CTRL+J to pull it up. saves a bit of time. This tip came from Mossgraber.

## Math
Some basic math utility function. Free to copy into your own library, most of these were found on stack exchange and tuned for Java.

## Midi
Some basic midi ids that you might need. NoteOn, CC, etc. Not fully filled out but might be inspiring to integrate and build your own set of Midi utilities.

# Connect / Support
If you want some help getting these working, have questions or feature suggestions reach out on the [kvr - bitwig controller scripting forum]( https://www.kvraudio.com/forum/viewforum.php?f=268). Always great to connect to fellow nerds. 

Also would love to hear from you if you used the Java files found here. can either connect through forum or here [kirkwoodwest@gmail.com](mailto:kirkwoodwest@gmail.com]
