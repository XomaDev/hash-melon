import xyz.kumaraswamy.hashmelon.MelonMap;

public class Main {
   public static void main(String[] args) {
      MelonMap melonMap = new MelonMap(6);
      melonMap.put("a", 7);
      melonMap.put("b", 9);
      melonMap.put("c", 15);
      melonMap.put("d", 16);
      melonMap.put("e", 17);
      melonMap.put("f", 18);
      melonMap.put("g", 19);
      melonMap.put("h", 20);

      assert (int) melonMap.get("a") == 7;
      assert (int) melonMap.get("b") == 9;
      assert(int) melonMap.get("c") == 15;
      assert (int) melonMap.get("d") == 16;
      assert (int) melonMap.get("e") == 17;
   }
}