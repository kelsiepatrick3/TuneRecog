// Recorder.java
// Barrett Koster 2014 from Matthias Pfisterer's SimpleAudioRecorder

package sonarOld;

import java.io.*;

import javax.sound.sampled.*;


public class Recorder extends Thread
{
   private TargetDataLine     m_line;
   private AudioFileFormat.Type  m_targetType;
   private AudioInputStream   m_audioInputStream;
   private File         m_outputFile;
   int intCount, N;
   int[] wave;

   public Recorder(TargetDataLine line,
                 AudioFileFormat.Type targetType,
                 File file)
   {
      m_line = line;
      m_audioInputStream = new AudioInputStream(line);
      m_targetType = targetType;
      m_outputFile = file;
      /*
      start();
      try{ Thread.sleep(3000); } catch ( Exception o ) { System.out.println("insomnia"); }
      
      byte[] dataMaybe = new byte[10000];
      int numnum =  line.read( dataMaybe, 0, 9996 );
      System.out.println("numnum="+numnum);
      intCount = numnum / 2;
      N = numnum/2;
      for ( int i=0; i<intCount; i++ )
      {
          int val = 256*dataMaybe[2*i+1] + dataMaybe[2*i];
          wave[i] = val;
      }
      
      return wave;
      */
   }




   /** Starts the recording.
       To accomplish this, (i) the line is started and (ii) the
       thread is started.
   */
   public void start()
   {
      /* Starting the TargetDataLine. It tells the line that
         we now want to read data from it. If this method
         isn't called, we won't
         be able to read data from the line at all.
      */
      m_line.start();

      /* Starting the thread. This call results in the
         method 'run()' (see below) being called. There, the
         data is actually read from the line.
      */
      super.start();
      //SoundWave wave = new SoundWave();
   }


   /** Stops the recording.

       Note that stopping the thread explicitely is not necessary. Once
       no more data can be read from the TargetDataLine, no more data
       be read from our AudioInputStream. And if there is no more
       data from the AudioInputStream, the method 'AudioSystem.write()'
       (called in 'run()' returns. Returning from 'AudioSystem.write()'
       is followed by returning from 'run()', and thus, the thread
       is terminated automatically.

       It's not a good idea to call this method just 'stop()'
       because stop() is a (deprecated) method of the class 'Thread'.
       And we don't want to override this method.
   */
   public void stopRecording()
   {
      m_line.stop();
      m_line.close();
   }

   /** Main working method.
       You may be surprised that here, just 'AudioSystem.write()' is
       called. But internally, it works like this: AudioSystem.write()
       contains a loop that is trying to read from the passed
       AudioInputStream. Since we have a special AudioInputStream
       that gets its data from a TargetDataLine, reading from the
       AudioInputStream leads to reading from the TargetDataLine. The
       data read this way is then written to the passed File. Before
       writing of audio data starts, a header is written according
       to the desired audio file type. Reading continues untill no
       more data can be read from the AudioInputStream. In our case,
       this happens if no more data can be read from the TargetDataLine.
       This, in turn, happens if the TargetDataLine is stopped or closed
       (which implies stopping). (Also see the comment above.) Then,
       the file is closed and 'AudioSystem.write()' returns.
   */
   public void run()
   {
         try
         {
            AudioSystem.write(
               m_audioInputStream,
               m_targetType,
               m_outputFile);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
   }

}




