public class InfantryUnit extends Unit {
    private final int attackBonus = 2;
    private final int resistBonus = 1;

    public InfantryUnit(String type, int health) {
        super(type, health, 15, 10);
    }

    @Override
    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public int getResistBonus() {
        return resistBonus;
    }
}
