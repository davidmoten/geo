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

###What is the problem?

Databases of events at specific times occurring at specific places on the earth's surface are likely to be queried in terms of ranges of time and position. One such query is a bounding box query involving a time range and position constraint defined by a bounding lat-long box. 

The challenge is to make your database run these queries quickly. 

Some databases may either not support or suffer major performance degradation when large datasets are queried with inequality conditions on more than one variable.

For example, a search for all ship reports within a time range and within a bounding box could be achieved with a range condition on time combined with a range condition on latitude combined with a range condition on longitude ( *combined with* = logical AND). This type of query *can* perform badly on many database types, SQL and NoSQL. On Google App Engine Datastore for instance only one variable with inequality conditions is allowed per query. This is a sensible step to take to meet scalability guarantees.

###What is a solution?
The bounding box query with a time range can be rewritten using geohashes so that only one variable is subject to a range condition: time.  The method is:

* store geohashes of all lengths (depends on the indexing strategies available, a single full length hash may be enough) in indexed fields against each lat long position in the database. Note that storing hashes as a single long integer value may be advantageous.
* calculate a set of geohashes that wholly covers the bounding box
* perform the query using the time range and equality against the geohashes. For example:

```
(startTime <= t < finishTime) and (hash3='drt' or hash3='dr2')
```

* filter the results of the query to include only those results within the bounding box

The last step is necessary because the set of geohashes contains the bounding box but may be larger than it.

###What hash length to use?
So how long should the hashes be that we try to cover the bounding box with? This will depend on your aims which might be one or more of minimizing: cpu, url fetch time, financial cost, total data transferred from datastore, database load, 2nd tier load, or a heap of other possible metrics. If you could boil things down to a *representative* use case I would suggest that a *reasonable* length of geohash to use to cover a bounding box is:

```
(the maximum length of hash to completely cover the bounding box with one hash) + 1
```

Calling `GeoHash.coverBoundingBox` without a hash length parameter will use the hash length recommended above.

Increasing this value by 1 or 2 should be considered if benchmarking indicates an advantage.

As a quick example, for a bounding box proportioned more a less like a [screen with Schenectady NY and Hartford CT in USA at the corners](https://maps.google.com.au/maps?q=schenectady+to+hartford&saddr=schenectady&daddr=hartford&hl=en&ll=42.287469,-73.265076&spn=1.692503,2.37854&sll=42.37072,-73.262329&sspn=1.690265,2.37854&geocode=FSNLjQIdj8WX-yml-HU1_W3eiTF6shJvjXCyGQ%3BFX9DfQId2-mq-ymlURHyEVPmiTGZWX3pqEqOzA&gl=au&t=m&z=9):

Here are the hash counts for different hash lengths:

`n` is number of hashes, `m` is the size in square degrees of the total hashed area and `a` is the area of the bounding box.

```
length  numHashes m/a    n*m/a
1       1         1694   1694
2       1         53     53
3*      4         6.6    26.4
4       30        1.6    48
5       667       1.08   720
6       20227     1.02   20631
```
The starred line corresponds to the hash length suggested above and corresponds to the lowest value of `n*m/a`.

The recommended hash length for this example is 3. Increasing to 4 or 5 may be advantageous depending on your situation. Increasing to 6 might have a big negative impact on processing times but this will depend again on your situation. 

A rigorous exploration of this topic would be fun to do or see. Let me know if you've done it or have a link and I'll update this page!

###Benchmarks
Inserted 10,000,000 records into an embedded H2 instance which uses B-tree indexes. The records were geographically randomly distributed across a region then a bounding box of 1/50th the area of the region was chosen. Query performed as follows (time is the time to run the query and iterate the results):

```
numHashes  found   from  time(s)
2          200K    10m   56.0
6          200k    1.2m  10.5
49         200k    303k  4.5
1128       200k    217K  3.6
none       200k    200k  31.1
```

To run this benchmark:

```
mvn clean test -Dn=10000000
```

Running with n=1,000,000 is much quicker to run and yields the same primary result:

```
multiple range query is 10X slower than geohash lookup if the hash length is chosen judiciously
```

Links
-------

Core geohash encoding code was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>
 
Project reports including Javadocs are [here](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/project-reports.html).

