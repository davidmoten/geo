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

import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.google.common.collect.Lists;

public class DatabaseTest {

	// @Test
	public void testCreateDatabase() throws IOException, SQLException {
		File dir = new File("target/db");
		dir.mkdir();
		FileUtils.deleteDirectory(dir);
		String sql = IOUtils.toString(
				DatabaseTest.class.getResourceAsStream("/create.sql"), "UTF-8");
		String[] commands = sql.split(";");
		String url = "jdbc:h2:file:target/db/test";
		Connection con = DriverManager.getConnection(url, "sa", "");
		for (String command : commands) {
			execute(con, command);
		}
		System.out.println("inserting...");
		PreparedStatement st = con
				.prepareStatement("insert into report(time,lat,lon,name,geohash1,geohash2,geohash3,geohash4,geohash5,geohash6,geohash7,geohash8,geohash9,geohash10,geohash11,geohash12) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
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
		System.out.println("creating indexes");
		execute(con, "create index idx_report_time on report(time)");
		for (int i = 1; i <= 12; i++)
			execute(con, "create index idx_geohash_" + i + " on report(geohash"
					+ i + ")");
		for (int n = 0; n < 1; n++) {
			for (int length = 2; length <= 4; length++) {
				Coverage coverage = GeoHash.coverBoundingBox(-5, 136, -6, 138,
						length);
				System.out.println(coverage.getHashes());
				StringBuilder s = new StringBuilder();
				for (String hash : coverage.getHashes()) {
					if (s.length() > 1)
						s.append(" or ");
					s.append("geohash" + hash.length() + "='" + hash + "'");
				}
				String sql2 = "select name,lat,lon from report where time >= ? and time <?  and ("
						+ s + ")";
				System.out.println(sql2);
				PreparedStatement ps = con.prepareStatement(sql2);
				ps.setLong(1, now - Math.round(TimeUnit.DAYS.toMillis(1)));
				ps.setLong(2, now);
				long t = System.currentTimeMillis();
				System.out.println("querying...");
				ResultSet rs = ps.executeQuery();
				List<String> names = Lists.newArrayList();
				System.out.println((System.currentTimeMillis() - t) / 1000.0
						+ "ms");
				while (rs.next()) {
					String name = rs.getString(1);
					double lat = rs.getDouble(2);
					double lon = rs.getDouble(3);
					if (lat >= -6 && lat <= -5 && lon >= 136 && lon < 138)
						names.add(name);
				}
				System.out.println("found=" + names.size());
				ps.close();
			}
		}
		con.close();
	}

	private void execute(Connection con, String command) throws SQLException {
		System.out.println(command);
		PreparedStatement st = con.prepareStatement(command);
		st.executeUpdate();
		st.close();
	}

}
