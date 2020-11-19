package com.github.davidmoten.geo.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.google.common.collect.Lists;

/**
 * Displays benchmarks using geohashing with an H2 database.
 * 
 * @author dave
 * 
 */
public class DatabaseTest {

	/**
	 * Tests inserting records into an H2 file system database with geohashes
	 * and display query times against this database under varying hash lengths.
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	@Test
	public void testCreateDatabaseInsertRecordsAndRunBenchmarkQueries()
			throws IOException, SQLException {
		Connection con = createDatabase();

		long now = System.currentTimeMillis();

		insertRecords(con, now);

		createIndexes(con);

		for (int length = 2; length <= 5; length++) {
			displayQueryTimes(con, now, length);
		}

		displayMultipleRangeQueryTime(con, now);

		con.close();
	}

	private void displayMultipleRangeQueryTime(Connection con, long now)
			throws SQLException {
		System.out.println("--------------------------------------");
		System.out.println("running multiple range query");
		PreparedStatement ps = con
				.prepareStatement("select name,lat,lon from report where time >= ? and time <?  and lat>=-6 and lat<=-5 and lon>=136 and lon<=138");
		ps.setLong(1, now - Math.round(TimeUnit.DAYS.toMillis(1)));
		ps.setLong(2, now);
		long t = System.currentTimeMillis();
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next())
			count++;
		System.out.println("found=" + count + " from " + count + " in "
				+ (System.currentTimeMillis() - t) / 1000.0 + "s");
	}

	private void displayQueryTimes(Connection con, long now, int length)
			throws SQLException {
		System.out.println("--------------------------------------");
		Coverage coverage = GeoHash.coverBoundingBox(-5, 136, -6, 138, length);
		System.out.println("numHashes=" + coverage.getHashes().size());
		StringBuilder s = new StringBuilder();
		for (String hash : coverage.getHashes()) {
			if (s.length() > 1)
				s.append(" or ");
			s.append("geohash" + hash.length() + "='" + hash + "'");
		}
		StringBuilder s2 = new StringBuilder();
		for (String hash : coverage.getHashes()) {
			if (s2.length() > 1)
				s2.append(" or ");
			s2.append("geohash12 like '" + hash + "%'");
		}
		String sql2 = "select name,lat,lon from report where time >= ? and time <?  and ("
				+ s + ")";
		processQuery(now, con, sql2);
		String sql3 = "select name,lat,lon from report where time >= ? and time <?  and ("
				+ s2 + ")";
		System.out.println("using like:");
		processQuery(now, con, sql3);
	}

	private void insertRecords(Connection con, long now) throws SQLException {
		System.out.println("inserting...");
		PreparedStatement st = con
				.prepareStatement("insert into report(time,lat,lon,name,geohash1,geohash2,geohash3,geohash4,geohash5,geohash6,geohash7,geohash8,geohash9,geohash10,geohash11,geohash12) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		long n;
		if (System.getProperty("n") != null)
			n = Long.parseLong(System.getProperty("n"));
		else
			n = 1000;
		for (int i = 0; i < n; i++) {
			long t = now - Math.round(TimeUnit.DAYS.toMillis(1));
			double lat = -Math.random() * 10;
			double lon = 135 + Math.random() * 10;
			st.setLong(1, t);
			st.setDouble(2, lat);
			st.setDouble(3, lon);
			st.setString(4, "name");
			for (int j = 5; j <= 16; j++)
				st.setString(j, GeoHash.encodeHash(lat, lon, j - 4));
			st.executeUpdate();
		}
		st.close();
	}

	private Connection createDatabase() throws IOException, SQLException {
		File dir = new File("target/db");
		FileUtils.deleteDirectory(dir);
		dir.mkdir();
		String script = IOUtils.toString(
				DatabaseTest.class.getResourceAsStream("/create.sql"), "UTF-8");
		String[] commands = script.split(";");
		String url = "jdbc:h2:file:target/db/test";
		Connection con = DriverManager.getConnection(url, "sa", "");
		for (String command : commands) {
			execute(con, command);
		}
		return con;
	}

	private void createIndexes(Connection con) throws SQLException {
		System.out.println("creating indexes");
		execute(con, "create index idx_report_time on report(time)");
		for (int i = 1; i <= 12; i++)
			execute(con, "create index idx_geohash_" + i + " on report(geohash"
					+ i + ")");
	}

	private void processQuery(long now, Connection con, String sql)
			throws SQLException {
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, now - Math.round(TimeUnit.DAYS.toMillis(1)));
		ps.setLong(2, now);
		long t = System.currentTimeMillis();
		System.out.println("querying...");
		ResultSet rs = ps.executeQuery();
		List<String> names = Lists.newArrayList();
		int count = 0;
		while (rs.next()) {
			String name = rs.getString(1);
			double lat = rs.getDouble(2);
			double lon = rs.getDouble(3);
			count++;
			if (lat >= -6 && lat <= -5 && lon >= 136 && lon <= 138)
				names.add(name);
		}
		System.out.println("found=" + names.size() + " from " + count + " in "
				+ (System.currentTimeMillis() - t) / 1000.0 + "s");
		ps.close();
	}

	private void execute(Connection con, String command) throws SQLException {
		System.out.println(command);
		PreparedStatement st = con.prepareStatement(command);
		st.executeUpdate();
		st.close();
	}

}
