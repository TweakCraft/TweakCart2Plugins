/*
 * Copyright (c) 2012.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package net.tweakcraft.tweakcart.cartstorage.parser;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public enum ItemCharacter {
    FLIP("!"),
    DELIMITER(":"),
    AMOUNT("@"),
    RANGE("-"),
    DATA_VALUE(";"),
    ID("");

    private String character;
    private int priority;
    private static HashMap<String, ItemCharacter> itemParserCharMap = new HashMap<String, ItemCharacter>();
    private static ItemCharacter[] itemParserPriorityArray;

    static {
        itemParserPriorityArray = new ItemCharacter[values().length];
        for (ItemCharacter aChar : values()) {
            itemParserCharMap.put(aChar.getCharacter(), aChar);
            itemParserPriorityArray[aChar.ordinal()] = aChar;
        }
    }

    private ItemCharacter(String c) {
        character = c;
        priority = ordinal();
    }

    public String getCharacter() {
        return character;
    }

    public int getPriority() {
        return priority;
    }

    public static ItemCharacter getItemParseCharacter(int prior) {
        if (prior >= itemParserPriorityArray.length) {
            return null;
        } else {
            return itemParserPriorityArray[prior];
        }
    }

    public static ItemCharacter getItemParseCharacter(String character) {
        return itemParserCharMap.get(character);
    }
}
