package xyz.kumaraswamy.hashmelon;

public class MelonMap {
   public static class Melon {

      public Object val;
      public int kHash;

      public Melon left;
      public Melon right;

      public Melon parent;

      public Melon(Object val, int kHash) {
         this.val = val;
         this.kHash = kHash;
      }

      public Melon getRoot() {
         for (Melon r = this, p; ; ) {
            if ((p = r.parent) == null)
               return r;
            r = p;
         }
      }

      @Override
      public String toString() {
         if (left == null && right == null)
            return "<" + val + ">";
         return "{value=" + val + ", left=" + left + ", right=" + right + "}";
      }
   }

   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
   private static final int MAX_TREE_THRESHOLD = 7;

   private Melon[] melons;
   private int capacity;

   private int lenAll = 0;

   public MelonMap() {
      melons = new Melon[16];
      capacity = 16;
   }

   public MelonMap(int capacity) {
      this.capacity = capacity <= 0 ? 16 : capacity;
      melons = new Melon[this.capacity];
   }

   public boolean contains(String key) {
      return get(key) != null;
   }

   public MelonMap put(String key, Object value) {
      if (lenAll++ > capacity * DEFAULT_LOAD_FACTOR)
         resize();
      int kHash = Hashmelon.hashCode(key);
      int index = indexOfHash(kHash);

      put(value, kHash, index);
      return this;
   }

   private void put(Object value, int kHash, int index) {
      Melon melon = melons[index];
      boolean isNull = melon == null;

      Melon newMelon = new Melon(value, kHash);
      // may not be the endpoint

      if (!isNull) {
         if (melon.kHash == kHash) {
            melons[index] = newMelon; // overwrite
         } else if (melon == melon.getRoot() && melon.left == null)
            // keep the right side to the
            // most recently accessed elements
            (newMelon.right = melon).parent = newMelon;
         else {
            if (melon.left == null) {
               // TODO:
               // to few tests on this

               Melon parent = melon.parent;
               // the root node
               (parent.left = newMelon).parent = parent;
               exchange(parent, newMelon);

               melons[index] = parent;
            } else {
               // so now both melon.{right && left} are not null
               // right values are always recent

               Melon nxtLeftOfHead = melon.right;

               Melon melonRight = melon.right = melon.left;
               melon.left = null;

               if (melonRight.right != null) {
                  // optimize the elements
                  // for example {value = 1, left = 2, right = {left = null, right = 3}}
                  // will be optimized to {value = 1, left = 2, right = 3}
                  (melon.left = melonRight.right).parent = melon;
                  melonRight.right = null;
               }
               // null point when index = 1
               (newMelon.left = nxtLeftOfHead).parent = newMelon;
               (newMelon.right = melon).parent = newMelon;

               melons[index] = newMelon;
            }
         }
      } else
         melons[index] = newMelon;
   }

   private void resize() {
      int newCap = capacity * 2;

      Melon[] oldTable = this.melons;

      melons = new Melon[newCap];
      this.capacity = newCap;

      for (Melon tab : oldTable)
         if (tab != null)
            transferAllOfTab(tab.getRoot());
   }

   private void transferAllOfTab(Melon tab) {
      int kHash = tab.kHash;
      Object val = tab.val;

      put(val, kHash, indexOfHash(kHash));

      if (tab.left != null) transferAllOfTab(tab.left);
      if (tab.right != null) transferAllOfTab(tab.right);
   }

   private void exchange(Melon melon, Melon newMelon) {
      Object val = melon.val;
      melon.val = newMelon.val;
      newMelon.val = val;

      int kHash = melon.kHash;
      melon.kHash = newMelon.kHash;
      newMelon.kHash = kHash;
   }

   private int indexOfHash(int h) {
      return h & (capacity - 1);
   }

   public Melon getNode(String key) {
      // for testing purpose
      return melons[indexOfHash(Hashmelon.hashCode(key))];
   }

   public Object get(String key) {
      int kHash = Hashmelon.hashCode(key);
      Melon melon = getMelon(kHash, indexOfHash(kHash));
      return melon == null ? null : melon.val;
   }

   public void delete(String key) {
      int kHash = Hashmelon.hashCode(key);
      int index = indexOfHash(kHash);

      Melon melon = getMelon(kHash, index);

      if (melon == null)
         return;
      melon = melon.getRoot();

      deleteRoot(index, melon);
   }

   private void deleteRoot(int index, Melon melon) {
      Melon left = melon.left, right = melon.right;
      if (left == null && right == null)
         melons[index] = null;
      else if (left != null && right == null)
         // orphans the left node from
         // the parent/root node
         (melons[index] = left).parent = null;
      else if (left == null)
         (melons[index] = right).parent = null;
      else {
         /**                                            5
          *                                            / \
          *     7             5                      /    \
          *   /  \          /  \                   /       \
          * 2     28  ,   3    10      = >       3         10
          *                  /   \                \       /  \
          *                6      11               7    6     11
          *                                      /  \
          *                                    2    28
          */
         for (Melon lOfR = right.left, p = right; ; ) {
            if (lOfR == null) {
               (p.right = left).parent = p;
               break;
            } else p = lOfR;
            lOfR = p.left;
         }
         right.parent = null;
         melons[index] = right;
      }
   }

   private Melon getMelon(int kHash, int index) {
      Melon melon = melons[index];
      if (melon == null)
         return null;
      if (melon.parent != null)
         melon = melon.getRoot();
      melon = getVal(kHash, melon, 0);
      splay(melon, index);
      return melon;
   }

   private Melon getVal(int kHash, Melon melon, int lenTravelled) {
      if (lenTravelled == MAX_TREE_THRESHOLD) {
         // collision is mostly caused by lack
         // of space
         resize();
         disconnect(melon);
      }
      if (kHash == melon.kHash) {
         return melon;
      }
      lenTravelled++;
      Melon val = melon.right != null ? getVal(kHash, melon.right, lenTravelled) : null;
      if (val == null && melon.left != null)
         val = getVal(kHash, melon.left, lenTravelled);
      return val;
   }

   /**
    * Disconnects and node from the parent
    * to move it to a new index in the table
    */
   private void disconnect(Melon melon) {
      Melon parent = melon.parent;
      if (parent == null) return;

      if (parent.right == melon) parent.right = null;
      else if (parent.left == melon) parent.left = null;

      int kHash = melon.kHash;
      put(melon.val, kHash, indexOfHash(kHash));
   }

   public void splay(Melon melon, int index) {
      if (melon == null || melons[index] == null) // the node can be moved
         return;
      Melon root = melon.getRoot();
      if (melon.parent == root) {
         if (root.right == melon) {
            leftDownRotation(melon, root);
         } else rightDownRotate(melon, root);
         melon.parent = null;
      } else {
         for (Melon p = melon.parent, gParent;
              p != null;
              p = melon.parent) {
            gParent = p.parent;
            if (p.right == melon)
               leftDownRotation(melon, p);
            else rightDownRotate(melon, p);
            if (gParent == null)
               (melons[index] = melon).parent = null;
            else if (gParent.left == p)
               (gParent.left = melon).parent = gParent;
            else
               (gParent.right = melon).parent = gParent;
         }
      }
   }

   private static void leftDownRotation(Melon melon, Melon parent) {
      Melon pRight = (parent.right = melon.left);
      if (pRight != null)
         pRight.parent = parent;
      (melon.left = parent).parent = melon;
   }

   private static void rightDownRotate(Melon melon, Melon root) {
      Melon right = root.left = melon.right;
      if (right != null)
         right.parent = root;
      (melon.right = root).parent = melon;
   }
}
