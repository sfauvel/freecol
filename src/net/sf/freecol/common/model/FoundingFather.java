
package net.sf.freecol.common.model;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.util.Xml;

/**
 * Represents one FoundingFather to be contained in a Player object.
 * The FoundingFather is able to grant new abilities or bonuses to the
 * player, or to cause certain events.
 */
public class FoundingFather extends FreeColGameObjectType implements Abilities, Modifiers {

    public static final String  COPYRIGHT = "Copyright (C) 2003-2005 The FreeCol Team";
    public static final String  LICENSE = "http://www.gnu.org/licenses/gpl.html";
    public static final String  REVISION = "$Revision$";
    
    /**
     * The probability of this FoundingFather being offered for selection.
     */
    private int[] weight = new int[4];

    /**
     * The type of this FoundingFather. One of the following constants.
     */
    private int type;

    public static final int TRADE = 0,
                            EXPLORATION = 1,
                            MILITARY = 2,
                            POLITICAL = 3,
                            RELIGIOUS = 4,
                            TYPE_COUNT = 5;

    /**
     * Stores the abilities of this Type.
     */
    private HashMap<String, Boolean> abilities = new HashMap<String, Boolean>();

    /**
     * Stores the Modifiers of this Type.
     */
    private HashMap<String, Modifier> modifiers = new HashMap<String, Modifier>();

    /**
     * Stores the Events of this Type.
     */
    private HashMap<String, String> events = new HashMap<String, String>();

    /**
     * Creates a new <code>FoundingFather</code> instance.
     *
     * @param newIndex an <code>int</code> value
     */
    public FoundingFather(int newIndex) {
        setIndex(newIndex);
    }

    /**
     * Return the localized text of this FoundingFather.
     *
     * @return a <code>String</code> value
     */
    public String getText() {
        return Messages.message(getID() + ".text");
    }

    /**
     * Return the localized birth and death dates of this FoundingFather.
     *
     * @return a <code>String</code> value
     */
    public String getBirthAndDeath() {
        return Messages.message(getID() + ".birthAndDeath");
    }

    /**
     * Return the type of this FoundingFather.
     *
     * @return an <code>int</code> value
     */
    public int getType() {
        return type;
    }
    
    /**
     * Return the localized type of this FoundingFather.
     *
     * @return a <code>String</code> value
     */
    public String getTypeAsString() {
        return getTypeAsString(type);
    }

    /**
     * Return the localized type of the given FoundingFather.
     *
     * @param type an <code>int</code> value
     * @return a <code>String</code> value
     */
    public static String getTypeAsString(int type) {
        switch (type) {
            case TRADE: return Messages.message("foundingFather.trade");
            case EXPLORATION: return Messages.message("foundingFather.exploration");
            case MILITARY: return Messages.message("foundingFather.military");
            case POLITICAL: return Messages.message("foundingFather.political");
            case RELIGIOUS: return Messages.message("foundingFather.religious");
        }
        
        return "";
    }

    /**
     * Get the weight of this FoundingFather. This is used to select a
     * random FoundingFather.
     *
     * @param age an <code>int</code> value
     * @return an <code>int</code> value
     */
    public int getWeight(int age) {
        switch(age) {
        case 1:
            return weight[1];
        case 2:
            return weight[2];
        case 3:
        default:
            return weight[3];
        }
    }
    

    /**
     * Returns true if this FoundingFather has the ability with the given ID.
     *
     * @param id a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasAbility(String id) {
        return abilities.containsKey(id) && abilities.get(id);
    }

    /**
     * Returns a copy of this FoundingFather's abilities.
     *
     * @return a <code>Map</code> value
     */
    public Map<String, Boolean> getAbilities() {
        return new HashMap<String, Boolean>(abilities);
    }

    /**
     * Sets the ability to newValue;
     *
     * @param id a <code>String</code> value
     * @param newValue a <code>boolean</code> value
     */
    public void setAbility(String id, boolean newValue) {
        abilities.put(id, newValue);
    }

    /**
     * Get the <code>Modifier</code> value.
     *
     * @param id a <code>String</code> value
     * @return a <code>Modifier</code> value
     */
    public final Modifier getModifier(String id) {
        return modifiers.get(id);
    }

    /**
     * Set the <code>Modifier</code> value.
     *
     * @param id a <code>String</code> value
     * @param newModifier a <code>Modifier</code> value
     */
    public final void setModifier(String id, final Modifier newModifier) {
        modifiers.put(id, newModifier);
    }

    /**
     * Returns all events.
     *
     * @return a <code>List</code> of Events.
     */
    public Map<String, String> getEvents() {
        return events;
    }

    /**
     * Returns a copy of this FoundingFather's modifiers.
     *
     * @return a <code>Map</code> value
     */
    public Map<String, Modifier> getModifiers() {
        return new HashMap<String, Modifier>(modifiers);
    }

    public void readFromXmlElement(Node xml, Map<String, GoodsType> goodsTypeByRef) {

        setID(Xml.attribute(xml, "id"));
        String typeString = Xml.attribute(xml, "type");
        if ("trade".equals(typeString)) {
            type = TRADE;
        } else if ("exploration".equals(typeString)) {
            type = EXPLORATION;
        } else if ("military".equals(typeString)) {
            type = MILITARY;
        } else if ("political".equals(typeString)) {
            type = POLITICAL;
        } else if ("religious".equals(typeString)) {
            type = RELIGIOUS;
        } else {
            throw new IllegalArgumentException("FoundingFather has unknown type " + typeString);
        }                           

        weight[1] = Xml.intAttribute(xml, "weight1");
        weight[2] = Xml.intAttribute(xml, "weight2");
        weight[3] = Xml.intAttribute(xml, "weight3");

        Xml.Method method = new Xml.Method() {
                public void invokeOn(Node node) {
                    if ("ability".equals(node.getNodeName())) {
                        String abilityId = Xml.attribute(node, "id");
                        boolean value = Xml.booleanAttribute(node, "value");
                        setAbility(abilityId, value);
                    } else if ("modifier".equals(node.getNodeName())) {
                        String modifierId = Xml.attribute(node, "id");
                        String type = Xml.attribute(node, "type");
                        Float value = Float.valueOf(Xml.attribute(node, "value"));
                        setModifier(modifierId, new Modifier(modifierId, value, type));
                    } else if ("event".equals(node.getNodeName())) {
                        String eventId = Xml.attribute(node, "id");
                        String value = null;
                        if (Xml.hasAttribute(node, "value")) {
                            value = Xml.attribute(node, "value");
                        }
                        events.put(eventId, value);
                    }
                }
            };
        Xml.forEachChild(xml, method);

    }

}
