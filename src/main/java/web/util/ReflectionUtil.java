package web.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangchen
 * @date 2018/4/10 17:27
 */
public class ReflectionUtil {

    /**
     * 根据传入的属性名字符串，修改对应的属性值
     *
     * @param name
     *            属性名
     * @param obj
     *            要修改的实例对象
     * @param value
     *            修改后的新值
     */
    public static void setFieldValue(Object obj, String name, String value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ParseException {

        Field field = obj.getClass().getDeclaredField(name);

        field.setAccessible(true);
        field.set(obj, formatValue(obj.getClass(), name, value));
    }

    /**
     *  修改值的类型
     *
     * @param clazz
     *              类型
     * @param name
     *              字段名
     * @param value
     *              修改后的新值
     */
    public static Object formatValue(Class<?> clazz, String name, String value) throws ParseException {
        Map<String, EntityClass> fields = getFields(clazz);

        EntityClass entityClass = fields.get(name);

        String typeClass = entityClass.getType().getName();

        /**
         * Date
         */
        if ("java.util.Date".equals(typeClass)) {
            value = value.replace("GMT", "").replaceAll("\\(.*\\)", "");
            return new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH).parse(value);
        }
        /**
         *  Long
         */
        else if ("long".equals(typeClass)){
            return Long.parseLong(value);
        }
        /**
         *  Integer
         */
        else if ("int".equals(typeClass)){
            return Integer.parseInt(value);
        }
        return value;
    }

    /**
     * 获取所有属性
     *
     * @param clazz
     *             类
     * @return
     */
    public static Map<String, EntityClass> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        Map<String, EntityClass> stringEntityClassMap = new HashMap();

        for (Field field : fields) {
            // 修饰符
            String modifier = Modifier.toString(field.getModifiers());

            // 数据类型
            Class<?> type = field.getType();

            // 属性名
            String fieldName = field.getName();

            stringEntityClassMap.put(fieldName, new EntityClass(modifier, type));
        }

        return stringEntityClassMap;
    }

    /**
     * 根据传入的方法名字符串，获取对应的方法
     *
     * @param clazz
     *            类的Class对象
     * @param name
     *            方法名
     * @param parameterTypes
     *            方法的形参对应的Class类型【可以不写】
     * @return 方法对象【Method类型】
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredMethod(name, parameterTypes);
    }

    /**
     * 根据传入的方法对象，调用对应的方法
     *
     * @param method
     *            方法对象
     * @param obj
     *            要调用的实例对象【如果是调用静态方法，则可以传入null】
     * @param args
     *            传入方法的实参【可以不写】
     * @return 方法的返回值【没有返回值，则返回null】
     */
    public static Object invokeMethod(Method method, Object obj, Object... args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    private static class EntityClass {
        /**
         * 修饰符
         */
        String modifier;
        /**
         * 类型
         */
        Class<?> type;

        public EntityClass(String modifier, Class<?> type) {
            this.modifier = modifier;
            this.type = type;
        }

        public String getModifier() {
            return modifier;
        }

        public void setModifier(String modifier) {
            this.modifier = modifier;
        }

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }
    }
}
