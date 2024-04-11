package net.sf.freecol.docastest;

import net.sf.freecol.common.model.*;
import net.sf.freecol.util.test.FreeColTestCase;

import java.lang.reflect.Field;

public class ModelObjects {

    public static void init(Class<?> clazz) {
        System.out.println("Clazz: " + clazz.getName());
        final Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                final String objectKey = getObjectKey(declaredField);
                System.out.println("ModelObjects.init key " + objectKey);
                final FreeColSpecObjectType type = FreeColTestCase.spec().getType(objectKey);
                System.out.println("ModelObjects.init TypeClass " + type.getClass().getSimpleName());
                declaredField.set(null, type);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                declaredField.setAccessible(false);
            }
        }
        System.out.println("ModelObjects.init muskets: " + goods.muskets);
        System.out.println("ModelObjects.init church: " + building.church);
//        System.out.println("ModelObjects.init dragoon: " + role.dragoon);
    }

    private static String getObjectKey(Field declaredField) {
        return String.format("model.%s.%s", declaredField.getDeclaringClass().getSimpleName(), declaredField.getName()).toLowerCase();
    }

    public static class building {
        public static BuildingType church;

        static {
            ModelObjects.init(building.class);
        }

    }

    public static class goods {
        public static GoodsType muskets;

        static {
            ModelObjects.init(goods.class);
        }

    }

    public static class role {
        public static Role dragoon;
        public static Role nativeDragoon;

        static {
            ModelObjects.init(role.class);
        }

    }

    public static class tile {
        public static TileType arctic;

        static {
            ModelObjects.init(tile.class);
        }

    }

    public static class unit {
        public static UnitType brave;

        static {
            ModelObjects.init(unit.class);
        }

    }
}

