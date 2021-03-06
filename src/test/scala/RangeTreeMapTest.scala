import com.charlesahunt.{RangeKey, RangeTreeMap}
import org.scalatest.{Matchers, WordSpec}

import scala.math.Ordering._

class RangeTreeMapTest extends WordSpec with Matchers  {

  "A RangeTreeMap" when {
    "empty" should {

      "have an empty span" in {
        RangeTreeMap.apply[Int, String].span() shouldBe empty
      }

      "put an element and retrieve it from the map by lower bound" in {
        val testRange = RangeKey[Int](0, 5)
        val testMap = RangeTreeMap.apply[Int, String]
        testMap.put(testRange, "test")
        testMap.get(testRange.lower) should contain ("test")
      }

      "put an element and retrieve it from the map by RangeKey" in {
        val testRange = RangeKey[Int](0, 5)
        val testMap = RangeTreeMap.apply[Int, String]
        testMap.put(testRange, "test")
        testMap.get(testRange) should contain ("test")
      }

      "put and element in the map, retrieve it, then clear the map" in {
        val testRange = RangeKey[Int](0, 5)
        val testMap = RangeTreeMap.apply[Int, String]
        testMap.put(testRange, "test")
        testMap.get(testRange) should contain ("test")
        testMap.clear()
        testMap.get(testRange) shouldBe empty
      }

      "put and element in the map, retrieve it, then remove it" in {
        val testRange = RangeKey[Int](0, 5)
        val testMap = RangeTreeMap.apply[Int, String]
        testMap.put(testRange, "test")
        testMap.get(testRange) should contain ("test")
        testMap.remove(testRange)
        testMap.get(testRange) shouldBe empty
      }
    }

    "nonempty" should {

      //TODO: Nonempty test cases

    }
  }
}