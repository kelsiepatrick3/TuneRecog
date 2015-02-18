package wave3;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;
//import javax.sound.sampled.*;

public class Sound 
{{
	try 
	{
		AudioClip clip = Applet.newAudioClip(new URL("file://c:/blah/foo.wav"));
		clip.play();
	} 
	catch (MalformedURLException murle) 
	{
		System.out.println(murle);
	}
		
}}
