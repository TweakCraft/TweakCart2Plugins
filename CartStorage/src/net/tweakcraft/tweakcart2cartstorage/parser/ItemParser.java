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

package net.tweakcraft.tweakcart2cartstorage.parser;

import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.model.IntMap;
import net.tweakcraft.tweakcart.util.StringUtil;
import net.tweakcraft.tweakcart2cartstorage.model.Action;
import org.bukkit.block.Sign;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class ItemParser {
    public static IntMap parseLine(String line, Direction d, IntMap map) {
        //TODO: Can't the ItemCharacters.getCharacter() function return type 'char'? AKA is it possible to split on char?
        //TODO: First chop of direction, then run through the rest of the parser
        boolean remove = line.charAt(0) == ItemCharacter.FLIP.getCharacter().charAt(0);
        if (remove) {
            line = line.substring(1);
        }
        String[] apples = line.split(ItemCharacter.DELIMITER.getCharacter());
        for (String apple : apples) {
            int amount = Integer.MAX_VALUE;
            byte data = 0;
            int id = 0;
            //Dit wordt als een los deel gezien. Geen check op .lenght omdat 't niet uitmaakt of er 1 of 900 zijn.
            //Omdat er geen index gebruikt hoeft te worden kan hier gewoon een for-each loop gebruikt worden.
            String[] mango = apple.split(ItemCharacter.AMOUNT.getCharacter());
            if (mango.length == 2) {
                //So there is an amount, nice!
                amount = Integer.parseInt(mango[1]);
                //TODO: check if amount < 1 etc.
            } else if (mango.length > 2) {
                return null;
            }
            mango = mango[0].split(ItemCharacter.RANGE.getCharacter());
            if (mango.length == 1) {
                //So, now we only need to check for a data-value :D
                mango = mango[0].split(ItemCharacter.DATA_VALUE.getCharacter());
                id = Integer.parseInt(mango[0]);
                if (mango.length == 2) {
                    data = Byte.parseByte(mango[1]);
                } else if (mango.length > 2) {
                    return null;
                }
                map.setInt(id, data, amount);
            } else if (mango.length == 2) {
                //So we have a range. Check range for data-values and put values into integers and bytes
                String[] banana = mango[0].split(ItemCharacter.DATA_VALUE.getCharacter());
                int sId = Integer.parseInt(banana[0]);
                byte sData = 0;
                if (banana.length == 2) {
                    sData = Byte.parseByte(banana[1]);
                } else if (banana.length > 2) {
                    return null;
                }
                banana = mango[1].split(ItemCharacter.DATA_VALUE.getCharacter());
                int eId = Integer.parseInt(banana[0]);
                byte eData = 0;
                if (banana.length == 2) {
                    eData = Byte.parseByte(banana[1]);
                } else if (banana.length > 2) {
                    return null;
                }
                map.setRange(sId, sData, eId, eData, amount);
            } else {
                return null;
            }
        }
        return map;
    }

    public static IntMap[] parseSign(Sign s, /* TODO: is this needed? StorageMinecart c,*/ Direction d) {
        IntMap[] maps = new IntMap[2];
        //TODO: better way to initialize this array?
        maps[0] = new IntMap();
        maps[1] = new IntMap();
        Action toDo = null;
        for (String action : s.getLines()) {
            action = StringUtil.stripBrackets(action.toLowerCase());
            switch (parseAction(action)) {
                case COLLECT:
                    toDo = Action.COLLECT;
                    break;
                case DEPOSIT:
                    toDo = Action.DEPOSIT;
                    break;
                case ALL:
                    if (toDo != null) {
                        maps[toDo.ordinal()].fillAll();
                    }
                    break;
                case ITEM:
                    //check for null;
                    if (toDo != null) {
                        maps[toDo.ordinal()] = parseLine(action, d, maps[toDo.ordinal()]);
                        if (maps[toDo.ordinal()] == null) {
                            //Sytnax error on sign, break form loop because continuing won't have anny effect and would only cause NPE's
                            return null;
                        }
                    }
                    break;
                default:
                    //Either the syntax is fucked up or some kind of comment. Don't parse but do continue.
                    break;
            }
        }
        return maps;
    }

    public static Action parseAction(String line) {
        if (line == null || line.equals("")) {
            return Action.NULL;
        }

        char firstChar = line.charAt(0);

        if (line.length() > 0) {
            if (Character.isDigit(firstChar) || firstChar == '!') {
                return Action.ITEM;
            } else if (line.charAt(1) == '+'/*TODO: insert direction parser check*/) {
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