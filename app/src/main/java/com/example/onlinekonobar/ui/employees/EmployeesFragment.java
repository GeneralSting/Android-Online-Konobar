package com.example.onlinekonobar.ui.employees;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinekonobar.Formatter.BarChartBills;
import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.CafeBill;
import com.example.onlinekonobar.Models.DrinkBill;
import com.example.onlinekonobar.Models.Employee;
import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.databinding.FragmentEmployeesBinding;
import com.example.onlinekonobar.ui.home.HomeViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeesFragment extends Fragment {

private FragmentEmployeesBinding binding;

    //global variables/objects
    BarChart barChart;
    ToastMessage toastMessage;
    HashMap<String, Integer> employeeBills;

    //firebase
    private DatabaseReference cafeBillsRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

    binding = FragmentEmployeesBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        toastMessage = new ToastMessage(getActivity());
        employeeBills = new HashMap<>();
        barChart = binding.employeesBarChart;
        barChart.setNoDataText(getResources().getString(R.string.no_bar_chart));
        Paint paint = barChart.getPaint(barChart.PAINT_INFO);
        paint.setTextSize(48);


        EmployeesViewModel employeesViewModel = new ViewModelProvider(getActivity()).get(EmployeesViewModel.class);
        collectCafeBills(employeesViewModel.getCafeId().getValue());

        return root;
    }

    public void collectCafeBills(String cafeId) {
        cafeBillsRef = FirebaseDatabase.getInstance().getReference("cafes/" + cafeId +
            "/cafeBills");
        cafeBillsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshotCafeBill: snapshot.getChildren()) {
                    CafeBill cafeBill = snapshotCafeBill.getValue(CafeBill.class);
                    if (employeeBills == null || employeeBills.isEmpty()) {
                        employeeBills.put(cafeBill.getCafeBillEmployee(), 1);
                    }
                    else {
                        if(employeeBills.containsKey(cafeBill.getCafeBillEmployee()))
                            employeeBills.put(cafeBill.getCafeBillEmployee(), employeeBills.get(cafeBill.getCafeBillEmployee()) + 1);
                        else
                            employeeBills.put(cafeBill.getCafeBillEmployee(), 1);
                    }
                }
                collectNumberEmployee();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    public void collectNumberEmployee() {
        DatabaseReference employeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
        employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot employeeSnapshot: snapshot.getChildren()) {
                    boolean collectedEmployee = false;
                    for (String key : employeeBills.keySet()) {
                        if(key.equals(employeeSnapshot.getKey())) {
                            collectedEmployee = true;
                        }
                    }
                    if(collectedEmployee) {
                        int employeeBillsAmount = employeeBills.get(employeeSnapshot.getKey());
                        employeeBills.remove(employeeSnapshot.getKey());
                        employeeBills.put(employeeSnapshot.getValue(Employee.class).getLastname(), employeeBillsAmount);
                    }
                }
                makeBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    public void makeBarChart() {
        if (employeeBills != null && !employeeBills.isEmpty()) {

            int[] barChartColors = new int[]{getResources().getColor(R.color.bar_chart_muddy_green),
                    getResources().getColor(R.color.bar_chart_muddy_blue),
                    getResources().getColor(R.color.bar_chart_muddy_red),
                    getResources().getColor(R.color.bar_chart_muddy_purple),
                    getResources().getColor(R.color.pewter_blue),
                    getResources().getColor(R.color.tumbleweed),
                    getResources().getColor(R.color.light_cyan)};

            int loopCounter = 0;
            ArrayList barChartValues = new ArrayList();
            List<LegendEntry> barChartLegendLabels = new ArrayList<>();

            for (String key : employeeBills.keySet()) {
                barChartValues.add(new BarEntry(loopCounter, employeeBills.get(key)));
                LegendEntry entry = new LegendEntry();
                entry.formColor = barChartColors[loopCounter];
                entry.label = key;
                barChartLegendLabels.add(entry);
                loopCounter++;
            }

            barChart.setExtraOffsets(5f,0f,5f,48f);
            barChart.getDescription().setText(getResources().getString(R.string.bar_chart_description));
            barChart.getDescription().setTextSize(20f);
            barChart.getDescription().setYOffset(-20f);

            BarDataSet barDataSet = new BarDataSet(barChartValues, "");
            barDataSet.setColors(barChartColors);
            barDataSet.setValueTextSize(16f);

            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new BarChartBills());

            XAxis xAxis = barChart.getXAxis();
            xAxis.setEnabled(false);

            Legend barChartLegend = barChart.getLegend();
            barChartLegend.setWordWrapEnabled(true);
            barChartLegend.setForm(Legend.LegendForm.CIRCLE);
            barChartLegend.setFormSize(10f);
            barChartLegend.setTextSize(12f);
            barChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            barChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            barChartLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            barChartLegend.setDrawInside(false);
            barChartLegend.setCustom(barChartLegendLabels);

            barChart.setData(barData);
            barChart.animateY(600);
            barChart.invalidate();
        }
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}