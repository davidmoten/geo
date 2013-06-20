geo
===

Geohash utilities:

* encodes geohashes from latitude,longitude to arbitrary length (`GeoHash.encodeHash()`)
* decodes latitude,longitude from geohashes (`GeoHash.decodeHash()`)
* finds adjacent hash in any direction (`GeoHash.adjacentHash()`)
* finds all 8 adjacent to a hash (`GeoHash.neighbours()`)
* hash length calculation based on max intercell distance (`GeoHash.minHashLengthToEnsureCellCentreSeparationDistanceIsLessThanMetres`)
* calculate gephashes to cover a bounding box given min hashes per axis (`GeoHash.hashesToCoverBoundingBox`)

Features

* simple [api](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/apidocs/index.html)
* good performance (~2 million encodeHash per second on an I7)
* no mutable types exposed by GeoHash class api
* threadsafe api

Primary source was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>

Project reports including Javadocs are [here](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/project-reports.html).

