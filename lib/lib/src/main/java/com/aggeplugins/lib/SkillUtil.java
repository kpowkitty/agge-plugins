//package com.aggeplugins.lib;
//
//import com.aggeplugins.lib.*;
//
//import com.example.PacketUtils.*;
//import com.example.Packets.*;
//
//import net.runelite.api.widgets.Widget;
//import net.runelite.api.Skill;
//
//import org.apache.commons.lang3.tuple.Pair;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Map;
//import java.util.HashMap;
//
//@Slf4j
//public class SkillUtil {
//    public static final Map<Skill, WidgetInfoExtended> map = new HashMap<>();
//    static {
//        map.put(AttackStyle.ACCURATE, WidgetInfoExtended.COMBAT_STYLE_ONE);
//        map.put(AttackStyle.AGGRESSIVE, WidgetInfoExtended.COMBAT_STYLE_TWO);
//        map.put(AttackStyle.DEFENSIVE, WidgetInfoExtended.COMBAT_STYLE_THREE);
//        map.put(AttackStyle.CONTROLLED, WidgetInfoExtended.COMBAT_STYLE_FOUR);
//        map.put(AttackStyle.CASTING, WidgetInfoExtended.COMBAT_SPELLS);
//        map.put(AttackStyle.DEFENSIVE_CASTING, 
//                WidgetInfoExtended.COMBAT_DEFENSIVE_SPELL_BOX);
//        map.put(AttackStyle.OTHER, 
//                WidgetInfoExtended.COMBAT_DEFENSIVE_SPELL_ICON);
//        map.put(AttackStyle.OTHER, 
//                WidgetInfoExtended.COMBAT_DEFENSIVE_SPELL_SHIELD);
//        map.put(AttackStyle.OTHER, 
//                WidgetInfoExtended.COMBAT_DEFENSIVE_SPELL_TEXT);
//        map.put(AttackStyle.OTHER, WidgetInfoExtended.COMBAT_SPELL_BOX);
//        map.put(AttackStyle.OTHER, WidgetInfoExtended.COMBAT_SPELL_ICON);
//        map.put(AttackStyle.OTHER, WidgetInfoExtended.COMBAT_SPELL_TEXT);
//        map.put(AttackStyle.OTHER, WidgetInfoExtended.COMBAT_AUTO_RETALIATE);
//    };
//
//    public static int getWidgetId(AttackStyle style)
//    {
//        log.info("AttackStyle Widget ID: " + map.get(style).getPackedId());
//        return map.getOrDefault(style, WidgetInfoExtended.COMBAT_STYLE_ONE)
//                  .getPackedId();
//    }
//
//    // xxx decide if it's already active
//
//    public static boolean changeAttackStyle(AttackStyle style)
//    {
//        if (style == null)
//            return false;
//
//        MousePackets.queueClickPacket();
//        WidgetPackets.queueWidgetActionPacket(1, getWidgetId(style), -1, -1);
//        return true;
//    }
//}
//
/* @note May be useful, for finding AttackStyle by weapon type: */

//private AttackStyle[] getWeaponTypeStyles(int weaponType)
//{
//	// from script4525
//	int weaponStyleEnum = client.getEnum(EnumID.WEAPON_STYLES).getIntValue(weaponType);
//	int[] weaponStyleStructs = client.getEnum(weaponStyleEnum).getIntVals();

//	AttackStyle[] styles = new AttackStyle[weaponStyleStructs.length];
//	int i = 0;
//	for (int style : weaponStyleStructs)
//	{
//		StructComposition attackStyleStruct = client.getStructComposition(style);
//		String attackStyleName = attackStyleStruct.getStringValue(ParamID.ATTACK_STYLE_NAME);

//		AttackStyle attackStyle = AttackStyle.valueOf(attackStyleName.toUpperCase());
//		if (attackStyle == OTHER)
//		{
//			// "Other" is used for no style
//			++i;
//			continue;
//		}

//		// "Defensive" is used for Defensive and also Defensive casting
//		if (i == 5 && attackStyle == DEFENSIVE)
//		{
//			attackStyle = DEFENSIVE_CASTING;
//		}

//		styles[i++] = attackStyle;
//	}
//	return styles;
//}

