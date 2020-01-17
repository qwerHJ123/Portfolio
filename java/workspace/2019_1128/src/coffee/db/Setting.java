package coffee.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.JOptionPane;

public class Setting {

	public void Go() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/?serverTimezone=UTC", "root", "1111");
			Statement stmt = con.createStatement();

			stmt.execute("DROP SCHEMA IF EXISTS coffee");
			stmt.execute("CREATE SCHEMA IF NOT EXISTS `coffee` DEFAULT CHARACTER SET utf8");
			stmt.execute("use `coffee` ;");

			stmt.execute(
					"CREATE TABLE IF NOT EXISTS `coffee`.`user` (\r\n" + "  `u_no` INT(11) NOT NULL AUTO_INCREMENT,\r\n"
							+ "  `u_id` VARCHAR(20) NULL,\r\n" + "  `u_pw` VARCHAR(4) NULL,\r\n"
							+ "  `u_name` VARCHAR(5) NULL,\r\n" + "  `u_bd` VARCHAR(14) NULL,\r\n"
							+ "  `u_point` INT(11) NULL,\r\n" + "  `u_grade` VARCHAR(45) NULL,\r\n"
							+ "  PRIMARY KEY (`u_no`))\r\n" + "ENGINE = InnoDB;\r\n" + "");

			stmt.execute(
					"CREATE TABLE IF NOT EXISTS `coffee`.`menu` (\r\n" + "  `m_no` INT NOT NULL AUTO_INCREMENT,\r\n"
							+ "  `m_group` VARCHAR(10) NULL,\r\n" + "  `m_name` VARCHAR(30) NULL,\r\n"
							+ "  `m_price` INT(11) NULL,\r\n" + "  PRIMARY KEY (`m_no`))\r\n" + "ENGINE = InnoDB;");

			stmt.execute("CREATE TABLE IF NOT EXISTS coffee.orderlist (\r\n" + "  `o_no` INT(11) NOT NULL,\r\n"
					+ "  `o_date` DATE NULL,\r\n" + "  `u_no` INT(11) NULL,\r\n" + "  `m_no` INT(11) NULL,\r\n"
					+ "  `o_group` VARCHAR(10) NULL,\r\n" + "  `o_size` VARCHAR(1) NULL,\r\n"
					+ "  `o_price` INT(11) NULL,\r\n" + "  `o_count` INT(11) NULL,\r\n"
					+ "  `o_amount` INT(11) NULL,\r\n" + "  PRIMARY KEY (`o_no`),\r\n"
					+ "  INDEX `fk_orderlist_user_idx` (`u_no` ASC) VISIBLE,\r\n"
					+ "  INDEX `fk_menu_orderlist_m_no_idx` (`m_no` ASC) VISIBLE,\r\n"
					+ "  CONSTRAINT `fk_user_orderlist_u_no`\r\n" + "    FOREIGN KEY (`u_no`)\r\n"
					+ "    REFERENCES `coffee`.`user` (`u_no`)\r\n" + "    ON DELETE CASCADE\r\n"
					+ "    ON UPDATE CASCADE,\r\n" + "  CONSTRAINT `fk_menu_orderlist_m_no`\r\n"
					+ "    FOREIGN KEY (`m_no`)\r\n" + "    REFERENCES `coffee`.`menu` (`m_no`)\r\n"
					+ "    ON DELETE CASCADE\r\n" + "    ON UPDATE CASCADE)\r\n" + "ENGINE = InnoDB;");

			stmt.execute("CREATE TABLE IF NOT EXISTS `coffee`.`shopping` (\r\n" + "  `s_no` INT(11) NOT NULL,\r\n"
					+ "  `m_no` INT(11) NULL,\r\n" + "  `s_price` INT(11) NULL,\r\n" + "  `s_count` INT(11) NULL,\r\n"
					+ "  `s_size` VARCHAR(1) NULL,\r\n" + "  `s_amount` INT(11) NULL,\r\n"
					+ "  PRIMARY KEY (`s_no`),\r\n" + "  INDEX `fk_menu_shopping_m_no_idx` (`m_no` ASC) VISIBLE,\r\n"
					+ "  CONSTRAINT `fk_menu_shopping_m_no`\r\n" + "    FOREIGN KEY (`m_no`)\r\n"
					+ "    REFERENCES `coffee`.`menu` (`m_no`)\r\n" + "    ON DELETE CASCADE\r\n"
					+ "    ON UPDATE CASCADE)\r\n" + "ENGINE = InnoDB;");

			PreparedStatement pstInsertUser = con.prepareStatement("INSERT INTO user VALUES(?, ?, ?, ?, ?, ?, ?)");
			PreparedStatement pstInsertMenu = con.prepareStatement("INSERT INTO menu VALUES(?, ?, ?, ?)");
			PreparedStatement pstInsertOrderlist = con.prepareStatement("INSERT INTO orderlist VALUES(?, ?, ?, ?, ?, ?, ?, ? ,?)");

			importData(pstInsertUser, "../DataFiles/user.txt");
			importData(pstInsertMenu, "../DataFiles/menu.txt");
			importData(pstInsertOrderlist, "../DataFiles/orderlist.txt");

			stmt.execute("DROP USER IF EXISTS 'user'@'%'");
			stmt.execute("CREATE USER 'user'@'%' IDENTIFIED BY '1111'");
			stmt.execute("GRANT SELECT, INSERT, UPDATE , DELETE ON coffee.* TO 'user'@'%'");
			JOptionPane.showMessageDialog(null, "DB�����Ϸ�", "�˸�", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "DB��������", "�˸�", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, e.getMessage(), "�˸�", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void importData(PreparedStatement pst, String path) throws IOException, SQLException {
		List<String> lines = Files.readAllLines(Paths.get(path));

		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] split = line.split("\t");

			for (int j = 0; j < split.length; j++) {
				pst.setString(j + 1, split[j]);

			}

			pst.executeUpdate();
		}
	}
}