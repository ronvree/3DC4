package client.communication;

import org.json.simple.JSONObject;

public abstract class Action extends Message {

    /** JSON representation of this action */
    protected final JSONObject json;

    /** Create am action with the required parameters.
     *  Build JSON representation at creation */
    protected Action(String action) {
        this.json = new JSONObject();
        this.json.put("action", action);
    }

    /** Get the JSON representation of this action */
    @Override
    public final String toJSON() {
        return json.toJSONString();
    }

}
