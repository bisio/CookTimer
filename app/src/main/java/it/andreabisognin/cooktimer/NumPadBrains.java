package it.andreabisognin.cooktimer;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;

public class NumPadBrains {

  private long seconds;
  private long[] multiplier = {1,10,60,600,3600};
  private Stack<Integer> numpad;
  private int i=0;
  private boolean full = false;
  private final int SIZE = 5;


  public NumPadBrains() {
    numpad = new Stack<Integer>();
  }

  void add(int digit) {
    if (!full)
      numpad.push(digit);
    if (i< SIZE - 1)
      i++;
    else
      full = true;
  }

  void remove(){
     if (!numpad.empty())
        numpad.pop();
     if (i > 0)
      i--; 
     if (full)
      full = false;    
  }

  void clear() {
      numpad.clear();
      i=0;
  }


  long toSeconds() {
    ArrayList<Integer> list = new ArrayList<Integer>(numpad);
    Collections.reverse(list);
    long time = 0;
    for(int i=0;i<list.size();i++) {
      time += multiplier[i] * list.get(i);  
    }
    return time;
  }

  

}

