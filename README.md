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
Some databases may either not support or suffer major performance degradation when large datasets are queried with inequality conditions on more than one variable.

For example, a search for all ship reports within a time range and within a bounding box could be achieved with a range condition on time combined with a range condition on latitude combined with a range condition on longitude (*combined with* = logical AND). This type of query *can* perform badly on many database types, SQL and NoSQL. On Google App Engine Datastore for instance only one variable with inequality conditions is allowed per query. This is a sensible step to take to meet scalability guarantees. In short it avoids O(n&sup;2) performance.

The bounding box query with a time range can be rewritten using geohashes so that only one variable is subject to a range condition: time.  The method is:

* store geohashes of all lengths (depends on the indexing strategies available, a single full length hash may be enough) in indexed fields against each lat long position in the database
* calculate a set of geohashes that wholly covers the bounding box
* perform the query using the time range and equality against the geohashes. For example:

```
(startTime < t < finishTime) and (hash3='drt' or hash3='dr2'))
```

* filter the results of the query to include only those results within the bounding box

The last step is necessary because the set of geohashes contains the bounding box but may be larger than it.

So how long should the hashes be that we try to cover the bounding box with? This will depend on your aims which might be one or more of:

* Minimize cpu
* Minimize total url fetch time
* Minimize cost (might be a combination of cpu, datastore reads, etc.)
* Minimize data transfer from datastore
* Minimize database load
* Minimize 2nd tier load
* any other metric!

My suggestion is that a *good* length of geohash to use to cover a bounding box is:
```
(the maximum length of hash to completely cover the bounding box with one hash) + 1
```

Calling `GeoHash.coverBoundingBox` without a hash length parameter will use the hash length recommended above.

This suggestion is based roughly on the assumptions that:

* no concurrent processing of database query
* points are uniformly distributed geographically
* bounding box is square to screen-like in proportions (rather than very wide/high and skinny)
* query time is O(n) where n is number of hashes

If you really needed to close approximate the bounding box with hashes then increment the hash length by 1 again but that's as far as I would go. As a quick example for a bounding box proportioned more a less like a screen with Schenectady and Hartford in Massachusets in USA at the corners here are the hash counts for different hash lengths:

length=1  numHashes=1
length=2  numHashes=2
length=3  numHashes=4*
length=4  numHashes=30
length=5  numHashes=667
length=6  numHashes=20227

The starred line corresponds to the hash length suggested above.


A rigorous exploration of this topic would be fun. Let me know if you've done it or have a link and I'll update this page!

Links
-------

Core geohash encoding code was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>
 
Project reports including Javadocs are [here](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/project-reports.html).

