package net.sf.freecol.docastest.gui;

import net.sf.freecol.common.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FreeColResources {
    public TileType mountains;
    public TileType prairie;
    public TileType plains;
    public TileType highSeas;

    public UnitType freeColonist;
    public UnitType veteranSoldier;

    public GoodsType sugar;

    public Role soldier;

    FreeColResources(Specification spec) {
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            try {
                final String type = String.format("model.%s.%s",
                        declaredField.getType().getSimpleName().replace("Type", "").toLowerCase(),
                        declaredField.getName());
                final Method method = spec.getClass().getMethod("get" + declaredField.getType().getSimpleName(), String.class);
                declaredField.set(this, method.invoke(spec, type));
            } catch (IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
