package edu.ntnu.arunang.wargames.Unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CavalryUnitTest {

    CavalryUnit attacker = new CavalryUnit("attacker", 20);
    CavalryUnit defender = new CavalryUnit("defender", 15);

    @Test
    @DisplayName("Attacking once")
    void testAttack() {
        attacker.attack(defender);

        assertEquals(20, attacker.getHealthPoints());
        assertEquals(2, defender.getHealthPoints());

    }

    @Test
    @DisplayName("Checking attack bonus after attacking once")
    void testAttackBonus() {
        int baseAttackBonus = CavalryUnit.BASE_ATTACK_BONUS;
        int attackBonus = CavalryUnit.FIRST_ATTACK_BONUS;

        attacker.attack(defender);

        assertEquals(baseAttackBonus, attacker.getAttackBonus());
        assertEquals(attackBonus, defender.getAttackBonus());
    }

    @Test
    @DisplayName("Test attackbonus after attacking twice")
    void testAttackBonusTwice() {
        int baseAttackBonus = CavalryUnit.BASE_ATTACK_BONUS;
        int attackBonus = CavalryUnit.FIRST_ATTACK_BONUS;

        attacker.attack(defender);
        attacker.attack(defender);

        assertEquals(baseAttackBonus, attacker.getAttackBonus());
        assertEquals(attackBonus, defender.getAttackBonus());
    }

    @Test
    @DisplayName("Death of CavalryUnit (attacking twice)")
    void testDeath() {
        attacker.attack(defender);

        assertEquals(20, attacker.getHealthPoints());
        assertEquals(2, defender.getHealthPoints());

        attacker.attack(defender);

        //Unit should be dead
        assertEquals(20, attacker.getHealthPoints());
        assertEquals(-7, defender.getHealthPoints());

        assertTrue(defender.isDead());
        assertFalse(attacker.isDead());
    }

    @Test
    @DisplayName("Test all fields on copying are equal when unit has attacked")
    void testCopy() {
        attacker.attack(defender);
        CavalryUnit copy = attacker.copy();
        assertEquals(attacker, copy);
    }

}
