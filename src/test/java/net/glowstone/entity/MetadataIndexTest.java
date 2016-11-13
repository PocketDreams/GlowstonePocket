package net.glowstone.entity;

import net.glowstone.entity.meta.MetadataIndex;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.fail;

/**
 * Tests for {@link MetadataIndex}.
 */
public class MetadataIndexTest {

    /**
     * Tests that more specific metadata always appears later.
     */
    @Test
    public void testOrdering() {
        HashMap<Class<?>, MetadataIndex> seen = new HashMap<>();

        for (MetadataIndex index : MetadataIndex.values()) {
            Class<?> clazz = index.getAppliesTo();

            seen.entrySet().stream().filter(entry -> clazz != entry.getKey() && clazz.isAssignableFrom(entry.getKey())).forEach(entry -> fail("Index " + index + "(" + clazz.getSimpleName() + ") follows index " + entry.getValue() + "(" + entry.getKey().getSimpleName() + ") which it parents"));

            if (!seen.containsKey(clazz)) {
                seen.put(clazz, index);
            }
        }
    }

    /**
     * Tests that no two MetadataIndex entries can overlap on a single
     * entity. Will not catch failure for entities without any metadata
     * keys defined.
     */
    @Test
    public void testNoOverlap() {
        HashMap<Class<?>, HashMap<Integer, MetadataIndex>> map = new HashMap<>();

        for (MetadataIndex index : MetadataIndex.values()) {
            Class<?> clazz = index.getAppliesTo();

            if (index == MetadataIndex.ARMORSTAND_LEFT_LEG_POSITION) { //TODO 1.9 - index == MetadataIndex.PLAYER_SKIN_FLAGS || has been removed
                // this index is permitted to override others
                continue;
            }

            // check for duplication
            // check that class is a parent
            // look for matching index
            map.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(clazz)).filter(entry -> entry.getValue().containsKey(index.getIndex())).forEach(entry -> {
                if (!index.name().startsWith("COMPATIBLE_") && !entry.getValue().get(index.getIndex()).name().startsWith("COMPATIBLE_")) {
                    fail("Index " + index + "(" + clazz.getSimpleName() + ") conflicts with " + entry.getValue().get(index.getIndex()) + "(" + entry.getKey().getSimpleName() + ")");
                }
            });

            // insert this index
            HashMap<Integer, MetadataIndex> classMap = map.get(index.getAppliesTo());
            if (classMap == null) {
                classMap = new HashMap<>();
                map.put(index.getAppliesTo(), classMap);
            }
            classMap.put(index.getIndex(), index);
        }
    }

}
