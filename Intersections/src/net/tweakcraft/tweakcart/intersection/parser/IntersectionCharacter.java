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

package net.tweakcraft.tweakcart.intersection.parser;

import net.tweakcraft.tweakcart.model.Direction;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public enum IntersectionCharacter {
    FULL_CART('f'),
    EMPTY_CART('&'),
    ANY_LOAD('@'),

    MINECART('m'),
    STORAGE_CART('c'),
    POWERED_CART('p'),
    ANY_CART('a'),

    DIRECTION_NORTH('n'),
    DIRECTION_SOUTH('s'),
    DIRECTION_EAST('e'),
    DIRECTION_WEST('w'),
    ANY_DIRECTION('*'),

    LEFT_DIRECTION_DELIMITER('['),
    RIGHT_DIRECTION_DELIMITER(']'),
    CART_DELIMITER(';'),
    STATEMENT_DELIMITER(':'),
    REMAINDER_DELIMITER('!');

    public static IntersectionCharacter getIntersectionCharacter(char character) {
        //line.toLowerCase() should have been used earlier
        switch (character) {
            case '#':
                return FULL_CART;
            case '&':
                return EMPTY_CART;
            case '@':
                return ANY_LOAD;
            case 'm':
                return MINECART;
            case 'c':
                return STORAGE_CART;
            case 'p':
                return POWERED_CART;
            case 'a':
                return ANY_CART;
            case '[':
                return LEFT_DIRECTION_DELIMITER;
            case ']':
                return RIGHT_DIRECTION_DELIMITER;
            case ';':
                return CART_DELIMITER;
            case ':':
                return STATEMENT_DELIMITER;
            case '!':
                return REMAINDER_DELIMITER;
            case 'n':
                return DIRECTION_NORTH;
            case 's':
                return DIRECTION_SOUTH;
            case 'e':
                return DIRECTION_EAST;
            case 'w':
                return DIRECTION_WEST;
            case '*':
                return ANY_DIRECTION;
            default:
                return null;
        }
    }

    private char character;

    private IntersectionCharacter(char c) {
        character = c;
    }

    public char getCharacter() {
        return character;
    }

    public static Direction parseDirection(char character){
        switch (character){
            case 'n':
                return Direction.NORTH;
            case 's':
                return Direction.SOUTH;
            case 'e':
                return Direction.EAST;
            case 'w':
                return Direction.WEST;
            default:
                return null;
        }
    }

    public static Direction parseDirection(IntersectionCharacter character){
        switch(character){
            case DIRECTION_NORTH:
                return Direction.NORTH;
            case DIRECTION_SOUTH:
                return Direction.SOUTH;
            case DIRECTION_EAST:
                return Direction.EAST;
            case DIRECTION_WEST:
                return Direction.WEST;
            default:
                return null;
        }
    }
}