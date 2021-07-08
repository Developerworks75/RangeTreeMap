import org.scalatest.{Matchers, WordSpec}
import RangeTreeMapFixture._
import com.charlesahunt.RangeKey

/**
  * TODO: Tests for: subRangeMap, putCoalescing
  */
class RangeTreeMapTest extends WordSpec with Matchers  {

  "A RangeTreeMap" when {
    "empty" should {

      "have an empty span" in {
        emptyRangeTreeMap.span() shouldBe empty
      }

      "have an empty map of ranges" in {
        emptyRangeTreeMap.asMapOfRanges() shouldBe empty
      }

      "put an element and retrieve it from the map by RangeKey" in {
        val testMap = emptyRangeTreeMap
        testMap.put(testRange0_5, "test")
        testMap.get(testRange0_5) should contain ("test")
      }

      "put and element in the map, retrieve it, then clear the map" in {
        val testMap = emptyRangeTreeMap
        testMap.put(testRange0_5, "test")
        testMap.get(testRange0_5) should contain ("test")
        testMap.clear()
        testMap.get(testRange0_5) shouldBe empty
      }

      "put and element in the map, retrieve it, then remove it" in {
        val testMap = emptyRangeTreeMap
        testMap.put(testRange0_5, "test")
        testMap.get(testRange0_5) should contain ("test")
        testMap.remove(testRange0_5)
        testMap.get(testRange0_5) shouldBe empty
      }

      "put all of another RangeTreeMap elements in the map, then retrieve it" in {
        val testMap1 = emptyRangeTreeMap
        val testMap2 = emptyRangeTreeMap
        testMap1.put(testRange0_5, "test1")
        testMap2.put(testRange6_10, "test2")
        testMap2.put(testRange11_13, "test3")
        testMap1.putAll(testMap2)
        testMap1.get(testRange0_5) should contain ("test1")
        testMap1.get(testRange6_10) should contain ("test2")
        testMap1.get(testRange11_13) should contain ("test3")
      }

      "return false if the range is not enclosed" in {
        emptyRangeTreeMap.encloses(RangeKey[Int](2, 77)) should be (false)
      }
    }

    "nonempty" should {

      "return true if the range is enclosed" in {
        nonEmptyRangeTreeMap.encloses(testRange0_5) should be (true)
        nonEmptyRangeTreeMap.encloses(RangeKey[Int](2, 7)) should be (true)
      }

      "return false if the range is not enclosed" in {
        nonEmptyRangeTreeMap.encloses(RangeKey[Int](12, 27)) should be (false)
      }

      "return the ranges successfully as a map of ranges" in {
        nonEmptyRangeTreeMap.asMapOfRanges() shouldEqual Map(testRange0_5 -> "test1", testRange6_10 -> "test2")
      }

      "return disjoint sets for non overlapping ranges" in {
        nonEmptyRangeTreeMap.disjoint(testRange0_5, testRange6_10) shouldEqual Set(testRange0_5, testRange6_10)
      }

      "return no disjoint sets for fully overlapping ranges" in {
        nonEmptyRangeTreeMap.disjoint(testRange0_5, testRange0_5) shouldEqual Set()
      }

      "return disjoint sets for partially overlapping ranges" in {
        nonEmptyRangeTreeMap.disjoint(testRange0_5, testRange2_7) shouldEqual Set(RangeKey(0,2), RangeKey(5,7))
      }

      "return nothing for non overlapping ranges" in {
        nonEmptyRangeTreeMap.intersection(testRange0_5, testRange6_10) shouldEqual None
      }

      "return intersection range for fully overlapping ranges" in {
        nonEmptyRangeTreeMap.intersection(testRange0_5, testRange0_5) shouldEqual Some(testRange0_5)
      }

      "return intersection range for partially overlapping ranges" in {
        nonEmptyRangeTreeMap.intersection(testRange0_5, testRange2_7) shouldEqual Some(RangeKey[Int](2, 5))
      }

    }
  }
}