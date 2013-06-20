geo
===

Geohash utilities:

* encodes geohashes from latitude,longitude to arbitrary length (GeoHash.encodeHash())
* decodes latitude,longitude from geohashes (GeoHash.decodeHash())
* finds adjacent hash in any direction (GeoHash.adjacentHash())
* finds all 8 adjacent to a hash (GeoHash.neighbours())

Features

* simple api
* good performance
* no mutable types exposed by api
* threadsafe api
* 100% unit test coverage

Primary source was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.
