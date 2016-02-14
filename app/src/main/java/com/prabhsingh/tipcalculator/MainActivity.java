package com.prabhsingh.tipcalculator;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
                    SwipeRefreshLayout.OnRefreshListener {

    //Tag for Debugging Puposes
    private static final String TAG = "TIP";

    private SwipeRefreshLayout swipeContainer;

    private CardView card_view1;
    private CardView card_view2;
    private CardView card_view3;

    private EditText billAmountField;
    private Spinner tipPercentField;

    private TextView tipAmountField;
    private TextView grandTotalField;

    private SeekBar splitField;
    private TextView splitProgress;
    private TextView amountPerPersonField;

    private Boolean firstTime = true;
    private Boolean animate = true;

    //Variables which will save the Instance State
    private static String AMOUNT_INDEX = "amount";
    private static String TIP_INDEX = "tip";
    private static String SPLIT_INDEX = "split";
    private Double tipPercent = 5.0;
    private Double amount = 0.0;
    private int split = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mapping all IDs to instance variables
        card_view1 = (CardView) findViewById(R.id.card_view1);
        card_view2 = (CardView) findViewById(R.id.card_view2);
        card_view3 = (CardView) findViewById(R.id.card_view3);

        billAmountField = (EditText) findViewById(R.id.billAmountField);
        tipPercentField = (Spinner) findViewById(R.id.tipPercentField);

        tipAmountField = (TextView) findViewById(R.id.tipAmountField);
        grandTotalField = (TextView) findViewById(R.id.grandTotalField);

        splitField = (SeekBar) findViewById(R.id.splitField);
        splitProgress = (TextView) findViewById(R.id.splitProgress);
        amountPerPersonField = (TextView) findViewById(R.id.amountPerPersonField);
        //All IDs Mapped


        //All values should change accordingly so Listeners added to Seekbar and EditText
        billAmountField.addTextChangedListener(mTextEditorWatcher);
        splitField.setOnSeekBarChangeListener(SplitWatcher);

        //Hiding Card_View2 and Card_View3 initially for the Animation
        card_view2.setTranslationY(1500f);
        card_view3.setTranslationY(2000f);


        //Retrieving Spinner Values for Tip Percentage
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tip_percent, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipPercentField.setAdapter(adapter);
        tipPercentField.setOnItemSelectedListener(this);

        //Swipe Gesture Support
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);

        //SeekBar Initial setup
        splitField.setProgress(1);
        splitField.incrementProgressBy(1);
        splitField.setMax(20);
    }

    //Clear all fields by swiping from top to down
    @Override
    public void onRefresh() {
        billAmountField.setText("");
        tipAmountField.setText("$0.00");
        grandTotalField.setText("$0.00");
        splitField.setProgress(1);
        amountPerPersonField.setText("$0.00");

        Log.i(TAG, "Swipe to Refresh initiated");
    }

    //All the Calculations are in this method and ChangeSplit()
    public void display() {
        try {
            amount = Double.parseDouble(billAmountField.getText().toString());
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        tipAmountField.setText("$" + String.format("%.2f", (Double) ((tipPercent / 100.0f) * amount)));
        grandTotalField.setText("$" + String.format("%.2f", (Double) (amount + ((tipPercent / 100.0f) * amount))));

        //If Split field is pre-selected(Orientation), adjust the distribution again.
        changeSplit();

        if (animate == true) {
            //Bringing the cards in the view
            card_view2.animate().translationYBy(-1500f).setDuration(1500);
            card_view3.animate().translationYBy(-2000f).setDuration(1500);
            //Animation should only happen once
            animate = false;
        }
    }

    //Distributing Money among friends
    public void changeSplit() {
        try {
            split = splitField.getProgress();
            double grandTotal = Double.parseDouble(grandTotalField.getText().toString().substring(1, grandTotalField.getText().length()));
            amountPerPersonField.setText("$" + String.format("%.2f", grandTotal / split));
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        if (firstTime == true) {
            //Don't do anything when the user opens up the App
            firstTime = false;
        } else if (firstTime == false) {
            Double amount = 0.00;
            try {
                amount = Double.parseDouble(billAmountField.getText().toString());
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }

            Double tipPercent = 0.00;
            try {
                //Why Substring: to remove $ sign
                tipPercent = Double.parseDouble(parent.getItemAtPosition(pos).toString().substring(0, 2));
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }

            tipAmountField.setText("$" + String.format("%.2f", (Double) ((tipPercent / 100.0f) * amount)));
            grandTotalField.setText("$" + String.format("%.2f", (Double) (amount + ((tipPercent / 100.0f) * amount))));

            //If Split field is pre-selected, adjust the distribution again.
            changeSplit();

            if (animate == true) {
                //Bringing the cards in the view
                card_view2.animate().translationYBy(-1000f).setDuration(1500);
                card_view3.animate().translationYBy(-1500f).setDuration(1500);
                //Animation should only happen once
                animate = false;
            }
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        //Have to implement Abstract Method even if nothing needs to be done
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            display();
        }
    };

    private final SeekBar.OnSeekBarChangeListener SplitWatcher = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                //Set the label for SeekBar progress
                splitProgress.setText(Integer.toString(progress));
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
            changeSplit();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    //Orientation Check


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "OnStart() Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause() Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "OnResume() Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "OnStop() Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "OnDestroy() Called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "Onrestart() Called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "Saved Instance State, Amount_INDEX:" + AMOUNT_INDEX);
        outState.putDouble(AMOUNT_INDEX, amount);
        outState.putDouble(TIP_INDEX, tipPercent);
        outState.putInt(SPLIT_INDEX, split);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "Restored Instance State " + savedInstanceState.getInt(AMOUNT_INDEX, 0));
        amount = savedInstanceState.getDouble(AMOUNT_INDEX, 100);
        tipPercent = savedInstanceState.getDouble(TIP_INDEX, 5);
        split = savedInstanceState.getInt(SPLIT_INDEX, 1);
    }
}
