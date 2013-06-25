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

For example, a search for all ship reports within a time range and within a bounding box could be achieved with a range condition on time combined with a range condition on latitude combined with a range condition on longitude ( *combined with* = logical AND). This type of query *can* perform badly on many database types, SQL and NoSQL. On Google App Engine Datastore for instance only one variable with inequality conditions is allowed per query. This is a sensible step to take to meet scalability guarantees.

The bounding box query with a time range can be rewritten using geohashes so that only one variable is subject to a range condition: time.  The method is:

* store geohashes of all lengths (depends on the indexing strategies available, a single full length hash may be enough) in indexed fields against each lat long position in the database
* calculate a set of geohashes that wholly covers the bounding box
* perform the query using the time range and equality against the geohashes. For example:

```
(startTime <= t < finishTime) and (hash3='drt' or hash3='dr2')
```

* filter the results of the query to include only those results within the bounding box

The last step is necessary because the set of geohashes contains the bounding box but may be larger than it.

So how long should the hashes be that we try to cover the bounding box with? This will depend on your aims which might be one or more of minimizing: cpu, url fetch time, financial cost, total data transferred from datastore, database load, 2nd tier load, or a heap of other possible metrics. If you could boil things down to a *representative* use case I would suggest that a *good* length of geohash to use to cover a bounding box is:

```
(the maximum length of hash to completely cover the bounding box with one hash) + 1
```

Calling `GeoHash.coverBoundingBox` without a hash length parameter will use the hash length recommended above.

The suggested hash length is based roughly on the assumptions that the:

* database query is not processed using concurrency
* points are uniformly distributed geographically
* points are numerous (quantify this!)
* bounding box is square to screen-like in proportions (rather than very wide/high and skinny)
* query time is O(n * m / a) where n is number of hashes, m is the size in square degrees of the hash and a is the area of the bounding box.

As a quick example for a bounding box proportioned more a less like a [screen with Schenectady NY and Hartford CT in USA at the corners](https://maps.google.com.au/maps?q=schenectady+to+hartford&saddr=schenectady&daddr=hartford&hl=en&ll=42.287469,-73.265076&spn=1.692503,2.37854&sll=42.37072,-73.262329&sspn=1.690265,2.37854&geocode=FSNLjQIdj8WX-yml-HU1_W3eiTF6shJvjXCyGQ%3BFX9DfQId2-mq-ymlURHyEVPmiTGZWX3pqEqOzA&gl=au&t=m&z=9) here are the hash counts for different hash lengths:
```
length  numHashes ratio  n*m/a
1       1         1694   1694
2       1         53     53
3*      4         6.6    26.4
4       30        1.6    48
5       667       1.08   720
6       20227     1.02   20631
```
The starred line corresponds to the hash length suggested above and corresponds to the lowest value of n*m/a.

The recommended hash length for this example is 3. Increasing to 5 and above could clearly have a big impact on processing times but this will depend on your situation. 

A rigorous exploration of this topic would be fun to do or see. Let me know if you've done it or have a link and I'll update this page!

Links
-------

Core geohash encoding code was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>
 
Project reports including Javadocs are [here](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/project-reports.html).

