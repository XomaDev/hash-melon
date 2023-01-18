package xyz.kumaraswamy.hashmelon;

public class Hashmelon {

   public static int hashCode(String text) {
      int h = 32;
      for (char ch : text.toCharArray())
         h = ch * h + (h / (ch * 2)) + ch;
      return h;
   }
}
