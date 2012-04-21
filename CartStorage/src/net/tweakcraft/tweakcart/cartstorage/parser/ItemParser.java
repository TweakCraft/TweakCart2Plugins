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

import net.tweakcraft.tweakcart.TweakCart;
import net.tweakcraft.tweakcart.api.model.parser.DirectionParser;
import net.tweakcraft.tweakcart.cartstorage.model.Action;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.model.IntMap;
import net.tweakcraft.tweakcart.util.StringUtil;
import org.bukkit.block.Sign;

import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class ItemParser {

    private static IntMap[] maps = new IntMap[]{new IntMap(), new IntMap()};

    //TODO: rename this. It's supposed to make the int[] allocation easier/more maintainable.
    private enum DataType {
        START_ID,
        START_DATA,
        END_ID,
        END_DATA,
        AMOUNT;

        public static final int size = values().length;
    }

    //TODO: optimize, don't use .toLowerCase() too often...
    public static IntMap parseLine(String line, IntMap map) throws ParseException {
        //The String 'line' shouldn't contain any directions anymore.
        line += ":";
        boolean isNegate = line.charAt(0) == ItemCharacter.NEGATE.getCharacter();
        if (isNegate) {
            line = line.substring(1);
        }
        String[] numbers = new String[]{"", "", "", "", ""};
        int[] ints = new int[DataType.size];
        ItemCharacter lastChar = null;
        boolean range = false;
        for (char character : line.toCharArray()) {
            ItemCharacter newChar = ItemCharacter.getItemParseCharacter(character);
            switch (newChar) {
                case DIGIT:
                    if (!range && lastChar == null) {
                        numbers[DataType.START_ID.ordinal()] += character;
                    } else if (!range && lastChar == ItemCharacter.DATA_VALUE) {
                        numbers[DataType.START_DATA.ordinal()] += character;
                    } else if (range && lastChar == null) {
                        numbers[DataType.END_ID.ordinal()] += character;
                    } else if (range && lastChar == ItemCharacter.DATA_VALUE) {
                        numbers[DataType.END_DATA.ordinal()] += character;
                    } else if (lastChar == ItemCharacter.AMOUNT) {
                        numbers[DataType.AMOUNT.ordinal()] += character;
                    }
                    break;
                case RANGE:
                    if (range) {
                        throw new ParseException(line);
                    } else {
                        range = true;
                        lastChar = null;
                    }
                    break;
                case DATA_VALUE:
                case AMOUNT:
                    lastChar = newChar;
                    break;
                case DELIMITER:
                    try {
                        for (int index = 0; index < numbers.length; index++) {
                            if (numbers[index].length() == 0)
                                numbers[index] = "0";
                        }
                        ints[DataType.START_ID.ordinal()] = Integer.parseInt(numbers[DataType.START_ID.ordinal()]);
                        ints[DataType.START_DATA.ordinal()] = Integer.parseInt(numbers[DataType.START_DATA.ordinal()]);
                        if (range) {
                            ints[DataType.END_ID.ordinal()] = Integer.parseInt(numbers[DataType.END_ID.ordinal()]);
                            ints[DataType.END_DATA.ordinal()] = Integer.parseInt(numbers[DataType.END_DATA.ordinal()]);
                        }
                        ints[DataType.AMOUNT.ordinal()] = Integer.parseInt(numbers[DataType.AMOUNT.ordinal()]);
                    } catch (NumberFormatException e) {
                        if (TweakCart.DEBUG) {
                            TweakCart.log("[Debug] NFE thrown: " + e.getMessage(), Level.WARNING);
                        }
                    }

                    if (isNegate) {
                        ints[DataType.AMOUNT.ordinal()] = 0;
                    } else if (ints[DataType.AMOUNT.ordinal()] < 1) {
                        ints[DataType.AMOUNT.ordinal()] = Integer.MAX_VALUE;
                    }

                    if (range) {
                        map.setRange(
                                ints[DataType.START_ID.ordinal()],
                                (byte) ints[DataType.START_DATA.ordinal()],
                                ints[DataType.END_ID.ordinal()],
                                (byte) ints[DataType.END_DATA.ordinal()],
                                ints[DataType.AMOUNT.ordinal()]
                        );
                    } else {
                        map.setInt(
                                ints[DataType.START_ID.ordinal()],
                                (byte) ints[DataType.START_DATA.ordinal()],
                                ints[DataType.AMOUNT.ordinal()]
                        );
                    }

                    ints = new int[DataType.size];
                    numbers = new String[]{"", "", "", "", ""};
                    lastChar = null;
                    range = false;
                    break;
                default:
                    //Syntax error
                    throw new ParseException(line);
            }
        }
        return map;
    }

    public static IntMap[] parseSign(Sign sign, Direction direction) throws ParseException {
        Action oldAction = Action.NULL;
        recycle();
        for (String line : sign.getLines()) {
            Direction requiredDirection = DirectionParser.parseDirection(line);
            if (requiredDirection == Direction.SELF || requiredDirection == direction) {
                if (requiredDirection != Direction.SELF)
                    line = DirectionParser.removeDirection(line);
                Action newAction = parseAction(StringUtil.stripBrackets(line));
                switch (newAction) {
                    case ALL:
                        if (oldAction != Action.NULL) {
                            maps[oldAction.ordinal()].fillAll();
                        }
                        break;
                    case ITEM:
                        if (oldAction != Action.NULL) {
                            maps[oldAction.ordinal()] = parseLine(line, maps[oldAction.ordinal()]);
                        }
                        break;
                    case COLLECT:
                        oldAction = newAction;
                        break;
                    case DEPOSIT:
                        oldAction = newAction;
                        break;
                    default:
                        //Syntax error, skip line
                        break;
                }
            }
        }
        return maps;
    }

    public static Action parseAction(String line) {
        line = line.toLowerCase();
        if (line == null || line.equals("")) {
            return Action.NULL;
        }

        char firstChar = line.charAt(0);

        if (line.length() > 0) {
            if (Character.isDigit(firstChar) || firstChar == '!') {
                return Action.ITEM;
            } else if (line.charAt(1) == '+') {
                switch (firstChar) {
                    case 'n':
                    case 's':
                    case 'w':
                    case 'e':
                        if (line.length() > 2) {
                            if (line.charAt(2) == 'a' && line.equals(Character.toString(line.charAt(0)) + "+all items")) {
                                return Action.ALL;
                            } else {
                                return Action.ITEM;
                            }
                        } else {
                            return Action.NULL;
                        }
                    default:
                        return Action.NULL;
                }
            } else {
                switch (firstChar) {
                    case 'c':
                        if (line.equals("collect items")) {
                            return Action.COLLECT;
                        }
                        return Action.NULL;
                    case 'd':
                        if (line.equals("deposit items")) {
                            return Action.DEPOSIT;
                        }
                        return Action.NULL;
                    case 'a':
                        if (line.equals("all items")) {
                            return Action.ALL;
                        }
                        return Action.NULL;
                    default:
                        return Action.NULL;
                }
            }
        } else {
            return Action.NULL;
        }
    }

    private static void recycle() {
        maps[0].clear();
        maps[1].clear();
    }
}