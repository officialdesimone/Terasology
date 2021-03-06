package org.terasology.entitySystem.metadata;

import com.google.common.collect.Maps;
import org.terasology.entitySystem.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Immortius <immortius@gmail.com>
 */
public final class ComponentMetadata<T extends Component> {
    private static final Logger logger = Logger.getLogger(ComponentMetadata.class.getName());

    private Map<String, FieldMetadata> fields = Maps.newHashMap();
    private Class<T> clazz;

    public ComponentMetadata(Class<T> componentClass) {
        this.clazz = componentClass;
    }

    public Class<T> getType() {
        return clazz;
    }

    public void addField(FieldMetadata fieldInfo) {
        fields.put(fieldInfo.getName().toLowerCase(Locale.ENGLISH), fieldInfo);
    }

    public FieldMetadata getField(String name) {
        return fields.get(name.toLowerCase(Locale.ENGLISH));
    }

    public Iterable<? extends FieldMetadata> iterateFields() {
        return fields.values();
    }

    public T newInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Exception instantiating component type: " + clazz, e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Exception instantiating component type: " + clazz, e);
        }
        return null;
    }

    public T clone(T component) {
        try {
            T result = clazz.newInstance();
            for (FieldMetadata field : fields.values()) {
                field.setValue(result, field.copy(field.getValue(component)));
            }
            return result;
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Exception during serializing component type: " + clazz, e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Exception during serializing component type: " + clazz, e);
        } catch (InvocationTargetException e) {
            logger.log(Level.SEVERE, "Exception during serializing component type: " + clazz, e);
        }
        return null;
    }


}