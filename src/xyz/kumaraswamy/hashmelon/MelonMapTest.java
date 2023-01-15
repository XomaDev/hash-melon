package xyz.kumaraswamy.hashmelon;

import org.junit.jupiter.api.Test;

class MelonMapTest {

   @Test
   public void testOverwrite() {
      MelonMap melonMap = new MelonMap()
              .put("a", 8)
              .put("a", 9);
      System.out.println(melonMap.get("a"));
   }

   @Test
   public void assertRootNode() {
      MelonMap melonMap = new MelonMap(5)
              .put("a", 7)
              .put("b", 8)
              .put("c", 9)
              .put("d", 10)
              .put("e", 11)
              .put("f", 12)
              .put("g", 13)
              .put("h", 14)
              .put("i", 15);
      assert (int) melonMap.getNode("i").val == 15;
   }

   @Test
   public void hashCollision() {
      MelonMap melonMap = new MelonMap()
              .put("a", 7)
              .put("b", 8)
              .put("c", 9)
              .put("d", 10);

      System.out.println(melonMap.get("a"));
   }

   @Test
   public void deleteOperation() {
      MelonMap melonMap = new MelonMap(2)
              .put("a", 7)
              .put("c", 9)
              .put("d", 10);

      melonMap.delete("a");
      // we check it with 10 since it was the last accessed element
      // than 9
      assert (int) melonMap.getNode("d").getRoot().val == 10;
   }

   @Test
   public void addAndGetElements() {
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

   @Test
   public void arrayResizeOperation() {
      MelonMap melonMap = new MelonMap(3)
              .put("a", 1)
              .put("b", 2)
              .put("c", 3)
              .put("d", 4);
      System.out.println(melonMap.getNode("a"));
   }
}