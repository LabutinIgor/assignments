package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    private static Object[] constructedObjects;
    private static Class<?>[] constructedClasses;
    private static boolean[] startedToCreate;

    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class resultClass = Class.forName(rootClassName);
        constructedObjects = new Object[implementationClassNames.size() + 1];
        constructedClasses = new Class[implementationClassNames.size() + 1];
        startedToCreate = new boolean[implementationClassNames.size() + 1];

        constructedClasses[0] = resultClass;
        for (int i = 0; i < implementationClassNames.size(); i++) {
            constructedClasses[i + 1] = Class.forName(implementationClassNames.get(i));
        }

        startedToCreate[0] = true;
        return makeObjectOfClass(resultClass);
    }

    private static Object makeObjectOfClass(Class rootClass) throws Exception {
        try {
            Constructor resultClassConstructor = rootClass.getConstructors()[0];
            Class<?>[] parametersClasses = resultClassConstructor.getParameterTypes();
            List<Object> arguments = new ArrayList<>();

            for (Class<?> parameterClass : parametersClasses) {
                boolean argumentAdded = false;
                for (int i = 0; i < constructedClasses.length; i++) {
                    if (parameterClass.isAssignableFrom(constructedClasses[i])) {
                        if (argumentAdded) {
                            throw new AmbiguousImplementationException();
                        }
                        if (constructedObjects[i] == null) {
                            if (startedToCreate[i]) {
                                throw new InjectionCycleException();
                            }
                            startedToCreate[i] = true;
                            constructedObjects[i] = makeObjectOfClass(constructedClasses[i]);
                        }
                        arguments.add(constructedObjects[i]);
                        argumentAdded = true;
                    }
                }
                if (!argumentAdded) {
                    throw new ImplementationNotFoundException();
                }
            }

            return resultClassConstructor.newInstance(arguments.toArray());
        } catch (InvocationTargetException ignored) {
            throw new AmbiguousImplementationException();
        }
    }
}