package net.tweakcraft.tweakcart.cartrouting.model;

/**
 * Created by nick on 25/03/2015.
 */
public enum ActionType {
    SETDEST("set destination"),
    GETDEST("get destination");

    private String text;

    private ActionType(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public static ActionType getActionType(String text){
        for(ActionType type : values()){
            if(type.getText().equalsIgnoreCase(text))
                return type;
        }

        return null;
    }
}