//private void updateWidgetsToHide(boolean enabled)
//{
//	AttackStyle[] attackStyles = getWeaponTypeStyles(equippedWeaponTypeVarbit);

//	// Iterate over attack styles
//	for (int i = 0; i < attackStyles.length; i++)
//	{
//		AttackStyle attackStyle = attackStyles[i];
//		if (attackStyle == null)
//		{
//			continue;
//		}

//		boolean warnedSkill = false;
//		for (Skill skill : attackStyle.getSkills())
//		{
//			if (warnedSkills.contains(skill))
//			{
//				warnedSkill = true;
//				break;
//			}
//		}

//		// Remove appropriate combat option
//		switch (i)
//		{
//			case 0:
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_STYLE_ONE, enabled && warnedSkill);
//				break;
//			case 1:
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_STYLE_TWO, enabled && warnedSkill);
//				break;
//			case 2:
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_STYLE_THREE, enabled && warnedSkill);
//				break;
//			case 3:
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_STYLE_FOUR, enabled && warnedSkill);
//				break;
//			case 4:
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_SPELLS, enabled && warnedSkill);
//				break;
//			case 5:
//				// Magic staves defensive casting mode
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_DEFENSIVE_SPELL_BOX, enabled && warnedSkill);
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_DEFENSIVE_SPELL_ICON, enabled && warnedSkill);
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_DEFENSIVE_SPELL_SHIELD, enabled && warnedSkill);
//				widgetsToHide.put(equippedWeaponTypeVarbit, ComponentID.COMBAT_DEFENSIVE_SPELL_TEXT, enabled && warnedSkill);
//				break;
//		}
//	}
//}

//private void updateAttackStyle(int equippedWeaponType, int attackStyleIndex, int castingMode)
//{
//	AttackStyle[] attackStyles = getWeaponTypeStyles(equippedWeaponType);
//	if (attackStyleIndex < attackStyles.length)
//	{
//		// from script4525
//		// Even though the client has 5 attack styles for Staffs, only attack styles 0-4 are used, with an additional
//		// casting mode set for defensive casting
//		if (attackStyleIndex == 4)
//		{
//			attackStyleIndex += castingMode;
//		}

//		attackStyle = attackStyles[attackStyleIndex];
//		if (attackStyle == null)
//		{
//			attackStyle = OTHER;
//		}
//	}
//}
//@Subscribe
//public void onVarbitChanged(VarbitChanged event)
//{
//	if (event.getVarpId() == VarPlayer.ATTACK_STYLE
//		|| event.getVarbitId() == Varbits.EQUIPPED_WEAPON_TYPE
//		|| event.getVarbitId() == Varbits.DEFENSIVE_CASTING_MODE)
//	{
//		final int currentAttackStyleVarbit = client.getVarpValue(VarPlayer.ATTACK_STYLE);
//		final int currentEquippedWeaponTypeVarbit = client.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);
//		final int currentCastingModeVarbit = client.getVarbitValue(Varbits.DEFENSIVE_CASTING_MODE);

//		boolean weaponSwitch = currentEquippedWeaponTypeVarbit != equippedWeaponTypeVarbit;

//		equippedWeaponTypeVarbit = currentEquippedWeaponTypeVarbit;

//		updateAttackStyle(equippedWeaponTypeVarbit, currentAttackStyleVarbit,
//			currentCastingModeVarbit);
//		updateWarning();

//		// this is required because the widgets need to be hidden prior to interface tick, which is soon after this,
//		// and before the client tick event.
//		if (weaponSwitch)
//		{
//			processWidgets();
//		}
//	}
//}

//	overlayManager.add(overlay);

//	clientThread.invoke(() ->
//	{
//		resetWarnings(); // setup warnedSkills

//		if (client.getGameState() == GameState.LOGGED_IN)
//		{
//			int attackStyleVarbit = client.getVarpValue(VarPlayer.ATTACK_STYLE);
//			equippedWeaponTypeVarbit = client.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);
//			int castingModeVarbit = client.getVarbitValue(Varbits.DEFENSIVE_CASTING_MODE);
//			updateAttackStyle(
//				equippedWeaponTypeVarbit,
//				attackStyleVarbit,
//				castingModeVarbit);
//			updateWarning();
//			processWidgets();
//		}
//	});
