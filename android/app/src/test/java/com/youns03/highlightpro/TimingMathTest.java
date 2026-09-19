package com.youns03.highlightpro;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class TimingMathTest {
    @Test public void progressIsClampedAndContinuous(){ assertEquals(0f,TimingMath.wordProgress(0f,1f,2f),0f); assertEquals(.5f,TimingMath.wordProgress(1.5f,1f,2f),.001f); assertEquals(1f,TimingMath.wordProgress(3f,1f,2f),0f); }
    @Test public void activeWordUsesOriginalBoundaries(){ LessonRepository.Sentence s=new LessonRepository.Sentence("s","bonjour","مرحبا",0,2,Arrays.asList(new LessonRepository.Word("w1","bonjour","مرحبا",0,1))); assertEquals("w1",TimingMath.activeWord(s,.5f).id); assertNull(TimingMath.activeWord(s,1.5f)); }
    @Test public void sentenceTransitionsAndBoundaryTolerance(){ LessonRepository.Sentence a=new LessonRepository.Sentence("a","un","واحد",0,1,Arrays.asList()); LessonRepository.Sentence b=new LessonRepository.Sentence("b","deux","اثنان",1.2f,2,Arrays.asList()); assertEquals("a",TimingMath.activeSentence(Arrays.asList(a,b),.5f).id); assertEquals("b",TimingMath.activeSentence(Arrays.asList(a,b),1.2f).id); assertEquals("a",TimingMath.activeSentence(Arrays.asList(a,b),1.1f).id); assertNull(TimingMath.activeSentence(Arrays.asList(a,b),1.11f)); }
    @Test public void seekIsClampedToLessonDuration(){ assertEquals(0f,TimingMath.clampSeek(-1,10),0f); assertEquals(4.5f,TimingMath.clampSeek(4.5f,10),0f); assertEquals(10f,TimingMath.clampSeek(12,10),0f); }
}
