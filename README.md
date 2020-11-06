# Kirkwoods Bitwig API Utilities
## Utilities for Bitwig API Nerds 

This is a set of bitwig api scripting utilities that you can merge and import into your own bitwig scripts. 

## *TODO* 
1. *Set up example bitwig extensions to show how these operate.*
1. *with example files*
1. *video*

## ChannelFinder
Need a way to always keep a CursorTrack or Cursor Bank assigned to a specific channel via name. This tool allows you to assign a string and object to the channel finder and call find() in the class. This will set bitwig to start searching through the live set. Banks do not have all their names available all at once so It goes through one by one to assign to the correct space. *I should note last time I tried using this with groups, I failed. So... I just no longer use groups.*

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
Store Data into clip objects. Different styles of clip note objects exist for data. There is Note & Velocity datatypes. When selecting a clip it updates the parent class with its data set.

## ClipFileData
Similar functionality as Clip Data. But no longer uses note data as its storage. Data is stored to a file and then able to be recalled via clip name. The file is plain text and can be edited later if deemed useful.

## Leds
Basic class with example to set led values without having to keep track of data manually if an LED has been set up or not. This is useful for tracking LED states and updating them for every flush. This only updates the values that have been changed via Lambda Supplier.

# Additional Utilities
## Log
A simple logger so you don't have to pass in host to all your classes. Instantiate the class once with the host and the use Log.println("") anywhere in your script.

## Math
Some basic math utility function. Free to copy into your own library, most of these were found on stack exchange.

## Midi
Some basic midi ids that you might need. NoteOn, CC, etc.

# Connect / Support
If you want some help getting these working, have questions or feature suggestions reach out on the [kvr - bitwig controller scripting forum]( https://www.kvraudio.com/forum/viewforum.php?f=268). Always great to connect to fellow nerds. 

Also would love to hear from you if you used the Java files found here. can either connect through forum or here [kirkwoodwest@gmail.com](mailto:kirkwoodwest@gmail.com]
