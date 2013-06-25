package com.github.davidmoten.geo.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.github.davidmoten.geo.GeoHash;

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
		for (int i = 0; i < 10000000; i++) {
			long now = System.currentTimeMillis();
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
		con.close();
	}

	private void execute(Connection con, String command) throws SQLException {
		System.out.println(command);
		PreparedStatement st = con.prepareStatement(command);
		st.executeUpdate();
		st.close();
	}

}
