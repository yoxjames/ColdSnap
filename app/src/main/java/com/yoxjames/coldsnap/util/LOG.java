/*
 * Copyright (c) 2017 James Yox
 *
 * This file is part of ColdSnap.
 *
 * ColdSnap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ColdSnap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ColdSnap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yoxjames.coldsnap.util;

/**
 * Created by yoxjames on 8/27/17.
 */

public class LOG
{
    static private Logger logger;

    public static void e(String tag, String msg)
    {
        defaultLogger();
        logger.e(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        defaultLogger();
        logger.d(tag, msg);
    }

    static void defaultLogger()
    {
        if (logger == null)
            logger = new DefaultLoggerImpl();
    }

    public static void androidLogger()
    {
        if (logger == null)
            logger = new AndroidLoggerImpl();
    }
}
