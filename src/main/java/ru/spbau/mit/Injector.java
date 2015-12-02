package ru.spbau.mit;

import java.lang.reflect.Constructor;
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
        constructedObjects = new Object[implementationClassNames.size()];
        constructedClasses = new Class[implementationClassNames.size()];
        startedToCreate = new boolean[implementationClassNames.size()];

        for (int i = 0; i < implementationClassNames.size(); i++) {
            constructedObjects[i] = null;
            startedToCreate[i] = false;
            constructedClasses[i] = Class.forName(implementationClassNames.get(i));
        }

        return makeObjectOfClass(resultClass);
    }

    private static Object makeObjectOfClass(Class rootClass) throws Exception {
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
    }
}