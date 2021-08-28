package me.faintcloudy.bedwars.scoreboard;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static java.util.Arrays.asList;

public class FixedBody implements Body
{
    private final LineListBuilder builder;

    private FixedBody(LineListBuilder builder)
    {
        this.builder = builder;
    }

    public static List<LinePair> getFixedList(List<String> list)
    {
        int size = list.size();
        ImmutableList.Builder<LinePair> b = ImmutableList.builder();
        for (String line : list)
        {
            b.add(LinePair.of(line, size--));
        }
        return b.build();
    }

    public static FixedBody of(String... list)
    {
        return of(asList(list));
    }

    public static FixedBody of(List<String> list)
    {
        return of(() -> list);
    }

    public static FixedBody of(LineListBuilder builder)
    {
        return new FixedBody(builder);
    }

    @Override public List<LinePair> getList()
    {
        return getFixedList(builder.build());
    }
}
