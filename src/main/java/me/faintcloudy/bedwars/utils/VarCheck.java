package me.faintcloudy.bedwars.utils;

public class VarCheck {

    public static boolean check(Object object)
    {
        try {
            Object no = object;
            if (no == null)
                return false;

        } catch (Exception exception)
        {
            exception.printStackTrace();
            return false;
        }

        return true;
    }
}
