package com.raoulvdberge.refinedstorage.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.reflect.ClassPath;

public class TestDependencies {

    public static void main(String... args) throws IOException {
        String myPackage = args[0];
        String obsPackage = args[1];
        Boolean debug = args.length > 2 && args[2].equals("-v");
        ClassPath cp = com.google.common.reflect.ClassPath.from(Thread.currentThread().getContextClassLoader());
        for (final ClassPath.ClassInfo classInfo : cp.getTopLevelClassesRecursive(myPackage)) {
            if (debug)
                System.out.println("Class:" + classInfo.getName());
            Class clazz = classInfo.load();
            if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                for (final Class inter : clazz.getInterfaces()) {
                    if (debug)
                        System.out.println("\tImpliments: " + inter.getSimpleName());
                    for (final Method method : inter.getDeclaredMethods()) {
                        if (debug)
                            System.out.println("\t\tMethod: " + method.toString());
                        if (!method.isSynthetic()) {
                            if (!Modifier.isStatic(method.getModifiers())) {
                                try {
                                    Method found = clazz.getMethod(method.getName(), method.getParameterTypes());
                                    if (debug) System.out.println("\t\t\tFound: " + found.toString());
                                    if (!inter.getPackage().getName().startsWith(obsPackage)) {
                                        if (found.getDeclaringClass().getPackage().getName().startsWith(obsPackage)) {
                                            System.out.println(
                                                    "ABSTRACT METHOD CALL: Class: " + clazz.getName() + ", Interface: "
                                                            + inter.getSimpleName() + ", Method: " + method
                                                            .toGenericString());
                                        } else if (debug) {
                                            System.out.println("\t\t\t\tNever obfuscated");
                                        }
                                    } else if (debug) {
                                        System.out.println("\t\t\t\tAlways obfuscated");
                                    }
                                } catch (NoSuchMethodException e) {
                                    System.out.println(
                                            "METHOD NOT FOUND: Class: " + clazz.getName() + ", Interface: " + inter
                                                    .getSimpleName() + ", Method: " + method.toGenericString());
                                }
                            } else if (debug) {
                                System.out.println("\t\t\tIs Static");
                            }
                        } else if (debug) {
                            System.out.println("\t\t\tIs Synthetic");
                        }
                    }
                }
            } else if (debug) {
                System.out.println("\tIs not a full class");
            }
        }
    }
}
