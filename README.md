geo
===
<a href="https://travis-ci.org/davidmoten/geo"><img src="https://travis-ci.org/davidmoten/geo.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/geo/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/geo)

Java utility methods for geohashing.

Status: *production*, available on Maven Central

Maven site reports are [here](http://davidmoten.github.io/geo/index.html) including [javadoc](http://davidmoten.github.io/geo/apidocs/index.html).

Add this to your pom:

    <dependency>
        <groupId>com.github.davidmoten</groupId>
        <artifactId>geo</artifactId>
        <version>0.7.1</version>
    </dependency>

Release Notes
----------------
* 0.7 - performance improvements to ```GeoHash.encodeHash``` and others ([#13](https://github.com/davidmoten/geo/issues/13)), ([#14](https://github.com/davidmoten/geo/issues/14)), thanks @niqueco
* 0.6.10 - compiled to java 1.6 for Android compatibility
* 0.6.8 - get Position class from grumpy-core artifact which includes ```Position.longitudeDiff``` fix.
* 0.6.7 - Base32.encodeBase32 now pads to max hash length which is a *breaking change* ([#9](https://github.com/davidmoten/geo/issues/9)), thanks @gnellzynga, 
fixed use of DEFAULT_MAX_HASHES in doco ([#10](https://github.com/davidmoten/geo/issues/10)).
* 0.6.6 - fixes [#8](https://github.com/davidmoten/geo/issues/8) boundary hash calculations should match geohash.org reference implementation (thanks D J Hagberg)
* 0.6.5 - fixes issue [#6](https://github.com/davidmoten/geo/issues/6) GeoHash.coverBoundingBox fails when extent is larger than that covered by a single 1 letter hash
* 0.6 - handles neighbour calculations on borders, removed guava dependency, minor api additions
* 0.5 - first release to Maven Central

Features
----------
* simple [api](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/apidocs/index.html)
* encodes geohashes from latitude, longitude to arbitrary length (`GeoHash.encodeHash`)
* decodes latitude, longitude from geohashes (`GeoHash.decodeHash`)
* finds adjacent hash in any direction (`GeoHash.adjacentHash`), works on borders including the poles too
* finds all 8 adjacent hashes to a hash (`GeoHash.neighbours`)
* calculates hash length to enclose a bounding box (`GeoHash.hashLengthToCoverBoundingBox`)
* calculates geohashes of given length to cover a bounding box. Returns coverage ratio as well (`GeoHash.coverBoundingBox`)
* calculates height and width of geohashes in degrees (`GeoHash.heightDegrees` and `GeoHash.widthDegrees`)
* encodes and decodes `long` values from geohashes (`Base32.encodeBase32` and `Base32.decodeBase32`)
* good performance (~3 million `GeoHash.encodeHash` calls per second on an I7, single thread)
* no mutable types exposed by api
* threadsafe 
* 100% [unit test coverage](https://xuml-tools.ci.cloudbees.com/job/geo%20site/site/cobertura/index.html) (for what that's worth of course!)
* Apache 2.0 licence
* Published to Maven Central

Bounding box searches using geohashing
---------------------------------------

###What is the problem?

Databases of events at specific times occurring at specific places on the earth's surface are likely to be queried in terms of ranges of time and position. One such query is a bounding box query involving a time range and position constraint defined by a bounding lat-long box. 

The challenge is to make your database run these queries quickly. 

Some databases may either not support or suffer significant performance degradation when large datasets are queried with inequality conditions on more than one variable.

For example, a search for all ship reports within a time range and within a bounding box could be achieved with a range condition on time combined with a range condition on latitude combined with a range condition on longitude ( *combined with* = logical AND). This type of query *can* perform badly on many database types, SQL and NoSQL. On Google App Engine Datastore for instance only one variable with inequality conditions is allowed per query. This is a sensible step to take to meet scalability guarantees.

###What is a solution?
The bounding box query with a time range can be rewritten using geohashes so that only one variable is subject to a range condition: time.  The method is:

* store geohashes of all lengths (depends on the indexing strategies available, a single full length hash may be enough) in indexed fields against each lat long position in the database. Note that storing hashes as a single long integer value may be advantageous (see `Base32.decodeBase32` to convert a hash to a long).
* calculate a set of geohashes that wholly covers the bounding box
* perform the query using the time range and equality against the geohashes. For example:

```
(startTime <= t < finishTime) and (hash3='drt' or hash3='dr2')
```

* filter the results of the query to include only those results within the bounding box

The last step is necessary because the set of geohashes contains the bounding box but may be larger than it.

###What hash length to use?
So how long should the hashes be that we try to cover the bounding box with? This will depend on your aims which might be one or more of minimizing: cpu, url fetch time, financial cost, total data transferred from datastore, database load, 2nd tier load, or a heap of other possible metrics. 

Calling `GeoHash.coverBoundingBox` with just the bounding points and no additional parameters will return hashes of a length such that the number of hashes is as many as possible but less than or equal to `GeoHash.DEFAULT_MAX_HASHES` (12).

You can explicitly control maxHashes by calling `GeoHash.coverBoundingBoxMaxHashes`.

As a quick example, for a bounding box proportioned more a less like a [screen with Schenectady NY and Hartford CT in USA at the corners](https://maps.google.com.au/maps?q=schenectady+to+hartford&saddr=schenectady&daddr=hartford&hl=en&ll=42.287469,-73.265076&spn=1.692503,2.37854&sll=42.37072,-73.262329&sspn=1.690265,2.37854&geocode=FSNLjQIdj8WX-yml-HU1_W3eiTF6shJvjXCyGQ%3BFX9DfQId2-mq-ymlURHyEVPmiTGZWX3pqEqOzA&gl=au&t=m&z=9):

Here are the hash counts for different hash lengths:

`m` is the size in square degrees of the total hashed area and `a` is the area of the bounding box.

```
length  numHashes m/a    
1           1     1694   
2           1       53     
3           4        6.6    
4          30        1.6    
5         667        1.08   
6       20227        1.02   
```

Only testing against your database and your preferrably real life data will determine what the optimal maxHashes value is. In the benchmarks section below a test with H2 database found that optimal query time was when maxHashes is about 700. I doubt that this would be the case for many other databases. 

A rigorous exploration of this topic would be fun to do or see. Let me know if you've done it or have a link and I'll update this page!

###Hash height and width formulas
This is the relationship between a hash of length n and its height and width in degrees:

First define this function:

&nbsp;&nbsp;&nbsp;&nbsp;parity(n) = 0 if n is even otherwise 1

Then

&nbsp;&nbsp;&nbsp;&nbsp;width = 180 / 2<sup>(5n+parity(n)-2)/2</sup> degrees

&nbsp;&nbsp;&nbsp;&nbsp;height = 180 / 2<sup>(5n-parity(n))/2</sup> degrees

The height and width in kilometres will be dependent on what part of the earth the hash is on and can be calculated using `Position.getDistanceToKm`. 
For example at (lat,lon):
```java
double distancePerDegreeWidth =
     new Position(lat,lon).getDistanceToKm(new Position(lat, lon+1));
``` 

###Benchmarks
Inserted 10,000,000 records into an embedded H2 filesystem database which uses B-tree indexes. The records were geographically randomly distributed across a region then a bounding box of 1/50th the area of the region was chosen. Query performed as follows (time is the time to run the query and iterate the results):

```
hashLength numHashes  found   from  time(s) 
2          2          200K    10m   56.0    
3          6          200k    1.2m  10.5
4          49         200k    303k   4.5
5          1128       200k    217K   3.6
none       none       200k    200k  31.1 (multiple range query)
```
I was pleasantly surprised that H2 allowed me to put over 1000 conditions in the where clause. I tried with the next higher hash length as well with over 22,000 hashes but H2 understandably threw a StackOverFlowError.  

To run the benchmark:

```
mvn clean test -Dn=10000000
```

Running with n=1,000,000 is much quicker to run and yields the same primary result:

```
multiple range query is 10X slower than geohash lookup if the hash length is chosen judiciously
```

Links
-------

* Core geohash encoding code was a translation to java of https://github.com/davetroy/geohash-js/blob/master/geohash.js.
* [Immutable R-tree implementation](https://github.com/davidmoten/rtree) in java by the same author
