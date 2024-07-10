/**
 * @package lib.export
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-25
 *
 * Agge Plugins export of EthansApi and PiggyAPI. Sometimes unchanged, sometimes 
 * heavily changed. Thank you for the source of code. The diffs represent my 
 * contributions.
 */

package com.aggeplugins.lib.export;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.EquipmentItemWidget;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public enum EquipmentSlotEx {
    HEAD(0), CAPE(1), NECKLACE(2), MAIN_HAND(3),
    TORSO(4), OFF_HAND(5), AMMO(13), LEGS(7),
    HANDS(9), FEET(10), RING(12);

    private final int idx;

    EquipmentSlotEx(int idx) {
        this.idx = idx;
    }

    public int getIdx()
    {
        return this.idx;
    }

    /**
     * Iterator for EquipmentSlotEx enum.
     */
    public static class EquipmentSlotExIterator implements 
                        Iterator<EquipmentSlotEx> {
        private int currIdx = 0;
        private final List<EquipmentSlotEx> slots;

        public EquipmentSlotExIterator()
        {
            this.slots = new ArrayList<>(List.of(EquipmentSlotEx.values()));
        }

        @Override
        public boolean hasNext()
        {
            return currIdx < slots.size();
        }

        @Override
        public EquipmentSlotEx next() 
        {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return slots.get(currIdx++);
        }

        @Override
        public void remove() 
        {
            if (currIdx <= 0) {
                throw new IllegalStateException();
            }
            slots.remove(--currIdx);
        }

        public void reset()
        {
            currIdx = 0;
        }

        public boolean isEmpty() 
        {
            return slots.isEmpty();
        }

        public int size()
        {
            return slots.size();
        }
    }

    // Method to get a mutable list of EquipmentSlotEx values.
    public static List<EquipmentSlotEx> getMutableSlotList()
    {
        List<EquipmentSlotEx> slots = new ArrayList<>();
        for (EquipmentSlotEx slot : EquipmentSlotEx.values()) {
            slots.add(slot);
        }
        return slots;
    }
}
