package xyz.kumaraswamy.hashmelon;

public class Hashmelon {

   public static HashCode DEFAULT_HASH_CODE_HANDLE = Hashmelon::hashCode;

   public interface HashCode {
      int hashCode(String key);
   }

   public static int hashCode(String text) {
      int h = 16;
      for (char ch : text.toCharArray())
         h = ch * 3 * h + (h / (ch * 2)) + ch;
      return h;
   }
}
