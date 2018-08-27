package com.yoxjames.coldsnap.util;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public class NullUtils
{
    @Contract("null -> false")
    public static boolean isNotNull(@Nullable Object objs)
    {
        return objs != null;
    }

    @Contract("null -> true")
    public static boolean isNull(@Nullable Object objs)
    {
        return objs == null;
    }

    @Contract("null -> fail")
    public static <T> T checkNotNull(@Nullable T obj)
    {
        if (obj == null)
            throw new NullPointerException();
        else
            return obj;
    }
}
