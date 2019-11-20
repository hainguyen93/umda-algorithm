
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pthnguyen
 */
public class Processer {
  public static void main(String[] args){
    BufferedWriter out = null;
    BufferedReader in = null;

    if (args.length != 2){
      //System.out.println("USAGE: inFile outFile");
      System.exit(0);
    }

    try {
      int count = 1;
      in = new BufferedReader(new FileReader(args[0]));
      out = new BufferedWriter(new FileWriter(args[1]));
      String line = in.readLine();
      System.out.println(line);
      while (line != null){
        if (count % 2 == 0){
          System.out.println(line);
          out.write(line);
          out.newLine();
        }
        line = in.readLine();
        count++;
      }
      in.close();
      out.close();
    } catch (FileNotFoundException e) {
      System.out.println(e);
    } catch (IOException e){
      System.out.println(e.toString());
    }
  }
}
