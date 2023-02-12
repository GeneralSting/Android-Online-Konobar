package com.example.onlinekonobar.Formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.Collection;

public class BarChartBills extends ValueFormatter {
    private DecimalFormat mFormat;

    public BarChartBills() {
        mFormat = new DecimalFormat("###,##0");
    }

    @Override
    public String getBarLabel(BarEntry barEntry) {
        return mFormat.format(barEntry.getY());
    }
}
