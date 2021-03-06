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

import java.util.Arrays;

import net.tweakcraft.tweakcart.TweakCart;
import net.tweakcraft.tweakcart.api.model.parser.DirectionParser;
import net.tweakcraft.tweakcart.cartstorage.CartStorage;
import net.tweakcraft.tweakcart.cartstorage.model.Action;
import net.tweakcraft.tweakcart.cartstorage.parser.ItemCharacter;
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
    //TODO: rename this. It's supposed to make the int[] allocation easier/more maintainable.
    private enum DataType {
        START_ID,
        START_DATA,
        END_ID,
        END_DATA,
        AMOUNT;
        public static final int size = values().length;
    }

    /*
      * The grammar of this function is: RawLine = !TCLine || TCLine TCLine =
      * ItemRangeAmount || ItemRangeAmount:ItemRangeAmount ItemRangeAmount =
      * ItemRange@Amount || ItemRange ItemRange = Item-Item || Item Item =
      * ID;Data || ID ID = int Data = int Amount = int
      */
    public static IntMap parseLineSplit(String line, Direction d, IntMap map) {
        boolean isNegate = false;
        if (line.length() > 0) {
            if (line.charAt(0) == ItemCharacter.NEGATE.getCharacter()) {
                isNegate = true;
                line = line.substring(1);
            }
        }

        String[] TCexpSplit = line.split("" + ItemCharacter.DELIMITER.getCharacter());

        for (String exp : TCexpSplit) {
            int amount = Integer.MAX_VALUE;
            boolean range = false;
            int[] itemFrom = null;
            int[] itemTo = null;

            String[] itemAmount = exp.split("" + ItemCharacter.AMOUNT.getCharacter());
            if (itemAmount.length == 2) {
                try {
                    amount = Integer.parseInt(itemAmount[1]);
                } catch (NumberFormatException ex) {
                    //We forgive the user, simply ignoring amount here
                }
            } else if (itemAmount.length != 1) {
                continue;
            }

            String[] itemRange = itemAmount[0].split("" + ItemCharacter.RANGE.getCharacter());
            if (itemRange.length == 1) {
                itemFrom = checkIDData(itemRange[0]);
            } else if (itemRange.length == 2) {
                itemFrom = checkIDData(itemRange[0]);
                itemTo = checkIDData(itemRange[1]);
                range = true;
            } else {
                //User has made a range of ranges, thats not possible :(
                continue;
            }

            if (range && itemFrom != null && itemTo != null) {
                //Something with ranges
                if (isNegate) {
                    amount = 0;
                }
                map.setRange(itemFrom[0], itemFrom[1], itemTo[0], itemTo[1], amount);

            } else if (itemFrom != null) {
                //Something without ranges
                if (isNegate) {
                    amount = 0;
                }
                map.setInt(itemFrom[0], itemFrom[1], amount);
            }


        }
        return map;
    }

    private static int[] checkIDData(String line) {
        int[] result = new int[2];
        String[] linesplit = line.split("" + ItemCharacter.DATA_VALUE.getCharacter());
        if (linesplit.length == 2) {
            try {
                result[0] = Integer.parseInt(linesplit[0]);
                result[1] = Integer.parseInt(linesplit[1]);
            } catch (NumberFormatException e) {
            }
        } else if (linesplit.length == 1) {
            try {
                result[0] = Integer.parseInt(linesplit[0]);
                result[1] = -1;
            } catch (NumberFormatException e) {
            }
        }
        return result;

    }

    //TODO: optimize, don't use .toLowerCase() too often...
    public static IntMap parseLine( String line, Direction d, IntMap map ) {
        //The String 'line' shouldn't contain any directions anymore.
        line += ":";
        /* if negate char is @ start of the line we need to negate the whole line, if not negate will only do a single item */
        boolean isNegate = false;
        boolean isNegateAtStart = line.charAt(0) == ItemCharacter.NEGATE.getCharacter();    
        if (isNegateAtStart) {
            line = line.substring(1);
            isNegate = true;
        }
        String[] numbers = new String[] { "", "", "", "", "" };
        int[] ints = new int[DataType.size];
        ItemCharacter lastChar = null;
        boolean range = false;
        for (char character : line.toCharArray()) {
            ItemCharacter newChar = ItemCharacter.getItemParseCharacter(character);
            if (newChar == null) return null;

            switch (newChar) {
                case NEGATE:
                    isNegate = !isNegate;
                    break;
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
                    if (range)    return null;

                    range = true;
                    lastChar = null;
                    break;
                case DATA_VALUE:
                case AMOUNT:
                    lastChar = newChar;
                    break;
                case DELIMITER:
                    try {
                        for (int index = 0; index < numbers.length; index++) {
                            if (numbers[index].length() == 0) {
                                numbers[index] = "-1";
                            }
                        }
                        ints[DataType.START_ID.ordinal()] = Integer.parseInt(numbers[DataType.START_ID.ordinal()]);
                        ints[DataType.START_DATA.ordinal()] = Integer.parseInt(numbers[DataType.START_DATA.ordinal()]);
                        if (range) {
                            ints[DataType.END_ID.ordinal()] = Integer.parseInt(numbers[DataType.END_ID.ordinal()]);
                            ints[DataType.END_DATA.ordinal()] = Integer.parseInt(numbers[DataType.END_DATA.ordinal()]);
                        }
                        ints[DataType.AMOUNT.ordinal()] = Integer.parseInt(numbers[DataType.AMOUNT.ordinal()]);
                    } catch (NumberFormatException e) {
                        if (CartStorage.DEBUG) {
                            TweakCart.log("NFE thrown: " + e.getMessage(), Level.WARNING);
                        }
                    }

                    if (isNegate) {
                        ints[DataType.AMOUNT.ordinal()] = 0;
                    } else if (ints[DataType.AMOUNT.ordinal()] < 1)    {
                        ints[DataType.AMOUNT.ordinal()] = Integer.MAX_VALUE;
                    }



                    if (range) {
                        /*
                         * Faulty sign fix
                         */
                        if (ints[DataType.START_ID.ordinal()] == -1 || ints[DataType.END_ID.ordinal()] == -1) {
                            continue;
                        }
                        boolean succeed = map.setRange(
                                ints[DataType.START_ID.ordinal()],
                                ints[DataType.START_DATA.ordinal()],
                                ints[DataType.END_ID.ordinal()],
                                ints[DataType.END_DATA.ordinal()],
                                ints[DataType.AMOUNT.ordinal()]
                        );
                    } else {
                        /*
                         * Faulty sign fix
                         */
                        if (ints[DataType.START_ID.ordinal()] == -1) {
                            continue;
                        }
                        map.setInt(
                                ints[DataType.START_ID.ordinal()],
                                ints[DataType.START_DATA.ordinal()],
                                ints[DataType.AMOUNT.ordinal()]
                        );
                    }
                    /* Only reset negate if negate wasnt detected @ start*/
                    if(!isNegateAtStart) {
                        isNegate = false;
                    }

                    ints = new int[DataType.size];
                    numbers = new String[] { "", "", "", "", ""    };
                    lastChar = null;
                    range = false;
                    break;
                default:
                    //Syntax error
                    return null;
            }
        }
        return map;
    }

    public static IntMap[] parseSign(Sign sign, Direction direction) {
        Action oldAction = Action.NULL;
        IntMap[] maps = new IntMap[] { new IntMap(), new IntMap() };
        for (String line : sign.getLines()) {
            Direction requiredDirection = DirectionParser.parseDirection(line);
            if (requiredDirection == Direction.SELF || requiredDirection == direction) {
                line = DirectionParser.removeDirection(line);
                Action newAction = parseAction(StringUtil.stripBrackets(line));
                switch (newAction) {
                    case ALL:
                        if (oldAction != Action.NULL) {
                            maps[oldAction.ordinal()].fillAll();
                            if (maps[oldAction.ordinal()] == null) {
                                //Syntax error, stop with map-building
                                System.err.println("syntax error 1");
                                return null;
                            }
                        }
                        break;
                    case ITEM:
                        if (oldAction != Action.NULL) {
                            maps[oldAction.ordinal()] = parseLine(line, direction, maps[oldAction.ordinal()]);
                            if (maps[oldAction.ordinal()] == null) {
                                //Syntax error, stop with map-building
                                System.err.println("syntax error 2");
                                return null;
                            }
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
}