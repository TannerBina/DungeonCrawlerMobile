package hu.ait.android.dungeoncrawler.imports.backend;

import java.util.Scanner;

/*
 * Struct that stores a weapon and its information.
 * All public as it is a struct. Can be created
 * from empty, all data, or string of data.
 */
public class Weapon {
	public String name;
	public String dice;
	public int bonus;
	public String finess;

	public Weapon() {
	}

	public Weapon(String name, String dice, int bonus, String finess) {
		this.name = name;
		this.dice = dice;
		this.bonus = bonus;
		this.finess = finess;
	}
	
	public String toString() {
		return "__WEAPON__" + name + "_" + dice +
				"_" + bonus + "_" + finess;
	}

	/*
	 * Create a weapon using the Form NAME_DICE_BONUS_FINESS
	 */
	public Weapon(String data) {
		Scanner s = new Scanner(data);
		s.useDelimiter("_");

		try {
			name = s.next();
			dice = s.next();
			bonus = Integer.parseInt(s.next());
			finess = s.next();
		} catch (Exception e) {
			System.err.println("CANNOT CREATE WEAPON. ERROR IN INPUTTED DATA : " + data);
		}

		s.close();
	}
}
