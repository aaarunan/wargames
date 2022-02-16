import Unit.*;

public class Battle {
    /**
     * A Battle is where Armies fight.
     * A fight is done by a random Unit in a random Army attacking
     * the opposing Army.
     */

    private Army attacker;
    private Army defender;

    private int numOfAttacks = 0;

    /**
     * Constructs a Battle by a defending army, and an attacking Army.
     *
     * @param attacker attacking Army.
     * @param defender defending Army.
     */

    public Battle(Army attacker, Army defender) {
        this.attacker = attacker;
        this.defender = defender;

    }

    /**
     * This simulates a fight.
     * A random Unit from each army will attack a random Unit
     * of the opposing Army. This happens in a loop unit there an army
     * has no units left to attack with.
     *
     * @return winning Army.
     * @throws IllegalStateException if the armies has no Units.
     */

    public Army simulate() throws IllegalStateException {
        if (!attacker.hasUnits() || !defender.hasUnits()) {
            throw new IllegalStateException("All armies must have atleast one unit.");
        }

        Army temp;

        while (attacker.hasUnits() && defender.hasUnits()) {
            Unit attackerUnit = attacker.getRandom();
            Unit defenderUnit = defender.getRandom();

            attackerUnit.attack(defenderUnit);

            if (defenderUnit.isDead()) {
                defender.remove(defenderUnit);
            }

            temp = attacker;
            attacker = defender;
            defender = temp;

            numOfAttacks++;

        }

        return attacker.hasUnits() ? attacker : defender;

    }

    public int getNumOfAttacks() {
        return numOfAttacks;
    }

    @Override
    public String toString() {
        return "Battle" +
                "attacker: " + attacker +
                "defender: " + defender;
    }
}
