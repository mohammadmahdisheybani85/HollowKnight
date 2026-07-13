package io.github.some_example_name.controller.achievement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EnemyKillTracker {
    private final Set<String> killedEnemyNames = new HashSet<>();

    public void markKilled(String enemyName) {
        if (enemyName == null || enemyName.isBlank()) {
            return;
        }

        killedEnemyNames.add(enemyName);
    }

    public void loadKilledEnemies(Collection<String> enemyNames) {
        reset();

        if (enemyNames == null) {
            return;
        }

        for (String enemyName : enemyNames) {
            markKilled(enemyName);
        }
    }

    public void reset() {
        killedEnemyNames.clear();
    }

    public boolean hasKilled(String enemyName) {
        return killedEnemyNames.contains(enemyName);
    }

    public int getKillCount() {
        return killedEnemyNames.size();
    }

    public Set<String> getKilledEnemyNames() {
        return Collections.unmodifiableSet(killedEnemyNames);
    }

    public boolean hasKilledAllRequiredEnemies() {
        return hasKilledBasicGroundEnemy()
            && hasKilledFlyingEnemy()
            && hasKilledHornheadHusk()
            && hasKilledGuardianCrystal()
            && hasKilledFalseKnight();
    }

    private boolean hasKilledBasicGroundEnemy() {
        return killedEnemyNames.contains("Crawlid")
            || killedEnemyNames.contains("Mosscreep")
            || killedEnemyNames.contains("Greenpath Crawler")
            || killedEnemyNames.contains("Leaf Stalker");
    }

    private boolean hasKilledFlyingEnemy() {
        return killedEnemyNames.contains("Mosquito")
            || killedEnemyNames.contains("Mossfly");
    }

    private boolean hasKilledHornheadHusk() {
        return killedEnemyNames.contains("Hornhead Husk");
    }

    private boolean hasKilledGuardianCrystal() {
        return killedEnemyNames.contains("Guardian Crystal");
    }

    private boolean hasKilledFalseKnight() {
        return killedEnemyNames.contains("False Knight");
    }
}
