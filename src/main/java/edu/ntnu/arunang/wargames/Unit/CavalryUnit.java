package edu.ntnu.arunang.wargames.Unit;

public class CavalryUnit extends Unit {

    /**
     * A CavalryUnit is a specialized melee unit, that does not have great defense.
     * CavalryUnit has a special ability where its first hit deals more damage.
     * <p>
     * Stats:
     * attackPoints of 20,
     * armorPoints of 12,
     * attackBonus of 4+2,
     * resistBonus of 1
     */

    protected final static int BASE_ATTACK_BONUS = 2;
    protected final static int FIRST_ATTACK_BONUS = 6;
    protected final static int RESIST_BONUS = 1;

    protected boolean hasAttacked = false;

    protected final static int ATTACK_POINTS = 20;
    protected final static int ARMOR_POINTS = 12;

    /**
     * Constructs the CavalryUnit with the given stats
     *
     * @param name   must not be empty
     * @param health must be greater than 0
     */

    public CavalryUnit(String name, int health) {
        super(name, health, ATTACK_POINTS, ARMOR_POINTS);
    }

    /**
     * Constructor designed for Unit classes that needs to
     * change the stats for the Unit.
     *
     * @param name         must not be empty
     * @param health       must be greater than 0
     * @param attackPoints must be greater than 0
     * @param armorPoints  must be greater than 0;
     */

    protected CavalryUnit(String name, int health, int attackPoints, int armorPoints) {
        super(name, health, attackPoints, armorPoints);
    }

    @Override
    public CavalryUnit copy() {
        CavalryUnit copy = new CavalryUnit(this.getName(), this.getHealthPoints(), this.getAttackPoints(), this.getArmorPoints() );
        copy.hasAttacked = this.hasAttacked;
        return copy;
    }

    @Override
    public int getAttackBonus() {
        return hasAttacked ? BASE_ATTACK_BONUS : FIRST_ATTACK_BONUS;
    }

    @Override
    public int getResistBonus() {
        return RESIST_BONUS;
    }


    /**
     * Calls the super method, and manages the units special ability.
     * Attacking once will demote to BASE_ATTACK_BONUS
     *
     * @param opponent The opposing unit that is being attacked
     */

    @Override
    public void attack(Unit opponent) {
        super.attack(opponent);
        hasAttacked = true;
    }


}