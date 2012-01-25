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

package net.tweakcraft.tweakcart2intersections.syntax;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public enum IntersectionCharacter {
    FULL_CART("#"),
    EMPTY_CART("&"),

    MINECART("m"),
    STORAGE_CART("c"),
    POWERED_CART("p"),
    ANY_CART("a"),

    DIRECTION_DELIMITER(";"),
    CART_DELIMITER(":"),
    REMAINDER_DELIMITER("!");

    private static HashMap<String, IntersectionCharacter> intersectionCharacterMap = new HashMap<String, IntersectionCharacter>();

    static {
        for (IntersectionCharacter ic : IntersectionCharacter.values()) {
            intersectionCharacterMap.put(ic.getCharacter(), ic);
        }
    }

    public static IntersectionCharacter getIntersectionCharacter(String character) {
        return intersectionCharacterMap.get(character);
    }

    private String character;

    private IntersectionCharacter(String c) {
        character = c;
    }

    public String getCharacter() {
        return character;
    }
}