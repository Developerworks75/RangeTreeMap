package com.charlesahunt

import scala.collection.mutable

/**
  * A data structure backed by a mutable TreeMap where numeric ranges map to values and where the key, K, is the lower bound.
  *
  * @param initialMap Optional TreeMap structure to initialize RangeTreeMap
  * @param ordering   A strategy for sorting instances of a type
  * @tparam K some type with an Ordering defined for it
  * @tparam V any value
  */
class RangeTreeMap[K, V](initialMap: Option[mutable.TreeMap[K, RangeEntry[K, V]]] = None)(implicit ordering : scala.Ordering[K]) {

  val rangeTreeMap: mutable.TreeMap[K, RangeEntry[K, V]] = initialMap.getOrElse(new mutable.TreeMap[K, RangeEntry[K, V]])

  /**
    * Returns a view of this range map as an unmodifiable Map[Range[K], V].
    */
  def asMapOfRanges(): Map[RangeKey[K], V] =
    rangeTreeMap.values.map(entry => entry.range -> entry.value).toMap

  def clear(): Unit = rangeTreeMap.clear

  def get(range: RangeKey[K]): Option[V] = rangeTreeMap.get(range.lower).map(_.value)

  /**
    * Puts given value in map. Does not coalesce ranges.
    */
  def put(range: RangeKey[K], value: V): Option[RangeEntry[K, V]] =
    rangeTreeMap.put(range.lower, RangeEntry(range, value))

  /**
    * Puts all the associations from rangeMap into this range map.
    */
  def putAll(rangeMap: RangeTreeMap[K, V]):  RangeTreeMap[K, V] = {
    rangeTreeMap.++=(rangeMap.rangeTreeMap)
    this
  }

  def remove(rangeToRemove: RangeKey[K]): Option[RangeEntry[K, V]] =
    rangeTreeMap.remove(rangeToRemove.lower)

  /**
    * Returns the minimal range enclosing the ranges in this RangeMap.
    */
  def span(): Option[RangeKey[K]] =
    rangeTreeMap.headOption.map(head => RangeKey(head._1, rangeTreeMap.last._1))

  /**
    * Returns a view of the part of this range map that intersects with range.
    */
  def subRangeMap(subRange: RangeKey[K]): RangeTreeMap[K, V] =
    RangeTreeMap.apply[K, V](Some(intersection(subRange)))

  /**
    * Maps a range to a specified value, coalescing this range with any existing ranges with the same value that are connected to this range.
    */
  def putCoalescing(range: RangeKey[K], value: V): Option[RangeEntry[K, V]] =
    if(rangeTreeMap.isEmpty || !encloses(range))
      put(range, value)
    else {
      val intersections = intersection(range)
      val lowerMatch = intersections.get(range.lower)
      if(intersections.isEmpty && lowerMatch.isEmpty) put(range, value)
      else {
        throw new Exception("Coalescing of intersecting ranges not yet implemented.")
      }
    }

  /**
    * Checks if the given range is inclusively within the lowest lower bound and the greatest upper bound of the entire RangeTreeMap
    *
    * @param range the range to check for enclosure
    * @return
    */
  def encloses(range: RangeKey[K]): Boolean =
    (ordering.gteq(range.lower, rangeTreeMap.head._2.range.lower) && ordering.lteq(range.lower, rangeTreeMap.last._2.range.upper)) &&
      (ordering.lteq(range.upper, rangeTreeMap.last._2.range.upper) && ordering.gteq(range.upper, rangeTreeMap.head._2.range.lower))

  /**
    * Finds all inclusively intersecting ranges with `subRange` in the map
    *
    * @param subRange
    * @return
    */
  def intersection(subRange: RangeKey[K]): mutable.TreeMap[K, RangeEntry[K, V]] =
    rangeTreeMap.filter { entry =>
      ordering.lteq(entry._2.range.lower, subRange.upper) && ordering.gteq(entry._2.range.upper, subRange.lower)
    }

}

object RangeTreeMap {

  def apply[K, V](implicit ordering : scala.Ordering[K]): RangeTreeMap[K, V] = new RangeTreeMap[K, V]

  def apply[K, V](initialMap: Option[mutable.TreeMap[K, RangeEntry[K, V]]])(implicit ordering : scala.Ordering[K]): RangeTreeMap[K, V] =
    new RangeTreeMap[K, V](initialMap)

}

final case class RangeKey[K](lower: K, upper: K)

final case class RangeEntry[K, V](
  range: RangeKey[K],
  value: V
)