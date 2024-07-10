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

import com.aggeplugins.lib.export.*;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.EquipmentItemWidget;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.Iterator;

@Slf4j
public class EquipmentUtilEx {
    public static Optional<EquipmentItemWidget> getItemInSlot(
                                                    EquipmentSlotEx slot) 
    {
        //List<EquipmentItemWidget> l = Equipment.search().result();
        //log.info("Number of equipped items: " + l.size());

        return Equipment.search().filter(item -> {
            //log.info("Item match found!");
            EquipmentItemWidget iw = (EquipmentItemWidget) item;
            //log.info("wIdx: " + iw.getEquipmentIndex() + "; sIdx: " + slot.getIdx()); 
            return iw.getEquipmentIndex() == slot.getIdx();
        }).first();
    }

    public static boolean hasItem(String name) 
    {
        return Equipment.search().nameContainsNoCase(name).first().isPresent();
    }

    public static boolean hasAnyItems(String... names) {
        for (String name : names) {
            if (hasItem(name)) {
                return true;
            }
        }

        return false;
    }

    @Deprecated
    public static boolean hasItems(String... names) {
        for (String name : names) {
            if (!hasItem(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItems(int... ids) {
        for (int id : ids) {
            if (!hasItem(id)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItems(List<Integer> ids) {
        for (int id : ids) {
            if (!hasItem(id)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItem(int id) {
        return Equipment.search().withId(id).first().isPresent();
    }
}
