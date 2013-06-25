geo
===

Java utility methods for creating and performing calculations with geohashes.

Status: **pre-alpha**

Features
----------

* encodes geohashes from latitude, longitude to arbitrary length (`GeoHash.encodeHash()`)
* decodes latitude, longitude from geohashes (`GeoHash.decodeHash()`)
* finds adjacent hash in any direction (`GeoHash.adjacentHash()`)
* finds all 8 adjacent hashes to a hash (`GeoHash.neighbours()`)
* calculates hash length to enclose a bounding box (`GeoHash.hashLengthToEncloseBoundingBox`)
* calculates geohashes of given length to cover a bounding box. Returns coverage ratio as well (`GeoHash.coverBoundingBox`)
* simple [api](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/apidocs/index.html)
* good performance (~3 million `GeoHash.encodeHash` calls per second on an I7, single thread)
* no mutable types exposed by api
* threadsafe 
* 100% [unit test coverage](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/cobertura/index.html) (for what that's worth of course!)
* Apache 2.0 licence

Bounding box searches using geohashing
---------------------------------------
Some databases may either not support or suffer major performance degradation when large datasets are queried with multiple range conditions. For example a search for all ship reports within a time range and within a bounding box could be achieved with a range condition on time combined with a range condition on latitude combined with a range condition on longitude (*combined with* = logical AND). This type of query can perform badly on many database types, SQL and NoSQL. On Google App Engine Datastore for instance only one variable with inequality conditions is allowed per query. This is a sensible step to take to meet scalability guarantees.

The bounding box query with a time range can be rejigged using geohashes so that only one variable is subject to a range condition: time.  The method is:

* store geohashes of all lengths (depends on the indexing available, a single full length hash may be enough) in indexed fields against each lat long position in the database
* calculate a set of geohashes that wholly covers the bounding box
* perform the query using the time range and equality against the geohashes. For example:

    `(startTime < t < finishTime) and (hash3='drt' or hash3='dr2'))`

* filter the results of the query to include only those results within the bounding box

The last step is necessary because the set of geohashes contains the bounding box but may be larger than it.

So how long should the hashes be that we try to cover the bounding box with? This will depend on your aims which might be one or more of:

* Minimize cpu
* Minimize query runtime
* Minimize cost 
* Minimize data read from datastore
* Minimize datastore load
* Minimize 2nd tier load


Links
-------

Core geohash encoding code was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>
 
Project reports including Javadocs are [here](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/project-reports.html).

